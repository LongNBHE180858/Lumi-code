package com.lumi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Lưu yêu cầu rút tiền và thông tin xử lý chuyển khoản cho giáo viên.
 */
@Entity
@Table(name = "payout_requests")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PayoutRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal amount;

    @NotNull
    @Size(max = 50)
    @Column(name = "bank_code", length = 50, nullable = false)
    private String bankCode;

    @NotNull
    @Size(max = 100)
    @Column(name = "bank_account", length = 100, nullable = false)
    private String bankAccount;

    @Size(max = 20)
    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "created_at")
    private Instant createdAt;

    @NotNull
    @Size(max = 255)
    @Column(name = "account_holder_name", length = 255, nullable = false)
    private String accountHolderName;

    @Column(name = "processed_at")
    private Instant processedAt;

    @Column(name = "rejection_reason", columnDefinition = "text")
    private String rejectionReason;

    @Size(max = 255)
    @Column(name = "transfer_reference", length = 255)
    private String transferReference;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "instructor" }, allowSetters = true)
    private InstructorWallet wallet;

    @ManyToOne(fetch = FetchType.LAZY)
    private User processedBy;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PayoutRequest id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public PayoutRequest amount(BigDecimal amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getBankCode() {
        return this.bankCode;
    }

    public PayoutRequest bankCode(String bankCode) {
        this.setBankCode(bankCode);
        return this;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankAccount() {
        return this.bankAccount;
    }

    public PayoutRequest bankAccount(String bankAccount) {
        this.setBankAccount(bankAccount);
        return this;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getStatus() {
        return this.status;
    }

    public PayoutRequest status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public PayoutRequest createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getAccountHolderName() {
        return this.accountHolderName;
    }

    public PayoutRequest accountHolderName(String accountHolderName) {
        this.setAccountHolderName(accountHolderName);
        return this;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public Instant getProcessedAt() {
        return this.processedAt;
    }

    public PayoutRequest processedAt(Instant processedAt) {
        this.setProcessedAt(processedAt);
        return this;
    }

    public void setProcessedAt(Instant processedAt) {
        this.processedAt = processedAt;
    }

    public String getRejectionReason() {
        return this.rejectionReason;
    }

    public PayoutRequest rejectionReason(String rejectionReason) {
        this.setRejectionReason(rejectionReason);
        return this;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getTransferReference() {
        return this.transferReference;
    }

    public PayoutRequest transferReference(String transferReference) {
        this.setTransferReference(transferReference);
        return this;
    }

    public void setTransferReference(String transferReference) {
        this.transferReference = transferReference;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public PayoutRequest updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public InstructorWallet getWallet() {
        return this.wallet;
    }

    public void setWallet(InstructorWallet instructorWallet) {
        this.wallet = instructorWallet;
    }

    public PayoutRequest wallet(InstructorWallet instructorWallet) {
        this.setWallet(instructorWallet);
        return this;
    }

    public User getProcessedBy() {
        return this.processedBy;
    }

    public void setProcessedBy(User user) {
        this.processedBy = user;
    }

    public PayoutRequest processedBy(User user) {
        this.setProcessedBy(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PayoutRequest)) {
            return false;
        }
        return getId() != null && getId().equals(((PayoutRequest) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PayoutRequest{" +
            "id=" + getId() +
            ", amount=" + getAmount() +
            ", bankCode='" + getBankCode() + "'" +
            ", bankAccount='" + getBankAccount() + "'" +
            ", status='" + getStatus() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", accountHolderName='" + getAccountHolderName() + "'" +
            ", processedAt='" + getProcessedAt() + "'" +
            ", rejectionReason='" + getRejectionReason() + "'" +
            ", transferReference='" + getTransferReference() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
