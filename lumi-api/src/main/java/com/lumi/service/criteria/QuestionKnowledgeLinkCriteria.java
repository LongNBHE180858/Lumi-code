package com.lumi.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.domain.QuestionKnowledgeLink} entity. This class is used
 * in {@link com.lumi.web.rest.QuestionKnowledgeLinkResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /question-knowledge-links?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class QuestionKnowledgeLinkCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private BigDecimalFilter weight;

    private LongFilter questionId;

    private LongFilter knowledgeNodeId;

    private Boolean distinct;

    public QuestionKnowledgeLinkCriteria() {}

    public QuestionKnowledgeLinkCriteria(QuestionKnowledgeLinkCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.weight = other.optionalWeight().map(BigDecimalFilter::copy).orElse(null);
        this.questionId = other.optionalQuestionId().map(LongFilter::copy).orElse(null);
        this.knowledgeNodeId = other.optionalKnowledgeNodeId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public QuestionKnowledgeLinkCriteria copy() {
        return new QuestionKnowledgeLinkCriteria(this);
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

    public BigDecimalFilter getWeight() {
        return weight;
    }

    public Optional<BigDecimalFilter> optionalWeight() {
        return Optional.ofNullable(weight);
    }

    public BigDecimalFilter weight() {
        if (weight == null) {
            setWeight(new BigDecimalFilter());
        }
        return weight;
    }

    public void setWeight(BigDecimalFilter weight) {
        this.weight = weight;
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

    public LongFilter getKnowledgeNodeId() {
        return knowledgeNodeId;
    }

    public Optional<LongFilter> optionalKnowledgeNodeId() {
        return Optional.ofNullable(knowledgeNodeId);
    }

    public LongFilter knowledgeNodeId() {
        if (knowledgeNodeId == null) {
            setKnowledgeNodeId(new LongFilter());
        }
        return knowledgeNodeId;
    }

    public void setKnowledgeNodeId(LongFilter knowledgeNodeId) {
        this.knowledgeNodeId = knowledgeNodeId;
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
        final QuestionKnowledgeLinkCriteria that = (QuestionKnowledgeLinkCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(weight, that.weight) &&
            Objects.equals(questionId, that.questionId) &&
            Objects.equals(knowledgeNodeId, that.knowledgeNodeId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, weight, questionId, knowledgeNodeId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "QuestionKnowledgeLinkCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalWeight().map(f -> "weight=" + f + ", ").orElse("") +
            optionalQuestionId().map(f -> "questionId=" + f + ", ").orElse("") +
            optionalKnowledgeNodeId().map(f -> "knowledgeNodeId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
