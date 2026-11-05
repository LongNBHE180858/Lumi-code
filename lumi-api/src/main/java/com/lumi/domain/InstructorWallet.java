package com.lumi.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Lưu số dư tiền chờ giải phóng và tiền khả dụng của giáo viên.
 */
@Entity
@Table(name = "instructor_wallets")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InstructorWallet implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "pending_amount", precision = 21, scale = 2)
    private BigDecimal pendingAmount;

    @Column(name = "available_amount", precision = 21, scale = 2)
    private BigDecimal availableAmount;

    @Column(name = "row_version")
    private Long rowVersion;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private User instructor;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public InstructorWallet id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getPendingAmount() {
        return this.pendingAmount;
    }

    public InstructorWallet pendingAmount(BigDecimal pendingAmount) {
        this.setPendingAmount(pendingAmount);
        return this;
    }

    public void setPendingAmount(BigDecimal pendingAmount) {
        this.pendingAmount = pendingAmount;
    }

    public BigDecimal getAvailableAmount() {
        return this.availableAmount;
    }

    public InstructorWallet availableAmount(BigDecimal availableAmount) {
        this.setAvailableAmount(availableAmount);
        return this;
    }

    public void setAvailableAmount(BigDecimal availableAmount) {
        this.availableAmount = availableAmount;
    }

    public Long getRowVersion() {
        return this.rowVersion;
    }

    public InstructorWallet rowVersion(Long rowVersion) {
        this.setRowVersion(rowVersion);
        return this;
    }

    public void setRowVersion(Long rowVersion) {
        this.rowVersion = rowVersion;
    }

    public User getInstructor() {
        return this.instructor;
    }

    public void setInstructor(User user) {
        this.instructor = user;
    }

    public InstructorWallet instructor(User user) {
        this.setInstructor(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InstructorWallet)) {
            return false;
        }
        return getId() != null && getId().equals(((InstructorWallet) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InstructorWallet{" +
            "id=" + getId() +
            ", pendingAmount=" + getPendingAmount() +
            ", availableAmount=" + getAvailableAmount() +
            ", rowVersion=" + getRowVersion() +
            "}";
    }
}
