package com.lumi.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.ClassMember} entity.
 */
@Schema(description = "Lưu thành viên lớp, yêu cầu tham gia và trạng thái duyệt.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ClassMemberDTO implements Serializable {

    private Long id;

    @Size(max = 20)
    private String status;

    private Instant joinedAt;

    @NotNull
    private Instant requestedAt;

    private Instant leftAt;

    @NotNull
    private Instant updatedAt;

    @NotNull
    private StudyClassDTO studyClass;

    @NotNull
    private UserDTO student;

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

    public Instant getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Instant joinedAt) {
        this.joinedAt = joinedAt;
    }

    public Instant getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(Instant requestedAt) {
        this.requestedAt = requestedAt;
    }

    public Instant getLeftAt() {
        return leftAt;
    }

    public void setLeftAt(Instant leftAt) {
        this.leftAt = leftAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public StudyClassDTO getStudyClass() {
        return studyClass;
    }

    public void setStudyClass(StudyClassDTO studyClass) {
        this.studyClass = studyClass;
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
        if (!(o instanceof ClassMemberDTO)) {
            return false;
        }

        ClassMemberDTO classMemberDTO = (ClassMemberDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, classMemberDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClassMemberDTO{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", joinedAt='" + getJoinedAt() + "'" +
            ", requestedAt='" + getRequestedAt() + "'" +
            ", leftAt='" + getLeftAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", studyClass=" + getStudyClass() +
            ", student=" + getStudent() +
            "}";
    }
}
