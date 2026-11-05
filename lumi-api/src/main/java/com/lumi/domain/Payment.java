package com.lumi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Lưu giao dịch thanh toán và thông tin đối chiếu từ cổng thanh toán.
 */
@Entity
@Table(name = "payments")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Payment implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(name = "gateway", length = 50, nullable = false)
    private String gateway;

    @Size(max = 255)
    @Column(name = "transaction_no", length = 255, unique = true)
    private String transactionNo;

    @NotNull
    @Column(name = "amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal amount;

    @Size(max = 20)
    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Size(max = 255)
    @Column(name = "payment_reference", length = 255)
    private String paymentReference;

    @Size(max = 255)
    @Column(name = "provider_event_id", length = 255)
    private String providerEventId;

    @Column(name = "received_at")
    private Instant receivedAt;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "student", "course", "plan", "coupon" }, allowSetters = true)
    private CourseOrder order;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Payment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGateway() {
        return this.gateway;
    }

    public Payment gateway(String gateway) {
        this.setGateway(gateway);
        return this;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getTransactionNo() {
        return this.transactionNo;
    }

    public Payment transactionNo(String transactionNo) {
        this.setTransactionNo(transactionNo);
        return this;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public Payment amount(BigDecimal amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return this.status;
    }

    public Payment status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getPaidAt() {
        return this.paidAt;
    }

    public Payment paidAt(Instant paidAt) {
        this.setPaidAt(paidAt);
        return this;
    }

    public void setPaidAt(Instant paidAt) {
        this.paidAt = paidAt;
    }

    public String getPaymentReference() {
        return this.paymentReference;
    }

    public Payment paymentReference(String paymentReference) {
        this.setPaymentReference(paymentReference);
        return this;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public String getProviderEventId() {
        return this.providerEventId;
    }

    public Payment providerEventId(String providerEventId) {
        this.setProviderEventId(providerEventId);
        return this;
    }

    public void setProviderEventId(String providerEventId) {
        this.providerEventId = providerEventId;
    }

    public Instant getReceivedAt() {
        return this.receivedAt;
    }

    public Payment receivedAt(Instant receivedAt) {
        this.setReceivedAt(receivedAt);
        return this;
    }

    public void setReceivedAt(Instant receivedAt) {
        this.receivedAt = receivedAt;
    }

    public CourseOrder getOrder() {
        return this.order;
    }

    public void setOrder(CourseOrder courseOrder) {
        this.order = courseOrder;
    }

    public Payment order(CourseOrder courseOrder) {
        this.setOrder(courseOrder);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Payment)) {
            return false;
        }
        return getId() != null && getId().equals(((Payment) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Payment{" +
            "id=" + getId() +
            ", gateway='" + getGateway() + "'" +
            ", transactionNo='" + getTransactionNo() + "'" +
            ", amount=" + getAmount() +
            ", status='" + getStatus() + "'" +
            ", paidAt='" + getPaidAt() + "'" +
            ", paymentReference='" + getPaymentReference() + "'" +
            ", providerEventId='" + getProviderEventId() + "'" +
            ", receivedAt='" + getReceivedAt() + "'" +
            "}";
    }
}
