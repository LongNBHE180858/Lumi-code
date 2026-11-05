package com.lumi.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.domain.StudentKnowledgeProgress} entity. This class is used
 * in {@link com.lumi.web.rest.StudentKnowledgeProgressResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /student-knowledge-progresses?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StudentKnowledgeProgressCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private BigDecimalFilter masteryScore;

    private IntegerFilter evidenceCount;

    private StringFilter calculationVersion;

    private InstantFilter updatedAt;

    private LongFilter studentId;

    private LongFilter knowledgeNodeId;

    private Boolean distinct;

    public StudentKnowledgeProgressCriteria() {}

    public StudentKnowledgeProgressCriteria(StudentKnowledgeProgressCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.masteryScore = other.optionalMasteryScore().map(BigDecimalFilter::copy).orElse(null);
        this.evidenceCount = other.optionalEvidenceCount().map(IntegerFilter::copy).orElse(null);
        this.calculationVersion = other.optionalCalculationVersion().map(StringFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.studentId = other.optionalStudentId().map(LongFilter::copy).orElse(null);
        this.knowledgeNodeId = other.optionalKnowledgeNodeId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public StudentKnowledgeProgressCriteria copy() {
        return new StudentKnowledgeProgressCriteria(this);
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

    public BigDecimalFilter getMasteryScore() {
        return masteryScore;
    }

    public Optional<BigDecimalFilter> optionalMasteryScore() {
        return Optional.ofNullable(masteryScore);
    }

    public BigDecimalFilter masteryScore() {
        if (masteryScore == null) {
            setMasteryScore(new BigDecimalFilter());
        }
        return masteryScore;
    }

    public void setMasteryScore(BigDecimalFilter masteryScore) {
        this.masteryScore = masteryScore;
    }

    public IntegerFilter getEvidenceCount() {
        return evidenceCount;
    }

    public Optional<IntegerFilter> optionalEvidenceCount() {
        return Optional.ofNullable(evidenceCount);
    }

    public IntegerFilter evidenceCount() {
        if (evidenceCount == null) {
            setEvidenceCount(new IntegerFilter());
        }
        return evidenceCount;
    }

    public void setEvidenceCount(IntegerFilter evidenceCount) {
        this.evidenceCount = evidenceCount;
    }

    public StringFilter getCalculationVersion() {
        return calculationVersion;
    }

    public Optional<StringFilter> optionalCalculationVersion() {
        return Optional.ofNullable(calculationVersion);
    }

    public StringFilter calculationVersion() {
        if (calculationVersion == null) {
            setCalculationVersion(new StringFilter());
        }
        return calculationVersion;
    }

    public void setCalculationVersion(StringFilter calculationVersion) {
        this.calculationVersion = calculationVersion;
    }

    public InstantFilter getUpdatedAt() {
        return updatedAt;
    }

    public Optional<InstantFilter> optionalUpdatedAt() {
        return Optional.ofNullable(updatedAt);
    }

    public InstantFilter updatedAt() {
        if (updatedAt == null) {
            setUpdatedAt(new InstantFilter());
        }
        return updatedAt;
    }

    public void setUpdatedAt(InstantFilter updatedAt) {
        this.updatedAt = updatedAt;
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
        final StudentKnowledgeProgressCriteria that = (StudentKnowledgeProgressCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(masteryScore, that.masteryScore) &&
            Objects.equals(evidenceCount, that.evidenceCount) &&
            Objects.equals(calculationVersion, that.calculationVersion) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(studentId, that.studentId) &&
            Objects.equals(knowledgeNodeId, that.knowledgeNodeId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, masteryScore, evidenceCount, calculationVersion, updatedAt, studentId, knowledgeNodeId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StudentKnowledgeProgressCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalMasteryScore().map(f -> "masteryScore=" + f + ", ").orElse("") +
            optionalEvidenceCount().map(f -> "evidenceCount=" + f + ", ").orElse("") +
            optionalCalculationVersion().map(f -> "calculationVersion=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalStudentId().map(f -> "studentId=" + f + ", ").orElse("") +
            optionalKnowledgeNodeId().map(f -> "knowledgeNodeId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
