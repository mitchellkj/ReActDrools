package io.freelance.kjm.react.facts;

import java.io.Serializable;
import java.util.Objects;

/**
 * Drools Working Memory Element representing the successful terminal resolution of the ReAct session.
 */
public class Finished implements Serializable {
    private static final long serialVersionUID = 1L;

    private String sessionId;
    private String product;
    private Double basePriceUsd;
    private String targetCurrency;
    private Double exchangeRate;
    private Double convertedPrice;
    private String finalAnswer;
    private long timestamp = System.currentTimeMillis();

    public Finished() {
    }

    public Finished(String sessionId, String product, Double basePriceUsd, String targetCurrency,
                    Double exchangeRate, Double convertedPrice, String finalAnswer) {
        this.sessionId = sessionId;
        this.product = product;
        this.basePriceUsd = basePriceUsd;
        this.targetCurrency = targetCurrency;
        this.exchangeRate = exchangeRate;
        this.convertedPrice = convertedPrice;
        this.finalAnswer = finalAnswer;
        this.timestamp = System.currentTimeMillis();
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Double getBasePriceUsd() {
        return basePriceUsd;
    }

    public void setBasePriceUsd(Double basePriceUsd) {
        this.basePriceUsd = basePriceUsd;
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

    public Double getConvertedPrice() {
        return convertedPrice;
    }

    public void setConvertedPrice(Double convertedPrice) {
        this.convertedPrice = convertedPrice;
    }

    public String getFinalAnswer() {
        return finalAnswer;
    }

    public void setFinalAnswer(String finalAnswer) {
        this.finalAnswer = finalAnswer;
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
        Finished finished = (Finished) o;
        return Objects.equals(sessionId, finished.sessionId) &&
               Objects.equals(product, finished.product) &&
               Objects.equals(targetCurrency, finished.targetCurrency) &&
               Objects.equals(convertedPrice, finished.convertedPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId, product, targetCurrency, convertedPrice);
    }

    @Override
    public String toString() {
        return "Finished{" +
                "sessionId='" + sessionId + '\'' +
                ", product='" + product + '\'' +
                ", basePriceUsd=" + basePriceUsd +
                ", targetCurrency='" + targetCurrency + '\'' +
                ", exchangeRate=" + exchangeRate +
                ", convertedPrice=" + convertedPrice +
                ", finalAnswer='" + finalAnswer + '\'' +
                '}';
    }
}
