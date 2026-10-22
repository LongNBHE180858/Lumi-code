package com.lumi.service.dto.auth;

import com.lumi.domain.TeacherVerification;
import java.io.Serializable;
import java.time.Instant;

public class TeacherVerificationAuthDTO implements Serializable {

    private Long id;
    private String verificationStatus;
    private String identityDocumentUrl;
    private String notes;
    private Instant submittedAt;
    private Instant approvedAt;

    public TeacherVerificationAuthDTO() {}

    public TeacherVerificationAuthDTO(TeacherVerification verification) {
        if (verification != null) {
            this.id = verification.getId();
            this.verificationStatus = verification.getVerificationStatus();
            this.identityDocumentUrl = verification.getIdentityDocumentUrl();
            this.notes = verification.getNotes();
            this.submittedAt = verification.getSubmittedAt();
            this.approvedAt = verification.getApprovedAt();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getIdentityDocumentUrl() {
        return identityDocumentUrl;
    }

    public void setIdentityDocumentUrl(String identityDocumentUrl) {
        this.identityDocumentUrl = identityDocumentUrl;
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
}
