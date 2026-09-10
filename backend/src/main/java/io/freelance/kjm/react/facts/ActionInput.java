package io.freelance.kjm.react.facts;

import java.io.Serializable;
import java.util.Objects;

/**
 * Encapsulates the argument or expression payload passed to a ReAct tool.
 */
public class ActionInput implements Serializable {
    private static final long serialVersionUID = 1L;

    private String rawInput;
    private String query;
    private String expression;

    public ActionInput() {
    }

    public ActionInput(String rawInput) {
        this.rawInput = rawInput;
        this.query = rawInput;
        this.expression = rawInput;
    }

    public String getRawInput() {
        return rawInput;
    }

    public void setRawInput(String rawInput) {
        this.rawInput = rawInput;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActionInput that = (ActionInput) o;
        return Objects.equals(rawInput, that.rawInput);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rawInput);
    }

    @Override
    public String toString() {
        return "ActionInput{" +
                "rawInput='" + rawInput + '\'' +
                '}';
    }
}
