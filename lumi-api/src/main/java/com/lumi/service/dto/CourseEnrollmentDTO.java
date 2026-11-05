package com.lumi.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.CourseEnrollment} entity.
 */
@Schema(description = "Lưu quyền học, thời hạn và tiến độ tổng hợp của học sinh theo khóa.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CourseEnrollmentDTO implements Serializable {

    private Long id;

    private BigDecimal progressPercent;

    @Size(max = 20)
    private String status;

    private Instant enrolledAt;

    @NotNull
    private Instant expiresAt;

    @NotNull
    private Instant updatedAt;

    @NotNull
    private CourseDTO course;

    @NotNull
    private UserDTO student;

    private LessonDTO lastAccessedLesson;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getProgressPercent() {
        return progressPercent;
    }

    public void setProgressPercent(BigDecimal progressPercent) {
        this.progressPercent = progressPercent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getEnrolledAt() {
        return enrolledAt;
    }

    public void setEnrolledAt(Instant enrolledAt) {
        this.enrolledAt = enrolledAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public CourseDTO getCourse() {
        return course;
    }

    public void setCourse(CourseDTO course) {
        this.course = course;
    }

    public UserDTO getStudent() {
        return student;
    }

    public void setStudent(UserDTO student) {
        this.student = student;
    }

    public LessonDTO getLastAccessedLesson() {
        return lastAccessedLesson;
    }

    public void setLastAccessedLesson(LessonDTO lastAccessedLesson) {
        this.lastAccessedLesson = lastAccessedLesson;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CourseEnrollmentDTO)) {
            return false;
        }

        CourseEnrollmentDTO courseEnrollmentDTO = (CourseEnrollmentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, courseEnrollmentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CourseEnrollmentDTO{" +
            "id=" + getId() +
            ", progressPercent=" + getProgressPercent() +
            ", status='" + getStatus() + "'" +
            ", enrolledAt='" + getEnrolledAt() + "'" +
            ", expiresAt='" + getExpiresAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", course=" + getCourse() +
            ", student=" + getStudent() +
            ", lastAccessedLesson=" + getLastAccessedLesson() +
            "}";
    }
}
