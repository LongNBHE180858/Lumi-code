package com.lumi.domain;

import com.lumi.domain.enumeration.PremiumPackageType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A SystemPlan.
 */
@Entity
@Table(name = "system_plan")
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
    @DecimalMin(value = "0")
    @Column(name = "price", precision = 21, scale = 2, nullable = false)
    private BigDecimal price;

    @NotNull
    @Min(value = 1)
    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;

    @NotNull
    @Min(value = 0L)
    @Column(name = "storage_quota_mb", nullable = false)
    private Long storageQuotaMb;

    @NotNull
    @Min(value = 0)
    @Column(name = "ai_generation_limit", nullable = false)
    private Integer aiGenerationLimit;

    @Min(value = 1)
    @Column(name = "max_classes")
    private Integer maxClasses;

    @Enumerated(EnumType.STRING)
    @Column(name = "package_type", length = 40)
    private PremiumPackageType packageType;

    @Column(name = "package_key", length = 36)
    private String packageKey;

    @NotNull
    @Min(value = 1)
    @Column(name = "revision", nullable = false)
    private Integer revision = 1;

    @NotNull
    @Column(name = "current_revision", nullable = false)
    private Boolean currentRevision = true;

    @NotNull
    @Column(name = "available", nullable = false)
    private Boolean available = false;

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

    public PremiumPackageType getPackageType() {
        return packageType;
    }

    public void setPackageType(PremiumPackageType packageType) {
        this.packageType = packageType;
    }

    public String getPackageKey() {
        return packageKey;
    }

    public void setPackageKey(String packageKey) {
        this.packageKey = packageKey;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    public Boolean getCurrentRevision() {
        return currentRevision;
    }

    public void setCurrentRevision(Boolean currentRevision) {
        this.currentRevision = currentRevision;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
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
            ", maxClasses=" + getMaxClasses() +
            "}";
    }
}
