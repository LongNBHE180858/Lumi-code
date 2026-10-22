package com.lumi.service.dto;

import com.lumi.domain.enumeration.FeedbackStatus;
import com.lumi.domain.enumeration.FeedbackType;
import java.io.Serializable;
import java.time.Instant;

/**
 * A DTO for the detailed view of Feedback.
 */
public class FeedbackDetailDTO implements Serializable {

    // Group A: Feedback information
    private Long id;
    private String title;
    private FeedbackType type;
    private String content;
    private CourseDTO course;
    private LessonDTO lesson;

    // Group B: Submitter information
    private String senderIdentity;
    private String contactEmail;
    private Instant createdAt;

    // Group C: Admin processing information
    private FeedbackStatus status;
    private String adminNotes;
    private Instant updatedAt;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public CourseDTO getCourse() {
        return course;
    }

    public void setCourse(CourseDTO course) {
        this.course = course;
    }

    public LessonDTO getLesson() {
        return lesson;
    }

    public void setLesson(LessonDTO lesson) {
        this.lesson = lesson;
    }

    public String getSenderIdentity() {
        return senderIdentity;
    }

    public void setSenderIdentity(String senderIdentity) {
        this.senderIdentity = senderIdentity;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public FeedbackStatus getStatus() {
        return status;
    }

    public void setStatus(FeedbackStatus status) {
        this.status = status;
    }

    public String getAdminNotes() {
        return adminNotes;
    }

    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "FeedbackDetailDTO{" +
            "id=" + id +
            ", title='" + title + '\'' +
            ", type=" + type +
            ", content='" + content + '\'' +
            ", course=" + course +
            ", lesson=" + lesson +
            ", senderIdentity='" + senderIdentity + '\'' +
            ", contactEmail='" + contactEmail + '\'' +
            ", createdAt=" + createdAt +
            ", status=" + status +
            ", adminNotes='" + adminNotes + '\'' +
            ", updatedAt=" + updatedAt +
            '}';
    }
}
