package com.lumi.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.SystemPlan} entity.
 */
@Schema(description = "Lưu gói dịch vụ, giá, thời hạn và hạn mức quyền lợi.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SystemPlanDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 100)
    private String name;

    @NotNull
    private BigDecimal price;

    @NotNull
    private Integer durationDays;

    @NotNull
    private Long storageQuotaMb;

    @NotNull
    private Integer aiGenerationLimit;

    @Min(value = 1)
    @NotNull
    private Integer maxClasses;

    @NotNull
    @Size(max = 30)
    private String planType;

    @NotNull
    @Size(max = 20)
    private String targetRole;

    @NotNull
    @Size(max = 20)
    private String status;

    @NotNull
    @Size(max = 20)
    private String aiLimitPeriod;

    @Lob
    private String features;

    @NotNull
    private Instant updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(Integer durationDays) {
        this.durationDays = durationDays;
    }

    public Long getStorageQuotaMb() {
        return storageQuotaMb;
    }

    public void setStorageQuotaMb(Long storageQuotaMb) {
        this.storageQuotaMb = storageQuotaMb;
    }

    public Integer getAiGenerationLimit() {
        return aiGenerationLimit;
    }

    public void setAiGenerationLimit(Integer aiGenerationLimit) {
        this.aiGenerationLimit = aiGenerationLimit;
    }

    public Integer getMaxClasses() {
        return maxClasses;
    }

    public void setMaxClasses(Integer maxClasses) {
        this.maxClasses = maxClasses;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAiLimitPeriod() {
        return aiLimitPeriod;
    }

    public void setAiLimitPeriod(String aiLimitPeriod) {
        this.aiLimitPeriod = aiLimitPeriod;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SystemPlanDTO)) {
            return false;
        }

        SystemPlanDTO systemPlanDTO = (SystemPlanDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, systemPlanDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SystemPlanDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", price=" + getPrice() +
            ", durationDays=" + getDurationDays() +
            ", storageQuotaMb=" + getStorageQuotaMb() +
            ", aiGenerationLimit=" + getAiGenerationLimit() +
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
