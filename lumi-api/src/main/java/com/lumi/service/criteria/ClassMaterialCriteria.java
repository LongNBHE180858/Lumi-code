package com.lumi.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.domain.ClassMaterial} entity. This class is used
 * in {@link com.lumi.web.rest.ClassMaterialResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /class-materials?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ClassMaterialCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter title;

    private StringFilter fileType;

    private StringFilter fileUrl;

    private LongFilter fileSizeBytes;

    private InstantFilter uploadedAt;

    private StringFilter fileObjectKey;

    private InstantFilter updatedAt;

    private LongFilter studyClassId;

    private LongFilter learningDocumentId;

    private Boolean distinct;

    public ClassMaterialCriteria() {}

    public ClassMaterialCriteria(ClassMaterialCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.title = other.optionalTitle().map(StringFilter::copy).orElse(null);
        this.fileType = other.optionalFileType().map(StringFilter::copy).orElse(null);
        this.fileUrl = other.optionalFileUrl().map(StringFilter::copy).orElse(null);
        this.fileSizeBytes = other.optionalFileSizeBytes().map(LongFilter::copy).orElse(null);
        this.uploadedAt = other.optionalUploadedAt().map(InstantFilter::copy).orElse(null);
        this.fileObjectKey = other.optionalFileObjectKey().map(StringFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.studyClassId = other.optionalStudyClassId().map(LongFilter::copy).orElse(null);
        this.learningDocumentId = other.optionalLearningDocumentId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ClassMaterialCriteria copy() {
        return new ClassMaterialCriteria(this);
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

    public StringFilter getFileType() {
        return fileType;
    }

    public Optional<StringFilter> optionalFileType() {
        return Optional.ofNullable(fileType);
    }

    public StringFilter fileType() {
        if (fileType == null) {
            setFileType(new StringFilter());
        }
        return fileType;
    }

    public void setFileType(StringFilter fileType) {
        this.fileType = fileType;
    }

    public StringFilter getFileUrl() {
        return fileUrl;
    }

    public Optional<StringFilter> optionalFileUrl() {
        return Optional.ofNullable(fileUrl);
    }

    public StringFilter fileUrl() {
        if (fileUrl == null) {
            setFileUrl(new StringFilter());
        }
        return fileUrl;
    }

    public void setFileUrl(StringFilter fileUrl) {
        this.fileUrl = fileUrl;
    }

    public LongFilter getFileSizeBytes() {
        return fileSizeBytes;
    }

    public Optional<LongFilter> optionalFileSizeBytes() {
        return Optional.ofNullable(fileSizeBytes);
    }

    public LongFilter fileSizeBytes() {
        if (fileSizeBytes == null) {
            setFileSizeBytes(new LongFilter());
        }
        return fileSizeBytes;
    }

    public void setFileSizeBytes(LongFilter fileSizeBytes) {
        this.fileSizeBytes = fileSizeBytes;
    }

    public InstantFilter getUploadedAt() {
        return uploadedAt;
    }

    public Optional<InstantFilter> optionalUploadedAt() {
        return Optional.ofNullable(uploadedAt);
    }

    public InstantFilter uploadedAt() {
        if (uploadedAt == null) {
            setUploadedAt(new InstantFilter());
        }
        return uploadedAt;
    }

    public void setUploadedAt(InstantFilter uploadedAt) {
        this.uploadedAt = uploadedAt;
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

    public LongFilter getStudyClassId() {
        return studyClassId;
    }

    public Optional<LongFilter> optionalStudyClassId() {
        return Optional.ofNullable(studyClassId);
    }

    public LongFilter studyClassId() {
        if (studyClassId == null) {
            setStudyClassId(new LongFilter());
        }
        return studyClassId;
    }

    public void setStudyClassId(LongFilter studyClassId) {
        this.studyClassId = studyClassId;
    }

    public LongFilter getLearningDocumentId() {
        return learningDocumentId;
    }

    public Optional<LongFilter> optionalLearningDocumentId() {
        return Optional.ofNullable(learningDocumentId);
    }

    public LongFilter learningDocumentId() {
        if (learningDocumentId == null) {
            setLearningDocumentId(new LongFilter());
        }
        return learningDocumentId;
    }

    public void setLearningDocumentId(LongFilter learningDocumentId) {
        this.learningDocumentId = learningDocumentId;
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
        final ClassMaterialCriteria that = (ClassMaterialCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(title, that.title) &&
            Objects.equals(fileType, that.fileType) &&
            Objects.equals(fileUrl, that.fileUrl) &&
            Objects.equals(fileSizeBytes, that.fileSizeBytes) &&
            Objects.equals(uploadedAt, that.uploadedAt) &&
            Objects.equals(fileObjectKey, that.fileObjectKey) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(studyClassId, that.studyClassId) &&
            Objects.equals(learningDocumentId, that.learningDocumentId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            title,
            fileType,
            fileUrl,
            fileSizeBytes,
            uploadedAt,
            fileObjectKey,
            updatedAt,
            studyClassId,
            learningDocumentId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClassMaterialCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalTitle().map(f -> "title=" + f + ", ").orElse("") +
            optionalFileType().map(f -> "fileType=" + f + ", ").orElse("") +
            optionalFileUrl().map(f -> "fileUrl=" + f + ", ").orElse("") +
            optionalFileSizeBytes().map(f -> "fileSizeBytes=" + f + ", ").orElse("") +
            optionalUploadedAt().map(f -> "uploadedAt=" + f + ", ").orElse("") +
            optionalFileObjectKey().map(f -> "fileObjectKey=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalStudyClassId().map(f -> "studyClassId=" + f + ", ").orElse("") +
            optionalLearningDocumentId().map(f -> "learningDocumentId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
