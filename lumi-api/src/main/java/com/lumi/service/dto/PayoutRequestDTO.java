package com.lumi.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.PayoutRequest} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PayoutRequestDTO implements Serializable {

    private Long id;

    @NotNull
    @DecimalMin(value = "0.01")
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
    private InstructorWalletDTO wallet;

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

    public InstructorWalletDTO getWallet() {
        return wallet;
    }

    public void setWallet(InstructorWalletDTO wallet) {
        this.wallet = wallet;
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
            ", wallet=" + getWallet() +
            "}";
    }
}
