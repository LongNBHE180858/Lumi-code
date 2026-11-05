package com.lumi.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.domain.PayoutRequest} entity. This class is used
 * in {@link com.lumi.web.rest.PayoutRequestResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /payout-requests?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PayoutRequestCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private BigDecimalFilter amount;

    private StringFilter bankCode;

    private StringFilter bankAccount;

    private StringFilter status;

    private InstantFilter createdAt;

    private StringFilter accountHolderName;

    private InstantFilter processedAt;

    private StringFilter transferReference;

    private InstantFilter updatedAt;

    private LongFilter walletId;

    private LongFilter processedById;

    private Boolean distinct;

    public PayoutRequestCriteria() {}

    public PayoutRequestCriteria(PayoutRequestCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.amount = other.optionalAmount().map(BigDecimalFilter::copy).orElse(null);
        this.bankCode = other.optionalBankCode().map(StringFilter::copy).orElse(null);
        this.bankAccount = other.optionalBankAccount().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(StringFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.accountHolderName = other.optionalAccountHolderName().map(StringFilter::copy).orElse(null);
        this.processedAt = other.optionalProcessedAt().map(InstantFilter::copy).orElse(null);
        this.transferReference = other.optionalTransferReference().map(StringFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.walletId = other.optionalWalletId().map(LongFilter::copy).orElse(null);
        this.processedById = other.optionalProcessedById().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public PayoutRequestCriteria copy() {
        return new PayoutRequestCriteria(this);
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

    public StringFilter getBankCode() {
        return bankCode;
    }

    public Optional<StringFilter> optionalBankCode() {
        return Optional.ofNullable(bankCode);
    }

    public StringFilter bankCode() {
        if (bankCode == null) {
            setBankCode(new StringFilter());
        }
        return bankCode;
    }

    public void setBankCode(StringFilter bankCode) {
        this.bankCode = bankCode;
    }

    public StringFilter getBankAccount() {
        return bankAccount;
    }

    public Optional<StringFilter> optionalBankAccount() {
        return Optional.ofNullable(bankAccount);
    }

    public StringFilter bankAccount() {
        if (bankAccount == null) {
            setBankAccount(new StringFilter());
        }
        return bankAccount;
    }

    public void setBankAccount(StringFilter bankAccount) {
        this.bankAccount = bankAccount;
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

    public StringFilter getAccountHolderName() {
        return accountHolderName;
    }

    public Optional<StringFilter> optionalAccountHolderName() {
        return Optional.ofNullable(accountHolderName);
    }

    public StringFilter accountHolderName() {
        if (accountHolderName == null) {
            setAccountHolderName(new StringFilter());
        }
        return accountHolderName;
    }

    public void setAccountHolderName(StringFilter accountHolderName) {
        this.accountHolderName = accountHolderName;
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

    public StringFilter getTransferReference() {
        return transferReference;
    }

    public Optional<StringFilter> optionalTransferReference() {
        return Optional.ofNullable(transferReference);
    }

    public StringFilter transferReference() {
        if (transferReference == null) {
            setTransferReference(new StringFilter());
        }
        return transferReference;
    }

    public void setTransferReference(StringFilter transferReference) {
        this.transferReference = transferReference;
    }

    public InstantFilter getUpdatedAt() {
        return updatedAt;
    }

    public Optional<InstantFilter> optionalUpdatedAt() {
        return Optional.ofNullable(updatedAt);
    }

    public InstantFilter updatedAt() {
        if (updatedAt == null) {
            setUpdatedAt(new InstantFilter());
        }
        return updatedAt;
    }

    public void setUpdatedAt(InstantFilter updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LongFilter getWalletId() {
        return walletId;
    }

    public Optional<LongFilter> optionalWalletId() {
        return Optional.ofNullable(walletId);
    }

    public LongFilter walletId() {
        if (walletId == null) {
            setWalletId(new LongFilter());
        }
        return walletId;
    }

    public void setWalletId(LongFilter walletId) {
        this.walletId = walletId;
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
        final PayoutRequestCriteria that = (PayoutRequestCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(amount, that.amount) &&
            Objects.equals(bankCode, that.bankCode) &&
            Objects.equals(bankAccount, that.bankAccount) &&
            Objects.equals(status, that.status) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(accountHolderName, that.accountHolderName) &&
            Objects.equals(processedAt, that.processedAt) &&
            Objects.equals(transferReference, that.transferReference) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(walletId, that.walletId) &&
            Objects.equals(processedById, that.processedById) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            amount,
            bankCode,
            bankAccount,
            status,
            createdAt,
            accountHolderName,
            processedAt,
            transferReference,
            updatedAt,
            walletId,
            processedById,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PayoutRequestCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalAmount().map(f -> "amount=" + f + ", ").orElse("") +
            optionalBankCode().map(f -> "bankCode=" + f + ", ").orElse("") +
            optionalBankAccount().map(f -> "bankAccount=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalAccountHolderName().map(f -> "accountHolderName=" + f + ", ").orElse("") +
            optionalProcessedAt().map(f -> "processedAt=" + f + ", ").orElse("") +
            optionalTransferReference().map(f -> "transferReference=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalWalletId().map(f -> "walletId=" + f + ", ").orElse("") +
            optionalProcessedById().map(f -> "processedById=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
