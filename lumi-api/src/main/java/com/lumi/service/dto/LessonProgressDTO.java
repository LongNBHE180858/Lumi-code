package com.lumi.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.LessonProgress} entity.
 */
@Schema(description = "Lưu tiến độ từng bài và vị trí phát video gần nhất của học sinh.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LessonProgressDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 20)
    private String status;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal lastPositionSeconds;

    private Instant lastAccessedAt;

    private Instant completedAt;

    @NotNull
    private Long rowVersion;

    @NotNull
    private CourseEnrollmentDTO enrollment;

    @NotNull
    private LessonDTO lesson;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getLastPositionSeconds() {
        return lastPositionSeconds;
    }

    public void setLastPositionSeconds(BigDecimal lastPositionSeconds) {
        this.lastPositionSeconds = lastPositionSeconds;
    }

    public Instant getLastAccessedAt() {
        return lastAccessedAt;
    }

    public void setLastAccessedAt(Instant lastAccessedAt) {
        this.lastAccessedAt = lastAccessedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public Long getRowVersion() {
        return rowVersion;
    }

    public void setRowVersion(Long rowVersion) {
        this.rowVersion = rowVersion;
    }

    public CourseEnrollmentDTO getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(CourseEnrollmentDTO enrollment) {
        this.enrollment = enrollment;
    }

    public LessonDTO getLesson() {
        return lesson;
    }

    public void setLesson(LessonDTO lesson) {
        this.lesson = lesson;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LessonProgressDTO)) {
            return false;
        }

        LessonProgressDTO lessonProgressDTO = (LessonProgressDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, lessonProgressDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LessonProgressDTO{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", lastPositionSeconds=" + getLastPositionSeconds() +
            ", lastAccessedAt='" + getLastAccessedAt() + "'" +
            ", completedAt='" + getCompletedAt() + "'" +
            ", rowVersion=" + getRowVersion() +
            ", enrollment=" + getEnrollment() +
            ", lesson=" + getLesson() +
            "}";
    }
}
