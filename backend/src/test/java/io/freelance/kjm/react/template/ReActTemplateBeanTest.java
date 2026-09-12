package io.freelance.kjm.react.template;

import io.freelance.kjm.react.facts.Action;
import io.freelance.kjm.react.facts.ActionInput;
import io.freelance.kjm.react.facts.Observation;
import io.freelance.kjm.react.facts.Thought;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 test suite verifying prompt template composition and response decomposition.
 */
class ReActTemplateBeanTest {

    private ReActTemplateBean templateBean;

    @BeforeEach
    void setUp() {
        templateBean = new ReActTemplateBean();
    }

    @Test
    @DisplayName("Should compose full ReAct prompt with tools, question, and scratchpad")
    void testComposePrompt() {
        String toolsDesc = "duckduck: Web search\nCalculator: Math calculations";
        String toolNames = "duckduck, Calculator";
        String question = "What is the price of MacBook Pro in EUR?";
        String scratchpad = "\nAction: duckduck\nAction Input: price\nObservation: 1599";

        String prompt = templateBean.composePrompt(toolsDesc, toolNames, question, scratchpad);

        assertNotNull(prompt);
        assertTrue(prompt.contains(toolsDesc));
        assertTrue(prompt.contains("one of [duckduck, Calculator]"));
        assertTrue(prompt.contains("Question: What is the price of MacBook Pro in EUR?"));
        assertTrue(prompt.contains("Thought:\nAction: duckduck"));
    }

    @Test
    @DisplayName("Should decompose standard Action response into ParsedReActResponse bean")
    void testDecomposeActionResponse() {
        String rawLlm = "Thought: I need to check retail search indices for starting MSRP of Apple MacBook Pro 14\" in USD.\n" +
                        "Action: duckduck\n" +
                        "Action Input: current price of Apple MacBook Pro 14\" in USD";

        ParsedReActResponse parsed = templateBean.decomposeResponse(rawLlm);

        assertNotNull(parsed);
        assertTrue(parsed.isAction());
        assertFalse(parsed.isFinalAnswer());
        assertEquals("duckduck", parsed.getAction());
        assertEquals("current price of Apple MacBook Pro 14\" in USD", parsed.getActionInput());
        assertTrue(parsed.getThought().contains("I need to check retail search indices"));
    }

    @Test
    @DisplayName("Should decompose Final Answer response into ParsedReActResponse bean")
    void testDecomposeFinalAnswerResponse() {
        String rawLlm = "Thought: I now know the final answer.\n" +
                        "Final Answer: The current retail price for Apple MacBook Pro 14\" is $1599.00 USD, which converts to approximately 1359.15 EUR.";

        ParsedReActResponse parsed = templateBean.decomposeResponse(rawLlm);

        assertNotNull(parsed);
        assertFalse(parsed.isAction());
        assertTrue(parsed.isFinalAnswer());
        assertEquals("I now know the final answer.", parsed.getThought());
        assertTrue(parsed.getFinalAnswer().contains("1359.15 EUR"));
    }

    @Test
    @DisplayName("Should handle bracketed action names and quotes gracefully")
    void testDecomposeSanitization() {
        String rawLlm = "Action: ['Calculator']\n" +
                        "Action Input: \"1599 * 0.85\"";

        ParsedReActResponse parsed = templateBean.decomposeResponse(rawLlm);

        assertEquals("Calculator", parsed.getAction());
        assertEquals("1599 * 0.85", parsed.getActionInput());
    }

    @Test
    @DisplayName("Should reconstruct scratchpad from history facts")
    void testBuildScratchpad() {
        String sessionId = "test-session";
        List<Object> facts = List.of(
                new Thought(sessionId, 1, "First thought"),
                new Action(sessionId, 1, "duckduck"),
                new ActionInput(sessionId, 1, "query term"),
                new Observation(sessionId, 1, "snippet: found price $1599 USD")
        );

        String scratchpad = templateBean.buildScratchpad(facts, sessionId, 2);

        assertNotNull(scratchpad);
        assertTrue(scratchpad.contains("First thought"));
        assertTrue(scratchpad.contains("Action: duckduck"));
        assertTrue(scratchpad.contains("Action Input: query term"));
        assertTrue(scratchpad.contains("Observation: snippet: found price $1599 USD"));
    }
}
