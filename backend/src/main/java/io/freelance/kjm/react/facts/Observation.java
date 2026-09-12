package io.freelance.kjm.react.facts;

import java.util.Objects;

/**
 * Drools Working Memory Element representing the tool execution result for a session.
 */
public class Observation extends BaseReActFact {
    private static final long serialVersionUID = 1L;

    private String text;
    private String toolName;
    private Double extractedValue;

    public Observation() {
        super();
    }

    public Observation(String sessionId, Integer step, String text) {
        super(sessionId, step);
        this.text = text;
    }

    public Observation(String sessionId, Integer step, String toolName, String text, Double extractedValue) {
        super(sessionId, step);
        this.toolName = toolName;
        this.text = text;
        this.extractedValue = extractedValue;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    // Aliases for compatibility
    public String getResult() {
        return text;
    }

    public void setResult(String result) {
        this.text = result;
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    public Double getExtractedValue() {
        return extractedValue;
    }

    public void setExtractedValue(Double extractedValue) {
        this.extractedValue = extractedValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Observation that = (Observation) o;
        return Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), text);
    }

    @Override
    public String toString() {
        return "Observation{" +
                "sessionId='" + getSessionId() + '\'' +
                ", step=" + getStep() +
                ", text='" + text + '\'' +
                '}';
    }
}
