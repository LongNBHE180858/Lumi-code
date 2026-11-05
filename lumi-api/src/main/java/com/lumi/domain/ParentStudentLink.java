package com.lumi.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * Lưu liên kết phụ huynh - học sinh và trạng thái xác minh quan hệ giám hộ.
 */
@Entity
@Table(name = "parent_student_links")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ParentStudentLink implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 10)
    @Column(name = "link_code", length = 10, nullable = false, unique = true)
    private String linkCode;

    @Size(max = 20)
    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "linked_at")
    private Instant linkedAt;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "code_expires_at", nullable = false)
    private Instant codeExpiresAt;

    @Column(name = "confirmed_at")
    private Instant confirmedAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Size(max = 30)
    @Column(name = "relationship_type", length = 30)
    private String relationshipType;

    @NotNull
    @Size(max = 20)
    @Column(name = "guardian_verification_status", length = 20, nullable = false)
    private String guardianVerificationStatus;

    @Column(name = "guardian_verified_at")
    private Instant guardianVerifiedAt;

    @Column(name = "guardian_verification_note", columnDefinition = "text")
    private String guardianVerificationNote;

    @ManyToOne(optional = false)
    @NotNull
    private User parent;

    @ManyToOne(optional = false)
    @NotNull
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    private User confirmedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    private User guardianVerifiedBy;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ParentStudentLink id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLinkCode() {
        return this.linkCode;
    }

    public ParentStudentLink linkCode(String linkCode) {
        this.setLinkCode(linkCode);
        return this;
    }

    public void setLinkCode(String linkCode) {
        this.linkCode = linkCode;
    }

    public String getStatus() {
        return this.status;
    }

    public ParentStudentLink status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getLinkedAt() {
        return this.linkedAt;
    }

    public ParentStudentLink linkedAt(Instant linkedAt) {
        this.setLinkedAt(linkedAt);
        return this;
    }

    public void setLinkedAt(Instant linkedAt) {
        this.linkedAt = linkedAt;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public ParentStudentLink createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getCodeExpiresAt() {
        return this.codeExpiresAt;
    }

    public ParentStudentLink codeExpiresAt(Instant codeExpiresAt) {
        this.setCodeExpiresAt(codeExpiresAt);
        return this;
    }

    public void setCodeExpiresAt(Instant codeExpiresAt) {
        this.codeExpiresAt = codeExpiresAt;
    }

    public Instant getConfirmedAt() {
        return this.confirmedAt;
    }

    public ParentStudentLink confirmedAt(Instant confirmedAt) {
        this.setConfirmedAt(confirmedAt);
        return this;
    }

    public void setConfirmedAt(Instant confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public Instant getRevokedAt() {
        return this.revokedAt;
    }

    public ParentStudentLink revokedAt(Instant revokedAt) {
        this.setRevokedAt(revokedAt);
        return this;
    }

    public void setRevokedAt(Instant revokedAt) {
        this.revokedAt = revokedAt;
    }

    public String getRelationshipType() {
        return this.relationshipType;
    }

    public ParentStudentLink relationshipType(String relationshipType) {
        this.setRelationshipType(relationshipType);
        return this;
    }

    public void setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
    }

    public String getGuardianVerificationStatus() {
        return this.guardianVerificationStatus;
    }

    public ParentStudentLink guardianVerificationStatus(String guardianVerificationStatus) {
        this.setGuardianVerificationStatus(guardianVerificationStatus);
        return this;
    }

    public void setGuardianVerificationStatus(String guardianVerificationStatus) {
        this.guardianVerificationStatus = guardianVerificationStatus;
    }

    public Instant getGuardianVerifiedAt() {
        return this.guardianVerifiedAt;
    }

    public ParentStudentLink guardianVerifiedAt(Instant guardianVerifiedAt) {
        this.setGuardianVerifiedAt(guardianVerifiedAt);
        return this;
    }

    public void setGuardianVerifiedAt(Instant guardianVerifiedAt) {
        this.guardianVerifiedAt = guardianVerifiedAt;
    }

    public String getGuardianVerificationNote() {
        return this.guardianVerificationNote;
    }

    public ParentStudentLink guardianVerificationNote(String guardianVerificationNote) {
        this.setGuardianVerificationNote(guardianVerificationNote);
        return this;
    }

    public void setGuardianVerificationNote(String guardianVerificationNote) {
        this.guardianVerificationNote = guardianVerificationNote;
    }

    public User getParent() {
        return this.parent;
    }

    public void setParent(User user) {
        this.parent = user;
    }

    public ParentStudentLink parent(User user) {
        this.setParent(user);
        return this;
    }

    public User getStudent() {
        return this.student;
    }

    public void setStudent(User user) {
        this.student = user;
    }

    public ParentStudentLink student(User user) {
        this.setStudent(user);
        return this;
    }

    public User getConfirmedBy() {
        return this.confirmedBy;
    }

    public void setConfirmedBy(User user) {
        this.confirmedBy = user;
    }

    public ParentStudentLink confirmedBy(User user) {
        this.setConfirmedBy(user);
        return this;
    }

    public User getGuardianVerifiedBy() {
        return this.guardianVerifiedBy;
    }

    public void setGuardianVerifiedBy(User user) {
        this.guardianVerifiedBy = user;
    }

    public ParentStudentLink guardianVerifiedBy(User user) {
        this.setGuardianVerifiedBy(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ParentStudentLink)) {
            return false;
        }
        return getId() != null && getId().equals(((ParentStudentLink) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ParentStudentLink{" +
            "id=" + getId() +
            ", linkCode='" + getLinkCode() + "'" +
            ", status='" + getStatus() + "'" +
            ", linkedAt='" + getLinkedAt() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", codeExpiresAt='" + getCodeExpiresAt() + "'" +
            ", confirmedAt='" + getConfirmedAt() + "'" +
            ", revokedAt='" + getRevokedAt() + "'" +
            ", relationshipType='" + getRelationshipType() + "'" +
            ", guardianVerificationStatus='" + getGuardianVerificationStatus() + "'" +
            ", guardianVerifiedAt='" + getGuardianVerifiedAt() + "'" +
            ", guardianVerificationNote='" + getGuardianVerificationNote() + "'" +
            "}";
    }
}
