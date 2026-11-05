package com.lumi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Lưu yêu cầu hoàn tiền, kết quả xử lý và việc thu hồi quyền học.
 */
@Entity
@Table(name = "refunds")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Refund implements Serializable {

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

    @Column(name = "reason", columnDefinition = "text")
    private String reason;

    @Size(max = 20)
    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "processed_at")
    private Instant processedAt;

    @Column(name = "review_note", columnDefinition = "text")
    private String reviewNote;

    @Size(max = 255)
    @Column(name = "refund_reference", length = 255)
    private String refundReference;

    @Column(name = "access_revoked_at")
    private Instant accessRevokedAt;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "student", "course", "plan", "coupon" }, allowSetters = true)
    private CourseOrder order;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "order" }, allowSetters = true)
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY)
    private User processedBy;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Refund id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public Refund amount(BigDecimal amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getReason() {
        return this.reason;
    }

    public Refund reason(String reason) {
        this.setReason(reason);
        return this;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return this.status;
    }

    public Refund status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Refund createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getProcessedAt() {
        return this.processedAt;
    }

    public Refund processedAt(Instant processedAt) {
        this.setProcessedAt(processedAt);
        return this;
    }

    public void setProcessedAt(Instant processedAt) {
        this.processedAt = processedAt;
    }

    public String getReviewNote() {
        return this.reviewNote;
    }

    public Refund reviewNote(String reviewNote) {
        this.setReviewNote(reviewNote);
        return this;
    }

    public void setReviewNote(String reviewNote) {
        this.reviewNote = reviewNote;
    }

    public String getRefundReference() {
        return this.refundReference;
    }

    public Refund refundReference(String refundReference) {
        this.setRefundReference(refundReference);
        return this;
    }

    public void setRefundReference(String refundReference) {
        this.refundReference = refundReference;
    }

    public Instant getAccessRevokedAt() {
        return this.accessRevokedAt;
    }

    public Refund accessRevokedAt(Instant accessRevokedAt) {
        this.setAccessRevokedAt(accessRevokedAt);
        return this;
    }

    public void setAccessRevokedAt(Instant accessRevokedAt) {
        this.accessRevokedAt = accessRevokedAt;
    }

    public CourseOrder getOrder() {
        return this.order;
    }

    public void setOrder(CourseOrder courseOrder) {
        this.order = courseOrder;
    }

    public Refund order(CourseOrder courseOrder) {
        this.setOrder(courseOrder);
        return this;
    }

    public Payment getPayment() {
        return this.payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Refund payment(Payment payment) {
        this.setPayment(payment);
        return this;
    }

    public User getProcessedBy() {
        return this.processedBy;
    }

    public void setProcessedBy(User user) {
        this.processedBy = user;
    }

    public Refund processedBy(User user) {
        this.setProcessedBy(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Refund)) {
            return false;
        }
        return getId() != null && getId().equals(((Refund) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Refund{" +
            "id=" + getId() +
            ", amount=" + getAmount() +
            ", reason='" + getReason() + "'" +
            ", status='" + getStatus() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", processedAt='" + getProcessedAt() + "'" +
            ", reviewNote='" + getReviewNote() + "'" +
            ", refundReference='" + getRefundReference() + "'" +
            ", accessRevokedAt='" + getAccessRevokedAt() + "'" +
            "}";
    }
}
