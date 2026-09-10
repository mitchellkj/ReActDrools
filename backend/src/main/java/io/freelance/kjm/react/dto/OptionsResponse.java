package io.freelance.kjm.react.dto;

import io.freelance.kjm.react.facts.CurrencyExchange;
import io.freelance.kjm.react.facts.Product;

import java.io.Serializable;
import java.util.List;

/**
 * Payload conveying available hardware products and international currencies for the frontend selector.
 */
public class OptionsResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Product> products;
    private List<CurrencyExchange> currencies;

    public OptionsResponse() {
    }

    public OptionsResponse(List<Product> products, List<CurrencyExchange> currencies) {
        this.products = products;
        this.currencies = currencies;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<CurrencyExchange> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<CurrencyExchange> currencies) {
        this.currencies = currencies;
    }
}
