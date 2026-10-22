package com.lumi.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.domain.Question} entity. This class is used
 * in {@link com.lumi.web.rest.QuestionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /questions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class QuestionCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter content;

    private StringFilter type;

    private IntegerFilter difficulty;

    private StringFilter explanation;

    private BooleanFilter isDeleted;

    private LongFilter bankId;

    private Boolean distinct;

    public QuestionCriteria() {}

    public QuestionCriteria(QuestionCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.content = other.optionalContent().map(StringFilter::copy).orElse(null);
        this.type = other.optionalType().map(StringFilter::copy).orElse(null);
        this.difficulty = other.optionalDifficulty().map(IntegerFilter::copy).orElse(null);
        this.explanation = other.optionalExplanation().map(StringFilter::copy).orElse(null);
        this.isDeleted = other.optionalIsDeleted().map(BooleanFilter::copy).orElse(null);
        this.bankId = other.optionalBankId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public QuestionCriteria copy() {
        return new QuestionCriteria(this);
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

    public StringFilter getType() {
        return type;
    }

    public Optional<StringFilter> optionalType() {
        return Optional.ofNullable(type);
    }

    public StringFilter type() {
        if (type == null) {
            setType(new StringFilter());
        }
        return type;
    }

    public void setType(StringFilter type) {
        this.type = type;
    }

    public IntegerFilter getDifficulty() {
        return difficulty;
    }

    public Optional<IntegerFilter> optionalDifficulty() {
        return Optional.ofNullable(difficulty);
    }

    public IntegerFilter difficulty() {
        if (difficulty == null) {
            setDifficulty(new IntegerFilter());
        }
        return difficulty;
    }

    public void setDifficulty(IntegerFilter difficulty) {
        this.difficulty = difficulty;
    }

    public StringFilter getExplanation() {
        return explanation;
    }

    public Optional<StringFilter> optionalExplanation() {
        return Optional.ofNullable(explanation);
    }

    public StringFilter explanation() {
        if (explanation == null) {
            setExplanation(new StringFilter());
        }
        return explanation;
    }

    public void setExplanation(StringFilter explanation) {
        this.explanation = explanation;
    }

    public BooleanFilter getIsDeleted() {
        return isDeleted;
    }

    public Optional<BooleanFilter> optionalIsDeleted() {
        return Optional.ofNullable(isDeleted);
    }

    public BooleanFilter isDeleted() {
        if (isDeleted == null) {
            setIsDeleted(new BooleanFilter());
        }
        return isDeleted;
    }

    public void setIsDeleted(BooleanFilter isDeleted) {
        this.isDeleted = isDeleted;
    }

    public LongFilter getBankId() {
        return bankId;
    }

    public Optional<LongFilter> optionalBankId() {
        return Optional.ofNullable(bankId);
    }

    public LongFilter bankId() {
        if (bankId == null) {
            setBankId(new LongFilter());
        }
        return bankId;
    }

    public void setBankId(LongFilter bankId) {
        this.bankId = bankId;
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
        final QuestionCriteria that = (QuestionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(content, that.content) &&
            Objects.equals(type, that.type) &&
            Objects.equals(difficulty, that.difficulty) &&
            Objects.equals(explanation, that.explanation) &&
            Objects.equals(isDeleted, that.isDeleted) &&
            Objects.equals(bankId, that.bankId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, content, type, difficulty, explanation, isDeleted, bankId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "QuestionCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalContent().map(f -> "content=" + f + ", ").orElse("") +
            optionalType().map(f -> "type=" + f + ", ").orElse("") +
            optionalDifficulty().map(f -> "difficulty=" + f + ", ").orElse("") +
            optionalExplanation().map(f -> "explanation=" + f + ", ").orElse("") +
            optionalIsDeleted().map(f -> "isDeleted=" + f + ", ").orElse("") +
            optionalBankId().map(f -> "bankId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
