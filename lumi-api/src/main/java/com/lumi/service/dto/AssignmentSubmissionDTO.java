package com.lumi.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.AssignmentSubmission} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AssignmentSubmissionDTO implements Serializable {

    private Long id;

    @Size(max = 5000)
    private String content;

    @DecimalMin(value = "0")
    private BigDecimal score;

    @Size(max = 5000)
    private String teacherFeedback;

    @Size(max = 20)
    private String status;

    private Instant submittedAt;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public String getTeacherFeedback() {
        return teacherFeedback;
    }

    public void setTeacherFeedback(String teacherFeedback) {
        this.teacherFeedback = teacherFeedback;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
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
        if (!(o instanceof AssignmentSubmissionDTO)) {
            return false;
        }

        AssignmentSubmissionDTO assignmentSubmissionDTO = (AssignmentSubmissionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, assignmentSubmissionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AssignmentSubmissionDTO{" +
            "id=" + getId() +
            ", content='" + getContent() + "'" +
            ", score=" + getScore() +
            ", teacherFeedback='" + getTeacherFeedback() + "'" +
            ", status='" + getStatus() + "'" +
            ", submittedAt='" + getSubmittedAt() + "'" +
            ", student=" + getStudent() +
            ", assignment=" + getAssignment() +
            "}";
    }
}
