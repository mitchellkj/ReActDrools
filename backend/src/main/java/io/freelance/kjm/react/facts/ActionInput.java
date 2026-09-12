package io.freelance.kjm.react.facts;

import java.util.Objects;

/**
 * Fact wrapping the tool input parameter for a session.
 */
public class ActionInput extends BaseReActFact {
    private static final long serialVersionUID = 1L;

    private String text;

    public ActionInput() {
        super();
    }

    public ActionInput(String text) {
        super();
        this.text = text;
    }

    public ActionInput(String sessionId, Integer step, String text) {
        super(sessionId, step);
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    // Aliases for compatibility
    public String getRawInput() {
        return text;
    }

    public void setRawInput(String rawInput) {
        this.text = rawInput;
    }

    public String getQuery() {
        return text;
    }

    public void setQuery(String query) {
        this.text = query;
    }

    public String getExpression() {
        return text;
    }

    public void setExpression(String expression) {
        this.text = expression;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ActionInput that = (ActionInput) o;
        return Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), text);
    }

    @Override
    public String toString() {
        return "ActionInput{" +
                "sessionId='" + getSessionId() + '\'' +
                ", step=" + getStep() +
                ", text='" + text + '\'' +
                '}';
    }
}
