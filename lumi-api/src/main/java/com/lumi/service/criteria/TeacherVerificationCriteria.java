package com.lumi.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.domain.TeacherVerification} entity. This class is used
 * in {@link com.lumi.web.rest.TeacherVerificationResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /teacher-verifications?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TeacherVerificationCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter identityDocumentUrl;

    private StringFilter verificationStatus;

    private StringFilter notes;

    private InstantFilter submittedAt;

    private InstantFilter approvedAt;

    private LongFilter userId;

    private Boolean distinct;

    public TeacherVerificationCriteria() {}

    public TeacherVerificationCriteria(TeacherVerificationCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.identityDocumentUrl = other.optionalIdentityDocumentUrl().map(StringFilter::copy).orElse(null);
        this.verificationStatus = other.optionalVerificationStatus().map(StringFilter::copy).orElse(null);
        this.notes = other.optionalNotes().map(StringFilter::copy).orElse(null);
        this.submittedAt = other.optionalSubmittedAt().map(InstantFilter::copy).orElse(null);
        this.approvedAt = other.optionalApprovedAt().map(InstantFilter::copy).orElse(null);
        this.userId = other.optionalUserId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TeacherVerificationCriteria copy() {
        return new TeacherVerificationCriteria(this);
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

    public StringFilter getIdentityDocumentUrl() {
        return identityDocumentUrl;
    }

    public Optional<StringFilter> optionalIdentityDocumentUrl() {
        return Optional.ofNullable(identityDocumentUrl);
    }

    public StringFilter identityDocumentUrl() {
        if (identityDocumentUrl == null) {
            setIdentityDocumentUrl(new StringFilter());
        }
        return identityDocumentUrl;
    }

    public void setIdentityDocumentUrl(StringFilter identityDocumentUrl) {
        this.identityDocumentUrl = identityDocumentUrl;
    }

    public StringFilter getVerificationStatus() {
        return verificationStatus;
    }

    public Optional<StringFilter> optionalVerificationStatus() {
        return Optional.ofNullable(verificationStatus);
    }

    public StringFilter verificationStatus() {
        if (verificationStatus == null) {
            setVerificationStatus(new StringFilter());
        }
        return verificationStatus;
    }

    public void setVerificationStatus(StringFilter verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public StringFilter getNotes() {
        return notes;
    }

    public Optional<StringFilter> optionalNotes() {
        return Optional.ofNullable(notes);
    }

    public StringFilter notes() {
        if (notes == null) {
            setNotes(new StringFilter());
        }
        return notes;
    }

    public void setNotes(StringFilter notes) {
        this.notes = notes;
    }

    public InstantFilter getSubmittedAt() {
        return submittedAt;
    }

    public Optional<InstantFilter> optionalSubmittedAt() {
        return Optional.ofNullable(submittedAt);
    }

    public InstantFilter submittedAt() {
        if (submittedAt == null) {
            setSubmittedAt(new InstantFilter());
        }
        return submittedAt;
    }

    public void setSubmittedAt(InstantFilter submittedAt) {
        this.submittedAt = submittedAt;
    }

    public InstantFilter getApprovedAt() {
        return approvedAt;
    }

    public Optional<InstantFilter> optionalApprovedAt() {
        return Optional.ofNullable(approvedAt);
    }

    public InstantFilter approvedAt() {
        if (approvedAt == null) {
            setApprovedAt(new InstantFilter());
        }
        return approvedAt;
    }

    public void setApprovedAt(InstantFilter approvedAt) {
        this.approvedAt = approvedAt;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public Optional<LongFilter> optionalUserId() {
        return Optional.ofNullable(userId);
    }

    public LongFilter userId() {
        if (userId == null) {
            setUserId(new LongFilter());
        }
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
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
        final TeacherVerificationCriteria that = (TeacherVerificationCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(identityDocumentUrl, that.identityDocumentUrl) &&
            Objects.equals(verificationStatus, that.verificationStatus) &&
            Objects.equals(notes, that.notes) &&
            Objects.equals(submittedAt, that.submittedAt) &&
            Objects.equals(approvedAt, that.approvedAt) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, identityDocumentUrl, verificationStatus, notes, submittedAt, approvedAt, userId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TeacherVerificationCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalIdentityDocumentUrl().map(f -> "identityDocumentUrl=" + f + ", ").orElse("") +
            optionalVerificationStatus().map(f -> "verificationStatus=" + f + ", ").orElse("") +
            optionalNotes().map(f -> "notes=" + f + ", ").orElse("") +
            optionalSubmittedAt().map(f -> "submittedAt=" + f + ", ").orElse("") +
            optionalApprovedAt().map(f -> "approvedAt=" + f + ", ").orElse("") +
            optionalUserId().map(f -> "userId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
