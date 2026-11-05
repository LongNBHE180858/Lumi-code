package com.lumi.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.domain.ParentStudentLink} entity. This class is used
 * in {@link com.lumi.web.rest.ParentStudentLinkResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /parent-student-links?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ParentStudentLinkCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter linkCode;

    private StringFilter status;

    private InstantFilter linkedAt;

    private InstantFilter createdAt;

    private InstantFilter codeExpiresAt;

    private InstantFilter confirmedAt;

    private InstantFilter revokedAt;

    private StringFilter relationshipType;

    private StringFilter guardianVerificationStatus;

    private InstantFilter guardianVerifiedAt;

    private LongFilter parentId;

    private LongFilter studentId;

    private LongFilter confirmedById;

    private LongFilter guardianVerifiedById;

    private Boolean distinct;

    public ParentStudentLinkCriteria() {}

    public ParentStudentLinkCriteria(ParentStudentLinkCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.linkCode = other.optionalLinkCode().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(StringFilter::copy).orElse(null);
        this.linkedAt = other.optionalLinkedAt().map(InstantFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.codeExpiresAt = other.optionalCodeExpiresAt().map(InstantFilter::copy).orElse(null);
        this.confirmedAt = other.optionalConfirmedAt().map(InstantFilter::copy).orElse(null);
        this.revokedAt = other.optionalRevokedAt().map(InstantFilter::copy).orElse(null);
        this.relationshipType = other.optionalRelationshipType().map(StringFilter::copy).orElse(null);
        this.guardianVerificationStatus = other.optionalGuardianVerificationStatus().map(StringFilter::copy).orElse(null);
        this.guardianVerifiedAt = other.optionalGuardianVerifiedAt().map(InstantFilter::copy).orElse(null);
        this.parentId = other.optionalParentId().map(LongFilter::copy).orElse(null);
        this.studentId = other.optionalStudentId().map(LongFilter::copy).orElse(null);
        this.confirmedById = other.optionalConfirmedById().map(LongFilter::copy).orElse(null);
        this.guardianVerifiedById = other.optionalGuardianVerifiedById().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ParentStudentLinkCriteria copy() {
        return new ParentStudentLinkCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getLinkCode() {
        return linkCode;
    }

    public Optional<StringFilter> optionalLinkCode() {
        return Optional.ofNullable(linkCode);
    }

    public StringFilter linkCode() {
        if (linkCode == null) {
            setLinkCode(new StringFilter());
        }
        return linkCode;
    }

    public void setLinkCode(StringFilter linkCode) {
        this.linkCode = linkCode;
    }

    public StringFilter getStatus() {
        return status;
    }

    public Optional<StringFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public StringFilter status() {
        if (status == null) {
            setStatus(new StringFilter());
        }
        return status;
    }

    public void setStatus(StringFilter status) {
        this.status = status;
    }

    public InstantFilter getLinkedAt() {
        return linkedAt;
    }

    public Optional<InstantFilter> optionalLinkedAt() {
        return Optional.ofNullable(linkedAt);
    }

    public InstantFilter linkedAt() {
        if (linkedAt == null) {
            setLinkedAt(new InstantFilter());
        }
        return linkedAt;
    }

    public void setLinkedAt(InstantFilter linkedAt) {
        this.linkedAt = linkedAt;
    }

    public InstantFilter getCreatedAt() {
        return createdAt;
    }

    public Optional<InstantFilter> optionalCreatedAt() {
        return Optional.ofNullable(createdAt);
    }

    public InstantFilter createdAt() {
        if (createdAt == null) {
            setCreatedAt(new InstantFilter());
        }
        return createdAt;
    }

    public void setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
    }

    public InstantFilter getCodeExpiresAt() {
        return codeExpiresAt;
    }

    public Optional<InstantFilter> optionalCodeExpiresAt() {
        return Optional.ofNullable(codeExpiresAt);
    }

    public InstantFilter codeExpiresAt() {
        if (codeExpiresAt == null) {
            setCodeExpiresAt(new InstantFilter());
        }
        return codeExpiresAt;
    }

    public void setCodeExpiresAt(InstantFilter codeExpiresAt) {
        this.codeExpiresAt = codeExpiresAt;
    }

    public InstantFilter getConfirmedAt() {
        return confirmedAt;
    }

    public Optional<InstantFilter> optionalConfirmedAt() {
        return Optional.ofNullable(confirmedAt);
    }

    public InstantFilter confirmedAt() {
        if (confirmedAt == null) {
            setConfirmedAt(new InstantFilter());
        }
        return confirmedAt;
    }

    public void setConfirmedAt(InstantFilter confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public InstantFilter getRevokedAt() {
        return revokedAt;
    }

    public Optional<InstantFilter> optionalRevokedAt() {
        return Optional.ofNullable(revokedAt);
    }

    public InstantFilter revokedAt() {
        if (revokedAt == null) {
            setRevokedAt(new InstantFilter());
        }
        return revokedAt;
    }

    public void setRevokedAt(InstantFilter revokedAt) {
        this.revokedAt = revokedAt;
    }

    public StringFilter getRelationshipType() {
        return relationshipType;
    }

    public Optional<StringFilter> optionalRelationshipType() {
        return Optional.ofNullable(relationshipType);
    }

    public StringFilter relationshipType() {
        if (relationshipType == null) {
            setRelationshipType(new StringFilter());
        }
        return relationshipType;
    }

    public void setRelationshipType(StringFilter relationshipType) {
        this.relationshipType = relationshipType;
    }

    public StringFilter getGuardianVerificationStatus() {
        return guardianVerificationStatus;
    }

    public Optional<StringFilter> optionalGuardianVerificationStatus() {
        return Optional.ofNullable(guardianVerificationStatus);
    }

    public StringFilter guardianVerificationStatus() {
        if (guardianVerificationStatus == null) {
            setGuardianVerificationStatus(new StringFilter());
        }
        return guardianVerificationStatus;
    }

    public void setGuardianVerificationStatus(StringFilter guardianVerificationStatus) {
        this.guardianVerificationStatus = guardianVerificationStatus;
    }

    public InstantFilter getGuardianVerifiedAt() {
        return guardianVerifiedAt;
    }

    public Optional<InstantFilter> optionalGuardianVerifiedAt() {
        return Optional.ofNullable(guardianVerifiedAt);
    }

    public InstantFilter guardianVerifiedAt() {
        if (guardianVerifiedAt == null) {
            setGuardianVerifiedAt(new InstantFilter());
        }
        return guardianVerifiedAt;
    }

    public void setGuardianVerifiedAt(InstantFilter guardianVerifiedAt) {
        this.guardianVerifiedAt = guardianVerifiedAt;
    }

    public LongFilter getParentId() {
        return parentId;
    }

    public Optional<LongFilter> optionalParentId() {
        return Optional.ofNullable(parentId);
    }

    public LongFilter parentId() {
        if (parentId == null) {
            setParentId(new LongFilter());
        }
        return parentId;
    }

    public void setParentId(LongFilter parentId) {
        this.parentId = parentId;
    }

    public LongFilter getStudentId() {
        return studentId;
    }

    public Optional<LongFilter> optionalStudentId() {
        return Optional.ofNullable(studentId);
    }

    public LongFilter studentId() {
        if (studentId == null) {
            setStudentId(new LongFilter());
        }
        return studentId;
    }

    public void setStudentId(LongFilter studentId) {
        this.studentId = studentId;
    }

    public LongFilter getConfirmedById() {
        return confirmedById;
    }

    public Optional<LongFilter> optionalConfirmedById() {
        return Optional.ofNullable(confirmedById);
    }

    public LongFilter confirmedById() {
        if (confirmedById == null) {
            setConfirmedById(new LongFilter());
        }
        return confirmedById;
    }

    public void setConfirmedById(LongFilter confirmedById) {
        this.confirmedById = confirmedById;
    }

    public LongFilter getGuardianVerifiedById() {
        return guardianVerifiedById;
    }

    public Optional<LongFilter> optionalGuardianVerifiedById() {
        return Optional.ofNullable(guardianVerifiedById);
    }

    public LongFilter guardianVerifiedById() {
        if (guardianVerifiedById == null) {
            setGuardianVerifiedById(new LongFilter());
        }
        return guardianVerifiedById;
    }

    public void setGuardianVerifiedById(LongFilter guardianVerifiedById) {
        this.guardianVerifiedById = guardianVerifiedById;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ParentStudentLinkCriteria that = (ParentStudentLinkCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(linkCode, that.linkCode) &&
            Objects.equals(status, that.status) &&
            Objects.equals(linkedAt, that.linkedAt) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(codeExpiresAt, that.codeExpiresAt) &&
            Objects.equals(confirmedAt, that.confirmedAt) &&
            Objects.equals(revokedAt, that.revokedAt) &&
            Objects.equals(relationshipType, that.relationshipType) &&
            Objects.equals(guardianVerificationStatus, that.guardianVerificationStatus) &&
            Objects.equals(guardianVerifiedAt, that.guardianVerifiedAt) &&
            Objects.equals(parentId, that.parentId) &&
            Objects.equals(studentId, that.studentId) &&
            Objects.equals(confirmedById, that.confirmedById) &&
            Objects.equals(guardianVerifiedById, that.guardianVerifiedById) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            linkCode,
            status,
            linkedAt,
            createdAt,
            codeExpiresAt,
            confirmedAt,
            revokedAt,
            relationshipType,
            guardianVerificationStatus,
            guardianVerifiedAt,
            parentId,
            studentId,
            confirmedById,
            guardianVerifiedById,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ParentStudentLinkCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalLinkCode().map(f -> "linkCode=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalLinkedAt().map(f -> "linkedAt=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalCodeExpiresAt().map(f -> "codeExpiresAt=" + f + ", ").orElse("") +
            optionalConfirmedAt().map(f -> "confirmedAt=" + f + ", ").orElse("") +
            optionalRevokedAt().map(f -> "revokedAt=" + f + ", ").orElse("") +
            optionalRelationshipType().map(f -> "relationshipType=" + f + ", ").orElse("") +
            optionalGuardianVerificationStatus().map(f -> "guardianVerificationStatus=" + f + ", ").orElse("") +
            optionalGuardianVerifiedAt().map(f -> "guardianVerifiedAt=" + f + ", ").orElse("") +
            optionalParentId().map(f -> "parentId=" + f + ", ").orElse("") +
            optionalStudentId().map(f -> "studentId=" + f + ", ").orElse("") +
            optionalConfirmedById().map(f -> "confirmedById=" + f + ", ").orElse("") +
            optionalGuardianVerifiedById().map(f -> "guardianVerifiedById=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
