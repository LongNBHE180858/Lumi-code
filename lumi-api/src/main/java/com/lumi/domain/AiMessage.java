package com.lumi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * Lưu tin nhắn AI, nguồn trích dẫn và đánh giá phản hồi của chủ hội thoại.
 */
@Entity
@Table(name = "ai_messages")
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

    @Column(name = "content", nullable = false, columnDefinition = "text")
    private String content;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "citations", nullable = false, columnDefinition = "text")
    private String citations;

    @NotNull
    @Size(max = 20)
    @Column(name = "generation_status", length = 20, nullable = false)
    private String generationStatus;

    @Size(max = 100)
    @Column(name = "model_name", length = 100)
    private String modelName;

    @Column(name = "feedback_rating")
    private Integer feedbackRating;

    @Column(name = "feedback_note", columnDefinition = "text")
    private String feedbackNote;

    @Column(name = "feedback_at")
    private Instant feedbackAt;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "user", "lesson", "document" }, allowSetters = true)
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

    public String getCitations() {
        return this.citations;
    }

    public AiMessage citations(String citations) {
        this.setCitations(citations);
        return this;
    }

    public void setCitations(String citations) {
        this.citations = citations;
    }

    public String getGenerationStatus() {
        return this.generationStatus;
    }

    public AiMessage generationStatus(String generationStatus) {
        this.setGenerationStatus(generationStatus);
        return this;
    }

    public void setGenerationStatus(String generationStatus) {
        this.generationStatus = generationStatus;
    }

    public String getModelName() {
        return this.modelName;
    }

    public AiMessage modelName(String modelName) {
        this.setModelName(modelName);
        return this;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Integer getFeedbackRating() {
        return this.feedbackRating;
    }

    public AiMessage feedbackRating(Integer feedbackRating) {
        this.setFeedbackRating(feedbackRating);
        return this;
    }

    public void setFeedbackRating(Integer feedbackRating) {
        this.feedbackRating = feedbackRating;
    }

    public String getFeedbackNote() {
        return this.feedbackNote;
    }

    public AiMessage feedbackNote(String feedbackNote) {
        this.setFeedbackNote(feedbackNote);
        return this;
    }

    public void setFeedbackNote(String feedbackNote) {
        this.feedbackNote = feedbackNote;
    }

    public Instant getFeedbackAt() {
        return this.feedbackAt;
    }

    public AiMessage feedbackAt(Instant feedbackAt) {
        this.setFeedbackAt(feedbackAt);
        return this;
    }

    public void setFeedbackAt(Instant feedbackAt) {
        this.feedbackAt = feedbackAt;
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
            ", citations='" + getCitations() + "'" +
            ", generationStatus='" + getGenerationStatus() + "'" +
            ", modelName='" + getModelName() + "'" +
            ", feedbackRating=" + getFeedbackRating() +
            ", feedbackNote='" + getFeedbackNote() + "'" +
            ", feedbackAt='" + getFeedbackAt() + "'" +
            "}";
    }
}
