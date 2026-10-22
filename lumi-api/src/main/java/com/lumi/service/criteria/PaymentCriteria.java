package com.lumi.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.domain.Payment} entity. This class is used
 * in {@link com.lumi.web.rest.PaymentResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /payments?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PaymentCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter gateway;

    private StringFilter transactionNo;

    private BigDecimalFilter amount;

    private StringFilter status;

    private InstantFilter paidAt;

    private LongFilter orderId;

    private Boolean distinct;

    public PaymentCriteria() {}

    public PaymentCriteria(PaymentCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.gateway = other.optionalGateway().map(StringFilter::copy).orElse(null);
        this.transactionNo = other.optionalTransactionNo().map(StringFilter::copy).orElse(null);
        this.amount = other.optionalAmount().map(BigDecimalFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(StringFilter::copy).orElse(null);
        this.paidAt = other.optionalPaidAt().map(InstantFilter::copy).orElse(null);
        this.orderId = other.optionalOrderId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public PaymentCriteria copy() {
        return new PaymentCriteria(this);
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

    public StringFilter getGateway() {
        return gateway;
    }

    public Optional<StringFilter> optionalGateway() {
        return Optional.ofNullable(gateway);
    }

    public StringFilter gateway() {
        if (gateway == null) {
            setGateway(new StringFilter());
        }
        return gateway;
    }

    public void setGateway(StringFilter gateway) {
        this.gateway = gateway;
    }

    public StringFilter getTransactionNo() {
        return transactionNo;
    }

    public Optional<StringFilter> optionalTransactionNo() {
        return Optional.ofNullable(transactionNo);
    }

    public StringFilter transactionNo() {
        if (transactionNo == null) {
            setTransactionNo(new StringFilter());
        }
        return transactionNo;
    }

    public void setTransactionNo(StringFilter transactionNo) {
        this.transactionNo = transactionNo;
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

    public InstantFilter getPaidAt() {
        return paidAt;
    }

    public Optional<InstantFilter> optionalPaidAt() {
        return Optional.ofNullable(paidAt);
    }

    public InstantFilter paidAt() {
        if (paidAt == null) {
            setPaidAt(new InstantFilter());
        }
        return paidAt;
    }

    public void setPaidAt(InstantFilter paidAt) {
        this.paidAt = paidAt;
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
        final PaymentCriteria that = (PaymentCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(gateway, that.gateway) &&
            Objects.equals(transactionNo, that.transactionNo) &&
            Objects.equals(amount, that.amount) &&
            Objects.equals(status, that.status) &&
            Objects.equals(paidAt, that.paidAt) &&
            Objects.equals(orderId, that.orderId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, gateway, transactionNo, amount, status, paidAt, orderId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PaymentCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalGateway().map(f -> "gateway=" + f + ", ").orElse("") +
            optionalTransactionNo().map(f -> "transactionNo=" + f + ", ").orElse("") +
            optionalAmount().map(f -> "amount=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalPaidAt().map(f -> "paidAt=" + f + ", ").orElse("") +
            optionalOrderId().map(f -> "orderId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
