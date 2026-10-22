package com.lumi.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.domain.ProctorSession} entity. This class is used
 * in {@link com.lumi.web.rest.ProctorSessionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /proctor-sessions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProctorSessionCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private BooleanFilter faceVerified;

    private IntegerFilter warningCount;

    private StringFilter status;

    private LongFilter attemptId;

    private Boolean distinct;

    public ProctorSessionCriteria() {}

    public ProctorSessionCriteria(ProctorSessionCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.faceVerified = other.optionalFaceVerified().map(BooleanFilter::copy).orElse(null);
        this.warningCount = other.optionalWarningCount().map(IntegerFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(StringFilter::copy).orElse(null);
        this.attemptId = other.optionalAttemptId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ProctorSessionCriteria copy() {
        return new ProctorSessionCriteria(this);
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

    public BooleanFilter getFaceVerified() {
        return faceVerified;
    }

    public Optional<BooleanFilter> optionalFaceVerified() {
        return Optional.ofNullable(faceVerified);
    }

    public BooleanFilter faceVerified() {
        if (faceVerified == null) {
            setFaceVerified(new BooleanFilter());
        }
        return faceVerified;
    }

    public void setFaceVerified(BooleanFilter faceVerified) {
        this.faceVerified = faceVerified;
    }

    public IntegerFilter getWarningCount() {
        return warningCount;
    }

    public Optional<IntegerFilter> optionalWarningCount() {
        return Optional.ofNullable(warningCount);
    }

    public IntegerFilter warningCount() {
        if (warningCount == null) {
            setWarningCount(new IntegerFilter());
        }
        return warningCount;
    }

    public void setWarningCount(IntegerFilter warningCount) {
        this.warningCount = warningCount;
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

    public LongFilter getAttemptId() {
        return attemptId;
    }

    public Optional<LongFilter> optionalAttemptId() {
        return Optional.ofNullable(attemptId);
    }

    public LongFilter attemptId() {
        if (attemptId == null) {
            setAttemptId(new LongFilter());
        }
        return attemptId;
    }

    public void setAttemptId(LongFilter attemptId) {
        this.attemptId = attemptId;
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
        final ProctorSessionCriteria that = (ProctorSessionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(faceVerified, that.faceVerified) &&
            Objects.equals(warningCount, that.warningCount) &&
            Objects.equals(status, that.status) &&
            Objects.equals(attemptId, that.attemptId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, faceVerified, warningCount, status, attemptId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProctorSessionCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalFaceVerified().map(f -> "faceVerified=" + f + ", ").orElse("") +
            optionalWarningCount().map(f -> "warningCount=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalAttemptId().map(f -> "attemptId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
