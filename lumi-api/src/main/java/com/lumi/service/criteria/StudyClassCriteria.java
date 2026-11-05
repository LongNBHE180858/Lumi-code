package com.lumi.service.criteria;

import com.lumi.domain.enumeration.ClassJoinMode;
import com.lumi.domain.enumeration.RecordStatus;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.domain.StudyClass} entity. This class is used
 * in {@link com.lumi.web.rest.StudyClassResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /study-classes?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StudyClassCriteria implements Serializable, Criteria {

    /**
     * Class for filtering ClassJoinMode
     */
    public static class ClassJoinModeFilter extends Filter<ClassJoinMode> {

        public ClassJoinModeFilter() {}

        public ClassJoinModeFilter(ClassJoinModeFilter filter) {
            super(filter);
        }

        @Override
        public ClassJoinModeFilter copy() {
            return new ClassJoinModeFilter(this);
        }
    }

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

    private StringFilter name;

    private StringFilter joinCode;

    private ClassJoinModeFilter joinMode;

    private IntegerFilter maxStudents;

    private IntegerFilter currentStudents;

    private RecordStatusFilter status;

    private BooleanFilter isDeleted;

    private InstantFilter createdAt;

    private InstantFilter joinCodeExpiresAt;

    private InstantFilter updatedAt;

    private LongFilter instructorId;

    private LongFilter subjectId;

    private LongFilter gradeId;

    private Boolean distinct;

    public StudyClassCriteria() {}

    public StudyClassCriteria(StudyClassCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.joinCode = other.optionalJoinCode().map(StringFilter::copy).orElse(null);
        this.joinMode = other.optionalJoinMode().map(ClassJoinModeFilter::copy).orElse(null);
        this.maxStudents = other.optionalMaxStudents().map(IntegerFilter::copy).orElse(null);
        this.currentStudents = other.optionalCurrentStudents().map(IntegerFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(RecordStatusFilter::copy).orElse(null);
        this.isDeleted = other.optionalIsDeleted().map(BooleanFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.joinCodeExpiresAt = other.optionalJoinCodeExpiresAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.instructorId = other.optionalInstructorId().map(LongFilter::copy).orElse(null);
        this.subjectId = other.optionalSubjectId().map(LongFilter::copy).orElse(null);
        this.gradeId = other.optionalGradeId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public StudyClassCriteria copy() {
        return new StudyClassCriteria(this);
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

    public StringFilter getName() {
        return name;
    }

    public Optional<StringFilter> optionalName() {
        return Optional.ofNullable(name);
    }

    public StringFilter name() {
        if (name == null) {
            setName(new StringFilter());
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getJoinCode() {
        return joinCode;
    }

    public Optional<StringFilter> optionalJoinCode() {
        return Optional.ofNullable(joinCode);
    }

    public StringFilter joinCode() {
        if (joinCode == null) {
            setJoinCode(new StringFilter());
        }
        return joinCode;
    }

    public void setJoinCode(StringFilter joinCode) {
        this.joinCode = joinCode;
    }

    public ClassJoinModeFilter getJoinMode() {
        return joinMode;
    }

    public Optional<ClassJoinModeFilter> optionalJoinMode() {
        return Optional.ofNullable(joinMode);
    }

    public ClassJoinModeFilter joinMode() {
        if (joinMode == null) {
            setJoinMode(new ClassJoinModeFilter());
        }
        return joinMode;
    }

    public void setJoinMode(ClassJoinModeFilter joinMode) {
        this.joinMode = joinMode;
    }

    public IntegerFilter getMaxStudents() {
        return maxStudents;
    }

    public Optional<IntegerFilter> optionalMaxStudents() {
        return Optional.ofNullable(maxStudents);
    }

    public IntegerFilter maxStudents() {
        if (maxStudents == null) {
            setMaxStudents(new IntegerFilter());
        }
        return maxStudents;
    }

    public void setMaxStudents(IntegerFilter maxStudents) {
        this.maxStudents = maxStudents;
    }

    public IntegerFilter getCurrentStudents() {
        return currentStudents;
    }

    public Optional<IntegerFilter> optionalCurrentStudents() {
        return Optional.ofNullable(currentStudents);
    }

    public IntegerFilter currentStudents() {
        if (currentStudents == null) {
            setCurrentStudents(new IntegerFilter());
        }
        return currentStudents;
    }

    public void setCurrentStudents(IntegerFilter currentStudents) {
        this.currentStudents = currentStudents;
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

    public InstantFilter getCreatedAt() {
        return createdAt;
    }

    public Optional<InstantFilter> optionalCreatedAt() {
        return Optional.ofNullable(createdAt);
    }

    public InstantFilter createdAt() {
        if (createdAt == null) {
            setCreatedAt(new InstantFilter());
        }
        return createdAt;
    }

    public void setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
    }

    public InstantFilter getJoinCodeExpiresAt() {
        return joinCodeExpiresAt;
    }

    public Optional<InstantFilter> optionalJoinCodeExpiresAt() {
        return Optional.ofNullable(joinCodeExpiresAt);
    }

    public InstantFilter joinCodeExpiresAt() {
        if (joinCodeExpiresAt == null) {
            setJoinCodeExpiresAt(new InstantFilter());
        }
        return joinCodeExpiresAt;
    }

    public void setJoinCodeExpiresAt(InstantFilter joinCodeExpiresAt) {
        this.joinCodeExpiresAt = joinCodeExpiresAt;
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

    public LongFilter getInstructorId() {
        return instructorId;
    }

    public Optional<LongFilter> optionalInstructorId() {
        return Optional.ofNullable(instructorId);
    }

    public LongFilter instructorId() {
        if (instructorId == null) {
            setInstructorId(new LongFilter());
        }
        return instructorId;
    }

    public void setInstructorId(LongFilter instructorId) {
        this.instructorId = instructorId;
    }

    public LongFilter getSubjectId() {
        return subjectId;
    }

    public Optional<LongFilter> optionalSubjectId() {
        return Optional.ofNullable(subjectId);
    }

    public LongFilter subjectId() {
        if (subjectId == null) {
            setSubjectId(new LongFilter());
        }
        return subjectId;
    }

    public void setSubjectId(LongFilter subjectId) {
        this.subjectId = subjectId;
    }

    public LongFilter getGradeId() {
        return gradeId;
    }

    public Optional<LongFilter> optionalGradeId() {
        return Optional.ofNullable(gradeId);
    }

    public LongFilter gradeId() {
        if (gradeId == null) {
            setGradeId(new LongFilter());
        }
        return gradeId;
    }

    public void setGradeId(LongFilter gradeId) {
        this.gradeId = gradeId;
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
        final StudyClassCriteria that = (StudyClassCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(joinCode, that.joinCode) &&
            Objects.equals(joinMode, that.joinMode) &&
            Objects.equals(maxStudents, that.maxStudents) &&
            Objects.equals(currentStudents, that.currentStudents) &&
            Objects.equals(status, that.status) &&
            Objects.equals(isDeleted, that.isDeleted) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(joinCodeExpiresAt, that.joinCodeExpiresAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(instructorId, that.instructorId) &&
            Objects.equals(subjectId, that.subjectId) &&
            Objects.equals(gradeId, that.gradeId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            name,
            joinCode,
            joinMode,
            maxStudents,
            currentStudents,
            status,
            isDeleted,
            createdAt,
            joinCodeExpiresAt,
            updatedAt,
            instructorId,
            subjectId,
            gradeId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StudyClassCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalJoinCode().map(f -> "joinCode=" + f + ", ").orElse("") +
            optionalJoinMode().map(f -> "joinMode=" + f + ", ").orElse("") +
            optionalMaxStudents().map(f -> "maxStudents=" + f + ", ").orElse("") +
            optionalCurrentStudents().map(f -> "currentStudents=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalIsDeleted().map(f -> "isDeleted=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalJoinCodeExpiresAt().map(f -> "joinCodeExpiresAt=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalInstructorId().map(f -> "instructorId=" + f + ", ").orElse("") +
            optionalSubjectId().map(f -> "subjectId=" + f + ", ").orElse("") +
            optionalGradeId().map(f -> "gradeId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
