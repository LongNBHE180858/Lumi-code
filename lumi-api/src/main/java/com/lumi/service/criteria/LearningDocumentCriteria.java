package com.lumi.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.domain.LearningDocument} entity. This class is used
 * in {@link com.lumi.web.rest.LearningDocumentResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /learning-documents?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LearningDocumentCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter sourceType;

    private StringFilter title;

    private StringFilter fileObjectKey;

    private StringFilter contentHash;

    private IntegerFilter sourceVersion;

    private StringFilter processingStatus;

    private StringFilter indexStatus;

    private StringFilter extractedTextObjectKey;

    private StringFilter embeddingModel;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    private StringFilter bookSeries;

    private LongFilter courseMaterialId;

    private LongFilter classMaterialId;

    private LongFilter uploadedById;

    private LongFilter subjectId;

    private LongFilter gradeId;

    private Boolean distinct;

    public LearningDocumentCriteria() {}

    public LearningDocumentCriteria(LearningDocumentCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.sourceType = other.optionalSourceType().map(StringFilter::copy).orElse(null);
        this.title = other.optionalTitle().map(StringFilter::copy).orElse(null);
        this.fileObjectKey = other.optionalFileObjectKey().map(StringFilter::copy).orElse(null);
        this.contentHash = other.optionalContentHash().map(StringFilter::copy).orElse(null);
        this.sourceVersion = other.optionalSourceVersion().map(IntegerFilter::copy).orElse(null);
        this.processingStatus = other.optionalProcessingStatus().map(StringFilter::copy).orElse(null);
        this.indexStatus = other.optionalIndexStatus().map(StringFilter::copy).orElse(null);
        this.extractedTextObjectKey = other.optionalExtractedTextObjectKey().map(StringFilter::copy).orElse(null);
        this.embeddingModel = other.optionalEmbeddingModel().map(StringFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.bookSeries = other.optionalBookSeries().map(StringFilter::copy).orElse(null);
        this.courseMaterialId = other.optionalCourseMaterialId().map(LongFilter::copy).orElse(null);
        this.classMaterialId = other.optionalClassMaterialId().map(LongFilter::copy).orElse(null);
        this.uploadedById = other.optionalUploadedById().map(LongFilter::copy).orElse(null);
        this.subjectId = other.optionalSubjectId().map(LongFilter::copy).orElse(null);
        this.gradeId = other.optionalGradeId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public LearningDocumentCriteria copy() {
        return new LearningDocumentCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getSourceType() {
        return sourceType;
    }

    public Optional<StringFilter> optionalSourceType() {
        return Optional.ofNullable(sourceType);
    }

    public StringFilter sourceType() {
        if (sourceType == null) {
            setSourceType(new StringFilter());
        }
        return sourceType;
    }

    public void setSourceType(StringFilter sourceType) {
        this.sourceType = sourceType;
    }

    public StringFilter getTitle() {
        return title;
    }

    public Optional<StringFilter> optionalTitle() {
        return Optional.ofNullable(title);
    }

    public StringFilter title() {
        if (title == null) {
            setTitle(new StringFilter());
        }
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
    }

    public StringFilter getFileObjectKey() {
        return fileObjectKey;
    }

    public Optional<StringFilter> optionalFileObjectKey() {
        return Optional.ofNullable(fileObjectKey);
    }

    public StringFilter fileObjectKey() {
        if (fileObjectKey == null) {
            setFileObjectKey(new StringFilter());
        }
        return fileObjectKey;
    }

    public void setFileObjectKey(StringFilter fileObjectKey) {
        this.fileObjectKey = fileObjectKey;
    }

    public StringFilter getContentHash() {
        return contentHash;
    }

    public Optional<StringFilter> optionalContentHash() {
        return Optional.ofNullable(contentHash);
    }

    public StringFilter contentHash() {
        if (contentHash == null) {
            setContentHash(new StringFilter());
        }
        return contentHash;
    }

    public void setContentHash(StringFilter contentHash) {
        this.contentHash = contentHash;
    }

    public IntegerFilter getSourceVersion() {
        return sourceVersion;
    }

    public Optional<IntegerFilter> optionalSourceVersion() {
        return Optional.ofNullable(sourceVersion);
    }

    public IntegerFilter sourceVersion() {
        if (sourceVersion == null) {
            setSourceVersion(new IntegerFilter());
        }
        return sourceVersion;
    }

    public void setSourceVersion(IntegerFilter sourceVersion) {
        this.sourceVersion = sourceVersion;
    }

    public StringFilter getProcessingStatus() {
        return processingStatus;
    }

    public Optional<StringFilter> optionalProcessingStatus() {
        return Optional.ofNullable(processingStatus);
    }

    public StringFilter processingStatus() {
        if (processingStatus == null) {
            setProcessingStatus(new StringFilter());
        }
        return processingStatus;
    }

    public void setProcessingStatus(StringFilter processingStatus) {
        this.processingStatus = processingStatus;
    }

    public StringFilter getIndexStatus() {
        return indexStatus;
    }

    public Optional<StringFilter> optionalIndexStatus() {
        return Optional.ofNullable(indexStatus);
    }

    public StringFilter indexStatus() {
        if (indexStatus == null) {
            setIndexStatus(new StringFilter());
        }
        return indexStatus;
    }

    public void setIndexStatus(StringFilter indexStatus) {
        this.indexStatus = indexStatus;
    }

    public StringFilter getExtractedTextObjectKey() {
        return extractedTextObjectKey;
    }

    public Optional<StringFilter> optionalExtractedTextObjectKey() {
        return Optional.ofNullable(extractedTextObjectKey);
    }

    public StringFilter extractedTextObjectKey() {
        if (extractedTextObjectKey == null) {
            setExtractedTextObjectKey(new StringFilter());
        }
        return extractedTextObjectKey;
    }

    public void setExtractedTextObjectKey(StringFilter extractedTextObjectKey) {
        this.extractedTextObjectKey = extractedTextObjectKey;
    }

    public StringFilter getEmbeddingModel() {
        return embeddingModel;
    }

    public Optional<StringFilter> optionalEmbeddingModel() {
        return Optional.ofNullable(embeddingModel);
    }

    public StringFilter embeddingModel() {
        if (embeddingModel == null) {
            setEmbeddingModel(new StringFilter());
        }
        return embeddingModel;
    }

    public void setEmbeddingModel(StringFilter embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public InstantFilter getCreatedAt() {
        return createdAt;
    }

    public Optional<InstantFilter> optionalCreatedAt() {
        return Optional.ofNullable(createdAt);
    }

    public InstantFilter createdAt() {
        if (createdAt == null) {
            setCreatedAt(new InstantFilter());
        }
        return createdAt;
    }

    public void setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
    }

    public InstantFilter getUpdatedAt() {
        return updatedAt;
    }

    public Optional<InstantFilter> optionalUpdatedAt() {
        return Optional.ofNullable(updatedAt);
    }

    public InstantFilter updatedAt() {
        if (updatedAt == null) {
            setUpdatedAt(new InstantFilter());
        }
        return updatedAt;
    }

    public void setUpdatedAt(InstantFilter updatedAt) {
        this.updatedAt = updatedAt;
    }

    public StringFilter getBookSeries() {
        return bookSeries;
    }

    public Optional<StringFilter> optionalBookSeries() {
        return Optional.ofNullable(bookSeries);
    }

    public StringFilter bookSeries() {
        if (bookSeries == null) {
            setBookSeries(new StringFilter());
        }
        return bookSeries;
    }

    public void setBookSeries(StringFilter bookSeries) {
        this.bookSeries = bookSeries;
    }

    public LongFilter getCourseMaterialId() {
        return courseMaterialId;
    }

    public Optional<LongFilter> optionalCourseMaterialId() {
        return Optional.ofNullable(courseMaterialId);
    }

    public LongFilter courseMaterialId() {
        if (courseMaterialId == null) {
            setCourseMaterialId(new LongFilter());
        }
        return courseMaterialId;
    }

    public void setCourseMaterialId(LongFilter courseMaterialId) {
        this.courseMaterialId = courseMaterialId;
    }

    public LongFilter getClassMaterialId() {
        return classMaterialId;
    }

    public Optional<LongFilter> optionalClassMaterialId() {
        return Optional.ofNullable(classMaterialId);
    }

    public LongFilter classMaterialId() {
        if (classMaterialId == null) {
            setClassMaterialId(new LongFilter());
        }
        return classMaterialId;
    }

    public void setClassMaterialId(LongFilter classMaterialId) {
        this.classMaterialId = classMaterialId;
    }

    public LongFilter getUploadedById() {
        return uploadedById;
    }

    public Optional<LongFilter> optionalUploadedById() {
        return Optional.ofNullable(uploadedById);
    }

    public LongFilter uploadedById() {
        if (uploadedById == null) {
            setUploadedById(new LongFilter());
        }
        return uploadedById;
    }

    public void setUploadedById(LongFilter uploadedById) {
        this.uploadedById = uploadedById;
    }

    public LongFilter getSubjectId() {
        return subjectId;
    }

    public Optional<LongFilter> optionalSubjectId() {
        return Optional.ofNullable(subjectId);
    }

    public LongFilter subjectId() {
        if (subjectId == null) {
            setSubjectId(new LongFilter());
        }
        return subjectId;
    }

    public void setSubjectId(LongFilter subjectId) {
        this.subjectId = subjectId;
    }

    public LongFilter getGradeId() {
        return gradeId;
    }

    public Optional<LongFilter> optionalGradeId() {
        return Optional.ofNullable(gradeId);
    }

    public LongFilter gradeId() {
        if (gradeId == null) {
            setGradeId(new LongFilter());
        }
        return gradeId;
    }

    public void setGradeId(LongFilter gradeId) {
        this.gradeId = gradeId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final LearningDocumentCriteria that = (LearningDocumentCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(sourceType, that.sourceType) &&
            Objects.equals(title, that.title) &&
            Objects.equals(fileObjectKey, that.fileObjectKey) &&
            Objects.equals(contentHash, that.contentHash) &&
            Objects.equals(sourceVersion, that.sourceVersion) &&
            Objects.equals(processingStatus, that.processingStatus) &&
            Objects.equals(indexStatus, that.indexStatus) &&
            Objects.equals(extractedTextObjectKey, that.extractedTextObjectKey) &&
            Objects.equals(embeddingModel, that.embeddingModel) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(bookSeries, that.bookSeries) &&
            Objects.equals(courseMaterialId, that.courseMaterialId) &&
            Objects.equals(classMaterialId, that.classMaterialId) &&
            Objects.equals(uploadedById, that.uploadedById) &&
            Objects.equals(subjectId, that.subjectId) &&
            Objects.equals(gradeId, that.gradeId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            sourceType,
            title,
            fileObjectKey,
            contentHash,
            sourceVersion,
            processingStatus,
            indexStatus,
            extractedTextObjectKey,
            embeddingModel,
            createdAt,
            updatedAt,
            bookSeries,
            courseMaterialId,
            classMaterialId,
            uploadedById,
            subjectId,
            gradeId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LearningDocumentCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalSourceType().map(f -> "sourceType=" + f + ", ").orElse("") +
            optionalTitle().map(f -> "title=" + f + ", ").orElse("") +
            optionalFileObjectKey().map(f -> "fileObjectKey=" + f + ", ").orElse("") +
            optionalContentHash().map(f -> "contentHash=" + f + ", ").orElse("") +
            optionalSourceVersion().map(f -> "sourceVersion=" + f + ", ").orElse("") +
            optionalProcessingStatus().map(f -> "processingStatus=" + f + ", ").orElse("") +
            optionalIndexStatus().map(f -> "indexStatus=" + f + ", ").orElse("") +
            optionalExtractedTextObjectKey().map(f -> "extractedTextObjectKey=" + f + ", ").orElse("") +
            optionalEmbeddingModel().map(f -> "embeddingModel=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalBookSeries().map(f -> "bookSeries=" + f + ", ").orElse("") +
            optionalCourseMaterialId().map(f -> "courseMaterialId=" + f + ", ").orElse("") +
            optionalClassMaterialId().map(f -> "classMaterialId=" + f + ", ").orElse("") +
            optionalUploadedById().map(f -> "uploadedById=" + f + ", ").orElse("") +
            optionalSubjectId().map(f -> "subjectId=" + f + ", ").orElse("") +
            optionalGradeId().map(f -> "gradeId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
