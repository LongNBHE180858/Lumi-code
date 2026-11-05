package com.lumi.service.dto;

import com.lumi.domain.enumeration.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.WalletTransaction} entity.
 */
@Schema(description = "Lưu sổ phát sinh tiền của ví giáo viên để đối soát doanh thu, hoàn và rút tiền.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WalletTransactionDTO implements Serializable {

    private Long id;

    @NotNull
    private TransactionType type;

    @NotNull
    private BigDecimal amount;

    @NotNull
    @Size(max = 20)
    private String status;

    @Size(max = 200)
    private String idempotencyKey;

    private Instant occurredAt;

    @NotNull
    @Size(max = 20)
    private String balanceBucket;

    @NotNull
    private InstructorWalletDTO wallet;

    private CourseOrderDTO order;

    private PayoutRequestDTO payoutRequest;

    private RefundDTO refund;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }

    public String getBalanceBucket() {
        return balanceBucket;
    }

    public void setBalanceBucket(String balanceBucket) {
        this.balanceBucket = balanceBucket;
    }

    public InstructorWalletDTO getWallet() {
        return wallet;
    }

    public void setWallet(InstructorWalletDTO wallet) {
        this.wallet = wallet;
    }

    public CourseOrderDTO getOrder() {
        return order;
    }

    public void setOrder(CourseOrderDTO order) {
        this.order = order;
    }

    public PayoutRequestDTO getPayoutRequest() {
        return payoutRequest;
    }

    public void setPayoutRequest(PayoutRequestDTO payoutRequest) {
        this.payoutRequest = payoutRequest;
    }

    public RefundDTO getRefund() {
        return refund;
    }

    public void setRefund(RefundDTO refund) {
        this.refund = refund;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WalletTransactionDTO)) {
            return false;
        }

        WalletTransactionDTO walletTransactionDTO = (WalletTransactionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, walletTransactionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WalletTransactionDTO{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", amount=" + getAmount() +
            ", status='" + getStatus() + "'" +
            ", idempotencyKey='" + getIdempotencyKey() + "'" +
            ", occurredAt='" + getOccurredAt() + "'" +
            ", balanceBucket='" + getBalanceBucket() + "'" +
            ", wallet=" + getWallet() +
            ", order=" + getOrder() +
            ", payoutRequest=" + getPayoutRequest() +
            ", refund=" + getRefund() +
            "}";
    }
}
