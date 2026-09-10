package io.freelance.kjm.react.services;

import io.freelance.kjm.react.dto.AuditItemDto;
import io.freelance.kjm.react.dto.PricingRequest;
import io.freelance.kjm.react.dto.PricingResponse;
import io.freelance.kjm.react.facts.*;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Enterprise coordinator service executing the goal-driven ReAct loop within a Drools KieSession.
 */
@Service
public class DroolsCoordinatorService {
    private static final Logger log = LoggerFactory.getLogger(DroolsCoordinatorService.class);

    private final KieContainer kieContainer;
    private final ToolExecutionService toolService;

    public DroolsCoordinatorService(KieContainer kieContainer, ToolExecutionService toolService) {
        this.kieContainer = kieContainer;
        this.toolService = toolService;
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

        log.info("[DroolsCoordinator] Starting session '{}' for product '{}' -> {} (rate: {})",
                sessionId, product, currencyCode, exchangeRate);

        KieSession kSession = kieContainer.newKieSession("ksession-rules");
        int totalRulesFired = 0;
        Finished finalFact = null;
        List<AuditItemDto> auditList = new ArrayList<>();

        try {
            // Register Spring tool execution service as Drools global
            kSession.setGlobal("toolService", toolService);

            // Seed Working Memory Elements
            Goal goal = new Goal(sessionId, product, currencyCode, exchangeRate);
            kSession.insert(goal);
            kSession.insert(new ToolFact("duckduck", "Web retail search engine for hardware MSRP"));
            kSession.insert(new ToolFact("Calculator", "Deterministic arithmetic and currency multiplier"));

            int maxCycles = 10;
            int cycle = 0;

            while (cycle++ < maxCycles) {
                // Fire rules in agenda
                int fired = kSession.fireAllRules();
                totalRulesFired += fired;
                log.debug("[DroolsCoordinator] Cycle {}: Fired {} rules (Total: {})", cycle, fired, totalRulesFired);

                // Check for pending actions to dispatch
                Map<FactHandle, Action> pendingActions = new HashMap<>();
                for (FactHandle handle : kSession.getFactHandles()) {
                    Object obj = kSession.getObject(handle);
                    if (obj instanceof Action) {
                        Action action = (Action) obj;
                        if (Action.STATUS_PENDING.equals(action.getStatus())) {
                            pendingActions.put(handle, action);
                        }
                    } else if (obj instanceof Finished) {
                        finalFact = (Finished) obj;
                    }
                }

                if (pendingActions.isEmpty()) {
                    // No pending actions; engine reached quiescence
                    break;
                }

                // Execute each pending action and insert resulting observation
                for (Map.Entry<FactHandle, Action> entry : pendingActions.entrySet()) {
                    FactHandle actionHandle = entry.getKey();
                    Action action = entry.getValue();

                    Observation obs = toolService.execute(action);
                    action.setStatus(Action.STATUS_EXECUTED);
                    kSession.update(actionHandle, action);
                    kSession.insert(obs);
                }
            }

            // Extract working memory facts for chronological audit trail
            List<Object> facts = new ArrayList<>(kSession.getObjects());

            for (Object f : facts) {
                if (f instanceof Thought) {
                    Thought t = (Thought) f;
                    auditList.add(new AuditItemDto(t.getStep(), "Thought", t.getContent()));
                } else if (f instanceof Action) {
                    Action a = (Action) f;
                    String input = a.getActionInput() != null ? a.getActionInput().getRawInput() : "";
                    auditList.add(new AuditItemDto(a.getStep(), "Action", "Tool: " + a.getToolName() + " | Input: " + input));
                } else if (f instanceof Observation) {
                    Observation o = (Observation) f;
                    auditList.add(new AuditItemDto(o.getStep(), "Observation", o.getResult()));
                } else if (f instanceof Finished) {
                    finalFact = (Finished) f;
                    auditList.add(new AuditItemDto(4, "Finished", finalFact.getFinalAnswer()));
                }
            }

            // Sort audit items chronologically by step number
            auditList.sort(Comparator.comparingInt(item -> item.getStep() != null ? item.getStep() : 0));

        } catch (Exception e) {
            log.error("[DroolsCoordinator] Error during rule coordination: {}", e.getMessage(), e);
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
        response.setQuery(request.getCustom_query() != null ? request.getCustom_query() : "What is the price of " + product + " in " + currencyCode + "?");
        response.setRules_fired(totalRulesFired);
        response.setAudit_facts(auditList);
        response.setCached(false);

        if (finalFact != null) {
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
}
