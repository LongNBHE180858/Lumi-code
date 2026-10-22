package com.lumi.service.criteria;

import com.lumi.domain.enumeration.RecordStatus;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.domain.Exam} entity. This class is used
 * in {@link com.lumi.web.rest.ExamResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /exams?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ExamCriteria implements Serializable, Criteria {

    /**
     * Class for filtering RecordStatus
     */
    public static class RecordStatusFilter extends Filter<RecordStatus> {

        public RecordStatusFilter() {}

        public RecordStatusFilter(RecordStatusFilter filter) {
            super(filter);
        }

        @Override
        public RecordStatusFilter copy() {
            return new RecordStatusFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter title;

    private IntegerFilter durationMinutes;

    private BigDecimalFilter passScore;

    private IntegerFilter maxAttempts;

    private BooleanFilter shuffleQuestions;

    private RecordStatusFilter status;

    private BooleanFilter isDeleted;

    private LongFilter studyClassId;

    private Boolean distinct;

    public ExamCriteria() {}

    public ExamCriteria(ExamCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.title = other.optionalTitle().map(StringFilter::copy).orElse(null);
        this.durationMinutes = other.optionalDurationMinutes().map(IntegerFilter::copy).orElse(null);
        this.passScore = other.optionalPassScore().map(BigDecimalFilter::copy).orElse(null);
        this.maxAttempts = other.optionalMaxAttempts().map(IntegerFilter::copy).orElse(null);
        this.shuffleQuestions = other.optionalShuffleQuestions().map(BooleanFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(RecordStatusFilter::copy).orElse(null);
        this.isDeleted = other.optionalIsDeleted().map(BooleanFilter::copy).orElse(null);
        this.studyClassId = other.optionalStudyClassId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ExamCriteria copy() {
        return new ExamCriteria(this);
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

    public IntegerFilter getDurationMinutes() {
        return durationMinutes;
    }

    public Optional<IntegerFilter> optionalDurationMinutes() {
        return Optional.ofNullable(durationMinutes);
    }

    public IntegerFilter durationMinutes() {
        if (durationMinutes == null) {
            setDurationMinutes(new IntegerFilter());
        }
        return durationMinutes;
    }

    public void setDurationMinutes(IntegerFilter durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public BigDecimalFilter getPassScore() {
        return passScore;
    }

    public Optional<BigDecimalFilter> optionalPassScore() {
        return Optional.ofNullable(passScore);
    }

    public BigDecimalFilter passScore() {
        if (passScore == null) {
            setPassScore(new BigDecimalFilter());
        }
        return passScore;
    }

    public void setPassScore(BigDecimalFilter passScore) {
        this.passScore = passScore;
    }

    public IntegerFilter getMaxAttempts() {
        return maxAttempts;
    }

    public Optional<IntegerFilter> optionalMaxAttempts() {
        return Optional.ofNullable(maxAttempts);
    }

    public IntegerFilter maxAttempts() {
        if (maxAttempts == null) {
            setMaxAttempts(new IntegerFilter());
        }
        return maxAttempts;
    }

    public void setMaxAttempts(IntegerFilter maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public BooleanFilter getShuffleQuestions() {
        return shuffleQuestions;
    }

    public Optional<BooleanFilter> optionalShuffleQuestions() {
        return Optional.ofNullable(shuffleQuestions);
    }

    public BooleanFilter shuffleQuestions() {
        if (shuffleQuestions == null) {
            setShuffleQuestions(new BooleanFilter());
        }
        return shuffleQuestions;
    }

    public void setShuffleQuestions(BooleanFilter shuffleQuestions) {
        this.shuffleQuestions = shuffleQuestions;
    }

    public RecordStatusFilter getStatus() {
        return status;
    }

    public Optional<RecordStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public RecordStatusFilter status() {
        if (status == null) {
            setStatus(new RecordStatusFilter());
        }
        return status;
    }

    public void setStatus(RecordStatusFilter status) {
        this.status = status;
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
        final ExamCriteria that = (ExamCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(title, that.title) &&
            Objects.equals(durationMinutes, that.durationMinutes) &&
            Objects.equals(passScore, that.passScore) &&
            Objects.equals(maxAttempts, that.maxAttempts) &&
            Objects.equals(shuffleQuestions, that.shuffleQuestions) &&
            Objects.equals(status, that.status) &&
            Objects.equals(isDeleted, that.isDeleted) &&
            Objects.equals(studyClassId, that.studyClassId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            title,
            durationMinutes,
            passScore,
            maxAttempts,
            shuffleQuestions,
            status,
            isDeleted,
            studyClassId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ExamCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalTitle().map(f -> "title=" + f + ", ").orElse("") +
            optionalDurationMinutes().map(f -> "durationMinutes=" + f + ", ").orElse("") +
            optionalPassScore().map(f -> "passScore=" + f + ", ").orElse("") +
            optionalMaxAttempts().map(f -> "maxAttempts=" + f + ", ").orElse("") +
            optionalShuffleQuestions().map(f -> "shuffleQuestions=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalIsDeleted().map(f -> "isDeleted=" + f + ", ").orElse("") +
            optionalStudyClassId().map(f -> "studyClassId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
