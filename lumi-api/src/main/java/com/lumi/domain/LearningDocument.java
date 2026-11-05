package com.lumi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * Lưu nguồn tài liệu học tập, metadata sách và trạng thái xử lý lập chỉ mục.
 */
@Entity
@Table(name = "learning_documents")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LearningDocument implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 20)
    @Column(name = "source_type", length = 20, nullable = false)
    private String sourceType;

    @NotNull
    @Size(max = 255)
    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @NotNull
    @Size(max = 1000)
    @Column(name = "file_object_key", length = 1000, nullable = false)
    private String fileObjectKey;

    @Size(max = 64)
    @Column(name = "content_hash", length = 64)
    private String contentHash;

    @NotNull
    @Min(value = 1)
    @Column(name = "source_version", nullable = false)
    private Integer sourceVersion;

    @NotNull
    @Size(max = 20)
    @Column(name = "processing_status", length = 20, nullable = false)
    private String processingStatus;

    @NotNull
    @Size(max = 20)
    @Column(name = "index_status", length = 20, nullable = false)
    private String indexStatus;

    @Size(max = 1000)
    @Column(name = "extracted_text_object_key", length = 1000)
    private String extractedTextObjectKey;

    @Size(max = 100)
    @Column(name = "embedding_model", length = 100)
    private String embeddingModel;

    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Size(max = 100)
    @Column(name = "book_series", length = 100)
    private String bookSeries;

    @Column(name = "source_metadata", nullable = false, columnDefinition = "text")
    private String sourceMetadata;

    @JsonIgnoreProperties(value = { "lesson", "learningDocument" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private CourseMaterial courseMaterial;

    @JsonIgnoreProperties(value = { "studyClass", "learningDocument" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private ClassMaterial classMaterial;

    @ManyToOne(optional = false)
    @NotNull
    private User uploadedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    private Grade grade;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public LearningDocument id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSourceType() {
        return this.sourceType;
    }

    public LearningDocument sourceType(String sourceType) {
        this.setSourceType(sourceType);
        return this;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getTitle() {
        return this.title;
    }

    public LearningDocument title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFileObjectKey() {
        return this.fileObjectKey;
    }

    public LearningDocument fileObjectKey(String fileObjectKey) {
        this.setFileObjectKey(fileObjectKey);
        return this;
    }

    public void setFileObjectKey(String fileObjectKey) {
        this.fileObjectKey = fileObjectKey;
    }

    public String getContentHash() {
        return this.contentHash;
    }

    public LearningDocument contentHash(String contentHash) {
        this.setContentHash(contentHash);
        return this;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
    }

    public Integer getSourceVersion() {
        return this.sourceVersion;
    }

    public LearningDocument sourceVersion(Integer sourceVersion) {
        this.setSourceVersion(sourceVersion);
        return this;
    }

    public void setSourceVersion(Integer sourceVersion) {
        this.sourceVersion = sourceVersion;
    }

    public String getProcessingStatus() {
        return this.processingStatus;
    }

    public LearningDocument processingStatus(String processingStatus) {
        this.setProcessingStatus(processingStatus);
        return this;
    }

    public void setProcessingStatus(String processingStatus) {
        this.processingStatus = processingStatus;
    }

    public String getIndexStatus() {
        return this.indexStatus;
    }

    public LearningDocument indexStatus(String indexStatus) {
        this.setIndexStatus(indexStatus);
        return this;
    }

    public void setIndexStatus(String indexStatus) {
        this.indexStatus = indexStatus;
    }

    public String getExtractedTextObjectKey() {
        return this.extractedTextObjectKey;
    }

    public LearningDocument extractedTextObjectKey(String extractedTextObjectKey) {
        this.setExtractedTextObjectKey(extractedTextObjectKey);
        return this;
    }

    public void setExtractedTextObjectKey(String extractedTextObjectKey) {
        this.extractedTextObjectKey = extractedTextObjectKey;
    }

    public String getEmbeddingModel() {
        return this.embeddingModel;
    }

    public LearningDocument embeddingModel(String embeddingModel) {
        this.setEmbeddingModel(embeddingModel);
        return this;
    }

    public void setEmbeddingModel(String embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public LearningDocument errorMessage(String errorMessage) {
        this.setErrorMessage(errorMessage);
        return this;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public LearningDocument createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public LearningDocument updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getBookSeries() {
        return this.bookSeries;
    }

    public LearningDocument bookSeries(String bookSeries) {
        this.setBookSeries(bookSeries);
        return this;
    }

    public void setBookSeries(String bookSeries) {
        this.bookSeries = bookSeries;
    }

    public String getSourceMetadata() {
        return this.sourceMetadata;
    }

    public LearningDocument sourceMetadata(String sourceMetadata) {
        this.setSourceMetadata(sourceMetadata);
        return this;
    }

    public void setSourceMetadata(String sourceMetadata) {
        this.sourceMetadata = sourceMetadata;
    }

    public CourseMaterial getCourseMaterial() {
        return this.courseMaterial;
    }

    public void setCourseMaterial(CourseMaterial courseMaterial) {
        this.courseMaterial = courseMaterial;
    }

    public LearningDocument courseMaterial(CourseMaterial courseMaterial) {
        this.setCourseMaterial(courseMaterial);
        return this;
    }

    public ClassMaterial getClassMaterial() {
        return this.classMaterial;
    }

    public void setClassMaterial(ClassMaterial classMaterial) {
        this.classMaterial = classMaterial;
    }

    public LearningDocument classMaterial(ClassMaterial classMaterial) {
        this.setClassMaterial(classMaterial);
        return this;
    }

    public User getUploadedBy() {
        return this.uploadedBy;
    }

    public void setUploadedBy(User user) {
        this.uploadedBy = user;
    }

    public LearningDocument uploadedBy(User user) {
        this.setUploadedBy(user);
        return this;
    }

    public Subject getSubject() {
        return this.subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public LearningDocument subject(Subject subject) {
        this.setSubject(subject);
        return this;
    }

    public Grade getGrade() {
        return this.grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    public LearningDocument grade(Grade grade) {
        this.setGrade(grade);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LearningDocument)) {
            return false;
        }
        return getId() != null && getId().equals(((LearningDocument) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LearningDocument{" +
            "id=" + getId() +
            ", sourceType='" + getSourceType() + "'" +
            ", title='" + getTitle() + "'" +
            ", fileObjectKey='" + getFileObjectKey() + "'" +
            ", contentHash='" + getContentHash() + "'" +
            ", sourceVersion=" + getSourceVersion() +
            ", processingStatus='" + getProcessingStatus() + "'" +
            ", indexStatus='" + getIndexStatus() + "'" +
            ", extractedTextObjectKey='" + getExtractedTextObjectKey() + "'" +
            ", embeddingModel='" + getEmbeddingModel() + "'" +
            ", errorMessage='" + getErrorMessage() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", bookSeries='" + getBookSeries() + "'" +
            ", sourceMetadata='" + getSourceMetadata() + "'" +
            "}";
    }
}
