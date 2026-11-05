package com.lumi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Lưu quyền học, thời hạn và tiến độ tổng hợp của học sinh theo khóa.
 */
@Entity
@Table(name = "course_enrollments")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CourseEnrollment implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "progress_percent", precision = 21, scale = 2)
    private BigDecimal progressPercent;

    @Size(max = 20)
    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "enrolled_at")
    private Instant enrolledAt;

    @NotNull
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "author", "subject", "grade" }, allowSetters = true)
    private Course course;

    @ManyToOne(optional = false)
    @NotNull
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "chapter" }, allowSetters = true)
    private Lesson lastAccessedLesson;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CourseEnrollment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getProgressPercent() {
        return this.progressPercent;
    }

    public CourseEnrollment progressPercent(BigDecimal progressPercent) {
        this.setProgressPercent(progressPercent);
        return this;
    }

    public void setProgressPercent(BigDecimal progressPercent) {
        this.progressPercent = progressPercent;
    }

    public String getStatus() {
        return this.status;
    }

    public CourseEnrollment status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getEnrolledAt() {
        return this.enrolledAt;
    }

    public CourseEnrollment enrolledAt(Instant enrolledAt) {
        this.setEnrolledAt(enrolledAt);
        return this;
    }

    public void setEnrolledAt(Instant enrolledAt) {
        this.enrolledAt = enrolledAt;
    }

    public Instant getExpiresAt() {
        return this.expiresAt;
    }

    public CourseEnrollment expiresAt(Instant expiresAt) {
        this.setExpiresAt(expiresAt);
        return this;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public CourseEnrollment updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Course getCourse() {
        return this.course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public CourseEnrollment course(Course course) {
        this.setCourse(course);
        return this;
    }

    public User getStudent() {
        return this.student;
    }

    public void setStudent(User user) {
        this.student = user;
    }

    public CourseEnrollment student(User user) {
        this.setStudent(user);
        return this;
    }

    public Lesson getLastAccessedLesson() {
        return this.lastAccessedLesson;
    }

    public void setLastAccessedLesson(Lesson lesson) {
        this.lastAccessedLesson = lesson;
    }

    public CourseEnrollment lastAccessedLesson(Lesson lesson) {
        this.setLastAccessedLesson(lesson);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CourseEnrollment)) {
            return false;
        }
        return getId() != null && getId().equals(((CourseEnrollment) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CourseEnrollment{" +
            "id=" + getId() +
            ", progressPercent=" + getProgressPercent() +
            ", status='" + getStatus() + "'" +
            ", enrolledAt='" + getEnrolledAt() + "'" +
            ", expiresAt='" + getExpiresAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
