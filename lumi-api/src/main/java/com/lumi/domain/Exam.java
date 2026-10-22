package com.lumi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lumi.domain.enumeration.RecordStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A Exam.
 */
@Entity
@Table(name = "exam")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Exam implements Serializable {

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

    @NotNull
    @Min(value = 1)
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "pass_score", precision = 21, scale = 2, nullable = false)
    private BigDecimal passScore;

    @Min(value = 1)
    @Column(name = "max_attempts")
    private Integer maxAttempts;

    @Column(name = "shuffle_questions")
    private Boolean shuffleQuestions;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RecordStatus status;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "instructor", "subject", "grade" }, allowSetters = true)
    private StudyClass studyClass;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Exam id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Exam title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getDurationMinutes() {
        return this.durationMinutes;
    }

    public Exam durationMinutes(Integer durationMinutes) {
        this.setDurationMinutes(durationMinutes);
        return this;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public BigDecimal getPassScore() {
        return this.passScore;
    }

    public Exam passScore(BigDecimal passScore) {
        this.setPassScore(passScore);
        return this;
    }

    public void setPassScore(BigDecimal passScore) {
        this.passScore = passScore;
    }

    public Integer getMaxAttempts() {
        return this.maxAttempts;
    }

    public Exam maxAttempts(Integer maxAttempts) {
        this.setMaxAttempts(maxAttempts);
        return this;
    }

    public void setMaxAttempts(Integer maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public Boolean getShuffleQuestions() {
        return this.shuffleQuestions;
    }

    public Exam shuffleQuestions(Boolean shuffleQuestions) {
        this.setShuffleQuestions(shuffleQuestions);
        return this;
    }

    public void setShuffleQuestions(Boolean shuffleQuestions) {
        this.shuffleQuestions = shuffleQuestions;
    }

    public RecordStatus getStatus() {
        return this.status;
    }

    public Exam status(RecordStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(RecordStatus status) {
        this.status = status;
    }

    public Boolean getIsDeleted() {
        return this.isDeleted;
    }

    public Exam isDeleted(Boolean isDeleted) {
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

    public Exam studyClass(StudyClass studyClass) {
        this.setStudyClass(studyClass);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Exam)) {
            return false;
        }
        return getId() != null && getId().equals(((Exam) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Exam{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", durationMinutes=" + getDurationMinutes() +
            ", passScore=" + getPassScore() +
            ", maxAttempts=" + getMaxAttempts() +
            ", shuffleQuestions='" + getShuffleQuestions() + "'" +
            ", status='" + getStatus() + "'" +
            ", isDeleted='" + getIsDeleted() + "'" +
            "}";
    }
}
