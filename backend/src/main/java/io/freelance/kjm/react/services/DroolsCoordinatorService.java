package io.freelance.kjm.react.services;

import io.freelance.kjm.react.dto.AuditItemDto;
import io.freelance.kjm.react.dto.PricingRequest;
import io.freelance.kjm.react.dto.PricingResponse;
import io.freelance.kjm.react.facts.*;
import io.freelance.kjm.react.storage.WorkingMemoryStore;
import io.freelance.kjm.react.template.ReActTemplateBean;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service coordinating Drools rule-based ReAct perception-action loop execution.
 * Integrates with WorkingMemoryStore to provide SHA-256 hashed memory restoration
 * and automatic cache persistence.
 */
@Service
public class DroolsCoordinatorService {
    private static final Logger log = LoggerFactory.getLogger(DroolsCoordinatorService.class);

    private final KieContainer kieContainer;
    private final ToolExecutionService toolService;
    private final GeminiService geminiService;
    private final ReActTemplateBean templateBean;
    private final WorkingMemoryStore wmStore;

    public DroolsCoordinatorService(
            KieContainer kieContainer,
            ToolExecutionService toolService,
            GeminiService geminiService,
            ReActTemplateBean templateBean,
            WorkingMemoryStore wmStore) {
        this.kieContainer = kieContainer;
        this.toolService = toolService;
        this.geminiService = geminiService;
        this.templateBean = templateBean;
        this.wmStore = wmStore;
    }

