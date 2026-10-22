package com.lumi.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.AiConversation} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AiConversationDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 50)
    private String type;

    private Instant createdAt;

    @NotNull
    private UserDTO user;

    private LessonDTO lesson;

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
            ", user=" + getUser() +
            ", lesson=" + getLesson() +
            "}";
    }
}
