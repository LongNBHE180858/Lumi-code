package com.lumi.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.PayoutRequest} entity.
 */
@Schema(description = "Lưu yêu cầu rút tiền và thông tin xử lý chuyển khoản cho giáo viên.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PayoutRequestDTO implements Serializable {

    private Long id;

    @NotNull
    private BigDecimal amount;

    @NotNull
    @Size(max = 50)
    private String bankCode;

    @NotNull
    @Size(max = 100)
    private String bankAccount;

    @Size(max = 20)
    private String status;

    private Instant createdAt;

    @NotNull
    @Size(max = 255)
    private String accountHolderName;

    private Instant processedAt;

    @Lob
    private String rejectionReason;

    @Size(max = 255)
    private String transferReference;

    @NotNull
    private Instant updatedAt;

    @NotNull
    private InstructorWalletDTO wallet;

    private UserDTO processedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Instant processedAt) {
        this.processedAt = processedAt;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getTransferReference() {
        return transferReference;
    }

    public void setTransferReference(String transferReference) {
        this.transferReference = transferReference;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public InstructorWalletDTO getWallet() {
        return wallet;
    }

    public void setWallet(InstructorWalletDTO wallet) {
        this.wallet = wallet;
    }

    public UserDTO getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(UserDTO processedBy) {
        this.processedBy = processedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PayoutRequestDTO)) {
            return false;
        }

        PayoutRequestDTO payoutRequestDTO = (PayoutRequestDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, payoutRequestDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PayoutRequestDTO{" +
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
            ", wallet=" + getWallet() +
            ", processedBy=" + getProcessedBy() +
            "}";
    }
}
