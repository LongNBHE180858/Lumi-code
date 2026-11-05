package com.lumi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Lưu tiến độ từng bài và vị trí phát video gần nhất của học sinh.
 */
@Entity
@Table(name = "lesson_progress")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LessonProgress implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 20)
    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "last_position_seconds", precision = 21, scale = 2, nullable = false)
    private BigDecimal lastPositionSeconds;

    @Column(name = "last_accessed_at")
    private Instant lastAccessedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @NotNull
    @Column(name = "row_version", nullable = false)
    private Long rowVersion;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "course", "student", "lastAccessedLesson" }, allowSetters = true)
    private CourseEnrollment enrollment;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "chapter" }, allowSetters = true)
    private Lesson lesson;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public LessonProgress id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return this.status;
    }

    public LessonProgress status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getLastPositionSeconds() {
        return this.lastPositionSeconds;
    }

    public LessonProgress lastPositionSeconds(BigDecimal lastPositionSeconds) {
        this.setLastPositionSeconds(lastPositionSeconds);
        return this;
    }

    public void setLastPositionSeconds(BigDecimal lastPositionSeconds) {
        this.lastPositionSeconds = lastPositionSeconds;
    }

    public Instant getLastAccessedAt() {
        return this.lastAccessedAt;
    }

    public LessonProgress lastAccessedAt(Instant lastAccessedAt) {
        this.setLastAccessedAt(lastAccessedAt);
        return this;
    }

    public void setLastAccessedAt(Instant lastAccessedAt) {
        this.lastAccessedAt = lastAccessedAt;
    }

    public Instant getCompletedAt() {
        return this.completedAt;
    }

    public LessonProgress completedAt(Instant completedAt) {
        this.setCompletedAt(completedAt);
        return this;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public Long getRowVersion() {
        return this.rowVersion;
    }

    public LessonProgress rowVersion(Long rowVersion) {
        this.setRowVersion(rowVersion);
        return this;
    }

    public void setRowVersion(Long rowVersion) {
        this.rowVersion = rowVersion;
    }

    public CourseEnrollment getEnrollment() {
        return this.enrollment;
    }

    public void setEnrollment(CourseEnrollment courseEnrollment) {
        this.enrollment = courseEnrollment;
    }

    public LessonProgress enrollment(CourseEnrollment courseEnrollment) {
        this.setEnrollment(courseEnrollment);
        return this;
    }

    public Lesson getLesson() {
        return this.lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public LessonProgress lesson(Lesson lesson) {
        this.setLesson(lesson);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LessonProgress)) {
            return false;
        }
        return getId() != null && getId().equals(((LessonProgress) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LessonProgress{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", lastPositionSeconds=" + getLastPositionSeconds() +
            ", lastAccessedAt='" + getLastAccessedAt() + "'" +
            ", completedAt='" + getCompletedAt() + "'" +
            ", rowVersion=" + getRowVersion() +
            "}";
    }
}
