package com.lumi.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.ContentReport} entity.
 */
@Schema(description = "Lưu báo sai nội dung, đối tượng bị báo và kết quả xử lý.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ContentReportDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 50)
    private String targetType;

    @NotNull
    private Long targetId;

    @Lob
    private String reason;

    @Size(max = 20)
    private String status;

    private Instant createdAt;

    private Instant resolvedAt;

    @Lob
    private String resolutionNote;

    @Size(max = 100)
    private String targetVersion;

    @NotNull
    private UserDTO reporter;

    private UserDTO assignedTo;

    private UserDTO resolvedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(Instant resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public String getResolutionNote() {
        return resolutionNote;
    }

    public void setResolutionNote(String resolutionNote) {
        this.resolutionNote = resolutionNote;
    }

    public String getTargetVersion() {
        return targetVersion;
    }

    public void setTargetVersion(String targetVersion) {
        this.targetVersion = targetVersion;
    }

    public UserDTO getReporter() {
        return reporter;
    }

    public void setReporter(UserDTO reporter) {
        this.reporter = reporter;
    }

    public UserDTO getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(UserDTO assignedTo) {
        this.assignedTo = assignedTo;
    }

    public UserDTO getResolvedBy() {
        return resolvedBy;
    }

    public void setResolvedBy(UserDTO resolvedBy) {
        this.resolvedBy = resolvedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ContentReportDTO)) {
            return false;
        }

        ContentReportDTO contentReportDTO = (ContentReportDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, contentReportDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ContentReportDTO{" +
            "id=" + getId() +
            ", targetType='" + getTargetType() + "'" +
            ", targetId=" + getTargetId() +
            ", reason='" + getReason() + "'" +
            ", status='" + getStatus() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", resolvedAt='" + getResolvedAt() + "'" +
            ", resolutionNote='" + getResolutionNote() + "'" +
            ", targetVersion='" + getTargetVersion() + "'" +
            ", reporter=" + getReporter() +
            ", assignedTo=" + getAssignedTo() +
            ", resolvedBy=" + getResolvedBy() +
            "}";
    }
}
