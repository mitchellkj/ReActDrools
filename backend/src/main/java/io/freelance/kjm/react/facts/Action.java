package io.freelance.kjm.react.facts;

import java.io.Serializable;
import java.util.Objects;

/**
 * Drools Working Memory Element representing a dispatched ReAct Action step.
 */
public class Action implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_EXECUTED = "EXECUTED";
    public static final String STATUS_FAILED = "FAILED";

    private String sessionId;
    private int step;
    private String toolName;
    private ActionInput actionInput;
    private String status = STATUS_PENDING;
    private long timestamp = System.currentTimeMillis();

    public Action() {
    }

    public Action(String sessionId, int step, String toolName, ActionInput actionInput, String status) {
        this.sessionId = sessionId;
        this.step = step;
        this.toolName = toolName;
        this.actionInput = actionInput;
        this.status = status;
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
        Action action = (Action) o;
        return step == action.step &&
               Objects.equals(sessionId, action.sessionId) &&
               Objects.equals(toolName, action.toolName) &&
               Objects.equals(status, action.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId, step, toolName, status);
    }

    @Override
    public String toString() {
        return "Action{" +
                "sessionId='" + sessionId + '\'' +
                ", step=" + step +
                ", toolName='" + toolName + '\'' +
                ", actionInput=" + actionInput +
                ", status='" + status + '\'' +
                '}';
    }
}
