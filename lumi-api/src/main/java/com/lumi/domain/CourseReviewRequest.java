package com.lumi.domain;

import com.lumi.domain.enumeration.CourseReviewRequestStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "course_review_requests")
public class CourseReviewRequest implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Min(0)
    @Column(name = "course_version")
    private Long courseVersion;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private CourseReviewRequestStatus status;

    @Size(max = 5000)
    @Column(name = "submission_note", length = 5000)
    private String submissionNote;

    @NotNull
    @Column(name = "rights_confirmed_at")
    private Instant rightsConfirmedAt;

    @NotNull
    @Column(name = "submitted_at")
    private Instant submittedAt;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    @Size(max = 5000)
    @Column(name = "reviewer_feedback", length = 5000)
    private String reviewerFeedback;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id")
    private Course course;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "submitted_by_id")
    private User submittedBy;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "reviewed_by_id")
    private User reviewedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public Long getCourseVersion() {
        return courseVersion;
    }

    public void setCourseVersion(Long value) {
        this.courseVersion = value;
    }

    public CourseReviewRequestStatus getStatus() {
        return status;
    }

    public void setStatus(CourseReviewRequestStatus value) {
        this.status = value;
    }

    public String getSubmissionNote() {
        return submissionNote;
    }

    public void setSubmissionNote(String value) {
        this.submissionNote = value;
    }

    public Instant getRightsConfirmedAt() {
        return rightsConfirmedAt;
    }

    public void setRightsConfirmedAt(Instant value) {
        this.rightsConfirmedAt = value;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Instant value) {
        this.submittedAt = value;
    }

    public Instant getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(Instant value) {
        this.reviewedAt = value;
    }

    public String getReviewerFeedback() {
        return reviewerFeedback;
    }

    public void setReviewerFeedback(String value) {
        this.reviewerFeedback = value;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course value) {
        this.course = value;
    }

    public User getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(User value) {
        this.submittedBy = value;
    }

    public User getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(User value) {
        this.reviewedBy = value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof CourseReviewRequest request && id != null && id.equals(request.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
