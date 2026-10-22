package com.lumi.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.ClassMember} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ClassMemberDTO implements Serializable {

    private Long id;

    @Size(max = 20)
    private String status;

    private Instant joinedAt;

    @NotNull
    private UserDTO student;

    @NotNull
    private StudyClassDTO studyClass;

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

    public UserDTO getStudent() {
        return student;
    }

    public void setStudent(UserDTO student) {
        this.student = student;
    }

    public StudyClassDTO getStudyClass() {
        return studyClass;
    }

    public void setStudyClass(StudyClassDTO studyClass) {
        this.studyClass = studyClass;
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
            ", student=" + getStudent() +
            ", studyClass=" + getStudyClass() +
            "}";
    }
}
