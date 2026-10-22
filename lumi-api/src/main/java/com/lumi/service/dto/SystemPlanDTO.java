package com.lumi.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.SystemPlan} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SystemPlanDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 100)
    private String name;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal price;

    @NotNull
    @Min(value = 1)
    private Integer durationDays;

    @NotNull
    @Min(value = 0L)
    private Long storageQuotaMb;

    @NotNull
    @Min(value = 0)
    private Integer aiGenerationLimit;

    @NotNull
    @Min(value = 1)
    private Integer maxClasses;

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
            "}";
    }
}
