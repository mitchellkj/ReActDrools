package io.freelance.kjm.react.template;

import java.io.Serializable;
import java.util.Objects;

/**
 * Java Bean representing the decomposed/parsed response from the ReAct LLM.
 */
public class ParsedReActResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Type {
        ACTION,
        FINAL_ANSWER
    }

    private Type type;
    private String thought;
    private String action;
    private String actionInput;
    private String finalAnswer;
    private String rawText;

    public ParsedReActResponse() {
    }

    public static ParsedReActResponse forFinalAnswer(String thought, String finalAnswer, String rawText) {
        ParsedReActResponse res = new ParsedReActResponse();
        res.setType(Type.FINAL_ANSWER);
        res.setThought(thought != null ? thought.trim() : "");
        res.setFinalAnswer(finalAnswer != null ? finalAnswer.trim() : "");
        res.setRawText(rawText);
        return res;
    }

    public static ParsedReActResponse forAction(String thought, String action, String actionInput, String rawText) {
        ParsedReActResponse res = new ParsedReActResponse();
        res.setType(Type.ACTION);
        res.setThought(thought != null ? thought.trim() : "");
        res.setAction(action != null ? action.trim().replaceAll("^[\\[\\]'\"`]+|[\\[\\]'\"`]+$", "").trim() : "");
        res.setActionInput(actionInput != null ? actionInput.trim().replaceAll("^['\"`]+|['\"`]+$", "").trim() : "");
        res.setRawText(rawText);
        return res;
    }

    public boolean isFinalAnswer() {
        return type == Type.FINAL_ANSWER;
    }

    public boolean isAction() {
        return type == Type.ACTION;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getThought() {
        return thought;
    }

    public void setThought(String thought) {
        this.thought = thought;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getActionInput() {
        return actionInput;
    }

    public void setActionInput(String actionInput) {
        this.actionInput = actionInput;
    }

    public String getFinalAnswer() {
        return finalAnswer;
    }

    public void setFinalAnswer(String finalAnswer) {
        this.finalAnswer = finalAnswer;
    }

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParsedReActResponse that = (ParsedReActResponse) o;
        return type == that.type &&
               Objects.equals(thought, that.thought) &&
               Objects.equals(action, that.action) &&
               Objects.equals(actionInput, that.actionInput) &&
               Objects.equals(finalAnswer, that.finalAnswer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, thought, action, actionInput, finalAnswer);
    }

    @Override
    public String toString() {
        return "ParsedReActResponse{" +
                "type=" + type +
                ", thought='" + thought + '\'' +
                ", action='" + action + '\'' +
                ", actionInput='" + actionInput + '\'' +
                ", finalAnswer='" + finalAnswer + '\'' +
                '}';
    }
}
