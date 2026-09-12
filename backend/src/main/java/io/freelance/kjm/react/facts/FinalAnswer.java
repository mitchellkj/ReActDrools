package io.freelance.kjm.react.facts;

import java.util.Objects;

/**
 * Fact wrapping the agent's final answer for a session.
 */
public class FinalAnswer extends BaseReActFact {
    private static final long serialVersionUID = 1L;

    private String text;

    public FinalAnswer() {
        super();
    }

    public FinalAnswer(String sessionId, Integer step, String text) {
        super(sessionId, step);
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FinalAnswer that = (FinalAnswer) o;
        return Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), text);
    }

    @Override
    public String toString() {
        return "FinalAnswer{" +
                "sessionId='" + getSessionId() + '\'' +
                ", step=" + getStep() +
                ", text='" + text + '\'' +
                '}';
    }
}
