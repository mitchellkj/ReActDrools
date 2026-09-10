package io.freelance.kjm.react.facts;

import java.io.Serializable;
import java.util.Objects;

/**
 * Drools Working Memory Element representing internal reasoning or plan formation.
 */
public class Thought implements Serializable {
    private static final long serialVersionUID = 1L;

    private String sessionId;
    private int step;
    private String content;
    private long timestamp = System.currentTimeMillis();

    public Thought() {
    }

    public Thought(String sessionId, int step, String content) {
        this.sessionId = sessionId;
        this.step = step;
        this.content = content;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
        Thought thought = (Thought) o;
        return step == thought.step &&
               Objects.equals(sessionId, thought.sessionId) &&
               Objects.equals(content, thought.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId, step, content);
    }

    @Override
    public String toString() {
        return "Thought{" +
                "sessionId='" + sessionId + '\'' +
                ", step=" + step +
                ", content='" + content + '\'' +
                '}';
    }
}
