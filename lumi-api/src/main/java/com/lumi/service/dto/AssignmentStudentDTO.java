package com.lumi.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.AssignmentStudent} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AssignmentStudentDTO implements Serializable {

    private Long id;

    @NotNull
    private UserDTO student;

    @NotNull
    private AssignmentDTO assignment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserDTO getStudent() {
        return student;
    }

    public void setStudent(UserDTO student) {
        this.student = student;
    }

    public AssignmentDTO getAssignment() {
        return assignment;
    }

    public void setAssignment(AssignmentDTO assignment) {
        this.assignment = assignment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AssignmentStudentDTO)) {
            return false;
        }

        AssignmentStudentDTO assignmentStudentDTO = (AssignmentStudentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, assignmentStudentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AssignmentStudentDTO{" +
            "id=" + getId() +
            ", student=" + getStudent() +
            ", assignment=" + getAssignment() +
            "}";
    }
}
