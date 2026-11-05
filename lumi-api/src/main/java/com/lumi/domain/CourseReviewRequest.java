package com.lumi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lumi.domain.enumeration.CourseReviewRequestStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * Lưu lần gửi duyệt khóa học, phiên bản gửi và phản hồi của Reviewer.
 */
@Entity
@Table(name = "course_review_requests")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CourseReviewRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "course_version", nullable = false)
    private Long courseVersion;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private CourseReviewRequestStatus status;

    @Size(max = 5000)
    @Column(name = "submission_note", length = 5000)
    private String submissionNote;

    @NotNull
    @Column(name = "rights_confirmed_at", nullable = false)
    private Instant rightsConfirmedAt;

    @NotNull
    @Column(name = "submitted_at", nullable = false)
    private Instant submittedAt;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    @Size(max = 5000)
    @Column(name = "reviewer_feedback", length = 5000)
    private String reviewerFeedback;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "author", "subject", "grade" }, allowSetters = true)
    private Course course;

    @ManyToOne(optional = false)
    @NotNull
    private User submittedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    private User reviewedBy;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CourseReviewRequest id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCourseVersion() {
        return this.courseVersion;
    }

    public CourseReviewRequest courseVersion(Long courseVersion) {
        this.setCourseVersion(courseVersion);
        return this;
    }

    public void setCourseVersion(Long courseVersion) {
        this.courseVersion = courseVersion;
    }

    public CourseReviewRequestStatus getStatus() {
        return this.status;
    }

    public CourseReviewRequest status(CourseReviewRequestStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(CourseReviewRequestStatus status) {
        this.status = status;
    }

    public String getSubmissionNote() {
        return this.submissionNote;
    }

    public CourseReviewRequest submissionNote(String submissionNote) {
        this.setSubmissionNote(submissionNote);
        return this;
    }

    public void setSubmissionNote(String submissionNote) {
        this.submissionNote = submissionNote;
    }

    public Instant getRightsConfirmedAt() {
        return this.rightsConfirmedAt;
    }

    public CourseReviewRequest rightsConfirmedAt(Instant rightsConfirmedAt) {
        this.setRightsConfirmedAt(rightsConfirmedAt);
        return this;
    }

    public void setRightsConfirmedAt(Instant rightsConfirmedAt) {
        this.rightsConfirmedAt = rightsConfirmedAt;
    }

    public Instant getSubmittedAt() {
        return this.submittedAt;
    }

    public CourseReviewRequest submittedAt(Instant submittedAt) {
        this.setSubmittedAt(submittedAt);
        return this;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Instant getReviewedAt() {
        return this.reviewedAt;
    }

    public CourseReviewRequest reviewedAt(Instant reviewedAt) {
        this.setReviewedAt(reviewedAt);
        return this;
    }

    public void setReviewedAt(Instant reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public String getReviewerFeedback() {
        return this.reviewerFeedback;
    }

    public CourseReviewRequest reviewerFeedback(String reviewerFeedback) {
        this.setReviewerFeedback(reviewerFeedback);
        return this;
    }

    public void setReviewerFeedback(String reviewerFeedback) {
        this.reviewerFeedback = reviewerFeedback;
    }

    public Course getCourse() {
        return this.course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public CourseReviewRequest course(Course course) {
        this.setCourse(course);
        return this;
    }

    public User getSubmittedBy() {
        return this.submittedBy;
    }

    public void setSubmittedBy(User user) {
        this.submittedBy = user;
    }

    public CourseReviewRequest submittedBy(User user) {
        this.setSubmittedBy(user);
        return this;
    }

    public User getReviewedBy() {
        return this.reviewedBy;
    }

    public void setReviewedBy(User user) {
        this.reviewedBy = user;
    }

    public CourseReviewRequest reviewedBy(User user) {
        this.setReviewedBy(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CourseReviewRequest)) {
            return false;
        }
        return getId() != null && getId().equals(((CourseReviewRequest) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CourseReviewRequest{" +
            "id=" + getId() +
            ", courseVersion=" + getCourseVersion() +
            ", status='" + getStatus() + "'" +
            ", submissionNote='" + getSubmissionNote() + "'" +
            ", rightsConfirmedAt='" + getRightsConfirmedAt() + "'" +
            ", submittedAt='" + getSubmittedAt() + "'" +
            ", reviewedAt='" + getReviewedAt() + "'" +
            ", reviewerFeedback='" + getReviewerFeedback() + "'" +
            "}";
    }
}
