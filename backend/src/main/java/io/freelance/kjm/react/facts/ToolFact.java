package io.freelance.kjm.react.facts;

import java.io.Serializable;
import java.util.Objects;

/**
 * Drools Fact representing an available registered tool in working memory.
 */
public class ToolFact implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String STATE_REGISTERED = "REGISTERED";
    public static final String STATE_ACTIVE = "ACTIVE";

    private String name;
    private String description;
    private String state = STATE_REGISTERED;

    public ToolFact() {
    }

    public ToolFact(String name, String description) {
        this.name = name;
        this.description = description;
        this.state = STATE_ACTIVE;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ToolFact toolFact = (ToolFact) o;
        return Objects.equals(name, toolFact.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "ToolFact{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
