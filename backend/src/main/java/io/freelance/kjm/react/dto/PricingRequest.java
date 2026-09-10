package io.freelance.kjm.react.dto;

import java.io.Serializable;

/**
 * Inbound HTTP request payload for price coordination.
 */
public class PricingRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String product;
    private String currency_code;
    private String currency_name = "Euro";
    private Double exchange_rate = 0.85;
    private String custom_query;
    private String session_id;

    public PricingRequest() {
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getCurrency_code() {
        return currency_code;
    }

    public void setCurrency_code(String currency_code) {
        this.currency_code = currency_code;
    }

    public String getCurrency_name() {
        return currency_name;
    }

    public void setCurrency_name(String currency_name) {
        this.currency_name = currency_name;
    }

    public Double getExchange_rate() {
        return exchange_rate;
    }

    public void setExchange_rate(Double exchange_rate) {
        this.exchange_rate = exchange_rate;
    }

    public String getCustom_query() {
        return custom_query;
    }

    public void setCustom_query(String custom_query) {
        this.custom_query = custom_query;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }
}
