package com.lumi.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.DiscussionReply} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DiscussionReplyDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 5000)
    private String content;

    private Instant createdAt;

    @NotNull
    private UserDTO user;

    @NotNull
    private DiscussionDTO discussion;

    private DiscussionReplyDTO parent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public DiscussionDTO getDiscussion() {
        return discussion;
    }

    public void setDiscussion(DiscussionDTO discussion) {
        this.discussion = discussion;
    }

    public DiscussionReplyDTO getParent() {
        return parent;
    }

    public void setParent(DiscussionReplyDTO parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DiscussionReplyDTO)) {
            return false;
        }

        DiscussionReplyDTO discussionReplyDTO = (DiscussionReplyDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, discussionReplyDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DiscussionReplyDTO{" +
            "id=" + getId() +
            ", content='" + getContent() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", user=" + getUser() +
            ", discussion=" + getDiscussion() +
            ", parent=" + getParent() +
            "}";
    }
}
