package com.lumi.service.dto;

import com.lumi.domain.enumeration.FeedbackStatus;
import com.lumi.domain.enumeration.FeedbackType;
import java.io.Serializable;
import java.time.Instant;

/**
 * A DTO for listing Feedbacks in Admin portal.
 */
public class FeedbackListDTO implements Serializable {

    private Long id;
    private String title;
    private FeedbackType type;
    private FeedbackStatus status;
    private String contactEmail;
    private String senderIdentity;
    private Instant createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public FeedbackType getType() {
        return type;
    }

    public void setType(FeedbackType type) {
        this.type = type;
    }

    public FeedbackStatus getStatus() {
        return status;
    }

    public void setStatus(FeedbackStatus status) {
        this.status = status;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getSenderIdentity() {
        return senderIdentity;
    }

    public void setSenderIdentity(String senderIdentity) {
        this.senderIdentity = senderIdentity;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "FeedbackListDTO{" +
            "id=" + id +
            ", title='" + title + '\'' +
            ", type=" + type +
            ", status=" + status +
            ", contactEmail='" + contactEmail + '\'' +
            ", senderIdentity='" + senderIdentity + '\'' +
            ", createdAt=" + createdAt +
            '}';
    }
}
