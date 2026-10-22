package com.lumi.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * A TeacherVerification.
 */
@Entity
@Table(name = "teacher_verification")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TeacherVerification implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 1000)
    @Column(name = "identity_document_url", length = 1000, nullable = false)
    private String identityDocumentUrl;

    @Size(max = 20)
    @Column(name = "verification_status", length = 20)
    private String verificationStatus;

    @Size(max = 5000)
    @Column(name = "notes", length = 5000)
    private String notes;

    @Column(name = "submitted_at")
    private Instant submittedAt;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TeacherVerification id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentityDocumentUrl() {
        return this.identityDocumentUrl;
    }

    public TeacherVerification identityDocumentUrl(String identityDocumentUrl) {
        this.setIdentityDocumentUrl(identityDocumentUrl);
        return this;
    }

    public void setIdentityDocumentUrl(String identityDocumentUrl) {
        this.identityDocumentUrl = identityDocumentUrl;
    }

    public String getVerificationStatus() {
        return this.verificationStatus;
    }

    public TeacherVerification verificationStatus(String verificationStatus) {
        this.setVerificationStatus(verificationStatus);
        return this;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getNotes() {
        return this.notes;
    }

    public TeacherVerification notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Instant getSubmittedAt() {
        return this.submittedAt;
    }

    public TeacherVerification submittedAt(Instant submittedAt) {
        this.setSubmittedAt(submittedAt);
        return this;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Instant getApprovedAt() {
        return this.approvedAt;
    }

    public TeacherVerification approvedAt(Instant approvedAt) {
        this.setApprovedAt(approvedAt);
        return this;
    }

    public void setApprovedAt(Instant approvedAt) {
        this.approvedAt = approvedAt;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public TeacherVerification user(User user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TeacherVerification)) {
            return false;
        }
        return getId() != null && getId().equals(((TeacherVerification) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TeacherVerification{" +
            "id=" + getId() +
            ", identityDocumentUrl='" + getIdentityDocumentUrl() + "'" +
            ", verificationStatus='" + getVerificationStatus() + "'" +
            ", notes='" + getNotes() + "'" +
            ", submittedAt='" + getSubmittedAt() + "'" +
            ", approvedAt='" + getApprovedAt() + "'" +
            "}";
    }
}
