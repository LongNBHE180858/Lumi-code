package com.lumi.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.domain.FlashcardReview} entity. This class is used
 * in {@link com.lumi.web.rest.FlashcardReviewResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /flashcard-reviews?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FlashcardReviewCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private InstantFilter nextReviewAt;

    private BigDecimalFilter intervalDays;

    private LongFilter studentId;

    private LongFilter flashcardId;

    private Boolean distinct;

    public FlashcardReviewCriteria() {}

    public FlashcardReviewCriteria(FlashcardReviewCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.nextReviewAt = other.optionalNextReviewAt().map(InstantFilter::copy).orElse(null);
        this.intervalDays = other.optionalIntervalDays().map(BigDecimalFilter::copy).orElse(null);
        this.studentId = other.optionalStudentId().map(LongFilter::copy).orElse(null);
        this.flashcardId = other.optionalFlashcardId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public FlashcardReviewCriteria copy() {
        return new FlashcardReviewCriteria(this);
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

    public InstantFilter getNextReviewAt() {
        return nextReviewAt;
    }

    public Optional<InstantFilter> optionalNextReviewAt() {
        return Optional.ofNullable(nextReviewAt);
    }

    public InstantFilter nextReviewAt() {
        if (nextReviewAt == null) {
            setNextReviewAt(new InstantFilter());
        }
        return nextReviewAt;
    }

    public void setNextReviewAt(InstantFilter nextReviewAt) {
        this.nextReviewAt = nextReviewAt;
    }

    public BigDecimalFilter getIntervalDays() {
        return intervalDays;
    }

    public Optional<BigDecimalFilter> optionalIntervalDays() {
        return Optional.ofNullable(intervalDays);
    }

    public BigDecimalFilter intervalDays() {
        if (intervalDays == null) {
            setIntervalDays(new BigDecimalFilter());
        }
        return intervalDays;
    }

    public void setIntervalDays(BigDecimalFilter intervalDays) {
        this.intervalDays = intervalDays;
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

    public LongFilter getFlashcardId() {
        return flashcardId;
    }

    public Optional<LongFilter> optionalFlashcardId() {
        return Optional.ofNullable(flashcardId);
    }

    public LongFilter flashcardId() {
        if (flashcardId == null) {
            setFlashcardId(new LongFilter());
        }
        return flashcardId;
    }

    public void setFlashcardId(LongFilter flashcardId) {
        this.flashcardId = flashcardId;
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
        final FlashcardReviewCriteria that = (FlashcardReviewCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(nextReviewAt, that.nextReviewAt) &&
            Objects.equals(intervalDays, that.intervalDays) &&
            Objects.equals(studentId, that.studentId) &&
            Objects.equals(flashcardId, that.flashcardId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nextReviewAt, intervalDays, studentId, flashcardId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FlashcardReviewCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalNextReviewAt().map(f -> "nextReviewAt=" + f + ", ").orElse("") +
            optionalIntervalDays().map(f -> "intervalDays=" + f + ", ").orElse("") +
            optionalStudentId().map(f -> "studentId=" + f + ", ").orElse("") +
            optionalFlashcardId().map(f -> "flashcardId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
