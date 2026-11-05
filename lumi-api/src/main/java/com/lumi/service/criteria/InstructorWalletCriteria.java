package com.lumi.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.domain.InstructorWallet} entity. This class is used
 * in {@link com.lumi.web.rest.InstructorWalletResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /instructor-wallets?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InstructorWalletCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private BigDecimalFilter pendingAmount;

    private BigDecimalFilter availableAmount;

    private LongFilter rowVersion;

    private LongFilter instructorId;

    private Boolean distinct;

    public InstructorWalletCriteria() {}

    public InstructorWalletCriteria(InstructorWalletCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.pendingAmount = other.optionalPendingAmount().map(BigDecimalFilter::copy).orElse(null);
        this.availableAmount = other.optionalAvailableAmount().map(BigDecimalFilter::copy).orElse(null);
        this.rowVersion = other.optionalRowVersion().map(LongFilter::copy).orElse(null);
        this.instructorId = other.optionalInstructorId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public InstructorWalletCriteria copy() {
        return new InstructorWalletCriteria(this);
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

    public BigDecimalFilter getPendingAmount() {
        return pendingAmount;
    }

    public Optional<BigDecimalFilter> optionalPendingAmount() {
        return Optional.ofNullable(pendingAmount);
    }

    public BigDecimalFilter pendingAmount() {
        if (pendingAmount == null) {
            setPendingAmount(new BigDecimalFilter());
        }
        return pendingAmount;
    }

    public void setPendingAmount(BigDecimalFilter pendingAmount) {
        this.pendingAmount = pendingAmount;
    }

    public BigDecimalFilter getAvailableAmount() {
        return availableAmount;
    }

    public Optional<BigDecimalFilter> optionalAvailableAmount() {
        return Optional.ofNullable(availableAmount);
    }

    public BigDecimalFilter availableAmount() {
        if (availableAmount == null) {
            setAvailableAmount(new BigDecimalFilter());
        }
        return availableAmount;
    }

    public void setAvailableAmount(BigDecimalFilter availableAmount) {
        this.availableAmount = availableAmount;
    }

    public LongFilter getRowVersion() {
        return rowVersion;
    }

    public Optional<LongFilter> optionalRowVersion() {
        return Optional.ofNullable(rowVersion);
    }

    public LongFilter rowVersion() {
        if (rowVersion == null) {
            setRowVersion(new LongFilter());
        }
        return rowVersion;
    }

    public void setRowVersion(LongFilter rowVersion) {
        this.rowVersion = rowVersion;
    }

    public LongFilter getInstructorId() {
        return instructorId;
    }

    public Optional<LongFilter> optionalInstructorId() {
        return Optional.ofNullable(instructorId);
    }

    public LongFilter instructorId() {
        if (instructorId == null) {
            setInstructorId(new LongFilter());
        }
        return instructorId;
    }

    public void setInstructorId(LongFilter instructorId) {
        this.instructorId = instructorId;
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
        final InstructorWalletCriteria that = (InstructorWalletCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(pendingAmount, that.pendingAmount) &&
            Objects.equals(availableAmount, that.availableAmount) &&
            Objects.equals(rowVersion, that.rowVersion) &&
            Objects.equals(instructorId, that.instructorId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, pendingAmount, availableAmount, rowVersion, instructorId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InstructorWalletCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalPendingAmount().map(f -> "pendingAmount=" + f + ", ").orElse("") +
            optionalAvailableAmount().map(f -> "availableAmount=" + f + ", ").orElse("") +
            optionalRowVersion().map(f -> "rowVersion=" + f + ", ").orElse("") +
            optionalInstructorId().map(f -> "instructorId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
