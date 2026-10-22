package com.lumi.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.ExamAttempt} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ExamAttemptDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant startTime;

    private Instant endTime;

    @DecimalMin(value = "0")
    private BigDecimal score;

    @Size(max = 20)
    private String status;

    @Min(value = 0L)
    private Long rowVersion;

    @NotNull
    private UserDTO student;

    @NotNull
    private ExamDTO exam;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getRowVersion() {
        return rowVersion;
    }

    public void setRowVersion(Long rowVersion) {
        this.rowVersion = rowVersion;
    }

    public UserDTO getStudent() {
        return student;
    }

    public void setStudent(UserDTO student) {
        this.student = student;
    }

    public ExamDTO getExam() {
        return exam;
    }

    public void setExam(ExamDTO exam) {
        this.exam = exam;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExamAttemptDTO)) {
            return false;
        }

        ExamAttemptDTO examAttemptDTO = (ExamAttemptDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, examAttemptDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ExamAttemptDTO{" +
            "id=" + getId() +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", score=" + getScore() +
            ", status='" + getStatus() + "'" +
            ", rowVersion=" + getRowVersion() +
            ", student=" + getStudent() +
            ", exam=" + getExam() +
            "}";
    }
}
