package com.lumi.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.domain.TeacherQuota} entity. This class is used
 * in {@link com.lumi.web.rest.TeacherQuotaResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /teacher-quotas?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TeacherQuotaCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private InstantFilter planExpiresAt;

    private LongFilter usedStorageBytes;

    private IntegerFilter aiUsedCount;

    private LongFilter rowVersion;

    private LongFilter userId;

    private LongFilter currentPlanId;

    private Boolean distinct;

    public TeacherQuotaCriteria() {}

    public TeacherQuotaCriteria(TeacherQuotaCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.planExpiresAt = other.optionalPlanExpiresAt().map(InstantFilter::copy).orElse(null);
        this.usedStorageBytes = other.optionalUsedStorageBytes().map(LongFilter::copy).orElse(null);
        this.aiUsedCount = other.optionalAiUsedCount().map(IntegerFilter::copy).orElse(null);
        this.rowVersion = other.optionalRowVersion().map(LongFilter::copy).orElse(null);
        this.userId = other.optionalUserId().map(LongFilter::copy).orElse(null);
        this.currentPlanId = other.optionalCurrentPlanId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TeacherQuotaCriteria copy() {
        return new TeacherQuotaCriteria(this);
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

    public InstantFilter getPlanExpiresAt() {
        return planExpiresAt;
    }

    public Optional<InstantFilter> optionalPlanExpiresAt() {
        return Optional.ofNullable(planExpiresAt);
    }

    public InstantFilter planExpiresAt() {
        if (planExpiresAt == null) {
            setPlanExpiresAt(new InstantFilter());
        }
        return planExpiresAt;
    }

    public void setPlanExpiresAt(InstantFilter planExpiresAt) {
        this.planExpiresAt = planExpiresAt;
    }

    public LongFilter getUsedStorageBytes() {
        return usedStorageBytes;
    }

    public Optional<LongFilter> optionalUsedStorageBytes() {
        return Optional.ofNullable(usedStorageBytes);
    }

    public LongFilter usedStorageBytes() {
        if (usedStorageBytes == null) {
            setUsedStorageBytes(new LongFilter());
        }
        return usedStorageBytes;
    }

    public void setUsedStorageBytes(LongFilter usedStorageBytes) {
        this.usedStorageBytes = usedStorageBytes;
    }

    public IntegerFilter getAiUsedCount() {
        return aiUsedCount;
    }

    public Optional<IntegerFilter> optionalAiUsedCount() {
        return Optional.ofNullable(aiUsedCount);
    }

    public IntegerFilter aiUsedCount() {
        if (aiUsedCount == null) {
            setAiUsedCount(new IntegerFilter());
        }
        return aiUsedCount;
    }

    public void setAiUsedCount(IntegerFilter aiUsedCount) {
        this.aiUsedCount = aiUsedCount;
    }

    public LongFilter getRowVersion() {
        return rowVersion;
    }

    public Optional<LongFilter> optionalRowVersion() {
        return Optional.ofNullable(rowVersion);
    }

    public LongFilter rowVersion() {
        if (rowVersion == null) {
            setRowVersion(new LongFilter());
        }
        return rowVersion;
    }

    public void setRowVersion(LongFilter rowVersion) {
        this.rowVersion = rowVersion;
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

    public LongFilter getCurrentPlanId() {
        return currentPlanId;
    }

    public Optional<LongFilter> optionalCurrentPlanId() {
        return Optional.ofNullable(currentPlanId);
    }

    public LongFilter currentPlanId() {
        if (currentPlanId == null) {
            setCurrentPlanId(new LongFilter());
        }
        return currentPlanId;
    }

    public void setCurrentPlanId(LongFilter currentPlanId) {
        this.currentPlanId = currentPlanId;
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
        final TeacherQuotaCriteria that = (TeacherQuotaCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(planExpiresAt, that.planExpiresAt) &&
            Objects.equals(usedStorageBytes, that.usedStorageBytes) &&
            Objects.equals(aiUsedCount, that.aiUsedCount) &&
            Objects.equals(rowVersion, that.rowVersion) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(currentPlanId, that.currentPlanId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, planExpiresAt, usedStorageBytes, aiUsedCount, rowVersion, userId, currentPlanId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TeacherQuotaCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalPlanExpiresAt().map(f -> "planExpiresAt=" + f + ", ").orElse("") +
            optionalUsedStorageBytes().map(f -> "usedStorageBytes=" + f + ", ").orElse("") +
            optionalAiUsedCount().map(f -> "aiUsedCount=" + f + ", ").orElse("") +
            optionalRowVersion().map(f -> "rowVersion=" + f + ", ").orElse("") +
            optionalUserId().map(f -> "userId=" + f + ", ").orElse("") +
            optionalCurrentPlanId().map(f -> "currentPlanId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
