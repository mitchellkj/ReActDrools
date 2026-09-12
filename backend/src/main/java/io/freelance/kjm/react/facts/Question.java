package io.freelance.kjm.react.facts;

import java.util.Objects;

/**
 * Fact wrapping the user's input question for a session.
 */
public class Question extends BaseReActFact {
    private static final long serialVersionUID = 1L;

    private String text;

    public Question() {
        super();
    }

    public Question(String sessionId, String text) {
        super(sessionId, 1);
        this.text = text;
    }

    public Question(String sessionId, String text, Integer step) {
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
        Question question = (Question) o;
        return Objects.equals(text, question.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), text);
    }

    @Override
    public String toString() {
        return "Question{" +
                "sessionId='" + getSessionId() + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
