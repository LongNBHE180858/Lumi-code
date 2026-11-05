package com.lumi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Lưu lượt làm bài, thời hạn, kết quả và trạng thái luyện tập của học sinh.
 */
@Entity
@Table(name = "exam_attempts")
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

    @Column(name = "score", precision = 21, scale = 2)
    private BigDecimal score;

    @Size(max = 20)
    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "row_version")
    private Long rowVersion;

    @NotNull
    @Column(name = "deadline_at", nullable = false)
    private Instant deadlineAt;

    @Column(name = "last_saved_at")
    private Instant lastSavedAt;

    @Column(name = "submitted_at")
    private Instant submittedAt;

    @Column(name = "presentation_snapshot", columnDefinition = "text")
    private String presentationSnapshot;

    @Column(name = "security_config_snapshot", columnDefinition = "text")
    private String securityConfigSnapshot;

    @Column(name = "adaptive_state", columnDefinition = "text")
    private String adaptiveState;

    @NotNull
    @Size(max = 20)
    @Column(name = "monitoring_status", length = 20, nullable = false)
    private String monitoringStatus;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "studyClass", "createdBy", "lesson", "assignedStudent" }, allowSetters = true)
    private Exam exam;

    @ManyToOne(optional = false)
    @NotNull
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "studyClass", "assignedBy", "exam" }, allowSetters = true)
    private Assignment assignment;

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

    public Instant getDeadlineAt() {
        return this.deadlineAt;
    }

    public ExamAttempt deadlineAt(Instant deadlineAt) {
        this.setDeadlineAt(deadlineAt);
        return this;
    }

    public void setDeadlineAt(Instant deadlineAt) {
        this.deadlineAt = deadlineAt;
    }

    public Instant getLastSavedAt() {
        return this.lastSavedAt;
    }

    public ExamAttempt lastSavedAt(Instant lastSavedAt) {
        this.setLastSavedAt(lastSavedAt);
        return this;
    }

    public void setLastSavedAt(Instant lastSavedAt) {
        this.lastSavedAt = lastSavedAt;
    }

    public Instant getSubmittedAt() {
        return this.submittedAt;
    }

    public ExamAttempt submittedAt(Instant submittedAt) {
        this.setSubmittedAt(submittedAt);
        return this;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }

    public String getPresentationSnapshot() {
        return this.presentationSnapshot;
    }

    public ExamAttempt presentationSnapshot(String presentationSnapshot) {
        this.setPresentationSnapshot(presentationSnapshot);
        return this;
    }

    public void setPresentationSnapshot(String presentationSnapshot) {
        this.presentationSnapshot = presentationSnapshot;
    }

    public String getSecurityConfigSnapshot() {
        return this.securityConfigSnapshot;
    }

    public ExamAttempt securityConfigSnapshot(String securityConfigSnapshot) {
        this.setSecurityConfigSnapshot(securityConfigSnapshot);
        return this;
    }

    public void setSecurityConfigSnapshot(String securityConfigSnapshot) {
        this.securityConfigSnapshot = securityConfigSnapshot;
    }

    public String getAdaptiveState() {
        return this.adaptiveState;
    }

    public ExamAttempt adaptiveState(String adaptiveState) {
        this.setAdaptiveState(adaptiveState);
        return this;
    }

    public void setAdaptiveState(String adaptiveState) {
        this.adaptiveState = adaptiveState;
    }

    public String getMonitoringStatus() {
        return this.monitoringStatus;
    }

    public ExamAttempt monitoringStatus(String monitoringStatus) {
        this.setMonitoringStatus(monitoringStatus);
        return this;
    }

    public void setMonitoringStatus(String monitoringStatus) {
        this.monitoringStatus = monitoringStatus;
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

    public Assignment getAssignment() {
        return this.assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public ExamAttempt assignment(Assignment assignment) {
        this.setAssignment(assignment);
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
            ", deadlineAt='" + getDeadlineAt() + "'" +
            ", lastSavedAt='" + getLastSavedAt() + "'" +
            ", submittedAt='" + getSubmittedAt() + "'" +
            ", presentationSnapshot='" + getPresentationSnapshot() + "'" +
            ", securityConfigSnapshot='" + getSecurityConfigSnapshot() + "'" +
            ", adaptiveState='" + getAdaptiveState() + "'" +
            ", monitoringStatus='" + getMonitoringStatus() + "'" +
            "}";
    }
}