    /**
     * Executes the ReAct perception-action loop using the Drools forward-chaining rule engine.
     */
    public PricingResponse coordinatePricing(PricingRequest request) {
        String sessionId = request.getSession_id();
        if (sessionId == null || sessionId.trim().isEmpty()) {
            sessionId = "sess-" + UUID.randomUUID().toString().substring(0, 12);
        }

        String product = request.getProduct() != null ? request.getProduct() : "Apple MacBook Pro 14\"";
        String currencyCode = request.getCurrency_code() != null ? request.getCurrency_code() : "EUR";
        Double exchangeRate = request.getExchange_rate() != null ? request.getExchange_rate() : 0.85;

        // Build guard-railed prompt template matching PyRete specification identically
        String query = request.getCustom_query();
        if (query == null || query.trim().isEmpty()) {
            String currName = request.getCurrency_name() != null && !request.getCurrency_name().trim().isEmpty()
                    ? request.getCurrency_name().trim()
                    : currencyCode;
            String rateStr = exchangeRate == Math.floor(exchangeRate) && !Double.isInfinite(exchangeRate)
                    ? String.valueOf((long) exchangeRate.doubleValue())
                    : String.valueOf(exchangeRate);
            query = String.format(
                    "What is the total retail purchase price (MSRP) of a new entry-level %s in USD? " +
                    "Do not use monthly financing. " +
                    "How much would it cost in %s (%s) if the exchange rate is %s %s for 1 USD? " +
                    "Use the Calculator tool to do the conversion. Do not do it yourself.",
                    product, currName, currencyCode, rateStr, currencyCode
            );
        }

        String queryHash = WorkingMemoryStore.computeQueryHash(query);
        boolean wasCachedInitially = wmStore.exists(query);

        log.info("[DroolsCoordinator] Starting session '{}' for query: '{}' [hash: {}... cached: {}]",
                sessionId, query, queryHash.substring(0, Math.min(10, queryHash.length())), wasCachedInitially);

        KieSession kSession = kieContainer.newKieSession("ksession-rules");
        int totalRulesFired = 0;
        Finished finalFact = null;
        List<AuditItemDto> auditList = new ArrayList<>();

        try {
            // Register Spring beans and services as Drools globals
            kSession.setGlobal("toolService", toolService);
            kSession.setGlobal("geminiService", geminiService);
            kSession.setGlobal("templateBean", templateBean);
            kSession.setGlobal("wmStore", wmStore);

            // Seed initial Working Memory Elements
            Question questionFact = new Question(sessionId, query, 1);
            Goal goalFact = new Goal(sessionId, Goal.REASON, 1);
            goalFact.setProduct(product);
            goalFact.setTargetCurrency(currencyCode);
            goalFact.setExchangeRate(exchangeRate);
            StepTracker trackerFact = new StepTracker(sessionId, 1, 8);

            kSession.insert(questionFact);
            kSession.insert(goalFact);
            kSession.insert(trackerFact);
            kSession.insert(new ToolFact("duckduck", toolService.getSearchService().getDescription()));
            kSession.insert(new ToolFact("Calculator", toolService.getCalculatorService().getDescription()));

            // Fire forward-chaining rules (Rule 0 salience 200 pulls cache if present;
            // otherwise Rule 3 Reason and Rule 4 Act execute, with Rule 1 persisting on Finished)
            totalRulesFired = kSession.fireAllRules(50);
            log.info("[DroolsCoordinator] Completed inference execution for session '{}': fired {} rules", sessionId, totalRulesFired);

            // Extract working memory facts for chronological audit trail
            Collection<?> facts = kSession.getObjects();

            for (Object f : facts) {
                if (f instanceof Thought) {
                    Thought t = (Thought) f;
                    if (sessionId.equals(t.getSessionId())) {
                        auditList.add(new AuditItemDto(t.getStep(), "Thought", t.getText()));
                    }
                } else if (f instanceof Action) {
                    Action a = (Action) f;
                    if (sessionId.equals(a.getSessionId())) {
                        String input = a.getActionInput() != null ? a.getActionInput().getText() : "";
                        auditList.add(new AuditItemDto(a.getStep(), "Action", "Tool: " + a.getName() + (input.isEmpty() ? "" : " | Input: " + input)));
                    }
                } else if (f instanceof ActionInput) {
                    ActionInput ai = (ActionInput) f;
                    if (sessionId.equals(ai.getSessionId())) {
                        auditList.add(new AuditItemDto(ai.getStep(), "Action Input", ai.getText()));
                    }
                } else if (f instanceof Observation) {
                    Observation o = (Observation) f;
                    if (sessionId.equals(o.getSessionId())) {
                        auditList.add(new AuditItemDto(o.getStep(), "Observation", o.getText()));
                    }
                } else if (f instanceof FinalAnswer) {
                    FinalAnswer fa = (FinalAnswer) f;
                    if (sessionId.equals(fa.getSessionId())) {
                        auditList.add(new AuditItemDto(fa.getStep(), "Final Answer", fa.getText()));
                    }
                } else if (f instanceof Finished) {
                    Finished fin = (Finished) f;
                    if (sessionId.equals(fin.getSessionId())) {
                        finalFact = fin;
                        auditList.add(new AuditItemDto(fin.getStep() != null ? fin.getStep() + 1 : 99, "Finished", fin.getFinalAnswer()));
                    }
                }
            }

            // Sort audit items chronologically by step number
            auditList.sort(Comparator.comparingInt(item -> item.getStep() != null ? item.getStep() : 0));

        } catch (Exception e) {
            log.error("[DroolsCoordinator] Error during rule coordination for session '{}': {}", sessionId, e.getMessage(), e);
        } finally {
            kSession.dispose();
            log.info("[DroolsCoordinator] Disposed KieSession for session '{}'", sessionId);
        }

        // Assemble PricingResponse
        PricingResponse response = new PricingResponse();
        response.setSession_id(sessionId);
        response.setProduct(product);
        response.setCurrency(currencyCode);
        response.setExchange_rate(exchangeRate);
        response.setQuery(query);
        response.setQuery_hash(queryHash);
        response.setRules_fired(totalRulesFired);
        response.setAudit_facts(auditList);
        response.setCached(wasCachedInitially);

        if (wasCachedInitially) {
            response.setKnowledge_source("Materialized Working Memory (SHA-256: " + queryHash.substring(0, Math.min(12, queryHash.length())) + "...)");
        } else {
            response.setKnowledge_source("Live Gemini LLM Inference (" + geminiService.getModelName() + ")");
        }

        if (finalFact != null && finalFact.getFinalAnswer() != null) {
            response.setSuccess(true);
            response.setStatus("COMPLETED");
            response.setFinal_answer(finalFact.getFinalAnswer());
        } else {
            response.setSuccess(false);
            response.setStatus("FAILED");
            response.setFinal_answer("Rule engine was unable to formulate a finalized price within cycle budget.");
        }

        return response;
    }

    public ToolExecutionService getToolService() {
        return toolService;
    }

    public GeminiService getGeminiService() {
        return geminiService;
    }

    public ReActTemplateBean getTemplateBean() {
        return templateBean;
    }

    public WorkingMemoryStore getWmStore() {
        return wmStore;
    }
}
