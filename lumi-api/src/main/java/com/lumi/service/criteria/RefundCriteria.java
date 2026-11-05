package com.lumi.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.domain.Refund} entity. This class is used
 * in {@link com.lumi.web.rest.RefundResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /refunds?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RefundCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private BigDecimalFilter amount;

    private StringFilter status;

    private InstantFilter createdAt;

    private InstantFilter processedAt;

    private StringFilter refundReference;

    private InstantFilter accessRevokedAt;

    private LongFilter orderId;

    private LongFilter paymentId;

    private LongFilter processedById;

    private Boolean distinct;

    public RefundCriteria() {}

    public RefundCriteria(RefundCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.amount = other.optionalAmount().map(BigDecimalFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(StringFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.processedAt = other.optionalProcessedAt().map(InstantFilter::copy).orElse(null);
        this.refundReference = other.optionalRefundReference().map(StringFilter::copy).orElse(null);
        this.accessRevokedAt = other.optionalAccessRevokedAt().map(InstantFilter::copy).orElse(null);
        this.orderId = other.optionalOrderId().map(LongFilter::copy).orElse(null);
        this.paymentId = other.optionalPaymentId().map(LongFilter::copy).orElse(null);
        this.processedById = other.optionalProcessedById().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public RefundCriteria copy() {
        return new RefundCriteria(this);
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

    public BigDecimalFilter getAmount() {
        return amount;
    }

    public Optional<BigDecimalFilter> optionalAmount() {
        return Optional.ofNullable(amount);
    }

    public BigDecimalFilter amount() {
        if (amount == null) {
            setAmount(new BigDecimalFilter());
        }
        return amount;
    }

    public void setAmount(BigDecimalFilter amount) {
        this.amount = amount;
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

    public InstantFilter getProcessedAt() {
        return processedAt;
    }

    public Optional<InstantFilter> optionalProcessedAt() {
        return Optional.ofNullable(processedAt);
    }

    public InstantFilter processedAt() {
        if (processedAt == null) {
            setProcessedAt(new InstantFilter());
        }
        return processedAt;
    }

    public void setProcessedAt(InstantFilter processedAt) {
        this.processedAt = processedAt;
    }

    public StringFilter getRefundReference() {
        return refundReference;
    }

    public Optional<StringFilter> optionalRefundReference() {
        return Optional.ofNullable(refundReference);
    }

    public StringFilter refundReference() {
        if (refundReference == null) {
            setRefundReference(new StringFilter());
        }
        return refundReference;
    }

    public void setRefundReference(StringFilter refundReference) {
        this.refundReference = refundReference;
    }

    public InstantFilter getAccessRevokedAt() {
        return accessRevokedAt;
    }

    public Optional<InstantFilter> optionalAccessRevokedAt() {
        return Optional.ofNullable(accessRevokedAt);
    }

    public InstantFilter accessRevokedAt() {
        if (accessRevokedAt == null) {
            setAccessRevokedAt(new InstantFilter());
        }
        return accessRevokedAt;
    }

    public void setAccessRevokedAt(InstantFilter accessRevokedAt) {
        this.accessRevokedAt = accessRevokedAt;
    }

    public LongFilter getOrderId() {
        return orderId;
    }

    public Optional<LongFilter> optionalOrderId() {
        return Optional.ofNullable(orderId);
    }

    public LongFilter orderId() {
        if (orderId == null) {
            setOrderId(new LongFilter());
        }
        return orderId;
    }

    public void setOrderId(LongFilter orderId) {
        this.orderId = orderId;
    }

    public LongFilter getPaymentId() {
        return paymentId;
    }

    public Optional<LongFilter> optionalPaymentId() {
        return Optional.ofNullable(paymentId);
    }

    public LongFilter paymentId() {
        if (paymentId == null) {
            setPaymentId(new LongFilter());
        }
        return paymentId;
    }

    public void setPaymentId(LongFilter paymentId) {
        this.paymentId = paymentId;
    }

    public LongFilter getProcessedById() {
        return processedById;
    }

    public Optional<LongFilter> optionalProcessedById() {
        return Optional.ofNullable(processedById);
    }

    public LongFilter processedById() {
        if (processedById == null) {
            setProcessedById(new LongFilter());
        }
        return processedById;
    }

    public void setProcessedById(LongFilter processedById) {
        this.processedById = processedById;
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
        final RefundCriteria that = (RefundCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(amount, that.amount) &&
            Objects.equals(status, that.status) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(processedAt, that.processedAt) &&
            Objects.equals(refundReference, that.refundReference) &&
            Objects.equals(accessRevokedAt, that.accessRevokedAt) &&
            Objects.equals(orderId, that.orderId) &&
            Objects.equals(paymentId, that.paymentId) &&
            Objects.equals(processedById, that.processedById) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            amount,
            status,
            createdAt,
            processedAt,
            refundReference,
            accessRevokedAt,
            orderId,
            paymentId,
            processedById,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RefundCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalAmount().map(f -> "amount=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalProcessedAt().map(f -> "processedAt=" + f + ", ").orElse("") +
            optionalRefundReference().map(f -> "refundReference=" + f + ", ").orElse("") +
            optionalAccessRevokedAt().map(f -> "accessRevokedAt=" + f + ", ").orElse("") +
            optionalOrderId().map(f -> "orderId=" + f + ", ").orElse("") +
            optionalPaymentId().map(f -> "paymentId=" + f + ", ").orElse("") +
            optionalProcessedById().map(f -> "processedById=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
