package com.lumi.service.dto;

import com.lumi.domain.enumeration.RecordStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.Exam} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ExamDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String title;

    @NotNull
    @Min(value = 1)
    private Integer durationMinutes;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal passScore;

    @Min(value = 1)
    private Integer maxAttempts;

    private Boolean shuffleQuestions;

    private RecordStatus status;

    private Boolean isDeleted;

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

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public BigDecimal getPassScore() {
        return passScore;
    }

    public void setPassScore(BigDecimal passScore) {
        this.passScore = passScore;
    }

    public Integer getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(Integer maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public Boolean getShuffleQuestions() {
        return shuffleQuestions;
    }

    public void setShuffleQuestions(Boolean shuffleQuestions) {
        this.shuffleQuestions = shuffleQuestions;
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
        if (!(o instanceof ExamDTO)) {
            return false;
        }

        ExamDTO examDTO = (ExamDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, examDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ExamDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", durationMinutes=" + getDurationMinutes() +
            ", passScore=" + getPassScore() +
            ", maxAttempts=" + getMaxAttempts() +
            ", shuffleQuestions='" + getShuffleQuestions() + "'" +
            ", status='" + getStatus() + "'" +
            ", isDeleted='" + getIsDeleted() + "'" +
            ", studyClass=" + getStudyClass() +
            "}";
    }
}
