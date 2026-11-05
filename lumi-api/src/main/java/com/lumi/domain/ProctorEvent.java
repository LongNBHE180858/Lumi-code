package com.lumi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * Lưu cảnh báo trình duyệt theo lượt thi và kết quả xem xét; không lưu ảnh hoặc nhận diện khuôn mặt.
 */
@Entity
@Table(name = "proctor_events")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProctorEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(name = "violation_type", length = 50, nullable = false)
    private String violationType;

    @Column(name = "occurred_at")
    private Instant occurredAt;

    @NotNull
    @Size(max = 20)
    @Column(name = "review_status", length = 20, nullable = false)
    private String reviewStatus;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    @Column(name = "review_note", columnDefinition = "text")
    private String reviewNote;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "exam", "student", "assignment" }, allowSetters = true)
    private ExamAttempt attempt;

    @ManyToOne(fetch = FetchType.LAZY)
    private User reviewedBy;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ProctorEvent id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getViolationType() {
        return this.violationType;
    }

    public ProctorEvent violationType(String violationType) {
        this.setViolationType(violationType);
        return this;
    }

    public void setViolationType(String violationType) {
        this.violationType = violationType;
    }

    public Instant getOccurredAt() {
        return this.occurredAt;
    }

    public ProctorEvent occurredAt(Instant occurredAt) {
        this.setOccurredAt(occurredAt);
        return this;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }

    public String getReviewStatus() {
        return this.reviewStatus;
    }

    public ProctorEvent reviewStatus(String reviewStatus) {
        this.setReviewStatus(reviewStatus);
        return this;
    }

    public void setReviewStatus(String reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public Instant getReviewedAt() {
        return this.reviewedAt;
    }

    public ProctorEvent reviewedAt(Instant reviewedAt) {
        this.setReviewedAt(reviewedAt);
        return this;
    }

    public void setReviewedAt(Instant reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public String getReviewNote() {
        return this.reviewNote;
    }

    public ProctorEvent reviewNote(String reviewNote) {
        this.setReviewNote(reviewNote);
        return this;
    }

    public void setReviewNote(String reviewNote) {
        this.reviewNote = reviewNote;
    }

    public ExamAttempt getAttempt() {
        return this.attempt;
    }

    public void setAttempt(ExamAttempt examAttempt) {
        this.attempt = examAttempt;
    }

    public ProctorEvent attempt(ExamAttempt examAttempt) {
        this.setAttempt(examAttempt);
        return this;
    }

    public User getReviewedBy() {
        return this.reviewedBy;
    }

    public void setReviewedBy(User user) {
        this.reviewedBy = user;
    }

    public ProctorEvent reviewedBy(User user) {
        this.setReviewedBy(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProctorEvent)) {
            return false;
        }
        return getId() != null && getId().equals(((ProctorEvent) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProctorEvent{" +
            "id=" + getId() +
            ", violationType='" + getViolationType() + "'" +
            ", occurredAt='" + getOccurredAt() + "'" +
            ", reviewStatus='" + getReviewStatus() + "'" +
            ", reviewedAt='" + getReviewedAt() + "'" +
            ", reviewNote='" + getReviewNote() + "'" +
            "}";
    }
}
