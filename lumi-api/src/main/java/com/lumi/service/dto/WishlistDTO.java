package com.lumi.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.Wishlist} entity.
 */
@Schema(description = "Lưu danh sách khóa học yêu thích của học sinh.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WishlistDTO implements Serializable {

    private Long id;

    private Instant createdAt;

    @NotNull
    private UserDTO student;

    @NotNull
    private CourseDTO course;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public UserDTO getStudent() {
        return student;
    }

    public void setStudent(UserDTO student) {
        this.student = student;
    }

    public CourseDTO getCourse() {
        return course;
    }

    public void setCourse(CourseDTO course) {
        this.course = course;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WishlistDTO)) {
            return false;
        }

        WishlistDTO wishlistDTO = (WishlistDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, wishlistDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WishlistDTO{" +
            "id=" + getId() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", student=" + getStudent() +
            ", course=" + getCourse() +
            "}";
    }
}
