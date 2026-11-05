package com.lumi.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.domain.AiGeneratedContent} entity. This class is used
 * in {@link com.lumi.web.rest.AiGeneratedContentResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /ai-generated-contents?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AiGeneratedContentCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter contentType;

    private StringFilter requestHash;

    private StringFilter sourceVersion;

    private StringFilter modelName;

    private StringFilter promptVersion;

    private StringFilter generationStatus;

    private StringFilter visibility;

    private LongFilter version;

    private StringFilter reviewStatus;

    private InstantFilter reviewedAt;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    private LongFilter sourceQuestionId;

    private StringFilter contentHash;

    private LongFilter createdById;

    private LongFilter documentId;

    private LongFilter lessonId;

    private LongFilter reviewedById;

    private LongFilter courseId;

    private Boolean distinct;

    public AiGeneratedContentCriteria() {}

    public AiGeneratedContentCriteria(AiGeneratedContentCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.contentType = other.optionalContentType().map(StringFilter::copy).orElse(null);
        this.requestHash = other.optionalRequestHash().map(StringFilter::copy).orElse(null);
        this.sourceVersion = other.optionalSourceVersion().map(StringFilter::copy).orElse(null);
        this.modelName = other.optionalModelName().map(StringFilter::copy).orElse(null);
        this.promptVersion = other.optionalPromptVersion().map(StringFilter::copy).orElse(null);
        this.generationStatus = other.optionalGenerationStatus().map(StringFilter::copy).orElse(null);
        this.visibility = other.optionalVisibility().map(StringFilter::copy).orElse(null);
        this.version = other.optionalVersion().map(LongFilter::copy).orElse(null);
        this.reviewStatus = other.optionalReviewStatus().map(StringFilter::copy).orElse(null);
        this.reviewedAt = other.optionalReviewedAt().map(InstantFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.sourceQuestionId = other.optionalSourceQuestionId().map(LongFilter::copy).orElse(null);
        this.contentHash = other.optionalContentHash().map(StringFilter::copy).orElse(null);
        this.createdById = other.optionalCreatedById().map(LongFilter::copy).orElse(null);
        this.documentId = other.optionalDocumentId().map(LongFilter::copy).orElse(null);
        this.lessonId = other.optionalLessonId().map(LongFilter::copy).orElse(null);
        this.reviewedById = other.optionalReviewedById().map(LongFilter::copy).orElse(null);
        this.courseId = other.optionalCourseId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public AiGeneratedContentCriteria copy() {
        return new AiGeneratedContentCriteria(this);
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

    public StringFilter getContentType() {
        return contentType;
    }

    public Optional<StringFilter> optionalContentType() {
        return Optional.ofNullable(contentType);
    }

    public StringFilter contentType() {
        if (contentType == null) {
            setContentType(new StringFilter());
        }
        return contentType;
    }

    public void setContentType(StringFilter contentType) {
        this.contentType = contentType;
    }

    public StringFilter getRequestHash() {
        return requestHash;
    }

    public Optional<StringFilter> optionalRequestHash() {
        return Optional.ofNullable(requestHash);
    }

    public StringFilter requestHash() {
        if (requestHash == null) {
            setRequestHash(new StringFilter());
        }
        return requestHash;
    }

    public void setRequestHash(StringFilter requestHash) {
        this.requestHash = requestHash;
    }

    public StringFilter getSourceVersion() {
        return sourceVersion;
    }

    public Optional<StringFilter> optionalSourceVersion() {
        return Optional.ofNullable(sourceVersion);
    }

    public StringFilter sourceVersion() {
        if (sourceVersion == null) {
            setSourceVersion(new StringFilter());
        }
        return sourceVersion;
    }

    public void setSourceVersion(StringFilter sourceVersion) {
        this.sourceVersion = sourceVersion;
    }

    public StringFilter getModelName() {
        return modelName;
    }

    public Optional<StringFilter> optionalModelName() {
        return Optional.ofNullable(modelName);
    }

    public StringFilter modelName() {
        if (modelName == null) {
            setModelName(new StringFilter());
        }
        return modelName;
    }

    public void setModelName(StringFilter modelName) {
        this.modelName = modelName;
    }

    public StringFilter getPromptVersion() {
        return promptVersion;
    }

    public Optional<StringFilter> optionalPromptVersion() {
        return Optional.ofNullable(promptVersion);
    }

    public StringFilter promptVersion() {
        if (promptVersion == null) {
            setPromptVersion(new StringFilter());
        }
        return promptVersion;
    }

    public void setPromptVersion(StringFilter promptVersion) {
        this.promptVersion = promptVersion;
    }

    public StringFilter getGenerationStatus() {
        return generationStatus;
    }

    public Optional<StringFilter> optionalGenerationStatus() {
        return Optional.ofNullable(generationStatus);
    }

    public StringFilter generationStatus() {
        if (generationStatus == null) {
            setGenerationStatus(new StringFilter());
        }
        return generationStatus;
    }

    public void setGenerationStatus(StringFilter generationStatus) {
        this.generationStatus = generationStatus;
    }

    public StringFilter getVisibility() {
        return visibility;
    }

    public Optional<StringFilter> optionalVisibility() {
        return Optional.ofNullable(visibility);
    }

    public StringFilter visibility() {
        if (visibility == null) {
            setVisibility(new StringFilter());
        }
        return visibility;
    }

    public void setVisibility(StringFilter visibility) {
        this.visibility = visibility;
    }

    public LongFilter getVersion() {
        return version;
    }

    public Optional<LongFilter> optionalVersion() {
        return Optional.ofNullable(version);
    }

    public LongFilter version() {
        if (version == null) {
            setVersion(new LongFilter());
        }
        return version;
    }

    public void setVersion(LongFilter version) {
        this.version = version;
    }

    public StringFilter getReviewStatus() {
        return reviewStatus;
    }

    public Optional<StringFilter> optionalReviewStatus() {
        return Optional.ofNullable(reviewStatus);
    }

    public StringFilter reviewStatus() {
        if (reviewStatus == null) {
            setReviewStatus(new StringFilter());
        }
        return reviewStatus;
    }

    public void setReviewStatus(StringFilter reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public InstantFilter getReviewedAt() {
        return reviewedAt;
    }

    public Optional<InstantFilter> optionalReviewedAt() {
        return Optional.ofNullable(reviewedAt);
    }

    public InstantFilter reviewedAt() {
        if (reviewedAt == null) {
            setReviewedAt(new InstantFilter());
        }
        return reviewedAt;
    }

    public void setReviewedAt(InstantFilter reviewedAt) {
        this.reviewedAt = reviewedAt;
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

    public LongFilter getSourceQuestionId() {
        return sourceQuestionId;
    }

    public Optional<LongFilter> optionalSourceQuestionId() {
        return Optional.ofNullable(sourceQuestionId);
    }

    public LongFilter sourceQuestionId() {
        if (sourceQuestionId == null) {
            setSourceQuestionId(new LongFilter());
        }
        return sourceQuestionId;
    }

    public void setSourceQuestionId(LongFilter sourceQuestionId) {
        this.sourceQuestionId = sourceQuestionId;
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

    public LongFilter getCreatedById() {
        return createdById;
    }

    public Optional<LongFilter> optionalCreatedById() {
        return Optional.ofNullable(createdById);
    }

    public LongFilter createdById() {
        if (createdById == null) {
            setCreatedById(new LongFilter());
        }
        return createdById;
    }

    public void setCreatedById(LongFilter createdById) {
        this.createdById = createdById;
    }

    public LongFilter getDocumentId() {
        return documentId;
    }

    public Optional<LongFilter> optionalDocumentId() {
        return Optional.ofNullable(documentId);
    }

    public LongFilter documentId() {
        if (documentId == null) {
            setDocumentId(new LongFilter());
        }
        return documentId;
    }

    public void setDocumentId(LongFilter documentId) {
        this.documentId = documentId;
    }

    public LongFilter getLessonId() {
        return lessonId;
    }

    public Optional<LongFilter> optionalLessonId() {
        return Optional.ofNullable(lessonId);
    }

    public LongFilter lessonId() {
        if (lessonId == null) {
            setLessonId(new LongFilter());
        }
        return lessonId;
    }

    public void setLessonId(LongFilter lessonId) {
        this.lessonId = lessonId;
    }

    public LongFilter getReviewedById() {
        return reviewedById;
    }

    public Optional<LongFilter> optionalReviewedById() {
        return Optional.ofNullable(reviewedById);
    }

    public LongFilter reviewedById() {
        if (reviewedById == null) {
            setReviewedById(new LongFilter());
        }
        return reviewedById;
    }

    public void setReviewedById(LongFilter reviewedById) {
        this.reviewedById = reviewedById;
    }

    public LongFilter getCourseId() {
        return courseId;
    }

    public Optional<LongFilter> optionalCourseId() {
        return Optional.ofNullable(courseId);
    }

    public LongFilter courseId() {
        if (courseId == null) {
            setCourseId(new LongFilter());
        }
        return courseId;
    }

    public void setCourseId(LongFilter courseId) {
        this.courseId = courseId;
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
        final AiGeneratedContentCriteria that = (AiGeneratedContentCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(contentType, that.contentType) &&
            Objects.equals(requestHash, that.requestHash) &&
            Objects.equals(sourceVersion, that.sourceVersion) &&
            Objects.equals(modelName, that.modelName) &&
            Objects.equals(promptVersion, that.promptVersion) &&
            Objects.equals(generationStatus, that.generationStatus) &&
            Objects.equals(visibility, that.visibility) &&
            Objects.equals(version, that.version) &&
            Objects.equals(reviewStatus, that.reviewStatus) &&
            Objects.equals(reviewedAt, that.reviewedAt) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(sourceQuestionId, that.sourceQuestionId) &&
            Objects.equals(contentHash, that.contentHash) &&
            Objects.equals(createdById, that.createdById) &&
            Objects.equals(documentId, that.documentId) &&
            Objects.equals(lessonId, that.lessonId) &&
            Objects.equals(reviewedById, that.reviewedById) &&
            Objects.equals(courseId, that.courseId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            contentType,
            requestHash,
            sourceVersion,
            modelName,
            promptVersion,
            generationStatus,
            visibility,
            version,
            reviewStatus,
            reviewedAt,
            createdAt,
            updatedAt,
            sourceQuestionId,
            contentHash,
            createdById,
            documentId,
            lessonId,
            reviewedById,
            courseId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AiGeneratedContentCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalContentType().map(f -> "contentType=" + f + ", ").orElse("") +
            optionalRequestHash().map(f -> "requestHash=" + f + ", ").orElse("") +
            optionalSourceVersion().map(f -> "sourceVersion=" + f + ", ").orElse("") +
            optionalModelName().map(f -> "modelName=" + f + ", ").orElse("") +
            optionalPromptVersion().map(f -> "promptVersion=" + f + ", ").orElse("") +
            optionalGenerationStatus().map(f -> "generationStatus=" + f + ", ").orElse("") +
            optionalVisibility().map(f -> "visibility=" + f + ", ").orElse("") +
            optionalVersion().map(f -> "version=" + f + ", ").orElse("") +
            optionalReviewStatus().map(f -> "reviewStatus=" + f + ", ").orElse("") +
            optionalReviewedAt().map(f -> "reviewedAt=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalSourceQuestionId().map(f -> "sourceQuestionId=" + f + ", ").orElse("") +
            optionalContentHash().map(f -> "contentHash=" + f + ", ").orElse("") +
            optionalCreatedById().map(f -> "createdById=" + f + ", ").orElse("") +
            optionalDocumentId().map(f -> "documentId=" + f + ", ").orElse("") +
            optionalLessonId().map(f -> "lessonId=" + f + ", ").orElse("") +
            optionalReviewedById().map(f -> "reviewedById=" + f + ", ").orElse("") +
            optionalCourseId().map(f -> "courseId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
