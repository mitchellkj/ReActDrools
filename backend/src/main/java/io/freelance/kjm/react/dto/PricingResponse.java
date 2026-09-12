package io.freelance.kjm.react.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Outbound HTTP response payload conveying the finalized price, audit trail, and execution status.
 */
public class PricingResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean success;
    private String session_id;
    private String product;
    private String currency;
    private Double exchange_rate;
    private String query;
    private String final_answer;
    private String status;
    private boolean cached;
    private String knowledge_source;
    private String query_hash;
    private int rules_fired;
    private List<AuditItemDto> audit_facts = new ArrayList<>();

    public PricingResponse() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getExchange_rate() {
        return exchange_rate;
    }

    public void setExchange_rate(Double exchange_rate) {
        this.exchange_rate = exchange_rate;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getFinal_answer() {
        return final_answer;
    }

    public void setFinal_answer(String final_answer) {
        this.final_answer = final_answer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isCached() {
        return cached;
    }

    public void setCached(boolean cached) {
        this.cached = cached;
    }

    public String getKnowledge_source() {
        return knowledge_source;
    }

    public void setKnowledge_source(String knowledge_source) {
        this.knowledge_source = knowledge_source;
    }

    public String getQuery_hash() {
        return query_hash;
    }

    public void setQuery_hash(String query_hash) {
        this.query_hash = query_hash;
    }

    public int getRules_fired() {
        return rules_fired;
    }

    public void setRules_fired(int rules_fired) {
        this.rules_fired = rules_fired;
    }

    public List<AuditItemDto> getAudit_facts() {
        return audit_facts;
    }

    public void setAudit_facts(List<AuditItemDto> audit_facts) {
        this.audit_facts = audit_facts;
    }
}
