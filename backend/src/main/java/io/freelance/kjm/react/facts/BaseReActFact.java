package io.freelance.kjm.react.facts;

import java.io.Serializable;
import java.util.Objects;

/**
 * Base fact ensuring class kind, step, and session identifier are preserved
 * in Drools working memory as first-class dimensions.
 */
public abstract class BaseReActFact implements Serializable {
    private static final long serialVersionUID = 1L;

    private String kind;
    private String sessionId;
    private Integer step;
    private long timestamp = System.currentTimeMillis();

    public BaseReActFact() {
        this.kind = this.getClass().getSimpleName();
    }

    public BaseReActFact(String sessionId, Integer step) {
        this.kind = this.getClass().getSimpleName();
        this.sessionId = sessionId;
        this.step = step;
        this.timestamp = System.currentTimeMillis();
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
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
        BaseReActFact that = (BaseReActFact) o;
        return Objects.equals(kind, that.kind) &&
               Objects.equals(sessionId, that.sessionId) &&
               Objects.equals(step, that.step);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kind, sessionId, step);
    }

    @Override
    public String toString() {
        return kind + "{" +
                "sessionId='" + sessionId + '\'' +
                ", step=" + step +
                '}';
    }
}
