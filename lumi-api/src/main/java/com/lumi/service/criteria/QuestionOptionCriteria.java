package com.lumi.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.domain.QuestionOption} entity. This class is used
 * in {@link com.lumi.web.rest.QuestionOptionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /question-options?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class QuestionOptionCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter content;

    private BooleanFilter isCorrect;

    private LongFilter questionId;

    private Boolean distinct;

    public QuestionOptionCriteria() {}

    public QuestionOptionCriteria(QuestionOptionCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.content = other.optionalContent().map(StringFilter::copy).orElse(null);
        this.isCorrect = other.optionalIsCorrect().map(BooleanFilter::copy).orElse(null);
        this.questionId = other.optionalQuestionId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public QuestionOptionCriteria copy() {
        return new QuestionOptionCriteria(this);
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
        final QuestionOptionCriteria that = (QuestionOptionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(content, that.content) &&
            Objects.equals(isCorrect, that.isCorrect) &&
            Objects.equals(questionId, that.questionId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, content, isCorrect, questionId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "QuestionOptionCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalContent().map(f -> "content=" + f + ", ").orElse("") +
            optionalIsCorrect().map(f -> "isCorrect=" + f + ", ").orElse("") +
            optionalQuestionId().map(f -> "questionId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
