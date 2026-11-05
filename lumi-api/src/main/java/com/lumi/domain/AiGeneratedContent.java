package com.lumi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * Lưu nội dung do AI tạo, nguồn sinh, phiên bản và trạng thái kiểm duyệt.
 */
@Entity
@Table(name = "ai_generated_contents")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AiGeneratedContent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 30)
    @Column(name = "content_type", length = 30, nullable = false)
    private String contentType;

    @Size(max = 64)
    @Column(name = "request_hash", length = 64)
    private String requestHash;

    @Column(name = "generation_config", nullable = false, columnDefinition = "text")
    private String generationConfig;

    @Size(max = 100)
    @Column(name = "source_version", length = 100)
    private String sourceVersion;

    @Size(max = 100)
    @Column(name = "model_name", length = 100)
    private String modelName;

    @Size(max = 50)
    @Column(name = "prompt_version", length = 50)
    private String promptVersion;

    @Column(name = "content", nullable = false, columnDefinition = "text")
    private String content;

    @NotNull
    @Size(max = 20)
    @Column(name = "generation_status", length = 20, nullable = false)
    private String generationStatus;

    @NotNull
    @Size(max = 20)
    @Column(name = "visibility", length = 20, nullable = false)
    private String visibility;

    @NotNull
    @Column(name = "version", nullable = false)
    private Long version;

    @NotNull
    @Size(max = 20)
    @Column(name = "review_status", length = 20, nullable = false)
    private String reviewStatus;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    @Column(name = "reviewer_feedback", columnDefinition = "text")
    private String reviewerFeedback;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "source_question_id")
    private Long sourceQuestionId;

    @Size(max = 64)
    @Column(name = "content_hash", length = 64)
    private String contentHash;

    @ManyToOne(optional = false)
    @NotNull
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "courseMaterial", "classMaterial", "uploadedBy", "subject", "grade" }, allowSetters = true)
    private LearningDocument document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "chapter" }, allowSetters = true)
    private Lesson lesson;

    @ManyToOne(fetch = FetchType.LAZY)
    private User reviewedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "author", "subject", "grade" }, allowSetters = true)
    private Course course;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AiGeneratedContent id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContentType() {
        return this.contentType;
    }

    public AiGeneratedContent contentType(String contentType) {
        this.setContentType(contentType);
        return this;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getRequestHash() {
        return this.requestHash;
    }

    public AiGeneratedContent requestHash(String requestHash) {
        this.setRequestHash(requestHash);
        return this;
    }

    public void setRequestHash(String requestHash) {
        this.requestHash = requestHash;
    }

    public String getGenerationConfig() {
        return this.generationConfig;
    }

    public AiGeneratedContent generationConfig(String generationConfig) {
        this.setGenerationConfig(generationConfig);
        return this;
    }

    public void setGenerationConfig(String generationConfig) {
        this.generationConfig = generationConfig;
    }

    public String getSourceVersion() {
        return this.sourceVersion;
    }

    public AiGeneratedContent sourceVersion(String sourceVersion) {
        this.setSourceVersion(sourceVersion);
        return this;
    }

    public void setSourceVersion(String sourceVersion) {
        this.sourceVersion = sourceVersion;
    }

    public String getModelName() {
        return this.modelName;
    }

    public AiGeneratedContent modelName(String modelName) {
        this.setModelName(modelName);
        return this;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getPromptVersion() {
        return this.promptVersion;
    }

    public AiGeneratedContent promptVersion(String promptVersion) {
        this.setPromptVersion(promptVersion);
        return this;
    }

    public void setPromptVersion(String promptVersion) {
        this.promptVersion = promptVersion;
    }

    public String getContent() {
        return this.content;
    }

    public AiGeneratedContent content(String content) {
        this.setContent(content);
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getGenerationStatus() {
        return this.generationStatus;
    }

    public AiGeneratedContent generationStatus(String generationStatus) {
        this.setGenerationStatus(generationStatus);
        return this;
    }

    public void setGenerationStatus(String generationStatus) {
        this.generationStatus = generationStatus;
    }

    public String getVisibility() {
        return this.visibility;
    }

    public AiGeneratedContent visibility(String visibility) {
        this.setVisibility(visibility);
        return this;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public Long getVersion() {
        return this.version;
    }

    public AiGeneratedContent version(Long version) {
        this.setVersion(version);
        return this;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getReviewStatus() {
        return this.reviewStatus;
    }

    public AiGeneratedContent reviewStatus(String reviewStatus) {
        this.setReviewStatus(reviewStatus);
        return this;
    }

    public void setReviewStatus(String reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public Instant getReviewedAt() {
        return this.reviewedAt;
    }

    public AiGeneratedContent reviewedAt(Instant reviewedAt) {
        this.setReviewedAt(reviewedAt);
        return this;
    }

    public void setReviewedAt(Instant reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public String getReviewerFeedback() {
        return this.reviewerFeedback;
    }

    public AiGeneratedContent reviewerFeedback(String reviewerFeedback) {
        this.setReviewerFeedback(reviewerFeedback);
        return this;
    }

    public void setReviewerFeedback(String reviewerFeedback) {
        this.reviewerFeedback = reviewerFeedback;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public AiGeneratedContent createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public AiGeneratedContent updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getSourceQuestionId() {
        return this.sourceQuestionId;
    }

    public AiGeneratedContent sourceQuestionId(Long sourceQuestionId) {
        this.setSourceQuestionId(sourceQuestionId);
        return this;
    }

    public void setSourceQuestionId(Long sourceQuestionId) {
        this.sourceQuestionId = sourceQuestionId;
    }

    public String getContentHash() {
        return this.contentHash;
    }

    public AiGeneratedContent contentHash(String contentHash) {
        this.setContentHash(contentHash);
        return this;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
    }

    public User getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(User user) {
        this.createdBy = user;
    }

    public AiGeneratedContent createdBy(User user) {
        this.setCreatedBy(user);
        return this;
    }

    public LearningDocument getDocument() {
        return this.document;
    }

    public void setDocument(LearningDocument learningDocument) {
        this.document = learningDocument;
    }

    public AiGeneratedContent document(LearningDocument learningDocument) {
        this.setDocument(learningDocument);
        return this;
    }

    public Lesson getLesson() {
        return this.lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public AiGeneratedContent lesson(Lesson lesson) {
        this.setLesson(lesson);
        return this;
    }

    public User getReviewedBy() {
        return this.reviewedBy;
    }

    public void setReviewedBy(User user) {
        this.reviewedBy = user;
    }

    public AiGeneratedContent reviewedBy(User user) {
        this.setReviewedBy(user);
        return this;
    }

    public Course getCourse() {
        return this.course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public AiGeneratedContent course(Course course) {
        this.setCourse(course);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AiGeneratedContent)) {
            return false;
        }
        return getId() != null && getId().equals(((AiGeneratedContent) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AiGeneratedContent{" +
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
            "}";
    }
}
