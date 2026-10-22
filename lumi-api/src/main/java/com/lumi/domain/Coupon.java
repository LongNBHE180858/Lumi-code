package com.lumi.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * A Coupon.
 */
@Entity
@Table(name = "coupon")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Coupon implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(name = "code", length = 50, nullable = false, unique = true)
    private String code;

    @Min(value = 1)
    @Max(value = 100)
    @Column(name = "discount_percent")
    private Integer discountPercent;

    @Min(value = 1)
    @Column(name = "max_uses")
    private Integer maxUses;

    @Min(value = 0)
    @Column(name = "used_count")
    private Integer usedCount;

    @Column(name = "valid_to")
    private Instant validTo;

    @ManyToOne(fetch = FetchType.LAZY)
    private User instructor;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Coupon id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public Coupon code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getDiscountPercent() {
        return this.discountPercent;
    }

    public Coupon discountPercent(Integer discountPercent) {
        this.setDiscountPercent(discountPercent);
        return this;
    }

    public void setDiscountPercent(Integer discountPercent) {
        this.discountPercent = discountPercent;
    }

    public Integer getMaxUses() {
        return this.maxUses;
    }

    public Coupon maxUses(Integer maxUses) {
        this.setMaxUses(maxUses);
        return this;
    }

    public void setMaxUses(Integer maxUses) {
        this.maxUses = maxUses;
    }

    public Integer getUsedCount() {
        return this.usedCount;
    }

    public Coupon usedCount(Integer usedCount) {
        this.setUsedCount(usedCount);
        return this;
    }

    public void setUsedCount(Integer usedCount) {
        this.usedCount = usedCount;
    }

    public Instant getValidTo() {
        return this.validTo;
    }

    public Coupon validTo(Instant validTo) {
        this.setValidTo(validTo);
        return this;
    }

    public void setValidTo(Instant validTo) {
        this.validTo = validTo;
    }

    public User getInstructor() {
        return this.instructor;
    }

    public void setInstructor(User user) {
        this.instructor = user;
    }

    public Coupon instructor(User user) {
        this.setInstructor(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Coupon)) {
            return false;
        }
        return getId() != null && getId().equals(((Coupon) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Coupon{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", discountPercent=" + getDiscountPercent() +
            ", maxUses=" + getMaxUses() +
            ", usedCount=" + getUsedCount() +
            ", validTo='" + getValidTo() + "'" +
            "}";
    }
}
