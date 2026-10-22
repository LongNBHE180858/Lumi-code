package com.lumi.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * A TeacherQuota.
 */
@Entity
@Table(name = "teacher_quota")
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

    @Min(value = 0L)
    @Column(name = "used_storage_bytes")
    private Long usedStorageBytes;

    @Min(value = 0)
    @Column(name = "ai_used_count")
    private Integer aiUsedCount;

    @Min(value = 0L)
    @Column(name = "row_version")
    private Long rowVersion;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private User user;

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

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public TeacherQuota user(User user) {
        this.setUser(user);
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
            "}";
    }
}
