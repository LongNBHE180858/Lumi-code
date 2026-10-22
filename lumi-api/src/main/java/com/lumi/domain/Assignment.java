package com.lumi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * A Assignment.
 */
@Entity
@Table(name = "assignment")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Assignment implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 255)
    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Size(max = 5000)
    @Column(name = "instructions", length = 5000)
    private String instructions;

    @Column(name = "due_at")
    private Instant dueAt;

    @DecimalMin(value = "0")
    @Column(name = "max_score", precision = 21, scale = 2)
    private BigDecimal maxScore;

    @Column(name = "allow_late")
    private Boolean allowLate;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "instructor", "subject", "grade" }, allowSetters = true)
    private StudyClass studyClass;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Assignment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Assignment title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInstructions() {
        return this.instructions;
    }

    public Assignment instructions(String instructions) {
        this.setInstructions(instructions);
        return this;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public Instant getDueAt() {
        return this.dueAt;
    }

    public Assignment dueAt(Instant dueAt) {
        this.setDueAt(dueAt);
        return this;
    }

    public void setDueAt(Instant dueAt) {
        this.dueAt = dueAt;
    }

    public BigDecimal getMaxScore() {
        return this.maxScore;
    }

    public Assignment maxScore(BigDecimal maxScore) {
        this.setMaxScore(maxScore);
        return this;
    }

    public void setMaxScore(BigDecimal maxScore) {
        this.maxScore = maxScore;
    }

    public Boolean getAllowLate() {
        return this.allowLate;
    }

    public Assignment allowLate(Boolean allowLate) {
        this.setAllowLate(allowLate);
        return this;
    }

    public void setAllowLate(Boolean allowLate) {
        this.allowLate = allowLate;
    }

    public Boolean getIsDeleted() {
        return this.isDeleted;
    }

    public Assignment isDeleted(Boolean isDeleted) {
        this.setIsDeleted(isDeleted);
        return this;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public StudyClass getStudyClass() {
        return this.studyClass;
    }

    public void setStudyClass(StudyClass studyClass) {
        this.studyClass = studyClass;
    }

    public Assignment studyClass(StudyClass studyClass) {
        this.setStudyClass(studyClass);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Assignment)) {
            return false;
        }
        return getId() != null && getId().equals(((Assignment) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Assignment{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", instructions='" + getInstructions() + "'" +
            ", dueAt='" + getDueAt() + "'" +
            ", maxScore=" + getMaxScore() +
            ", allowLate='" + getAllowLate() + "'" +
            ", isDeleted='" + getIsDeleted() + "'" +
            "}";
    }
}
