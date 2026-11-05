package com.lumi.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.Refund} entity.
 */
@Schema(description = "Lưu yêu cầu hoàn tiền, kết quả xử lý và việc thu hồi quyền học.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RefundDTO implements Serializable {

    private Long id;

    @NotNull
    private BigDecimal amount;

    @Lob
    private String reason;

    @Size(max = 20)
    private String status;

    private Instant createdAt;

    private Instant processedAt;

    @Lob
    private String reviewNote;

    @Size(max = 255)
    private String refundReference;

    private Instant accessRevokedAt;

    @NotNull
    private CourseOrderDTO order;

    @NotNull
    private PaymentDTO payment;

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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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

    public Instant getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Instant processedAt) {
        this.processedAt = processedAt;
    }

    public String getReviewNote() {
        return reviewNote;
    }

    public void setReviewNote(String reviewNote) {
        this.reviewNote = reviewNote;
    }

    public String getRefundReference() {
        return refundReference;
    }

    public void setRefundReference(String refundReference) {
        this.refundReference = refundReference;
    }

    public Instant getAccessRevokedAt() {
        return accessRevokedAt;
    }

    public void setAccessRevokedAt(Instant accessRevokedAt) {
        this.accessRevokedAt = accessRevokedAt;
    }

    public CourseOrderDTO getOrder() {
        return order;
    }

    public void setOrder(CourseOrderDTO order) {
        this.order = order;
    }

    public PaymentDTO getPayment() {
        return payment;
    }

    public void setPayment(PaymentDTO payment) {
        this.payment = payment;
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
        if (!(o instanceof RefundDTO)) {
            return false;
        }

        RefundDTO refundDTO = (RefundDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, refundDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RefundDTO{" +
            "id=" + getId() +
            ", amount=" + getAmount() +
            ", reason='" + getReason() + "'" +
            ", status='" + getStatus() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", processedAt='" + getProcessedAt() + "'" +
            ", reviewNote='" + getReviewNote() + "'" +
            ", refundReference='" + getRefundReference() + "'" +
            ", accessRevokedAt='" + getAccessRevokedAt() + "'" +
            ", order=" + getOrder() +
            ", payment=" + getPayment() +
            ", processedBy=" + getProcessedBy() +
            "}";
    }
}
