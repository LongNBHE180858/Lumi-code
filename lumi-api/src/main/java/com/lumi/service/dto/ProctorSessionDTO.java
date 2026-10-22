package com.lumi.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.ProctorSession} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProctorSessionDTO implements Serializable {

    private Long id;

    private Boolean faceVerified;

    @Min(value = 0)
    private Integer warningCount;

    @Size(max = 20)
    private String status;

    @NotNull
    private ExamAttemptDTO attempt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getFaceVerified() {
        return faceVerified;
    }

    public void setFaceVerified(Boolean faceVerified) {
        this.faceVerified = faceVerified;
    }

    public Integer getWarningCount() {
        return warningCount;
    }

    public void setWarningCount(Integer warningCount) {
        this.warningCount = warningCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ExamAttemptDTO getAttempt() {
        return attempt;
    }

    public void setAttempt(ExamAttemptDTO attempt) {
        this.attempt = attempt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProctorSessionDTO)) {
            return false;
        }

        ProctorSessionDTO proctorSessionDTO = (ProctorSessionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, proctorSessionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProctorSessionDTO{" +
            "id=" + getId() +
            ", faceVerified='" + getFaceVerified() + "'" +
            ", warningCount=" + getWarningCount() +
            ", status='" + getStatus() + "'" +
            ", attempt=" + getAttempt() +
            "}";
    }
}
