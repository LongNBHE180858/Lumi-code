package com.lumi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * A AiMessage.
 */
@Entity
@Table(name = "ai_message")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AiMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 20)
    @Column(name = "sender_role", length = 20, nullable = false)
    private String senderRole;

    @NotNull
    @Size(max = 5000)
    @Column(name = "content", length = 5000, nullable = false)
    private String content;

    @Column(name = "created_at")
    private Instant createdAt;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "user", "lesson" }, allowSetters = true)
    private AiConversation conversation;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AiMessage id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSenderRole() {
        return this.senderRole;
    }

    public AiMessage senderRole(String senderRole) {
        this.setSenderRole(senderRole);
        return this;
    }

    public void setSenderRole(String senderRole) {
        this.senderRole = senderRole;
    }

    public String getContent() {
        return this.content;
    }

    public AiMessage content(String content) {
        this.setContent(content);
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public AiMessage createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public AiConversation getConversation() {
        return this.conversation;
    }

    public void setConversation(AiConversation aiConversation) {
        this.conversation = aiConversation;
    }

    public AiMessage conversation(AiConversation aiConversation) {
        this.setConversation(aiConversation);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AiMessage)) {
            return false;
        }
        return getId() != null && getId().equals(((AiMessage) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AiMessage{" +
            "id=" + getId() +
            ", senderRole='" + getSenderRole() + "'" +
            ", content='" + getContent() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
