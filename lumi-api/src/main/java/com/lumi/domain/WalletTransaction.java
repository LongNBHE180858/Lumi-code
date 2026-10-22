package com.lumi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lumi.domain.enumeration.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * A WalletTransaction.
 */
@Entity
@Table(name = "wallet_transaction")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WalletTransaction implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TransactionType type;

    @NotNull
    @Column(name = "amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal amount;

    @NotNull
    @Size(max = 20)
    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Size(max = 200)
    @Column(name = "idempotency_key", length = 200, unique = true)
    private String idempotencyKey;

    @Column(name = "occurred_at")
    private Instant occurredAt;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "user" }, allowSetters = true)
    private InstructorWallet wallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "student", "course", "plan", "coupon" }, allowSetters = true)
    private CourseOrder order;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public WalletTransaction id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TransactionType getType() {
        return this.type;
    }

    public WalletTransaction type(TransactionType type) {
        this.setType(type);
        return this;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public WalletTransaction amount(BigDecimal amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return this.status;
    }

    public WalletTransaction status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIdempotencyKey() {
        return this.idempotencyKey;
    }

    public WalletTransaction idempotencyKey(String idempotencyKey) {
        this.setIdempotencyKey(idempotencyKey);
        return this;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public Instant getOccurredAt() {
        return this.occurredAt;
    }

    public WalletTransaction occurredAt(Instant occurredAt) {
        this.setOccurredAt(occurredAt);
        return this;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }

    public InstructorWallet getWallet() {
        return this.wallet;
    }

    public void setWallet(InstructorWallet instructorWallet) {
        this.wallet = instructorWallet;
    }

    public WalletTransaction wallet(InstructorWallet instructorWallet) {
        this.setWallet(instructorWallet);
        return this;
    }

    public CourseOrder getOrder() {
        return this.order;
    }

    public void setOrder(CourseOrder courseOrder) {
        this.order = courseOrder;
    }

    public WalletTransaction order(CourseOrder courseOrder) {
        this.setOrder(courseOrder);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WalletTransaction)) {
            return false;
        }
        return getId() != null && getId().equals(((WalletTransaction) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WalletTransaction{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", amount=" + getAmount() +
            ", status='" + getStatus() + "'" +
            ", idempotencyKey='" + getIdempotencyKey() + "'" +
            ", occurredAt='" + getOccurredAt() + "'" +
            "}";
    }
}
