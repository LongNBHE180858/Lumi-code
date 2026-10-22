package com.lumi.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.domain.ExamQuestion} entity. This class is used
 * in {@link com.lumi.web.rest.ExamQuestionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /exam-questions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ExamQuestionCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter orderIndex;

    private BigDecimalFilter score;

    private LongFilter examId;

    private LongFilter questionId;

    private Boolean distinct;

    public ExamQuestionCriteria() {}

    public ExamQuestionCriteria(ExamQuestionCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.orderIndex = other.optionalOrderIndex().map(IntegerFilter::copy).orElse(null);
        this.score = other.optionalScore().map(BigDecimalFilter::copy).orElse(null);
        this.examId = other.optionalExamId().map(LongFilter::copy).orElse(null);
        this.questionId = other.optionalQuestionId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ExamQuestionCriteria copy() {
        return new ExamQuestionCriteria(this);
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

    public IntegerFilter getOrderIndex() {
        return orderIndex;
    }

    public Optional<IntegerFilter> optionalOrderIndex() {
        return Optional.ofNullable(orderIndex);
    }

    public IntegerFilter orderIndex() {
        if (orderIndex == null) {
            setOrderIndex(new IntegerFilter());
        }
        return orderIndex;
    }

    public void setOrderIndex(IntegerFilter orderIndex) {
        this.orderIndex = orderIndex;
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

    public LongFilter getQuestionId() {
        return questionId;
    }

    public Optional<LongFilter> optionalQuestionId() {
        return Optional.ofNullable(questionId);
    }

    public LongFilter questionId() {
        if (questionId == null) {
            setQuestionId(new LongFilter());
        }
        return questionId;
    }

    public void setQuestionId(LongFilter questionId) {
        this.questionId = questionId;
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
        final ExamQuestionCriteria that = (ExamQuestionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(orderIndex, that.orderIndex) &&
            Objects.equals(score, that.score) &&
            Objects.equals(examId, that.examId) &&
            Objects.equals(questionId, that.questionId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orderIndex, score, examId, questionId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ExamQuestionCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalOrderIndex().map(f -> "orderIndex=" + f + ", ").orElse("") +
            optionalScore().map(f -> "score=" + f + ", ").orElse("") +
            optionalExamId().map(f -> "examId=" + f + ", ").orElse("") +
            optionalQuestionId().map(f -> "questionId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
