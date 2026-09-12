package io.freelance.kjm.react.facts;

import java.util.Objects;

/**
 * Fact holding raw LLM text response during Reason phase for a session.
 */
public class LLMPrediction extends BaseReActFact {
    private static final long serialVersionUID = 1L;

    private String text;

    public LLMPrediction() {
        super();
    }

    public LLMPrediction(String sessionId, Integer step, String text) {
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
        LLMPrediction that = (LLMPrediction) o;
        return Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), text);
    }

    @Override
    public String toString() {
        return "LLMPrediction{" +
                "sessionId='" + getSessionId() + '\'' +
                ", step=" + getStep() +
                ", text='" + (text != null && text.length() > 50 ? text.substring(0, 50) + "..." : text) + '\'' +
                '}';
    }
}
