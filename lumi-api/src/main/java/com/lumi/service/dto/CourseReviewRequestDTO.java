package com.lumi.service.dto;

import com.lumi.domain.enumeration.CourseReviewRequestStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

public class CourseReviewRequestDTO implements Serializable {

    private Long id;

    @NotNull
    @Min(0)
    private Long courseVersion;

    @NotNull
    private CourseReviewRequestStatus status;

    @Size(max = 5000)
    private String submissionNote;

    @NotNull
    private Instant rightsConfirmedAt;

    @NotNull
    private Instant submittedAt;

    private Instant reviewedAt;

    @Size(max = 5000)
    private String reviewerFeedback;

    @NotNull
    private CourseDTO course;

    @NotNull
    private UserDTO submittedBy;

    private UserDTO reviewedBy;

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

    public CourseDTO getCourse() {
        return course;
    }

    public void setCourse(CourseDTO value) {
        this.course = value;
    }

    public UserDTO getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(UserDTO value) {
        this.submittedBy = value;
    }

    public UserDTO getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(UserDTO value) {
        this.reviewedBy = value;
    }
}
