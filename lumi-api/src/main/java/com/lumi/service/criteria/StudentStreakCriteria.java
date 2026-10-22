package com.lumi.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.domain.StudentStreak} entity. This class is used
 * in {@link com.lumi.web.rest.StudentStreakResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /student-streaks?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StudentStreakCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter currentStreak;

    private IntegerFilter longestStreak;

    private IntegerFilter gemsBalance;

    private LocalDateFilter lastActiveDate;

    private LongFilter userId;

    private Boolean distinct;

    public StudentStreakCriteria() {}

    public StudentStreakCriteria(StudentStreakCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.currentStreak = other.optionalCurrentStreak().map(IntegerFilter::copy).orElse(null);
        this.longestStreak = other.optionalLongestStreak().map(IntegerFilter::copy).orElse(null);
        this.gemsBalance = other.optionalGemsBalance().map(IntegerFilter::copy).orElse(null);
        this.lastActiveDate = other.optionalLastActiveDate().map(LocalDateFilter::copy).orElse(null);
        this.userId = other.optionalUserId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public StudentStreakCriteria copy() {
        return new StudentStreakCriteria(this);
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

    public IntegerFilter getCurrentStreak() {
        return currentStreak;
    }

    public Optional<IntegerFilter> optionalCurrentStreak() {
        return Optional.ofNullable(currentStreak);
    }

    public IntegerFilter currentStreak() {
        if (currentStreak == null) {
            setCurrentStreak(new IntegerFilter());
        }
        return currentStreak;
    }

    public void setCurrentStreak(IntegerFilter currentStreak) {
        this.currentStreak = currentStreak;
    }

    public IntegerFilter getLongestStreak() {
        return longestStreak;
    }

    public Optional<IntegerFilter> optionalLongestStreak() {
        return Optional.ofNullable(longestStreak);
    }

    public IntegerFilter longestStreak() {
        if (longestStreak == null) {
            setLongestStreak(new IntegerFilter());
        }
        return longestStreak;
    }

    public void setLongestStreak(IntegerFilter longestStreak) {
        this.longestStreak = longestStreak;
    }

    public IntegerFilter getGemsBalance() {
        return gemsBalance;
    }

    public Optional<IntegerFilter> optionalGemsBalance() {
        return Optional.ofNullable(gemsBalance);
    }

    public IntegerFilter gemsBalance() {
        if (gemsBalance == null) {
            setGemsBalance(new IntegerFilter());
        }
        return gemsBalance;
    }

    public void setGemsBalance(IntegerFilter gemsBalance) {
        this.gemsBalance = gemsBalance;
    }

    public LocalDateFilter getLastActiveDate() {
        return lastActiveDate;
    }

    public Optional<LocalDateFilter> optionalLastActiveDate() {
        return Optional.ofNullable(lastActiveDate);
    }

    public LocalDateFilter lastActiveDate() {
        if (lastActiveDate == null) {
            setLastActiveDate(new LocalDateFilter());
        }
        return lastActiveDate;
    }

    public void setLastActiveDate(LocalDateFilter lastActiveDate) {
        this.lastActiveDate = lastActiveDate;
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
        final StudentStreakCriteria that = (StudentStreakCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(currentStreak, that.currentStreak) &&
            Objects.equals(longestStreak, that.longestStreak) &&
            Objects.equals(gemsBalance, that.gemsBalance) &&
            Objects.equals(lastActiveDate, that.lastActiveDate) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, currentStreak, longestStreak, gemsBalance, lastActiveDate, userId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StudentStreakCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCurrentStreak().map(f -> "currentStreak=" + f + ", ").orElse("") +
            optionalLongestStreak().map(f -> "longestStreak=" + f + ", ").orElse("") +
            optionalGemsBalance().map(f -> "gemsBalance=" + f + ", ").orElse("") +
            optionalLastActiveDate().map(f -> "lastActiveDate=" + f + ", ").orElse("") +
            optionalUserId().map(f -> "userId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
