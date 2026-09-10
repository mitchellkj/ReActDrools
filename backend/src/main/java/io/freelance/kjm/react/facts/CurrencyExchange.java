package io.freelance.kjm.react.facts;

import java.io.Serializable;
import java.util.Objects;

/**
 * Drools Fact representing a supported international currency and baseline exchange rate.
 */
public class CurrencyExchange implements Serializable {
    private static final long serialVersionUID = 1L;

    private String code;
    private String name;
    private String symbol;
    private Double approxRate;

    public CurrencyExchange() {
    }

    public CurrencyExchange(String code, String name, String symbol, Double approxRate) {
        this.code = code;
        this.name = name;
        this.symbol = symbol;
        this.approxRate = approxRate;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Double getApproxRate() {
        return approxRate;
    }

    public void setApproxRate(Double approxRate) {
        this.approxRate = approxRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyExchange that = (CurrencyExchange) o;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return "CurrencyExchange{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", symbol='" + symbol + '\'' +
                ", approxRate=" + approxRate +
                '}';
    }
}
