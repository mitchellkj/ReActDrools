package io.freelance.kjm.react.facts;

import java.util.Objects;

/**
 * Fact indicating coordinator execution for a given session has completed.
 */
public class Finished extends BaseReActFact {
    private static final long serialVersionUID = 1L;

    private String product;
    private Double basePriceUsd;
    private String targetCurrency;
    private Double exchangeRate;
    private Double convertedPrice;
    private String finalAnswer;

    public Finished() {
        super();
    }

    public Finished(String sessionId, Integer step) {
        super(sessionId, step);
    }

    public Finished(String sessionId, Integer step, String finalAnswer) {
        super(sessionId, step);
        this.finalAnswer = finalAnswer;
    }

    public Finished(String sessionId, String product, Double basePriceUsd, String targetCurrency,
                    Double exchangeRate, Double convertedPrice, String finalAnswer) {
        super(sessionId, 4);
        this.product = product;
        this.basePriceUsd = basePriceUsd;
        this.targetCurrency = targetCurrency;
        this.exchangeRate = exchangeRate;
        this.convertedPrice = convertedPrice;
        this.finalAnswer = finalAnswer;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Finished finished = (Finished) o;
        return Objects.equals(finalAnswer, finished.finalAnswer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), finalAnswer);
    }

    @Override
    public String toString() {
        return "Finished{" +
                "sessionId='" + getSessionId() + '\'' +
                ", step=" + getStep() +
                ", finalAnswer='" + finalAnswer + '\'' +
                '}';
    }
}
