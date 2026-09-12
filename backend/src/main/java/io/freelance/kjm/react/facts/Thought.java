package io.freelance.kjm.react.facts;

import java.util.Objects;

/**
 * Drools Working Memory Element representing an agent's reasoning step for a session.
 */
public class Thought extends BaseReActFact {
    private static final long serialVersionUID = 1L;

    private String text;

    public Thought() {
        super();
    }

    public Thought(String sessionId, Integer step, String text) {
        super(sessionId, step);
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    // Alias for compatibility
    public String getContent() {
        return text;
    }

    public void setContent(String content) {
        this.text = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Thought thought = (Thought) o;
        return Objects.equals(text, thought.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), text);
    }

    @Override
    public String toString() {
        return "Thought{" +
                "sessionId='" + getSessionId() + '\'' +
                ", step=" + getStep() +
                ", text='" + text + '\'' +
                '}';
    }
}
