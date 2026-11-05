package com.lumi.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.StudentKnowledgeProgress} entity.
 */
@Schema(description = "Lưu mức thành thạo tổng hợp của học sinh theo đơn vị kiến thức.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StudentKnowledgeProgressDTO implements Serializable {

    private Long id;

    @DecimalMin(value = "0")
    @DecimalMax(value = "100")
    private BigDecimal masteryScore;

    @NotNull
    @Min(value = 0)
    private Integer evidenceCount;

    @Size(max = 50)
    private String calculationVersion;

    @NotNull
    private Instant updatedAt;

    @NotNull
    private UserDTO student;

    @NotNull
    private KnowledgeNodeDTO knowledgeNode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getMasteryScore() {
        return masteryScore;
    }

    public void setMasteryScore(BigDecimal masteryScore) {
        this.masteryScore = masteryScore;
    }

    public Integer getEvidenceCount() {
        return evidenceCount;
    }

    public void setEvidenceCount(Integer evidenceCount) {
        this.evidenceCount = evidenceCount;
    }

    public String getCalculationVersion() {
        return calculationVersion;
    }

    public void setCalculationVersion(String calculationVersion) {
        this.calculationVersion = calculationVersion;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UserDTO getStudent() {
        return student;
    }

    public void setStudent(UserDTO student) {
        this.student = student;
    }

    public KnowledgeNodeDTO getKnowledgeNode() {
        return knowledgeNode;
    }

    public void setKnowledgeNode(KnowledgeNodeDTO knowledgeNode) {
        this.knowledgeNode = knowledgeNode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StudentKnowledgeProgressDTO)) {
            return false;
        }

        StudentKnowledgeProgressDTO studentKnowledgeProgressDTO = (StudentKnowledgeProgressDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, studentKnowledgeProgressDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StudentKnowledgeProgressDTO{" +
            "id=" + getId() +
            ", masteryScore=" + getMasteryScore() +
            ", evidenceCount=" + getEvidenceCount() +
            ", calculationVersion='" + getCalculationVersion() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", student=" + getStudent() +
            ", knowledgeNode=" + getKnowledgeNode() +
            "}";
    }
}
