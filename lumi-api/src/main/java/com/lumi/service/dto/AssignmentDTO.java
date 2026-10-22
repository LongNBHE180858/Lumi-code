package com.lumi.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.Assignment} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AssignmentDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String title;

    @Size(max = 5000)
    private String instructions;

    private Instant dueAt;

    @DecimalMin(value = "0")
    private BigDecimal maxScore;

    private Boolean allowLate;

    private Boolean isDeleted;

    @NotNull
    private StudyClassDTO studyClass;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public Instant getDueAt() {
        return dueAt;
    }

    public void setDueAt(Instant dueAt) {
        this.dueAt = dueAt;
    }

    public BigDecimal getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(BigDecimal maxScore) {
        this.maxScore = maxScore;
    }

    public Boolean getAllowLate() {
        return allowLate;
    }

    public void setAllowLate(Boolean allowLate) {
        this.allowLate = allowLate;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
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
        if (!(o instanceof AssignmentDTO)) {
            return false;
        }

        AssignmentDTO assignmentDTO = (AssignmentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, assignmentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AssignmentDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", instructions='" + getInstructions() + "'" +
            ", dueAt='" + getDueAt() + "'" +
            ", maxScore=" + getMaxScore() +
            ", allowLate='" + getAllowLate() + "'" +
            ", isDeleted='" + getIsDeleted() + "'" +
            ", studyClass=" + getStudyClass() +
            "}";
    }
}
