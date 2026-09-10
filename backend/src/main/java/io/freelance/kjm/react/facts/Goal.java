package io.freelance.kjm.react.facts;

import java.io.Serializable;
import java.util.Objects;

/**
 * Drools Working Memory Element representing the active ReAct coordinator goal.
 */
public class Goal implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Type {
        PRICE_COORDINATION,
        FIND_PRICE,
        CONVERT_CURRENCY
    }

    public static final String STATUS_INIT = "INIT";
    public static final String STATUS_NEED_BASE_PRICE = "NEED_BASE_PRICE";
    public static final String STATUS_AWAITING_SEARCH = "AWAITING_SEARCH";
    public static final String STATUS_AWAITING_CONVERSION = "AWAITING_CONVERSION";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_FAILED = "FAILED";

    private String sessionId;
    private Type type = Type.PRICE_COORDINATION;
    private String status = STATUS_INIT;
    private String product;
    private String targetCurrency;
    private Double exchangeRate;

    public Goal() {
    }

    public Goal(String sessionId, String product, String targetCurrency, Double exchangeRate) {
        this.sessionId = sessionId;
        this.type = Type.PRICE_COORDINATION;
        this.status = STATUS_INIT;
        this.product = product;
        this.targetCurrency = targetCurrency;
        this.exchangeRate = exchangeRate;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
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
        Goal goal = (Goal) o;
        return Objects.equals(sessionId, goal.sessionId) &&
               type == goal.type &&
               Objects.equals(status, goal.status) &&
               Objects.equals(product, goal.product) &&
               Objects.equals(targetCurrency, goal.targetCurrency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId, type, status, product, targetCurrency);
    }

    @Override
    public String toString() {
        return "Goal{" +
                "sessionId='" + sessionId + '\'' +
                ", type=" + type +
                ", status='" + status + '\'' +
                ", product='" + product + '\'' +
                ", targetCurrency='" + targetCurrency + '\'' +
                ", exchangeRate=" + exchangeRate +
                '}';
    }
}
