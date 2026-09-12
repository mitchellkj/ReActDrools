package io.freelance.kjm.react.facts;

import java.util.Objects;

/**
 * Drools Working Memory Element representing the active ReAct coordinator goal.
 * Driven by goalType: "Reason", "Act", or "Finished".
 */
public class Goal extends BaseReActFact {
    private static final long serialVersionUID = 1L;

    public static final String REASON = "Reason";
    public static final String ACT = "Act";
    public static final String FINISHED = "Finished";

    private String goalType = REASON;
    private String status = "INIT";
    private String product;
    private String targetCurrency;
    private Double exchangeRate;

    public Goal() {
        super();
    }

    public Goal(String sessionId, String goalType) {
        super(sessionId, 1);
        this.goalType = goalType;
    }

    public Goal(String sessionId, String goalType, Integer step) {
        super(sessionId, step);
        this.goalType = goalType;
    }

    public Goal(String sessionId, String product, String targetCurrency, Double exchangeRate) {
        super(sessionId, 1);
        this.goalType = REASON;
        this.status = "INIT";
        this.product = product;
        this.targetCurrency = targetCurrency;
        this.exchangeRate = exchangeRate;
    }

    public String getGoalType() {
        return goalType;
    }

    public void setGoalType(String goalType) {
        this.goalType = goalType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(String targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public Double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(Double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Goal goal = (Goal) o;
        return Objects.equals(goalType, goal.goalType) &&
               Objects.equals(product, goal.product) &&
               Objects.equals(targetCurrency, goal.targetCurrency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), goalType, product, targetCurrency);
    }

    @Override
    public String toString() {
        return "Goal{" +
                "sessionId='" + getSessionId() + '\'' +
                ", goalType='" + goalType + '\'' +
                ", step=" + getStep() +
                ", product='" + product + '\'' +
                ", targetCurrency='" + targetCurrency + '\'' +
                ", exchangeRate=" + exchangeRate +
                '}';
    }
}
