package com.lumi.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.AiConversation} entity.
 */
@Schema(description = "Lưu phiên hội thoại AI theo người dùng và ngữ cảnh bài hoặc tài liệu.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AiConversationDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 50)
    private String type;

    private Instant createdAt;

    @NotNull
    private Instant updatedAt;

    @NotNull
    private UserDTO user;

    private LessonDTO lesson;

    private LearningDocumentDTO document;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public LessonDTO getLesson() {
        return lesson;
    }

    public void setLesson(LessonDTO lesson) {
        this.lesson = lesson;
    }

    public LearningDocumentDTO getDocument() {
        return document;
    }

    public void setDocument(LearningDocumentDTO document) {
        this.document = document;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AiConversationDTO)) {
            return false;
        }

        AiConversationDTO aiConversationDTO = (AiConversationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, aiConversationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AiConversationDTO{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", user=" + getUser() +
            ", lesson=" + getLesson() +
            ", document=" + getDocument() +
            "}";
    }
}
