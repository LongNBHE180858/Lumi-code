package com.lumi.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.domain.AssignmentSubmission} entity. This class is used
 * in {@link com.lumi.web.rest.AssignmentSubmissionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /assignment-submissions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AssignmentSubmissionCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter content;

    private BigDecimalFilter score;

    private StringFilter teacherFeedback;

    private StringFilter status;

    private InstantFilter submittedAt;

    private LongFilter studentId;

    private LongFilter assignmentId;

    private Boolean distinct;

    public AssignmentSubmissionCriteria() {}

    public AssignmentSubmissionCriteria(AssignmentSubmissionCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.content = other.optionalContent().map(StringFilter::copy).orElse(null);
        this.score = other.optionalScore().map(BigDecimalFilter::copy).orElse(null);
        this.teacherFeedback = other.optionalTeacherFeedback().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(StringFilter::copy).orElse(null);
        this.submittedAt = other.optionalSubmittedAt().map(InstantFilter::copy).orElse(null);
        this.studentId = other.optionalStudentId().map(LongFilter::copy).orElse(null);
        this.assignmentId = other.optionalAssignmentId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public AssignmentSubmissionCriteria copy() {
        return new AssignmentSubmissionCriteria(this);
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

    public StringFilter getContent() {
        return content;
    }

    public Optional<StringFilter> optionalContent() {
        return Optional.ofNullable(content);
    }

    public StringFilter content() {
        if (content == null) {
            setContent(new StringFilter());
        }
        return content;
    }

    public void setContent(StringFilter content) {
        this.content = content;
    }

    public BigDecimalFilter getScore() {
        return score;
    }

    public Optional<BigDecimalFilter> optionalScore() {
        return Optional.ofNullable(score);
    }

    public BigDecimalFilter score() {
        if (score == null) {
            setScore(new BigDecimalFilter());
        }
        return score;
    }

    public void setScore(BigDecimalFilter score) {
        this.score = score;
    }

    public StringFilter getTeacherFeedback() {
        return teacherFeedback;
    }

    public Optional<StringFilter> optionalTeacherFeedback() {
        return Optional.ofNullable(teacherFeedback);
    }

    public StringFilter teacherFeedback() {
        if (teacherFeedback == null) {
            setTeacherFeedback(new StringFilter());
        }
        return teacherFeedback;
    }

    public void setTeacherFeedback(StringFilter teacherFeedback) {
        this.teacherFeedback = teacherFeedback;
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

    public LongFilter getAssignmentId() {
        return assignmentId;
    }

    public Optional<LongFilter> optionalAssignmentId() {
        return Optional.ofNullable(assignmentId);
    }

    public LongFilter assignmentId() {
        if (assignmentId == null) {
            setAssignmentId(new LongFilter());
        }
        return assignmentId;
    }

    public void setAssignmentId(LongFilter assignmentId) {
        this.assignmentId = assignmentId;
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
        final AssignmentSubmissionCriteria that = (AssignmentSubmissionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(content, that.content) &&
            Objects.equals(score, that.score) &&
            Objects.equals(teacherFeedback, that.teacherFeedback) &&
            Objects.equals(status, that.status) &&
            Objects.equals(submittedAt, that.submittedAt) &&
            Objects.equals(studentId, that.studentId) &&
            Objects.equals(assignmentId, that.assignmentId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, content, score, teacherFeedback, status, submittedAt, studentId, assignmentId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AssignmentSubmissionCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalContent().map(f -> "content=" + f + ", ").orElse("") +
            optionalScore().map(f -> "score=" + f + ", ").orElse("") +
            optionalTeacherFeedback().map(f -> "teacherFeedback=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalSubmittedAt().map(f -> "submittedAt=" + f + ", ").orElse("") +
            optionalStudentId().map(f -> "studentId=" + f + ", ").orElse("") +
            optionalAssignmentId().map(f -> "assignmentId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
