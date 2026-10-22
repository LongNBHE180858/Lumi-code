package com.lumi.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.TeacherVerification} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TeacherVerificationDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 1000)
    private String identityDocumentUrl;

    @Size(max = 20)
    private String verificationStatus;

    @Size(max = 5000)
    private String notes;

    private Instant submittedAt;

    private Instant approvedAt;

    @NotNull
    private UserDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentityDocumentUrl() {
        return identityDocumentUrl;
    }

    public void setIdentityDocumentUrl(String identityDocumentUrl) {
        this.identityDocumentUrl = identityDocumentUrl;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Instant getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(Instant approvedAt) {
        this.approvedAt = approvedAt;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TeacherVerificationDTO)) {
            return false;
        }

        TeacherVerificationDTO teacherVerificationDTO = (TeacherVerificationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, teacherVerificationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TeacherVerificationDTO{" +
            "id=" + getId() +
            ", identityDocumentUrl='" + getIdentityDocumentUrl() + "'" +
            ", verificationStatus='" + getVerificationStatus() + "'" +
            ", notes='" + getNotes() + "'" +
            ", submittedAt='" + getSubmittedAt() + "'" +
            ", approvedAt='" + getApprovedAt() + "'" +
            ", user=" + getUser() +
            "}";
    }
}
