package io.freelance.kjm.react.facts;

import java.io.Serializable;
import java.util.Objects;

/**
 * Drools Working Memory Element representing the perceived tool output.
 */
public class Observation implements Serializable {
    private static final long serialVersionUID = 1L;

    private String sessionId;
    private int step;
    private String toolName;
    private String result;
    private Double extractedValue;
    private long timestamp = System.currentTimeMillis();

    public Observation() {
    }

    public Observation(String sessionId, int step, String toolName, String result, Double extractedValue) {
        this.sessionId = sessionId;
        this.step = step;
        this.toolName = toolName;
        this.result = result;
        this.extractedValue = extractedValue;
        this.timestamp = System.currentTimeMillis();
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Double getExtractedValue() {
        return extractedValue;
    }

    public void setExtractedValue(Double extractedValue) {
        this.extractedValue = extractedValue;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Observation that = (Observation) o;
        return step == that.step &&
               Objects.equals(sessionId, that.sessionId) &&
               Objects.equals(toolName, that.toolName) &&
               Objects.equals(result, that.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId, step, toolName, result);
    }

    @Override
    public String toString() {
        return "Observation{" +
                "sessionId='" + sessionId + '\'' +
                ", step=" + step +
                ", toolName='" + toolName + '\'' +
                ", result='" + result + '\'' +
                ", extractedValue=" + extractedValue +
                '}';
    }
}
