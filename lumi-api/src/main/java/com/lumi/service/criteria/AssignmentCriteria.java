package com.lumi.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.domain.Assignment} entity. This class is used
 * in {@link com.lumi.web.rest.AssignmentResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /assignments?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AssignmentCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter title;

    private StringFilter instructions;

    private InstantFilter dueAt;

    private BigDecimalFilter maxScore;

    private BooleanFilter allowLate;

    private BooleanFilter isDeleted;

    private LongFilter studyClassId;

    private Boolean distinct;

    public AssignmentCriteria() {}

    public AssignmentCriteria(AssignmentCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.title = other.optionalTitle().map(StringFilter::copy).orElse(null);
        this.instructions = other.optionalInstructions().map(StringFilter::copy).orElse(null);
        this.dueAt = other.optionalDueAt().map(InstantFilter::copy).orElse(null);
        this.maxScore = other.optionalMaxScore().map(BigDecimalFilter::copy).orElse(null);
        this.allowLate = other.optionalAllowLate().map(BooleanFilter::copy).orElse(null);
        this.isDeleted = other.optionalIsDeleted().map(BooleanFilter::copy).orElse(null);
        this.studyClassId = other.optionalStudyClassId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public AssignmentCriteria copy() {
        return new AssignmentCriteria(this);
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

    public StringFilter getTitle() {
        return title;
    }

    public Optional<StringFilter> optionalTitle() {
        return Optional.ofNullable(title);
    }

    public StringFilter title() {
        if (title == null) {
            setTitle(new StringFilter());
        }
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
    }

    public StringFilter getInstructions() {
        return instructions;
    }

    public Optional<StringFilter> optionalInstructions() {
        return Optional.ofNullable(instructions);
    }

    public StringFilter instructions() {
        if (instructions == null) {
            setInstructions(new StringFilter());
        }
        return instructions;
    }

    public void setInstructions(StringFilter instructions) {
        this.instructions = instructions;
    }

    public InstantFilter getDueAt() {
        return dueAt;
    }

    public Optional<InstantFilter> optionalDueAt() {
        return Optional.ofNullable(dueAt);
    }

    public InstantFilter dueAt() {
        if (dueAt == null) {
            setDueAt(new InstantFilter());
        }
        return dueAt;
    }

    public void setDueAt(InstantFilter dueAt) {
        this.dueAt = dueAt;
    }

    public BigDecimalFilter getMaxScore() {
        return maxScore;
    }

    public Optional<BigDecimalFilter> optionalMaxScore() {
        return Optional.ofNullable(maxScore);
    }

    public BigDecimalFilter maxScore() {
        if (maxScore == null) {
            setMaxScore(new BigDecimalFilter());
        }
        return maxScore;
    }

    public void setMaxScore(BigDecimalFilter maxScore) {
        this.maxScore = maxScore;
    }

    public BooleanFilter getAllowLate() {
        return allowLate;
    }

    public Optional<BooleanFilter> optionalAllowLate() {
        return Optional.ofNullable(allowLate);
    }

    public BooleanFilter allowLate() {
        if (allowLate == null) {
            setAllowLate(new BooleanFilter());
        }
        return allowLate;
    }

    public void setAllowLate(BooleanFilter allowLate) {
        this.allowLate = allowLate;
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

    public LongFilter getStudyClassId() {
        return studyClassId;
    }

    public Optional<LongFilter> optionalStudyClassId() {
        return Optional.ofNullable(studyClassId);
    }

    public LongFilter studyClassId() {
        if (studyClassId == null) {
            setStudyClassId(new LongFilter());
        }
        return studyClassId;
    }

    public void setStudyClassId(LongFilter studyClassId) {
        this.studyClassId = studyClassId;
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
        final AssignmentCriteria that = (AssignmentCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(title, that.title) &&
            Objects.equals(instructions, that.instructions) &&
            Objects.equals(dueAt, that.dueAt) &&
            Objects.equals(maxScore, that.maxScore) &&
            Objects.equals(allowLate, that.allowLate) &&
            Objects.equals(isDeleted, that.isDeleted) &&
            Objects.equals(studyClassId, that.studyClassId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, instructions, dueAt, maxScore, allowLate, isDeleted, studyClassId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AssignmentCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalTitle().map(f -> "title=" + f + ", ").orElse("") +
            optionalInstructions().map(f -> "instructions=" + f + ", ").orElse("") +
            optionalDueAt().map(f -> "dueAt=" + f + ", ").orElse("") +
            optionalMaxScore().map(f -> "maxScore=" + f + ", ").orElse("") +
            optionalAllowLate().map(f -> "allowLate=" + f + ", ").orElse("") +
            optionalIsDeleted().map(f -> "isDeleted=" + f + ", ").orElse("") +
            optionalStudyClassId().map(f -> "studyClassId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
