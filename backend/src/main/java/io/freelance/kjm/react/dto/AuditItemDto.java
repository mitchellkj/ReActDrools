package io.freelance.kjm.react.dto;

import java.io.Serializable;

/**
 * Individual step item in the ReAct reasoning and rule audit trail.
 */
public class AuditItemDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer step;
    private String kind; // "Thought", "Action", "Observation", "Finished", "Fact"
    private String content;

    public AuditItemDto() {
    }

    public AuditItemDto(Integer step, String kind, String content) {
        this.step = step;
        this.kind = kind;
        this.content = content;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
