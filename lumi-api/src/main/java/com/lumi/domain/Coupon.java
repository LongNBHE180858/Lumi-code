package com.lumi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Lưu mã giảm giá, điều kiện sử dụng và tỷ lệ giáo viên chịu giảm giá.
 */
@Entity
@Table(name = "coupons")
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

    @Column(name = "max_uses")
    private Integer maxUses;

    @Column(name = "used_count")
    private Integer usedCount;

    @Column(name = "valid_to")
    private Instant validTo;

    @Column(name = "valid_from")
    private Instant validFrom;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "min_order_amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal minOrderAmount;

    @Min(value = 1)
    @Column(name = "max_uses_per_user")
    private Integer maxUsesPerUser;

    @DecimalMin(value = "0")
    @DecimalMax(value = "100")
    @Column(name = "instructor_discount_share_percent", precision = 21, scale = 2)
    private BigDecimal instructorDiscountSharePercent;

    @ManyToOne(fetch = FetchType.LAZY)
    private User instructor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "author", "subject", "grade" }, allowSetters = true)
    private Course course;

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

    public Instant getValidFrom() {
        return this.validFrom;
    }

    public Coupon validFrom(Instant validFrom) {
        this.setValidFrom(validFrom);
        return this;
    }

    public void setValidFrom(Instant validFrom) {
        this.validFrom = validFrom;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public Coupon isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public BigDecimal getMinOrderAmount() {
        return this.minOrderAmount;
    }

    public Coupon minOrderAmount(BigDecimal minOrderAmount) {
        this.setMinOrderAmount(minOrderAmount);
        return this;
    }

    public void setMinOrderAmount(BigDecimal minOrderAmount) {
        this.minOrderAmount = minOrderAmount;
    }

    public Integer getMaxUsesPerUser() {
        return this.maxUsesPerUser;
    }

    public Coupon maxUsesPerUser(Integer maxUsesPerUser) {
        this.setMaxUsesPerUser(maxUsesPerUser);
        return this;
    }

    public void setMaxUsesPerUser(Integer maxUsesPerUser) {
        this.maxUsesPerUser = maxUsesPerUser;
    }

    public BigDecimal getInstructorDiscountSharePercent() {
        return this.instructorDiscountSharePercent;
    }

    public Coupon instructorDiscountSharePercent(BigDecimal instructorDiscountSharePercent) {
        this.setInstructorDiscountSharePercent(instructorDiscountSharePercent);
        return this;
    }

    public void setInstructorDiscountSharePercent(BigDecimal instructorDiscountSharePercent) {
        this.instructorDiscountSharePercent = instructorDiscountSharePercent;
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

    public Course getCourse() {
        return this.course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Coupon course(Course course) {
        this.setCourse(course);
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
            ", validFrom='" + getValidFrom() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", minOrderAmount=" + getMinOrderAmount() +
            ", maxUsesPerUser=" + getMaxUsesPerUser() +
            ", instructorDiscountSharePercent=" + getInstructorDiscountSharePercent() +
            "}";
    }
}
