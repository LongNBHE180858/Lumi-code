package com.lumi.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.CourseReviewRequest} entity.
 */
@Schema(description = "Lưu lần gửi duyệt khóa học, phiên bản gửi và phản hồi của Reviewer.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CourseReviewRequestDTO implements Serializable {

    private Long id;

    @NotNull
    private Long courseVersion;

    @NotNull
    @Size(max = 20)
    private String status;

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

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCourseVersion() {
        return courseVersion;
    }

    public void setCourseVersion(Long courseVersion) {
        this.courseVersion = courseVersion;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubmissionNote() {
        return submissionNote;
    }

    public void setSubmissionNote(String submissionNote) {
        this.submissionNote = submissionNote;
    }

    public Instant getRightsConfirmedAt() {
        return rightsConfirmedAt;
    }

    public void setRightsConfirmedAt(Instant rightsConfirmedAt) {
        this.rightsConfirmedAt = rightsConfirmedAt;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Instant getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(Instant reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public String getReviewerFeedback() {
        return reviewerFeedback;
    }

    public void setReviewerFeedback(String reviewerFeedback) {
        this.reviewerFeedback = reviewerFeedback;
    }

    public CourseDTO getCourse() {
        return course;
    }

    public void setCourse(CourseDTO course) {
        this.course = course;
    }

    public UserDTO getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(UserDTO submittedBy) {
        this.submittedBy = submittedBy;
    }

    public UserDTO getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(UserDTO reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CourseReviewRequestDTO)) {
            return false;
        }

        CourseReviewRequestDTO courseReviewRequestDTO = (CourseReviewRequestDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, courseReviewRequestDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CourseReviewRequestDTO{" +
            "id=" + getId() +
            ", courseVersion=" + getCourseVersion() +
            ", status='" + getStatus() + "'" +
            ", submissionNote='" + getSubmissionNote() + "'" +
            ", rightsConfirmedAt='" + getRightsConfirmedAt() + "'" +
            ", submittedAt='" + getSubmittedAt() + "'" +
            ", reviewedAt='" + getReviewedAt() + "'" +
            ", reviewerFeedback='" + getReviewerFeedback() + "'" +
            ", course=" + getCourse() +
            ", submittedBy=" + getSubmittedBy() +
            ", reviewedBy=" + getReviewedBy() +
            "}";
    }
}
