package com.lumi.service.dto;

import com.lumi.domain.enumeration.ClassJoinMode;
import com.lumi.domain.enumeration.RecordStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.StudyClass} entity.
 */
@Schema(description = "Lưu lớp riêng, giáo viên quản lý, mã tham gia và sĩ số.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StudyClassDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String name;

    @Lob
    private String description;

    @Size(max = 10)
    private String joinCode;

    private ClassJoinMode joinMode;

    private Integer maxStudents;

    private Integer currentStudents;

    private RecordStatus status;

    private Boolean isDeleted;

    private Instant createdAt;

    private Instant joinCodeExpiresAt;

    @NotNull
    private Instant updatedAt;

    @NotNull
    private UserDTO instructor;

    private SubjectDTO subject;

    private GradeDTO grade;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getJoinCode() {
        return joinCode;
    }

    public void setJoinCode(String joinCode) {
        this.joinCode = joinCode;
    }

    public ClassJoinMode getJoinMode() {
        return joinMode;
    }

    public void setJoinMode(ClassJoinMode joinMode) {
        this.joinMode = joinMode;
    }

    public Integer getMaxStudents() {
        return maxStudents;
    }

    public void setMaxStudents(Integer maxStudents) {
        this.maxStudents = maxStudents;
    }

    public Integer getCurrentStudents() {
        return currentStudents;
    }

    public void setCurrentStudents(Integer currentStudents) {
        this.currentStudents = currentStudents;
    }

    public RecordStatus getStatus() {
        return status;
    }

    public void setStatus(RecordStatus status) {
        this.status = status;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getJoinCodeExpiresAt() {
        return joinCodeExpiresAt;
    }

    public void setJoinCodeExpiresAt(Instant joinCodeExpiresAt) {
        this.joinCodeExpiresAt = joinCodeExpiresAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UserDTO getInstructor() {
        return instructor;
    }

    public void setInstructor(UserDTO instructor) {
        this.instructor = instructor;
    }

    public SubjectDTO getSubject() {
        return subject;
    }

    public void setSubject(SubjectDTO subject) {
        this.subject = subject;
    }

    public GradeDTO getGrade() {
        return grade;
    }

    public void setGrade(GradeDTO grade) {
        this.grade = grade;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StudyClassDTO)) {
            return false;
        }

        StudyClassDTO studyClassDTO = (StudyClassDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, studyClassDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StudyClassDTO{" +
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
            ", instructor=" + getInstructor() +
            ", subject=" + getSubject() +
            ", grade=" + getGrade() +
            "}";
    }
}
