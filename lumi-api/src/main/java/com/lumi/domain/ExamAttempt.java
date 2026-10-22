package com.lumi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * A ExamAttempt.
 */
@Entity
@Table(name = "exam_attempt")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ExamAttempt implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    @DecimalMin(value = "0")
    @Column(name = "score", precision = 21, scale = 2)
    private BigDecimal score;

    @Size(max = 20)
    @Column(name = "status", length = 20)
    private String status;

    @Min(value = 0L)
    @Column(name = "row_version")
    private Long rowVersion;

    @ManyToOne(optional = false)
    @NotNull
    private User student;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "studyClass" }, allowSetters = true)
    private Exam exam;

    @JsonIgnoreProperties(value = { "attempt" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "attempt")
    private ProctorSession proctorSession;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ExamAttempt id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getStartTime() {
        return this.startTime;
    }

    public ExamAttempt startTime(Instant startTime) {
        this.setStartTime(startTime);
        return this;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return this.endTime;
    }

    public ExamAttempt endTime(Instant endTime) {
        this.setEndTime(endTime);
        return this;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public BigDecimal getScore() {
        return this.score;
    }

    public ExamAttempt score(BigDecimal score) {
        this.setScore(score);
        return this;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public String getStatus() {
        return this.status;
    }

    public ExamAttempt status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getRowVersion() {
        return this.rowVersion;
    }

    public ExamAttempt rowVersion(Long rowVersion) {
        this.setRowVersion(rowVersion);
        return this;
    }

    public void setRowVersion(Long rowVersion) {
        this.rowVersion = rowVersion;
    }

    public User getStudent() {
        return this.student;
    }

    public void setStudent(User user) {
        this.student = user;
    }

    public ExamAttempt student(User user) {
        this.setStudent(user);
        return this;
    }

    public Exam getExam() {
        return this.exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }

    public ExamAttempt exam(Exam exam) {
        this.setExam(exam);
        return this;
    }

    public ProctorSession getProctorSession() {
        return this.proctorSession;
    }

    public void setProctorSession(ProctorSession proctorSession) {
        if (this.proctorSession != null) {
            this.proctorSession.setAttempt(null);
        }
        if (proctorSession != null) {
            proctorSession.setAttempt(this);
        }
        this.proctorSession = proctorSession;
    }

    public ExamAttempt proctorSession(ProctorSession proctorSession) {
        this.setProctorSession(proctorSession);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExamAttempt)) {
            return false;
        }
        return getId() != null && getId().equals(((ExamAttempt) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ExamAttempt{" +
            "id=" + getId() +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", score=" + getScore() +
            ", status='" + getStatus() + "'" +
            ", rowVersion=" + getRowVersion() +
            "}";
    }
}
