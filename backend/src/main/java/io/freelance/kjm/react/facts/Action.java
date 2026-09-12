package io.freelance.kjm.react.facts;

import java.util.Objects;

/**
 * Drools Working Memory Element representing the selected tool name for a session.
 */
public class Action extends BaseReActFact {
    private static final long serialVersionUID = 1L;

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_EXECUTED = "EXECUTED";
    public static final String STATUS_FAILED = "FAILED";

    private String name;
    private ActionInput actionInput;
    private String status = STATUS_PENDING;

    public Action() {
        super();
    }

    public Action(String sessionId, Integer step, String name) {
        super(sessionId, step);
        this.name = name;
        this.status = STATUS_PENDING;
    }

    public Action(String sessionId, Integer step, String name, ActionInput actionInput, String status) {
        super(sessionId, step);
        this.name = name;
        this.actionInput = actionInput;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Alias for toolName compatibility
    public String getToolName() {
        return name;
    }

    public void setToolName(String toolName) {
        this.name = toolName;
    }

    public ActionInput getActionInput() {
        return actionInput;
    }

    public void setActionInput(ActionInput actionInput) {
        this.actionInput = actionInput;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Action action = (Action) o;
        return Objects.equals(name, action.name) &&
               Objects.equals(status, action.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, status);
    }

    @Override
    public String toString() {
        return "Action{" +
                "sessionId='" + getSessionId() + '\'' +
                ", step=" + getStep() +
                ", name='" + name + '\'' +
                ", actionInput=" + actionInput +
                ", status='" + status + '\'' +
                '}';
    }
}
