package com.lumi.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.domain.ExamAttempt} entity. This class is used
 * in {@link com.lumi.web.rest.ExamAttemptResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /exam-attempts?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ExamAttemptCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private InstantFilter startTime;

    private InstantFilter endTime;

    private BigDecimalFilter score;

    private StringFilter status;

    private LongFilter rowVersion;

    private LongFilter studentId;

    private LongFilter examId;

    private LongFilter proctorSessionId;

    private Boolean distinct;

    public ExamAttemptCriteria() {}

    public ExamAttemptCriteria(ExamAttemptCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.startTime = other.optionalStartTime().map(InstantFilter::copy).orElse(null);
        this.endTime = other.optionalEndTime().map(InstantFilter::copy).orElse(null);
        this.score = other.optionalScore().map(BigDecimalFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(StringFilter::copy).orElse(null);
        this.rowVersion = other.optionalRowVersion().map(LongFilter::copy).orElse(null);
        this.studentId = other.optionalStudentId().map(LongFilter::copy).orElse(null);
        this.examId = other.optionalExamId().map(LongFilter::copy).orElse(null);
        this.proctorSessionId = other.optionalProctorSessionId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ExamAttemptCriteria copy() {
        return new ExamAttemptCriteria(this);
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

    public InstantFilter getStartTime() {
        return startTime;
    }

    public Optional<InstantFilter> optionalStartTime() {
        return Optional.ofNullable(startTime);
    }

    public InstantFilter startTime() {
        if (startTime == null) {
            setStartTime(new InstantFilter());
        }
        return startTime;
    }

    public void setStartTime(InstantFilter startTime) {
        this.startTime = startTime;
    }

    public InstantFilter getEndTime() {
        return endTime;
    }

    public Optional<InstantFilter> optionalEndTime() {
        return Optional.ofNullable(endTime);
    }

    public InstantFilter endTime() {
        if (endTime == null) {
            setEndTime(new InstantFilter());
        }
        return endTime;
    }

    public void setEndTime(InstantFilter endTime) {
        this.endTime = endTime;
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

    public LongFilter getExamId() {
        return examId;
    }

    public Optional<LongFilter> optionalExamId() {
        return Optional.ofNullable(examId);
    }

    public LongFilter examId() {
        if (examId == null) {
            setExamId(new LongFilter());
        }
        return examId;
    }

    public void setExamId(LongFilter examId) {
        this.examId = examId;
    }

    public LongFilter getProctorSessionId() {
        return proctorSessionId;
    }

    public Optional<LongFilter> optionalProctorSessionId() {
        return Optional.ofNullable(proctorSessionId);
    }

    public LongFilter proctorSessionId() {
        if (proctorSessionId == null) {
            setProctorSessionId(new LongFilter());
        }
        return proctorSessionId;
    }

    public void setProctorSessionId(LongFilter proctorSessionId) {
        this.proctorSessionId = proctorSessionId;
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
        final ExamAttemptCriteria that = (ExamAttemptCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(startTime, that.startTime) &&
            Objects.equals(endTime, that.endTime) &&
            Objects.equals(score, that.score) &&
            Objects.equals(status, that.status) &&
            Objects.equals(rowVersion, that.rowVersion) &&
            Objects.equals(studentId, that.studentId) &&
            Objects.equals(examId, that.examId) &&
            Objects.equals(proctorSessionId, that.proctorSessionId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startTime, endTime, score, status, rowVersion, studentId, examId, proctorSessionId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ExamAttemptCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalStartTime().map(f -> "startTime=" + f + ", ").orElse("") +
            optionalEndTime().map(f -> "endTime=" + f + ", ").orElse("") +
            optionalScore().map(f -> "score=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalRowVersion().map(f -> "rowVersion=" + f + ", ").orElse("") +
            optionalStudentId().map(f -> "studentId=" + f + ", ").orElse("") +
            optionalExamId().map(f -> "examId=" + f + ", ").orElse("") +
            optionalProctorSessionId().map(f -> "proctorSessionId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
