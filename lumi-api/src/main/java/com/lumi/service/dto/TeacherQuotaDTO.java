package com.lumi.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.TeacherQuota} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TeacherQuotaDTO implements Serializable {

    private Long id;

    private Instant planExpiresAt;

    @Min(value = 0L)
    private Long usedStorageBytes;

    @Min(value = 0)
    private Integer aiUsedCount;

    @Min(value = 0L)
    private Long rowVersion;

    @NotNull
    private UserDTO user;

    private SystemPlanDTO currentPlan;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getPlanExpiresAt() {
        return planExpiresAt;
    }

    public void setPlanExpiresAt(Instant planExpiresAt) {
        this.planExpiresAt = planExpiresAt;
    }

    public Long getUsedStorageBytes() {
        return usedStorageBytes;
    }

    public void setUsedStorageBytes(Long usedStorageBytes) {
        this.usedStorageBytes = usedStorageBytes;
    }

    public Integer getAiUsedCount() {
        return aiUsedCount;
    }

    public void setAiUsedCount(Integer aiUsedCount) {
        this.aiUsedCount = aiUsedCount;
    }

    public Long getRowVersion() {
        return rowVersion;
    }

    public void setRowVersion(Long rowVersion) {
        this.rowVersion = rowVersion;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public SystemPlanDTO getCurrentPlan() {
        return currentPlan;
    }

    public void setCurrentPlan(SystemPlanDTO currentPlan) {
        this.currentPlan = currentPlan;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TeacherQuotaDTO)) {
            return false;
        }

        TeacherQuotaDTO teacherQuotaDTO = (TeacherQuotaDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, teacherQuotaDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TeacherQuotaDTO{" +
            "id=" + getId() +
            ", planExpiresAt='" + getPlanExpiresAt() + "'" +
            ", usedStorageBytes=" + getUsedStorageBytes() +
            ", aiUsedCount=" + getAiUsedCount() +
            ", rowVersion=" + getRowVersion() +
            ", user=" + getUser() +
            ", currentPlan=" + getCurrentPlan() +
            "}";
    }
}
