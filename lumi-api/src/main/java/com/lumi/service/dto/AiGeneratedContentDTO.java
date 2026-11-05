package com.lumi.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.AiGeneratedContent} entity.
 */
@Schema(description = "Lưu nội dung do AI tạo, nguồn sinh, phiên bản và trạng thái kiểm duyệt.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AiGeneratedContentDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 30)
    private String contentType;

    @Size(max = 64)
    private String requestHash;

    @Lob
    private String generationConfig;

    @Size(max = 100)
    private String sourceVersion;

    @Size(max = 100)
    private String modelName;

    @Size(max = 50)
    private String promptVersion;

    @Lob
    private String content;

    @NotNull
    @Size(max = 20)
    private String generationStatus;

    @NotNull
    @Size(max = 20)
    private String visibility;

    @NotNull
    private Long version;

    @NotNull
    @Size(max = 20)
    private String reviewStatus;

    private Instant reviewedAt;

    @Lob
    private String reviewerFeedback;

    @NotNull
    private Instant createdAt;

    @NotNull
    private Instant updatedAt;

    private Long sourceQuestionId;

    @Size(max = 64)
    private String contentHash;

    @NotNull
    private UserDTO createdBy;

    private LearningDocumentDTO document;

    private LessonDTO lesson;

    private UserDTO reviewedBy;

    private CourseDTO course;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getRequestHash() {
        return requestHash;
    }

    public void setRequestHash(String requestHash) {
        this.requestHash = requestHash;
    }

    public String getGenerationConfig() {
        return generationConfig;
    }

    public void setGenerationConfig(String generationConfig) {
        this.generationConfig = generationConfig;
    }

    public String getSourceVersion() {
        return sourceVersion;
    }

    public void setSourceVersion(String sourceVersion) {
        this.sourceVersion = sourceVersion;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getPromptVersion() {
        return promptVersion;
    }

    public void setPromptVersion(String promptVersion) {
        this.promptVersion = promptVersion;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getGenerationStatus() {
        return generationStatus;
    }

    public void setGenerationStatus(String generationStatus) {
        this.generationStatus = generationStatus;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(String reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public Instant getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(Instant reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public String getReviewerFeedback() {
        return reviewerFeedback;
    }

    public void setReviewerFeedback(String reviewerFeedback) {
        this.reviewerFeedback = reviewerFeedback;
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

    public Long getSourceQuestionId() {
        return sourceQuestionId;
    }

    public void setSourceQuestionId(Long sourceQuestionId) {
        this.sourceQuestionId = sourceQuestionId;
    }

    public String getContentHash() {
        return contentHash;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
    }

    public UserDTO getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserDTO createdBy) {
        this.createdBy = createdBy;
    }

    public LearningDocumentDTO getDocument() {
        return document;
    }

    public void setDocument(LearningDocumentDTO document) {
        this.document = document;
    }

    public LessonDTO getLesson() {
        return lesson;
    }

    public void setLesson(LessonDTO lesson) {
        this.lesson = lesson;
    }

    public UserDTO getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(UserDTO reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public CourseDTO getCourse() {
        return course;
    }

    public void setCourse(CourseDTO course) {
        this.course = course;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AiGeneratedContentDTO)) {
            return false;
        }

        AiGeneratedContentDTO aiGeneratedContentDTO = (AiGeneratedContentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, aiGeneratedContentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AiGeneratedContentDTO{" +
            "id=" + getId() +
            ", contentType='" + getContentType() + "'" +
            ", requestHash='" + getRequestHash() + "'" +
            ", generationConfig='" + getGenerationConfig() + "'" +
            ", sourceVersion='" + getSourceVersion() + "'" +
            ", modelName='" + getModelName() + "'" +
            ", promptVersion='" + getPromptVersion() + "'" +
            ", content='" + getContent() + "'" +
            ", generationStatus='" + getGenerationStatus() + "'" +
            ", visibility='" + getVisibility() + "'" +
            ", version=" + getVersion() +
            ", reviewStatus='" + getReviewStatus() + "'" +
            ", reviewedAt='" + getReviewedAt() + "'" +
            ", reviewerFeedback='" + getReviewerFeedback() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", sourceQuestionId=" + getSourceQuestionId() +
            ", contentHash='" + getContentHash() + "'" +
            ", createdBy=" + getCreatedBy() +
            ", document=" + getDocument() +
            ", lesson=" + getLesson() +
            ", reviewedBy=" + getReviewedBy() +
            ", course=" + getCourse() +
            "}";
    }
}
