package com.lumi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Lưu mức thành thạo tổng hợp của học sinh theo đơn vị kiến thức.
 */
@Entity
@Table(name = "student_knowledge_progress")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StudentKnowledgeProgress implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @DecimalMin(value = "0")
    @DecimalMax(value = "100")
    @Column(name = "mastery_score", precision = 21, scale = 2)
    private BigDecimal masteryScore;

    @NotNull
    @Min(value = 0)
    @Column(name = "evidence_count", nullable = false)
    private Integer evidenceCount;

    @Size(max = 50)
    @Column(name = "calculation_version", length = 50)
    private String calculationVersion;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ManyToOne(optional = false)
    @NotNull
    private User student;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "subject" }, allowSetters = true)
    private KnowledgeNode knowledgeNode;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public StudentKnowledgeProgress id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getMasteryScore() {
        return this.masteryScore;
    }

    public StudentKnowledgeProgress masteryScore(BigDecimal masteryScore) {
        this.setMasteryScore(masteryScore);
        return this;
    }

    public void setMasteryScore(BigDecimal masteryScore) {
        this.masteryScore = masteryScore;
    }

    public Integer getEvidenceCount() {
        return this.evidenceCount;
    }

    public StudentKnowledgeProgress evidenceCount(Integer evidenceCount) {
        this.setEvidenceCount(evidenceCount);
        return this;
    }

    public void setEvidenceCount(Integer evidenceCount) {
        this.evidenceCount = evidenceCount;
    }

    public String getCalculationVersion() {
        return this.calculationVersion;
    }

    public StudentKnowledgeProgress calculationVersion(String calculationVersion) {
        this.setCalculationVersion(calculationVersion);
        return this;
    }

    public void setCalculationVersion(String calculationVersion) {
        this.calculationVersion = calculationVersion;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public StudentKnowledgeProgress updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getStudent() {
        return this.student;
    }

    public void setStudent(User user) {
        this.student = user;
    }

    public StudentKnowledgeProgress student(User user) {
        this.setStudent(user);
        return this;
    }

    public KnowledgeNode getKnowledgeNode() {
        return this.knowledgeNode;
    }

    public void setKnowledgeNode(KnowledgeNode knowledgeNode) {
        this.knowledgeNode = knowledgeNode;
    }

    public StudentKnowledgeProgress knowledgeNode(KnowledgeNode knowledgeNode) {
        this.setKnowledgeNode(knowledgeNode);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StudentKnowledgeProgress)) {
            return false;
        }
        return getId() != null && getId().equals(((StudentKnowledgeProgress) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StudentKnowledgeProgress{" +
            "id=" + getId() +
            ", masteryScore=" + getMasteryScore() +
            ", evidenceCount=" + getEvidenceCount() +
            ", calculationVersion='" + getCalculationVersion() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
