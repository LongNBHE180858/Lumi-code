package com.lumi.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.domain.CourseReviewRequest} entity. This class is used
 * in {@link com.lumi.web.rest.CourseReviewRequestResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /course-review-requests?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CourseReviewRequestCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter courseVersion;

    private StringFilter status;

    private StringFilter submissionNote;

    private InstantFilter rightsConfirmedAt;

    private InstantFilter submittedAt;

    private InstantFilter reviewedAt;

    private StringFilter reviewerFeedback;

    private LongFilter courseId;

    private LongFilter submittedById;

    private LongFilter reviewedById;

    private Boolean distinct;

    public CourseReviewRequestCriteria() {}

    public CourseReviewRequestCriteria(CourseReviewRequestCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.courseVersion = other.optionalCourseVersion().map(LongFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(StringFilter::copy).orElse(null);
        this.submissionNote = other.optionalSubmissionNote().map(StringFilter::copy).orElse(null);
        this.rightsConfirmedAt = other.optionalRightsConfirmedAt().map(InstantFilter::copy).orElse(null);
        this.submittedAt = other.optionalSubmittedAt().map(InstantFilter::copy).orElse(null);
        this.reviewedAt = other.optionalReviewedAt().map(InstantFilter::copy).orElse(null);
        this.reviewerFeedback = other.optionalReviewerFeedback().map(StringFilter::copy).orElse(null);
        this.courseId = other.optionalCourseId().map(LongFilter::copy).orElse(null);
        this.submittedById = other.optionalSubmittedById().map(LongFilter::copy).orElse(null);
        this.reviewedById = other.optionalReviewedById().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CourseReviewRequestCriteria copy() {
        return new CourseReviewRequestCriteria(this);
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

    public LongFilter getCourseVersion() {
        return courseVersion;
    }

    public Optional<LongFilter> optionalCourseVersion() {
        return Optional.ofNullable(courseVersion);
    }

    public LongFilter courseVersion() {
        if (courseVersion == null) {
            setCourseVersion(new LongFilter());
        }
        return courseVersion;
    }

    public void setCourseVersion(LongFilter courseVersion) {
        this.courseVersion = courseVersion;
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

    public StringFilter getSubmissionNote() {
        return submissionNote;
    }

    public Optional<StringFilter> optionalSubmissionNote() {
        return Optional.ofNullable(submissionNote);
    }

    public StringFilter submissionNote() {
        if (submissionNote == null) {
            setSubmissionNote(new StringFilter());
        }
        return submissionNote;
    }

    public void setSubmissionNote(StringFilter submissionNote) {
        this.submissionNote = submissionNote;
    }

    public InstantFilter getRightsConfirmedAt() {
        return rightsConfirmedAt;
    }

    public Optional<InstantFilter> optionalRightsConfirmedAt() {
        return Optional.ofNullable(rightsConfirmedAt);
    }

    public InstantFilter rightsConfirmedAt() {
        if (rightsConfirmedAt == null) {
            setRightsConfirmedAt(new InstantFilter());
        }
        return rightsConfirmedAt;
    }

    public void setRightsConfirmedAt(InstantFilter rightsConfirmedAt) {
        this.rightsConfirmedAt = rightsConfirmedAt;
    }

    public InstantFilter getSubmittedAt() {
        return submittedAt;
    }

    public Optional<InstantFilter> optionalSubmittedAt() {
        return Optional.ofNullable(submittedAt);
    }

    public InstantFilter submittedAt() {
        if (submittedAt == null) {
            setSubmittedAt(new InstantFilter());
        }
        return submittedAt;
    }

    public void setSubmittedAt(InstantFilter submittedAt) {
        this.submittedAt = submittedAt;
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

    public StringFilter getReviewerFeedback() {
        return reviewerFeedback;
    }

    public Optional<StringFilter> optionalReviewerFeedback() {
        return Optional.ofNullable(reviewerFeedback);
    }

    public StringFilter reviewerFeedback() {
        if (reviewerFeedback == null) {
            setReviewerFeedback(new StringFilter());
        }
        return reviewerFeedback;
    }

    public void setReviewerFeedback(StringFilter reviewerFeedback) {
        this.reviewerFeedback = reviewerFeedback;
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

    public LongFilter getSubmittedById() {
        return submittedById;
    }

    public Optional<LongFilter> optionalSubmittedById() {
        return Optional.ofNullable(submittedById);
    }

    public LongFilter submittedById() {
        if (submittedById == null) {
            setSubmittedById(new LongFilter());
        }
        return submittedById;
    }

    public void setSubmittedById(LongFilter submittedById) {
        this.submittedById = submittedById;
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
        final CourseReviewRequestCriteria that = (CourseReviewRequestCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(courseVersion, that.courseVersion) &&
            Objects.equals(status, that.status) &&
            Objects.equals(submissionNote, that.submissionNote) &&
            Objects.equals(rightsConfirmedAt, that.rightsConfirmedAt) &&
            Objects.equals(submittedAt, that.submittedAt) &&
            Objects.equals(reviewedAt, that.reviewedAt) &&
            Objects.equals(reviewerFeedback, that.reviewerFeedback) &&
            Objects.equals(courseId, that.courseId) &&
            Objects.equals(submittedById, that.submittedById) &&
            Objects.equals(reviewedById, that.reviewedById) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            courseVersion,
            status,
            submissionNote,
            rightsConfirmedAt,
            submittedAt,
            reviewedAt,
            reviewerFeedback,
            courseId,
            submittedById,
            reviewedById,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CourseReviewRequestCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCourseVersion().map(f -> "courseVersion=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalSubmissionNote().map(f -> "submissionNote=" + f + ", ").orElse("") +
            optionalRightsConfirmedAt().map(f -> "rightsConfirmedAt=" + f + ", ").orElse("") +
            optionalSubmittedAt().map(f -> "submittedAt=" + f + ", ").orElse("") +
            optionalReviewedAt().map(f -> "reviewedAt=" + f + ", ").orElse("") +
            optionalReviewerFeedback().map(f -> "reviewerFeedback=" + f + ", ").orElse("") +
            optionalCourseId().map(f -> "courseId=" + f + ", ").orElse("") +
            optionalSubmittedById().map(f -> "submittedById=" + f + ", ").orElse("") +
            optionalReviewedById().map(f -> "reviewedById=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
