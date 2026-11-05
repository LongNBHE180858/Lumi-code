package com.lumi.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.AiMessage} entity.
 */
@Schema(description = "Lưu tin nhắn AI, nguồn trích dẫn và đánh giá phản hồi của chủ hội thoại.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AiMessageDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 20)
    private String senderRole;

    @Lob
    private String content;

    private Instant createdAt;

    @Lob
    private String citations;

    @NotNull
    @Size(max = 20)
    private String generationStatus;

    @Size(max = 100)
    private String modelName;

    private Integer feedbackRating;

    @Lob
    private String feedbackNote;

    private Instant feedbackAt;

    @NotNull
    private AiConversationDTO conversation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSenderRole() {
        return senderRole;
    }

    public void setSenderRole(String senderRole) {
        this.senderRole = senderRole;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getCitations() {
        return citations;
    }

    public void setCitations(String citations) {
        this.citations = citations;
    }

    public String getGenerationStatus() {
        return generationStatus;
    }

    public void setGenerationStatus(String generationStatus) {
        this.generationStatus = generationStatus;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Integer getFeedbackRating() {
        return feedbackRating;
    }

    public void setFeedbackRating(Integer feedbackRating) {
        this.feedbackRating = feedbackRating;
    }

    public String getFeedbackNote() {
        return feedbackNote;
    }

    public void setFeedbackNote(String feedbackNote) {
        this.feedbackNote = feedbackNote;
    }

    public Instant getFeedbackAt() {
        return feedbackAt;
    }

    public void setFeedbackAt(Instant feedbackAt) {
        this.feedbackAt = feedbackAt;
    }

    public AiConversationDTO getConversation() {
        return conversation;
    }

    public void setConversation(AiConversationDTO conversation) {
        this.conversation = conversation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AiMessageDTO)) {
            return false;
        }

        AiMessageDTO aiMessageDTO = (AiMessageDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, aiMessageDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AiMessageDTO{" +
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
            ", conversation=" + getConversation() +
            "}";
    }
}
