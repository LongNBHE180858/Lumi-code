package com.lumi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * A AssignmentSubmission.
 */
@Entity
@Table(name = "assignment_submission")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AssignmentSubmission implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Size(max = 5000)
    @Column(name = "content", length = 5000)
    private String content;

    @DecimalMin(value = "0")
    @Column(name = "score", precision = 21, scale = 2)
    private BigDecimal score;

    @Size(max = 5000)
    @Column(name = "teacher_feedback", length = 5000)
    private String teacherFeedback;

    @Size(max = 20)
    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "submitted_at")
    private Instant submittedAt;

    @ManyToOne(optional = false)
    @NotNull
    private User student;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "studyClass" }, allowSetters = true)
    private Assignment assignment;

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
            "}";
    }
}
