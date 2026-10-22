package com.lumi.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.domain.CourseMaterial} entity. This class is used
 * in {@link com.lumi.web.rest.CourseMaterialResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /course-materials?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CourseMaterialCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter fileType;

    private StringFilter fileUrl;

    private LongFilter fileSizeBytes;

    private LongFilter lessonId;

    private Boolean distinct;

    public CourseMaterialCriteria() {}

    public CourseMaterialCriteria(CourseMaterialCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.fileType = other.optionalFileType().map(StringFilter::copy).orElse(null);
        this.fileUrl = other.optionalFileUrl().map(StringFilter::copy).orElse(null);
        this.fileSizeBytes = other.optionalFileSizeBytes().map(LongFilter::copy).orElse(null);
        this.lessonId = other.optionalLessonId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CourseMaterialCriteria copy() {
        return new CourseMaterialCriteria(this);
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
        final CourseMaterialCriteria that = (CourseMaterialCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(fileType, that.fileType) &&
            Objects.equals(fileUrl, that.fileUrl) &&
            Objects.equals(fileSizeBytes, that.fileSizeBytes) &&
            Objects.equals(lessonId, that.lessonId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fileType, fileUrl, fileSizeBytes, lessonId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CourseMaterialCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalFileType().map(f -> "fileType=" + f + ", ").orElse("") +
            optionalFileUrl().map(f -> "fileUrl=" + f + ", ").orElse("") +
            optionalFileSizeBytes().map(f -> "fileSizeBytes=" + f + ", ").orElse("") +
            optionalLessonId().map(f -> "lessonId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
