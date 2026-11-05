package com.lumi.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.ParentStudentLink} entity.
 */
@Schema(description = "Lưu liên kết phụ huynh - học sinh và trạng thái xác minh quan hệ giám hộ.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ParentStudentLinkDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 10)
    private String linkCode;

    @Size(max = 20)
    private String status;

    private Instant linkedAt;

    @NotNull
    private Instant createdAt;

    @NotNull
    private Instant codeExpiresAt;

    private Instant confirmedAt;

    private Instant revokedAt;

    @Size(max = 30)
    private String relationshipType;

    @NotNull
    @Size(max = 20)
    private String guardianVerificationStatus;

    private Instant guardianVerifiedAt;

    @Lob
    private String guardianVerificationNote;

    @NotNull
    private UserDTO parent;

    @NotNull
    private UserDTO student;

    private UserDTO confirmedBy;

    private UserDTO guardianVerifiedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLinkCode() {
        return linkCode;
    }

    public void setLinkCode(String linkCode) {
        this.linkCode = linkCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getLinkedAt() {
        return linkedAt;
    }

    public void setLinkedAt(Instant linkedAt) {
        this.linkedAt = linkedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getCodeExpiresAt() {
        return codeExpiresAt;
    }

    public void setCodeExpiresAt(Instant codeExpiresAt) {
        this.codeExpiresAt = codeExpiresAt;
    }

    public Instant getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(Instant confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public Instant getRevokedAt() {
        return revokedAt;
    }

    public void setRevokedAt(Instant revokedAt) {
        this.revokedAt = revokedAt;
    }

    public String getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
    }

    public String getGuardianVerificationStatus() {
        return guardianVerificationStatus;
    }

    public void setGuardianVerificationStatus(String guardianVerificationStatus) {
        this.guardianVerificationStatus = guardianVerificationStatus;
    }

    public Instant getGuardianVerifiedAt() {
        return guardianVerifiedAt;
    }

    public void setGuardianVerifiedAt(Instant guardianVerifiedAt) {
        this.guardianVerifiedAt = guardianVerifiedAt;
    }

    public String getGuardianVerificationNote() {
        return guardianVerificationNote;
    }

    public void setGuardianVerificationNote(String guardianVerificationNote) {
        this.guardianVerificationNote = guardianVerificationNote;
    }

    public UserDTO getParent() {
        return parent;
    }

    public void setParent(UserDTO parent) {
        this.parent = parent;
    }

    public UserDTO getStudent() {
        return student;
    }

    public void setStudent(UserDTO student) {
        this.student = student;
    }

    public UserDTO getConfirmedBy() {
        return confirmedBy;
    }

    public void setConfirmedBy(UserDTO confirmedBy) {
        this.confirmedBy = confirmedBy;
    }

    public UserDTO getGuardianVerifiedBy() {
        return guardianVerifiedBy;
    }

    public void setGuardianVerifiedBy(UserDTO guardianVerifiedBy) {
        this.guardianVerifiedBy = guardianVerifiedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ParentStudentLinkDTO)) {
            return false;
        }

        ParentStudentLinkDTO parentStudentLinkDTO = (ParentStudentLinkDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, parentStudentLinkDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ParentStudentLinkDTO{" +
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
            ", parent=" + getParent() +
            ", student=" + getStudent() +
            ", confirmedBy=" + getConfirmedBy() +
            ", guardianVerifiedBy=" + getGuardianVerifiedBy() +
            "}";
    }
}
