package io.freelance.kjm.react.template;

import io.freelance.kjm.react.facts.Action;
import io.freelance.kjm.react.facts.ActionInput;
import io.freelance.kjm.react.facts.Observation;
import io.freelance.kjm.react.facts.Thought;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Java Bean responsible for composing ReAct prompts and decomposing (parsing) LLM responses
 * using regex and string partitioning.
 */
@Component
public class ReActTemplateBean implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String REACT_PROMPT_TEMPLATE =
            "Answer the following questions as best you can. You have access to the following tools:\n\n" +
            "{tools}\n\n" +
            "Use the following format:\n\n" +
            "Question: the input question you must answer\n" +
            "Thought: you should always think about what to do\n" +
            "Action: the action to take, should be one of [{tool_names}]\n" +
            "Action Input: the input to the action\n" +
            "Observation: the result of the action\n" +
            "... (this Thought/Action/Action Input/Observation can repeat N times)\n" +
            "Thought: I now know the final answer\n" +
            "Final Answer: the final answer to the original input question\n\n" +
            "Begin!\n\n" +
            "Question: {question}\n" +
            "Thought:{scratchpad}";

    private static final Pattern ACTION_PATTERN = Pattern.compile("Action:\\s*(.*?)(?:\\n|$)", Pattern.CASE_INSENSITIVE);
    private static final Pattern THOUGHT_PATTERN = Pattern.compile("Thought:\\s*(.*?)(?=\\nAction:|$)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    /**
     * Composes the full ReAct prompt string from tools, question, and current scratchpad history.
     */
    public String composePrompt(String toolsDescription, String toolNames, String question, String scratchpad) {
        return REACT_PROMPT_TEMPLATE
                .replace("{tools}", toolsDescription != null ? toolsDescription : "")
                .replace("{tool_names}", toolNames != null ? toolNames : "")
                .replace("{question}", question != null ? question : "")
                .replace("{scratchpad}", scratchpad != null ? scratchpad : "");
    }

    /**
     * Reconstructs the multi-turn Thought/Action/Action Input/Observation scratchpad
     * for all steps leading up to upToStep.
     */
    public String buildScratchpad(Collection<?> facts, String sessionId, int upToStep) {
        StringBuilder sb = new StringBuilder();

        for (int s = 1; s < upToStep; s++) {
            final int step = s;

            Thought thought = facts.stream()
                    .filter(f -> f instanceof Thought)
                    .map(f -> (Thought) f)
                    .filter(t -> sessionId.equals(t.getSessionId()) && t.getStep() != null && t.getStep() == step)
                    .findFirst().orElse(null);

            Action action = facts.stream()
                    .filter(f -> f instanceof Action)
                    .map(f -> (Action) f)
                    .filter(a -> sessionId.equals(a.getSessionId()) && a.getStep() != null && a.getStep() == step)
                    .findFirst().orElse(null);

            ActionInput input = facts.stream()
                    .filter(f -> f instanceof ActionInput)
                    .map(f -> (ActionInput) f)
                    .filter(i -> sessionId.equals(i.getSessionId()) && i.getStep() != null && i.getStep() == step)
                    .findFirst().orElse(null);

            Observation obs = facts.stream()
                    .filter(f -> f instanceof Observation)
                    .map(f -> (Observation) f)
                    .filter(o -> sessionId.equals(o.getSessionId()) && o.getStep() != null && o.getStep() == step)
                    .findFirst().orElse(null);

            String thText = thought != null && thought.getText() != null ? " " + thought.getText() : "";
            String actText = action != null && action.getName() != null ? action.getName() : "";
            String inpText = input != null && input.getText() != null ? input.getText() : "";
            String obsText = obs != null && obs.getText() != null ? obs.getText() : "";

            sb.append(thText)
              .append("\nAction: ").append(actText)
              .append("\nAction Input: ").append(inpText)
              .append("\nObservation: ").append(obsText)
              .append("\nThought:");
        }

        return sb.toString();
    }

    /**
     * Decomposes and parses raw LLM output into a strongly typed ParsedReActResponse bean.
     */
    public ParsedReActResponse decomposeResponse(String rawText) {
        if (rawText == null || rawText.trim().isEmpty()) {
            return ParsedReActResponse.forAction("", "", "", "");
        }

        String cleaned = rawText.trim();

        // 1. Check for Final Answer
        if (cleaned.contains("Final Answer:")) {
            String[] parts = cleaned.split("Final Answer:", 2);
            String thoughtPart = parts[0].replaceFirst("(?i)^Thought:\\s*", "").trim();
            String finalAnswer = parts[1].trim();
            return ParsedReActResponse.forFinalAnswer(thoughtPart, finalAnswer, rawText);
        }

        // 2. Extract Action
        String action = "";
        Matcher actionMatcher = ACTION_PATTERN.matcher(cleaned);
        if (actionMatcher.find()) {
            action = actionMatcher.group(1).trim();
        }

        // 3. Extract Action Input
        String actionInput = "";
        if (cleaned.contains("Action Input:")) {
            String afterActionInput = cleaned.split("Action Input:", 2)[1];
            if (afterActionInput.contains("\nObservation:")) {
                afterActionInput = afterActionInput.split("\nObservation:", 2)[0];
            } else if (afterActionInput.contains("Observation:")) {
                afterActionInput = afterActionInput.split("Observation:", 2)[0];
            }
            actionInput = afterActionInput.trim();
        }

        // 4. Extract Thought
        String thought = "";
        Matcher thoughtMatcher = THOUGHT_PATTERN.matcher(cleaned);
        if (thoughtMatcher.find()) {
            thought = thoughtMatcher.group(1).trim();
        } else if (!cleaned.toLowerCase().startsWith("action:")) {
            thought = cleaned.split("(?i)Action:")[0].trim();
            thought = thought.replaceFirst("(?i)^Thought:\\s*", "").trim();
        }

        return ParsedReActResponse.forAction(thought, action, actionInput, rawText);
    }
}
