package com.lumi.domain;

import com.lumi.domain.enumeration.ClassJoinMode;
import com.lumi.domain.enumeration.RecordStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * Lưu lớp riêng, giáo viên quản lý, mã tham gia và sĩ số.
 */
@Entity
@Table(name = "classes")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StudyClass implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 255)
    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Size(max = 10)
    @Column(name = "join_code", length = 10, unique = true)
    private String joinCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "join_mode")
    private ClassJoinMode joinMode;

    @Column(name = "max_students")
    private Integer maxStudents;

    @Column(name = "current_students")
    private Integer currentStudents;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RecordStatus status;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "join_code_expires_at")
    private Instant joinCodeExpiresAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ManyToOne(optional = false)
    @NotNull
    private User instructor;

    @ManyToOne(fetch = FetchType.LAZY)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    private Grade grade;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public StudyClass id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public StudyClass name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public StudyClass description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getJoinCode() {
        return this.joinCode;
    }

    public StudyClass joinCode(String joinCode) {
        this.setJoinCode(joinCode);
        return this;
    }

    public void setJoinCode(String joinCode) {
        this.joinCode = joinCode;
    }

    public ClassJoinMode getJoinMode() {
        return this.joinMode;
    }

    public StudyClass joinMode(ClassJoinMode joinMode) {
        this.setJoinMode(joinMode);
        return this;
    }

    public void setJoinMode(ClassJoinMode joinMode) {
        this.joinMode = joinMode;
    }

    public Integer getMaxStudents() {
        return this.maxStudents;
    }

    public StudyClass maxStudents(Integer maxStudents) {
        this.setMaxStudents(maxStudents);
        return this;
    }

    public void setMaxStudents(Integer maxStudents) {
        this.maxStudents = maxStudents;
    }

    public Integer getCurrentStudents() {
        return this.currentStudents;
    }

    public StudyClass currentStudents(Integer currentStudents) {
        this.setCurrentStudents(currentStudents);
        return this;
    }

    public void setCurrentStudents(Integer currentStudents) {
        this.currentStudents = currentStudents;
    }

    public RecordStatus getStatus() {
        return this.status;
    }

    public StudyClass status(RecordStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(RecordStatus status) {
        this.status = status;
    }

    public Boolean getIsDeleted() {
        return this.isDeleted;
    }

    public StudyClass isDeleted(Boolean isDeleted) {
        this.setIsDeleted(isDeleted);
        return this;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public StudyClass createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getJoinCodeExpiresAt() {
        return this.joinCodeExpiresAt;
    }

    public StudyClass joinCodeExpiresAt(Instant joinCodeExpiresAt) {
        this.setJoinCodeExpiresAt(joinCodeExpiresAt);
        return this;
    }

    public void setJoinCodeExpiresAt(Instant joinCodeExpiresAt) {
        this.joinCodeExpiresAt = joinCodeExpiresAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public StudyClass updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getInstructor() {
        return this.instructor;
    }

    public void setInstructor(User user) {
        this.instructor = user;
    }

    public StudyClass instructor(User user) {
        this.setInstructor(user);
        return this;
    }

    public Subject getSubject() {
        return this.subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public StudyClass subject(Subject subject) {
        this.setSubject(subject);
        return this;
    }

    public Grade getGrade() {
        return this.grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    public StudyClass grade(Grade grade) {
        this.setGrade(grade);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StudyClass)) {
            return false;
        }
        return getId() != null && getId().equals(((StudyClass) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StudyClass{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", joinCode='" + getJoinCode() + "'" +
            ", joinMode='" + getJoinMode() + "'" +
            ", maxStudents=" + getMaxStudents() +
            ", currentStudents=" + getCurrentStudents() +
            ", status='" + getStatus() + "'" +
            ", isDeleted='" + getIsDeleted() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", joinCodeExpiresAt='" + getJoinCodeExpiresAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
