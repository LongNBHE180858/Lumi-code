package com.lumi.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Lưu gói dịch vụ, giá, thời hạn và hạn mức quyền lợi.
 */
@Entity
@Table(name = "system_plans")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SystemPlan implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @NotNull
    @Column(name = "price", precision = 21, scale = 2, nullable = false)
    private BigDecimal price;

    @NotNull
    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;

    @NotNull
    @Column(name = "storage_quota_mb", nullable = false)
    private Long storageQuotaMb;

    @NotNull
    @Column(name = "ai_generation_limit", nullable = false)
    private Integer aiGenerationLimit;

    @Column(name = "package_key", length = 36)
    private String packageKey;

    @NotNull
    @Column(name = "max_classes", nullable = false)
    private Integer maxClasses;

    @NotNull
    @Size(max = 30)
    @Column(name = "plan_type", length = 30, nullable = false)
    private String planType;

    @NotNull
    @Size(max = 20)
    @Column(name = "target_role", length = 20, nullable = false)
    private String targetRole;

    @NotNull
    @Size(max = 20)
    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @NotNull
    @Size(max = 20)
    @Column(name = "ai_limit_period", length = 20, nullable = false)
    private String aiLimitPeriod;

    @Column(name = "features", nullable = false, columnDefinition = "text")
    private String features;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SystemPlan id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public SystemPlan name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public SystemPlan price(BigDecimal price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getDurationDays() {
        return this.durationDays;
    }

    public SystemPlan durationDays(Integer durationDays) {
        this.setDurationDays(durationDays);
        return this;
    }

    public void setDurationDays(Integer durationDays) {
        this.durationDays = durationDays;
    }

    public Long getStorageQuotaMb() {
        return this.storageQuotaMb;
    }

    public SystemPlan storageQuotaMb(Long storageQuotaMb) {
        this.setStorageQuotaMb(storageQuotaMb);
        return this;
    }

    public void setStorageQuotaMb(Long storageQuotaMb) {
        this.storageQuotaMb = storageQuotaMb;
    }

    public Integer getAiGenerationLimit() {
        return this.aiGenerationLimit;
    }

    public SystemPlan aiGenerationLimit(Integer aiGenerationLimit) {
        this.setAiGenerationLimit(aiGenerationLimit);
        return this;
    }

    public void setAiGenerationLimit(Integer aiGenerationLimit) {
        this.aiGenerationLimit = aiGenerationLimit;
    }

    public String getPackageKey() {
        return this.packageKey;
    }

    public SystemPlan packageKey(String packageKey) {
        this.setPackageKey(packageKey);
        return this;
    }

    public void setPackageKey(String packageKey) {
        this.packageKey = packageKey;
    }

    public Integer getMaxClasses() {
        return this.maxClasses;
    }

    public SystemPlan maxClasses(Integer maxClasses) {
        this.setMaxClasses(maxClasses);
        return this;
    }

    public void setMaxClasses(Integer maxClasses) {
        this.maxClasses = maxClasses;
    }

    public String getPlanType() {
        return this.planType;
    }

    public SystemPlan planType(String planType) {
        this.setPlanType(planType);
        return this;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public String getTargetRole() {
        return this.targetRole;
    }

    public SystemPlan targetRole(String targetRole) {
        this.setTargetRole(targetRole);
        return this;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public String getStatus() {
        return this.status;
    }

    public SystemPlan status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAiLimitPeriod() {
        return this.aiLimitPeriod;
    }

    public SystemPlan aiLimitPeriod(String aiLimitPeriod) {
        this.setAiLimitPeriod(aiLimitPeriod);
        return this;
    }

    public void setAiLimitPeriod(String aiLimitPeriod) {
        this.aiLimitPeriod = aiLimitPeriod;
    }

    public String getFeatures() {
        return this.features;
    }

    public SystemPlan features(String features) {
        this.setFeatures(features);
        return this;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public SystemPlan updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SystemPlan)) {
            return false;
        }
        return getId() != null && getId().equals(((SystemPlan) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SystemPlan{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", price=" + getPrice() +
            ", durationDays=" + getDurationDays() +
            ", storageQuotaMb=" + getStorageQuotaMb() +
            ", aiGenerationLimit=" + getAiGenerationLimit() +
            ", packageKey='" + getPackageKey() + "'" +
            ", maxClasses=" + getMaxClasses() +
            ", planType='" + getPlanType() + "'" +
            ", targetRole='" + getTargetRole() + "'" +
            ", status='" + getStatus() + "'" +
            ", aiLimitPeriod='" + getAiLimitPeriod() + "'" +
            ", features='" + getFeatures() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
