package io.freelance.kjm.react.facts;

import java.util.Objects;

/**
 * Fact tracking current loop step and iteration limits for a session.
 */
public class StepTracker extends BaseReActFact {
    private static final long serialVersionUID = 1L;

    private int currentStep = 1;
    private int maxSteps = 8;

    public StepTracker() {
        super();
    }

    public StepTracker(String sessionId, int currentStep, int maxSteps) {
        super(sessionId, currentStep);
        this.currentStep = currentStep;
        this.maxSteps = maxSteps;
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(int currentStep) {
        this.currentStep = currentStep;
        setStep(currentStep);
    }

    public int getMaxSteps() {
        return maxSteps;
    }

    public void setMaxSteps(int maxSteps) {
        this.maxSteps = maxSteps;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        StepTracker that = (StepTracker) o;
        return currentStep == that.currentStep &&
               maxSteps == that.maxSteps;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), currentStep, maxSteps);
    }

    @Override
    public String toString() {
        return "StepTracker{" +
                "sessionId='" + getSessionId() + '\'' +
                ", currentStep=" + currentStep +
                ", maxSteps=" + maxSteps +
                '}';
    }
}
