package com.lumi.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.AiMessage} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AiMessageDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 20)
    private String senderRole;

    @NotNull
    @Size(max = 5000)
    private String content;

    private Instant createdAt;

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
            ", conversation=" + getConversation() +
            "}";
    }
}
