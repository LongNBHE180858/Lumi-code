package com.lumi.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * Lưu hạn mức và mức sử dụng tài nguyên của giáo viên.
 */
@Entity
@Table(name = "teacher_quotas")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TeacherQuota implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "plan_expires_at")
    private Instant planExpiresAt;

    @Column(name = "used_storage_bytes")
    private Long usedStorageBytes;

    @Column(name = "ai_used_count")
    private Integer aiUsedCount;

    @Column(name = "row_version")
    private Long rowVersion;

    @NotNull
    @Min(value = 0L)
    @Column(name = "effective_storage_limit_bytes", nullable = false)
    private Long effectiveStorageLimitBytes;

    @Column(name = "ai_period_started_at")
    private Instant aiPeriodStartedAt;

    @Column(name = "ai_period_ends_at")
    private Instant aiPeriodEndsAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private User instructor;

    @ManyToOne(fetch = FetchType.LAZY)
    private SystemPlan currentPlan;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TeacherQuota id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getPlanExpiresAt() {
        return this.planExpiresAt;
    }

    public TeacherQuota planExpiresAt(Instant planExpiresAt) {
        this.setPlanExpiresAt(planExpiresAt);
        return this;
    }

    public void setPlanExpiresAt(Instant planExpiresAt) {
        this.planExpiresAt = planExpiresAt;
    }

    public Long getUsedStorageBytes() {
        return this.usedStorageBytes;
    }

    public TeacherQuota usedStorageBytes(Long usedStorageBytes) {
        this.setUsedStorageBytes(usedStorageBytes);
        return this;
    }

    public void setUsedStorageBytes(Long usedStorageBytes) {
        this.usedStorageBytes = usedStorageBytes;
    }

    public Integer getAiUsedCount() {
        return this.aiUsedCount;
    }

    public TeacherQuota aiUsedCount(Integer aiUsedCount) {
        this.setAiUsedCount(aiUsedCount);
        return this;
    }

    public void setAiUsedCount(Integer aiUsedCount) {
        this.aiUsedCount = aiUsedCount;
    }

    public Long getRowVersion() {
        return this.rowVersion;
    }

    public TeacherQuota rowVersion(Long rowVersion) {
        this.setRowVersion(rowVersion);
        return this;
    }

    public void setRowVersion(Long rowVersion) {
        this.rowVersion = rowVersion;
    }

    public Long getEffectiveStorageLimitBytes() {
        return this.effectiveStorageLimitBytes;
    }

    public TeacherQuota effectiveStorageLimitBytes(Long effectiveStorageLimitBytes) {
        this.setEffectiveStorageLimitBytes(effectiveStorageLimitBytes);
        return this;
    }

    public void setEffectiveStorageLimitBytes(Long effectiveStorageLimitBytes) {
        this.effectiveStorageLimitBytes = effectiveStorageLimitBytes;
    }

    public Instant getAiPeriodStartedAt() {
        return this.aiPeriodStartedAt;
    }

    public TeacherQuota aiPeriodStartedAt(Instant aiPeriodStartedAt) {
        this.setAiPeriodStartedAt(aiPeriodStartedAt);
        return this;
    }

    public void setAiPeriodStartedAt(Instant aiPeriodStartedAt) {
        this.aiPeriodStartedAt = aiPeriodStartedAt;
    }

    public Instant getAiPeriodEndsAt() {
        return this.aiPeriodEndsAt;
    }

    public TeacherQuota aiPeriodEndsAt(Instant aiPeriodEndsAt) {
        this.setAiPeriodEndsAt(aiPeriodEndsAt);
        return this;
    }

    public void setAiPeriodEndsAt(Instant aiPeriodEndsAt) {
        this.aiPeriodEndsAt = aiPeriodEndsAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public TeacherQuota updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getInstructor() {
        return this.instructor;
    }

    public void setInstructor(User user) {
        this.instructor = user;
    }

    public TeacherQuota instructor(User user) {
        this.setInstructor(user);
        return this;
    }

    public SystemPlan getCurrentPlan() {
        return this.currentPlan;
    }

    public void setCurrentPlan(SystemPlan systemPlan) {
        this.currentPlan = systemPlan;
    }

    public TeacherQuota currentPlan(SystemPlan systemPlan) {
        this.setCurrentPlan(systemPlan);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TeacherQuota)) {
            return false;
        }
        return getId() != null && getId().equals(((TeacherQuota) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TeacherQuota{" +
            "id=" + getId() +
            ", planExpiresAt='" + getPlanExpiresAt() + "'" +
            ", usedStorageBytes=" + getUsedStorageBytes() +
            ", aiUsedCount=" + getAiUsedCount() +
            ", rowVersion=" + getRowVersion() +
            ", effectiveStorageLimitBytes=" + getEffectiveStorageLimitBytes() +
            ", aiPeriodStartedAt='" + getAiPeriodStartedAt() + "'" +
            ", aiPeriodEndsAt='" + getAiPeriodEndsAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
