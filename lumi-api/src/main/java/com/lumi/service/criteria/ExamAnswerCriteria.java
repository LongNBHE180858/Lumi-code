package com.lumi.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.domain.ExamAnswer} entity. This class is used
 * in {@link com.lumi.web.rest.ExamAnswerResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /exam-answers?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ExamAnswerCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter textAnswer;

    private BooleanFilter isCorrect;

    private BigDecimalFilter score;

    private LongFilter attemptId;

    private LongFilter questionId;

    private LongFilter optionId;

    private Boolean distinct;

    public ExamAnswerCriteria() {}

    public ExamAnswerCriteria(ExamAnswerCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.textAnswer = other.optionalTextAnswer().map(StringFilter::copy).orElse(null);
        this.isCorrect = other.optionalIsCorrect().map(BooleanFilter::copy).orElse(null);
        this.score = other.optionalScore().map(BigDecimalFilter::copy).orElse(null);
        this.attemptId = other.optionalAttemptId().map(LongFilter::copy).orElse(null);
        this.questionId = other.optionalQuestionId().map(LongFilter::copy).orElse(null);
        this.optionId = other.optionalOptionId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ExamAnswerCriteria copy() {
        return new ExamAnswerCriteria(this);
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

    public StringFilter getTextAnswer() {
        return textAnswer;
    }

    public Optional<StringFilter> optionalTextAnswer() {
        return Optional.ofNullable(textAnswer);
    }

    public StringFilter textAnswer() {
        if (textAnswer == null) {
            setTextAnswer(new StringFilter());
        }
        return textAnswer;
    }

    public void setTextAnswer(StringFilter textAnswer) {
        this.textAnswer = textAnswer;
    }

    public BooleanFilter getIsCorrect() {
        return isCorrect;
    }

    public Optional<BooleanFilter> optionalIsCorrect() {
        return Optional.ofNullable(isCorrect);
    }

    public BooleanFilter isCorrect() {
        if (isCorrect == null) {
            setIsCorrect(new BooleanFilter());
        }
        return isCorrect;
    }

    public void setIsCorrect(BooleanFilter isCorrect) {
        this.isCorrect = isCorrect;
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

    public LongFilter getOptionId() {
        return optionId;
    }

    public Optional<LongFilter> optionalOptionId() {
        return Optional.ofNullable(optionId);
    }

    public LongFilter optionId() {
        if (optionId == null) {
            setOptionId(new LongFilter());
        }
        return optionId;
    }

    public void setOptionId(LongFilter optionId) {
        this.optionId = optionId;
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
        final ExamAnswerCriteria that = (ExamAnswerCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(textAnswer, that.textAnswer) &&
            Objects.equals(isCorrect, that.isCorrect) &&
            Objects.equals(score, that.score) &&
            Objects.equals(attemptId, that.attemptId) &&
            Objects.equals(questionId, that.questionId) &&
            Objects.equals(optionId, that.optionId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, textAnswer, isCorrect, score, attemptId, questionId, optionId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ExamAnswerCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalTextAnswer().map(f -> "textAnswer=" + f + ", ").orElse("") +
            optionalIsCorrect().map(f -> "isCorrect=" + f + ", ").orElse("") +
            optionalScore().map(f -> "score=" + f + ", ").orElse("") +
            optionalAttemptId().map(f -> "attemptId=" + f + ", ").orElse("") +
            optionalQuestionId().map(f -> "questionId=" + f + ", ").orElse("") +
            optionalOptionId().map(f -> "optionId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
