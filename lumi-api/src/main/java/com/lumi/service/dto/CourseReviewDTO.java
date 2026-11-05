package com.lumi.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.CourseReview} entity.
 */
@Schema(description = "Lưu đánh giá sao và nhận xét của học sinh về khóa học.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CourseReviewDTO implements Serializable {

    private Long id;

    @Min(value = 1)
    @Max(value = 5)
    private Integer rating;

    @Lob
    private String content;

    private Instant createdAt;

    @NotNull
    private Instant updatedAt;

    @NotNull
    private CourseDTO course;

    @NotNull
    private UserDTO student;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CourseReviewDTO)) {
            return false;
        }

        CourseReviewDTO courseReviewDTO = (CourseReviewDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, courseReviewDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CourseReviewDTO{" +
            "id=" + getId() +
            ", rating=" + getRating() +
            ", content='" + getContent() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", course=" + getCourse() +
            ", student=" + getStudent() +
            "}";
    }
}
