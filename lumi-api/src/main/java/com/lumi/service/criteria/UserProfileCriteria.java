package com.lumi.service.criteria;

import com.lumi.domain.enumeration.InstructorApplicationStatus;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.domain.UserProfile} entity. This class is used
 * in {@link com.lumi.web.rest.UserProfileResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /user-profiles?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserProfileCriteria implements Serializable, Criteria {

    /**
     * Class for filtering instructor application status.
     */
    public static class InstructorApplicationStatusFilter extends Filter<InstructorApplicationStatus> {

        public InstructorApplicationStatusFilter() {}

        public InstructorApplicationStatusFilter(InstructorApplicationStatusFilter filter) {
            super(filter);
        }

        @Override
        public InstructorApplicationStatusFilter copy() {
            return new InstructorApplicationStatusFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter phoneNumber;

    private StringFilter schoolName;

    private IntegerFilter gradeLevel;

    private BooleanFilter is2faEnabled;

    private InstructorApplicationStatusFilter instructorStatus;

    private InstantFilter updatedAt;

    private LocalDateFilter birthDate;

    private LongFilter userId;

    private Boolean distinct;

    public UserProfileCriteria() {}

    public UserProfileCriteria(UserProfileCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.phoneNumber = other.optionalPhoneNumber().map(StringFilter::copy).orElse(null);
        this.schoolName = other.optionalSchoolName().map(StringFilter::copy).orElse(null);
        this.gradeLevel = other.optionalGradeLevel().map(IntegerFilter::copy).orElse(null);
        this.is2faEnabled = other.optionalIs2faEnabled().map(BooleanFilter::copy).orElse(null);
        this.instructorStatus = other.optionalInstructorStatus().map(InstructorApplicationStatusFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.birthDate = other.optionalBirthDate().map(LocalDateFilter::copy).orElse(null);
        this.userId = other.optionalUserId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public UserProfileCriteria copy() {
        return new UserProfileCriteria(this);
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

    public StringFilter getPhoneNumber() {
        return phoneNumber;
    }

    public Optional<StringFilter> optionalPhoneNumber() {
        return Optional.ofNullable(phoneNumber);
    }

    public StringFilter phoneNumber() {
        if (phoneNumber == null) {
            setPhoneNumber(new StringFilter());
        }
        return phoneNumber;
    }

    public void setPhoneNumber(StringFilter phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public StringFilter getSchoolName() {
        return schoolName;
    }

    public Optional<StringFilter> optionalSchoolName() {
        return Optional.ofNullable(schoolName);
    }

    public StringFilter schoolName() {
        if (schoolName == null) {
            setSchoolName(new StringFilter());
        }
        return schoolName;
    }

    public void setSchoolName(StringFilter schoolName) {
        this.schoolName = schoolName;
    }

    public IntegerFilter getGradeLevel() {
        return gradeLevel;
    }

    public Optional<IntegerFilter> optionalGradeLevel() {
        return Optional.ofNullable(gradeLevel);
    }

    public IntegerFilter gradeLevel() {
        if (gradeLevel == null) {
            setGradeLevel(new IntegerFilter());
        }
        return gradeLevel;
    }

    public void setGradeLevel(IntegerFilter gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public BooleanFilter getIs2faEnabled() {
        return is2faEnabled;
    }

    public Optional<BooleanFilter> optionalIs2faEnabled() {
        return Optional.ofNullable(is2faEnabled);
    }

    public BooleanFilter is2faEnabled() {
        if (is2faEnabled == null) {
            setIs2faEnabled(new BooleanFilter());
        }
        return is2faEnabled;
    }

    public void setIs2faEnabled(BooleanFilter is2faEnabled) {
        this.is2faEnabled = is2faEnabled;
    }

    public InstructorApplicationStatusFilter getInstructorStatus() {
        return instructorStatus;
    }

    public Optional<InstructorApplicationStatusFilter> optionalInstructorStatus() {
        return Optional.ofNullable(instructorStatus);
    }

    public InstructorApplicationStatusFilter instructorStatus() {
        if (instructorStatus == null) {
            setInstructorStatus(new InstructorApplicationStatusFilter());
        }
        return instructorStatus;
    }

    public void setInstructorStatus(InstructorApplicationStatusFilter instructorStatus) {
        this.instructorStatus = instructorStatus;
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

    public LocalDateFilter getBirthDate() {
        return birthDate;
    }

    public Optional<LocalDateFilter> optionalBirthDate() {
        return Optional.ofNullable(birthDate);
    }

    public LocalDateFilter birthDate() {
        if (birthDate == null) {
            setBirthDate(new LocalDateFilter());
        }
        return birthDate;
    }

    public void setBirthDate(LocalDateFilter birthDate) {
        this.birthDate = birthDate;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public Optional<LongFilter> optionalUserId() {
        return Optional.ofNullable(userId);
    }

    public LongFilter userId() {
        if (userId == null) {
            setUserId(new LongFilter());
        }
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
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
        final UserProfileCriteria that = (UserProfileCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(phoneNumber, that.phoneNumber) &&
            Objects.equals(schoolName, that.schoolName) &&
            Objects.equals(gradeLevel, that.gradeLevel) &&
            Objects.equals(is2faEnabled, that.is2faEnabled) &&
            Objects.equals(instructorStatus, that.instructorStatus) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(birthDate, that.birthDate) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            phoneNumber,
            schoolName,
            gradeLevel,
            is2faEnabled,
            instructorStatus,
            updatedAt,
            birthDate,
            userId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserProfileCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalPhoneNumber().map(f -> "phoneNumber=" + f + ", ").orElse("") +
            optionalSchoolName().map(f -> "schoolName=" + f + ", ").orElse("") +
            optionalGradeLevel().map(f -> "gradeLevel=" + f + ", ").orElse("") +
            optionalIs2faEnabled().map(f -> "is2faEnabled=" + f + ", ").orElse("") +
            optionalInstructorStatus().map(f -> "instructorStatus=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalBirthDate().map(f -> "birthDate=" + f + ", ").orElse("") +
            optionalUserId().map(f -> "userId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
