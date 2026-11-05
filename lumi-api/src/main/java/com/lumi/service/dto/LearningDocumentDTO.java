package com.lumi.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.LearningDocument} entity.
 */
@Schema(description = "Lưu nguồn tài liệu học tập, metadata sách và trạng thái xử lý lập chỉ mục.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LearningDocumentDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 20)
    private String sourceType;

    @NotNull
    @Size(max = 255)
    private String title;

    @NotNull
    @Size(max = 1000)
    private String fileObjectKey;

    @Size(max = 64)
    private String contentHash;

    @NotNull
    @Min(value = 1)
    private Integer sourceVersion;

    @NotNull
    @Size(max = 20)
    private String processingStatus;

    @NotNull
    @Size(max = 20)
    private String indexStatus;

    @Size(max = 1000)
    private String extractedTextObjectKey;

    @Size(max = 100)
    private String embeddingModel;

    @Lob
    private String errorMessage;

    @NotNull
    private Instant createdAt;

    @NotNull
    private Instant updatedAt;

    @Size(max = 100)
    private String bookSeries;

    @Lob
    private String sourceMetadata;

    private CourseMaterialDTO courseMaterial;

    private ClassMaterialDTO classMaterial;

    @NotNull
    private UserDTO uploadedBy;

    private SubjectDTO subject;

    private GradeDTO grade;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFileObjectKey() {
        return fileObjectKey;
    }

    public void setFileObjectKey(String fileObjectKey) {
        this.fileObjectKey = fileObjectKey;
    }

    public String getContentHash() {
        return contentHash;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
    }

    public Integer getSourceVersion() {
        return sourceVersion;
    }

    public void setSourceVersion(Integer sourceVersion) {
        this.sourceVersion = sourceVersion;
    }

    public String getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(String processingStatus) {
        this.processingStatus = processingStatus;
    }

    public String getIndexStatus() {
        return indexStatus;
    }

    public void setIndexStatus(String indexStatus) {
        this.indexStatus = indexStatus;
    }

    public String getExtractedTextObjectKey() {
        return extractedTextObjectKey;
    }

    public void setExtractedTextObjectKey(String extractedTextObjectKey) {
        this.extractedTextObjectKey = extractedTextObjectKey;
    }

    public String getEmbeddingModel() {
        return embeddingModel;
    }

    public void setEmbeddingModel(String embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
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

    public String getBookSeries() {
        return bookSeries;
    }

    public void setBookSeries(String bookSeries) {
        this.bookSeries = bookSeries;
    }

    public String getSourceMetadata() {
        return sourceMetadata;
    }

    public void setSourceMetadata(String sourceMetadata) {
        this.sourceMetadata = sourceMetadata;
    }

    public CourseMaterialDTO getCourseMaterial() {
        return courseMaterial;
    }

    public void setCourseMaterial(CourseMaterialDTO courseMaterial) {
        this.courseMaterial = courseMaterial;
    }

    public ClassMaterialDTO getClassMaterial() {
        return classMaterial;
    }

    public void setClassMaterial(ClassMaterialDTO classMaterial) {
        this.classMaterial = classMaterial;
    }

    public UserDTO getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(UserDTO uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public SubjectDTO getSubject() {
        return subject;
    }

    public void setSubject(SubjectDTO subject) {
        this.subject = subject;
    }

    public GradeDTO getGrade() {
        return grade;
    }

    public void setGrade(GradeDTO grade) {
        this.grade = grade;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LearningDocumentDTO)) {
            return false;
        }

        LearningDocumentDTO learningDocumentDTO = (LearningDocumentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, learningDocumentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LearningDocumentDTO{" +
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
            ", courseMaterial=" + getCourseMaterial() +
            ", classMaterial=" + getClassMaterial() +
            ", uploadedBy=" + getUploadedBy() +
            ", subject=" + getSubject() +
            ", grade=" + getGrade() +
            "}";
    }
}
