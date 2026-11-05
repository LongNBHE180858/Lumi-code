package com.lumi.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.domain.LessonProgress} entity. This class is used
 * in {@link com.lumi.web.rest.LessonProgressResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /lesson-progresses?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LessonProgressCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter status;

    private BigDecimalFilter lastPositionSeconds;

    private InstantFilter lastAccessedAt;

    private InstantFilter completedAt;

    private LongFilter rowVersion;

    private LongFilter enrollmentId;

    private LongFilter lessonId;

    private Boolean distinct;

    public LessonProgressCriteria() {}

    public LessonProgressCriteria(LessonProgressCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(StringFilter::copy).orElse(null);
        this.lastPositionSeconds = other.optionalLastPositionSeconds().map(BigDecimalFilter::copy).orElse(null);
        this.lastAccessedAt = other.optionalLastAccessedAt().map(InstantFilter::copy).orElse(null);
        this.completedAt = other.optionalCompletedAt().map(InstantFilter::copy).orElse(null);
        this.rowVersion = other.optionalRowVersion().map(LongFilter::copy).orElse(null);
        this.enrollmentId = other.optionalEnrollmentId().map(LongFilter::copy).orElse(null);
        this.lessonId = other.optionalLessonId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public LessonProgressCriteria copy() {
        return new LessonProgressCriteria(this);
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

    public StringFilter getStatus() {
        return status;
    }

    public Optional<StringFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public StringFilter status() {
        if (status == null) {
            setStatus(new StringFilter());
        }
        return status;
    }

    public void setStatus(StringFilter status) {
        this.status = status;
    }

    public BigDecimalFilter getLastPositionSeconds() {
        return lastPositionSeconds;
    }

    public Optional<BigDecimalFilter> optionalLastPositionSeconds() {
        return Optional.ofNullable(lastPositionSeconds);
    }

    public BigDecimalFilter lastPositionSeconds() {
        if (lastPositionSeconds == null) {
            setLastPositionSeconds(new BigDecimalFilter());
        }
        return lastPositionSeconds;
    }

    public void setLastPositionSeconds(BigDecimalFilter lastPositionSeconds) {
        this.lastPositionSeconds = lastPositionSeconds;
    }

    public InstantFilter getLastAccessedAt() {
        return lastAccessedAt;
    }

    public Optional<InstantFilter> optionalLastAccessedAt() {
        return Optional.ofNullable(lastAccessedAt);
    }

    public InstantFilter lastAccessedAt() {
        if (lastAccessedAt == null) {
            setLastAccessedAt(new InstantFilter());
        }
        return lastAccessedAt;
    }

    public void setLastAccessedAt(InstantFilter lastAccessedAt) {
        this.lastAccessedAt = lastAccessedAt;
    }

    public InstantFilter getCompletedAt() {
        return completedAt;
    }

    public Optional<InstantFilter> optionalCompletedAt() {
        return Optional.ofNullable(completedAt);
    }

    public InstantFilter completedAt() {
        if (completedAt == null) {
            setCompletedAt(new InstantFilter());
        }
        return completedAt;
    }

    public void setCompletedAt(InstantFilter completedAt) {
        this.completedAt = completedAt;
    }

    public LongFilter getRowVersion() {
        return rowVersion;
    }

    public Optional<LongFilter> optionalRowVersion() {
        return Optional.ofNullable(rowVersion);
    }

    public LongFilter rowVersion() {
        if (rowVersion == null) {
            setRowVersion(new LongFilter());
        }
        return rowVersion;
    }

    public void setRowVersion(LongFilter rowVersion) {
        this.rowVersion = rowVersion;
    }

    public LongFilter getEnrollmentId() {
        return enrollmentId;
    }

    public Optional<LongFilter> optionalEnrollmentId() {
        return Optional.ofNullable(enrollmentId);
    }

    public LongFilter enrollmentId() {
        if (enrollmentId == null) {
            setEnrollmentId(new LongFilter());
        }
        return enrollmentId;
    }

    public void setEnrollmentId(LongFilter enrollmentId) {
        this.enrollmentId = enrollmentId;
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
        final LessonProgressCriteria that = (LessonProgressCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(status, that.status) &&
            Objects.equals(lastPositionSeconds, that.lastPositionSeconds) &&
            Objects.equals(lastAccessedAt, that.lastAccessedAt) &&
            Objects.equals(completedAt, that.completedAt) &&
            Objects.equals(rowVersion, that.rowVersion) &&
            Objects.equals(enrollmentId, that.enrollmentId) &&
            Objects.equals(lessonId, that.lessonId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, lastPositionSeconds, lastAccessedAt, completedAt, rowVersion, enrollmentId, lessonId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LessonProgressCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalLastPositionSeconds().map(f -> "lastPositionSeconds=" + f + ", ").orElse("") +
            optionalLastAccessedAt().map(f -> "lastAccessedAt=" + f + ", ").orElse("") +
            optionalCompletedAt().map(f -> "completedAt=" + f + ", ").orElse("") +
            optionalRowVersion().map(f -> "rowVersion=" + f + ", ").orElse("") +
            optionalEnrollmentId().map(f -> "enrollmentId=" + f + ", ").orElse("") +
            optionalLessonId().map(f -> "lessonId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
