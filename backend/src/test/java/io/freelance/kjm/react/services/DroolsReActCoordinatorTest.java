package io.freelance.kjm.react.services;

import io.freelance.kjm.react.facts.*;
import io.freelance.kjm.react.storage.WorkingMemoryStore;
import io.freelance.kjm.react.template.ReActTemplateBean;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 test suite verifying the end-to-end Drools forward-chaining Reason & Act inference cycle,
 * rule salience conflict resolution, and working memory cache loading/persisting.
 */
class DroolsReActCoordinatorTest {

    private KieContainer kieContainer;
    private ToolExecutionService toolService;
    private ReActTemplateBean templateBean;
    private WorkingMemoryStore wmStore;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        KieServices ks = KieServices.Factory.get();
        kieContainer = ks.getKieClasspathContainer();
        toolService = new ToolExecutionService(new DuckDuckGoSearchService(), new CalculatorService());
        templateBean = new ReActTemplateBean();
        wmStore = new WorkingMemoryStore(tempDir.toString());
    }

    @Test
    @DisplayName("Should execute 2-turn ReAct loop in Drools, persist WM cache upon Finished, and terminate")
    void testDroolsReasonActCycleAndCachePersistence() {
        KieSession kSession = kieContainer.newKieSession("ksession-rules");
        assertNotNull(kSession, "KieSession 'ksession-rules' must be defined in kmodule.xml");

        String sessionId = "mock-react-session-001";
        String question = "What is the price of Apple MacBook Pro 14\" in EUR?";

        // Mock Gemini service that simulates 2-turn ReAct reasoning
        AtomicInteger invocationCount = new AtomicInteger(0);
        GeminiService mockGemini = new GeminiService() {
            @Override
            public String invoke(String prompt) {
                int count = invocationCount.incrementAndGet();
                if (count == 1) {
                    return "Thought: I need to check retail search indices for starting MSRP of Apple MacBook Pro 14\" in USD.\n" +
                           "Action: duckduck\n" +
                           "Action Input: current price of Apple MacBook Pro 14\" in USD";
                } else {
                    return "Thought: I now know the final answer based on retail observation.\n" +
                           "Final Answer: The current retail price for Apple MacBook Pro 14\" is $1599.00 USD, which converts to approximately 1359.15 EUR.";
                }
            }

            @Override
            public boolean isConfigured() {
                return true;
            }
        };

        // Register globals
        kSession.setGlobal("toolService", toolService);
        kSession.setGlobal("geminiService", mockGemini);
        kSession.setGlobal("templateBean", templateBean);
        kSession.setGlobal("wmStore", wmStore);

        // Seed initial Working Memory facts
        Question questionFact = new Question(sessionId, question, 1);
        Goal goalFact = new Goal(sessionId, Goal.REASON, 1);
        StepTracker trackerFact = new StepTracker(sessionId, 1, 8);

        kSession.insert(questionFact);
        kSession.insert(goalFact);
        kSession.insert(trackerFact);

        // Fire all rules
        int rulesFired = kSession.fireAllRules(50);
        assertTrue(rulesFired >= 4, "Should fire at least Reason, Act, Reason, Act, Finished rules");

        // Inspect Working Memory
        Collection<?> facts = kSession.getObjects();

        Finished finishedFact = facts.stream()
                .filter(f -> f instanceof Finished)
                .map(f -> (Finished) f)
                .filter(f -> sessionId.equals(f.getSessionId()))
                .findFirst().orElse(null);

        assertNotNull(finishedFact, "Finished fact must be asserted in Working Memory");
        assertTrue(finishedFact.getFinalAnswer().contains("1359.15 EUR"));

        // Verify Goal reached FINISHED state via high-salience rule
        assertEquals(Goal.FINISHED, goalFact.getGoalType());

        // Verify Observation fact was produced and inserted into Working Memory
        boolean hasObservation = facts.stream()
                .filter(f -> f instanceof Observation)
                .map(f -> (Observation) f)
                .anyMatch(o -> sessionId.equals(o.getSessionId()) && o.getText().contains("1599.00 USD"));

        assertTrue(hasObservation, "Working Memory must contain Observation asserted by Act production");

        // Verify that newly deduced working memory was persisted into WorkingMemoryStore
        assertTrue(wmStore.exists(question), "WorkingMemoryStore should have persisted cache for query");

        kSession.dispose();
    }

    @Test
    @DisplayName("Should load cached WM with Rule 0 (salience 200), substitute new session ID, and bypass LLM")
    void testCachedWorkingMemoryRuleSalience200() {
        String question = "What is the price of Dell XPS 15 in CHF?";
        String originalSessionId = "old-session-xyz";

        // Seed cache in wmStore beforehand with facts from an old session
        List<BaseReActFact> originalFacts = List.of(
                new Question(originalSessionId, question, 1),
                new StepTracker(originalSessionId, 3, 8),
                new Thought(originalSessionId, 1, "Cached Thought from previous session"),
                new FinalAnswer(originalSessionId, 2, "The price is 1499.00 USD, which is 1319.12 CHF."),
                new Finished(originalSessionId, 2, "The price is 1499.00 USD, which is 1319.12 CHF.")
        );
        wmStore.save(question, originalSessionId, originalFacts);
        assertTrue(wmStore.exists(question), "Cached WM file must exist");

        // Now initiate a brand new session with a new session ID
        String newSessionId = "new-session-789";
        KieSession kSession = kieContainer.newKieSession("ksession-rules");

        AtomicInteger geminiCalls = new AtomicInteger(0);
        GeminiService mockGemini = new GeminiService() {
            @Override
            public String invoke(String prompt) {
                geminiCalls.incrementAndGet();
                return "Should not be called!";
            }
        };

        kSession.setGlobal("toolService", toolService);
        kSession.setGlobal("geminiService", mockGemini);
        kSession.setGlobal("templateBean", templateBean);
        kSession.setGlobal("wmStore", wmStore);

        Question newQuestion = new Question(newSessionId, question, 1);
        Goal newGoal = new Goal(newSessionId, Goal.REASON, 1);
        StepTracker newTracker = new StepTracker(newSessionId, 1, 8);

        kSession.insert(newQuestion);
        kSession.insert(newGoal);
        kSession.insert(newTracker);

        // Fire rules: Rule 0 (salience 200) should fire immediately
        int rulesFired = kSession.fireAllRules(10);
        assertEquals(0, geminiCalls.get(), "LLM must NOT be called when materialized WM cache exists");
        assertEquals(Goal.FINISHED, newGoal.getGoalType(), "Goal should transition to FINISHED immediately");

        // Verify that materialized facts in working memory have the NEW session ID substituted
        Collection<?> facts = kSession.getObjects();
        boolean hasFinishedWithNewSid = facts.stream()
                .filter(f -> f instanceof Finished)
                .map(f -> (Finished) f)
                .anyMatch(f -> newSessionId.equals(f.getSessionId()) && f.getFinalAnswer().contains("1319.12 CHF"));

        assertTrue(hasFinishedWithNewSid, "Finished fact in memory must have substituted new session ID");

        // Verify that StepTracker info persisted
        boolean hasStepTracker = facts.stream()
                .filter(f -> f instanceof StepTracker)
                .map(f -> (StepTracker) f)
                .anyMatch(st -> newSessionId.equals(st.getSessionId()) && st.getCurrentStep() == 3);

        assertTrue(hasStepTracker, "Persisted StepTracker info (step 3) must be present for new session ID");

        kSession.dispose();
    }
}
