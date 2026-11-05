package com.lumi.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.domain.CourseEnrollment} entity. This class is used
 * in {@link com.lumi.web.rest.CourseEnrollmentResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /course-enrollments?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CourseEnrollmentCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private BigDecimalFilter progressPercent;

    private StringFilter status;

    private InstantFilter enrolledAt;

    private InstantFilter expiresAt;

    private InstantFilter updatedAt;

    private LongFilter courseId;

    private LongFilter studentId;

    private LongFilter lastAccessedLessonId;

    private Boolean distinct;

    public CourseEnrollmentCriteria() {}

    public CourseEnrollmentCriteria(CourseEnrollmentCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.progressPercent = other.optionalProgressPercent().map(BigDecimalFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(StringFilter::copy).orElse(null);
        this.enrolledAt = other.optionalEnrolledAt().map(InstantFilter::copy).orElse(null);
        this.expiresAt = other.optionalExpiresAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.courseId = other.optionalCourseId().map(LongFilter::copy).orElse(null);
        this.studentId = other.optionalStudentId().map(LongFilter::copy).orElse(null);
        this.lastAccessedLessonId = other.optionalLastAccessedLessonId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CourseEnrollmentCriteria copy() {
        return new CourseEnrollmentCriteria(this);
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

    public BigDecimalFilter getProgressPercent() {
        return progressPercent;
    }

    public Optional<BigDecimalFilter> optionalProgressPercent() {
        return Optional.ofNullable(progressPercent);
    }

    public BigDecimalFilter progressPercent() {
        if (progressPercent == null) {
            setProgressPercent(new BigDecimalFilter());
        }
        return progressPercent;
    }

    public void setProgressPercent(BigDecimalFilter progressPercent) {
        this.progressPercent = progressPercent;
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

    public InstantFilter getEnrolledAt() {
        return enrolledAt;
    }

    public Optional<InstantFilter> optionalEnrolledAt() {
        return Optional.ofNullable(enrolledAt);
    }

    public InstantFilter enrolledAt() {
        if (enrolledAt == null) {
            setEnrolledAt(new InstantFilter());
        }
        return enrolledAt;
    }

    public void setEnrolledAt(InstantFilter enrolledAt) {
        this.enrolledAt = enrolledAt;
    }

    public InstantFilter getExpiresAt() {
        return expiresAt;
    }

    public Optional<InstantFilter> optionalExpiresAt() {
        return Optional.ofNullable(expiresAt);
    }

    public InstantFilter expiresAt() {
        if (expiresAt == null) {
            setExpiresAt(new InstantFilter());
        }
        return expiresAt;
    }

    public void setExpiresAt(InstantFilter expiresAt) {
        this.expiresAt = expiresAt;
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

    public LongFilter getStudentId() {
        return studentId;
    }

    public Optional<LongFilter> optionalStudentId() {
        return Optional.ofNullable(studentId);
    }

    public LongFilter studentId() {
        if (studentId == null) {
            setStudentId(new LongFilter());
        }
        return studentId;
    }

    public void setStudentId(LongFilter studentId) {
        this.studentId = studentId;
    }

    public LongFilter getLastAccessedLessonId() {
        return lastAccessedLessonId;
    }

    public Optional<LongFilter> optionalLastAccessedLessonId() {
        return Optional.ofNullable(lastAccessedLessonId);
    }

    public LongFilter lastAccessedLessonId() {
        if (lastAccessedLessonId == null) {
            setLastAccessedLessonId(new LongFilter());
        }
        return lastAccessedLessonId;
    }

    public void setLastAccessedLessonId(LongFilter lastAccessedLessonId) {
        this.lastAccessedLessonId = lastAccessedLessonId;
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
        final CourseEnrollmentCriteria that = (CourseEnrollmentCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(progressPercent, that.progressPercent) &&
            Objects.equals(status, that.status) &&
            Objects.equals(enrolledAt, that.enrolledAt) &&
            Objects.equals(expiresAt, that.expiresAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(courseId, that.courseId) &&
            Objects.equals(studentId, that.studentId) &&
            Objects.equals(lastAccessedLessonId, that.lastAccessedLessonId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            progressPercent,
            status,
            enrolledAt,
            expiresAt,
            updatedAt,
            courseId,
            studentId,
            lastAccessedLessonId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CourseEnrollmentCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalProgressPercent().map(f -> "progressPercent=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalEnrolledAt().map(f -> "enrolledAt=" + f + ", ").orElse("") +
            optionalExpiresAt().map(f -> "expiresAt=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalCourseId().map(f -> "courseId=" + f + ", ").orElse("") +
            optionalStudentId().map(f -> "studentId=" + f + ", ").orElse("") +
            optionalLastAccessedLessonId().map(f -> "lastAccessedLessonId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
