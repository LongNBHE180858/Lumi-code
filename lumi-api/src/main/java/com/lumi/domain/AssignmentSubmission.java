package com.lumi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Lưu bài nộp, tệp đính kèm, điểm và phản hồi chấm bài tập.
 */
@Entity
@Table(name = "assignment_submissions")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AssignmentSubmission implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "content", columnDefinition = "text")
    private String content;

    @Column(name = "score", precision = 21, scale = 2)
    private BigDecimal score;

    @Column(name = "teacher_feedback", columnDefinition = "text")
    private String teacherFeedback;

    @Size(max = 20)
    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "submitted_at")
    private Instant submittedAt;

    @Column(name = "attachments", nullable = false, columnDefinition = "text")
    private String attachments;

    @Column(name = "graded_at")
    private Instant gradedAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @NotNull
    @Column(name = "row_version", nullable = false)
    private Long rowVersion;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "studyClass", "assignedBy", "exam" }, allowSetters = true)
    private Assignment assignment;

    @ManyToOne(optional = false)
    @NotNull
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    private User gradedBy;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AssignmentSubmission id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return this.content;
    }

    public AssignmentSubmission content(String content) {
        this.setContent(content);
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public BigDecimal getScore() {
        return this.score;
    }

    public AssignmentSubmission score(BigDecimal score) {
        this.setScore(score);
        return this;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public String getTeacherFeedback() {
        return this.teacherFeedback;
    }

    public AssignmentSubmission teacherFeedback(String teacherFeedback) {
        this.setTeacherFeedback(teacherFeedback);
        return this;
    }

    public void setTeacherFeedback(String teacherFeedback) {
        this.teacherFeedback = teacherFeedback;
    }

    public String getStatus() {
        return this.status;
    }

    public AssignmentSubmission status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getSubmittedAt() {
        return this.submittedAt;
    }

    public AssignmentSubmission submittedAt(Instant submittedAt) {
        this.setSubmittedAt(submittedAt);
        return this;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }

    public String getAttachments() {
        return this.attachments;
    }

    public AssignmentSubmission attachments(String attachments) {
        this.setAttachments(attachments);
        return this;
    }

    public void setAttachments(String attachments) {
        this.attachments = attachments;
    }

    public Instant getGradedAt() {
        return this.gradedAt;
    }

    public AssignmentSubmission gradedAt(Instant gradedAt) {
        this.setGradedAt(gradedAt);
        return this;
    }

    public void setGradedAt(Instant gradedAt) {
        this.gradedAt = gradedAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public AssignmentSubmission updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getRowVersion() {
        return this.rowVersion;
    }

    public AssignmentSubmission rowVersion(Long rowVersion) {
        this.setRowVersion(rowVersion);
        return this;
    }

    public void setRowVersion(Long rowVersion) {
        this.rowVersion = rowVersion;
    }

    public Assignment getAssignment() {
        return this.assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public AssignmentSubmission assignment(Assignment assignment) {
        this.setAssignment(assignment);
        return this;
    }

    public User getStudent() {
        return this.student;
    }

    public void setStudent(User user) {
        this.student = user;
    }

    public AssignmentSubmission student(User user) {
        this.setStudent(user);
        return this;
    }

    public User getGradedBy() {
        return this.gradedBy;
    }

    public void setGradedBy(User user) {
        this.gradedBy = user;
    }

    public AssignmentSubmission gradedBy(User user) {
        this.setGradedBy(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AssignmentSubmission)) {
            return false;
        }
        return getId() != null && getId().equals(((AssignmentSubmission) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AssignmentSubmission{" +
            "id=" + getId() +
            ", content='" + getContent() + "'" +
            ", score=" + getScore() +
            ", teacherFeedback='" + getTeacherFeedback() + "'" +
            ", status='" + getStatus() + "'" +
            ", submittedAt='" + getSubmittedAt() + "'" +
            ", attachments='" + getAttachments() + "'" +
            ", gradedAt='" + getGradedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", rowVersion=" + getRowVersion() +
            "}";
    }
}
