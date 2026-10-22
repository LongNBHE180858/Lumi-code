package com.lumi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lumi.domain.enumeration.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * A CourseOrder.
 */
@Entity
@Table(name = "course_order")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CourseOrder implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "original_price", precision = 21, scale = 2, nullable = false)
    private BigDecimal originalPrice;

    @DecimalMin(value = "0")
    @Column(name = "discount_amount", precision = 21, scale = 2)
    private BigDecimal discountAmount;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "final_price", precision = 21, scale = 2, nullable = false)
    private BigDecimal finalPrice;

    @Size(max = 10)
    @Column(name = "revenue_split_rate", length = 10)
    private String revenueSplitRate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;

    @Size(max = 200)
    @Column(name = "idempotency_key", length = 200, unique = true)
    private String idempotencyKey;

    @Min(value = 0L)
    @Column(name = "row_version")
    private Long rowVersion;

    @Column(name = "created_at")
    private Instant createdAt;

    @ManyToOne(optional = false)
    @NotNull
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "author", "category", "subject", "grade" }, allowSetters = true)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    private SystemPlan plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "instructor" }, allowSetters = true)
    private Coupon coupon;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CourseOrder id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getOriginalPrice() {
        return this.originalPrice;
    }

    public CourseOrder originalPrice(BigDecimal originalPrice) {
        this.setOriginalPrice(originalPrice);
        return this;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public BigDecimal getDiscountAmount() {
        return this.discountAmount;
    }

    public CourseOrder discountAmount(BigDecimal discountAmount) {
        this.setDiscountAmount(discountAmount);
        return this;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getFinalPrice() {
        return this.finalPrice;
    }

    public CourseOrder finalPrice(BigDecimal finalPrice) {
        this.setFinalPrice(finalPrice);
        return this;
    }

    public void setFinalPrice(BigDecimal finalPrice) {
        this.finalPrice = finalPrice;
    }

    public String getRevenueSplitRate() {
        return this.revenueSplitRate;
    }

    public CourseOrder revenueSplitRate(String revenueSplitRate) {
        this.setRevenueSplitRate(revenueSplitRate);
        return this;
    }

    public void setRevenueSplitRate(String revenueSplitRate) {
        this.revenueSplitRate = revenueSplitRate;
    }

    public OrderStatus getStatus() {
        return this.status;
    }

    public CourseOrder status(OrderStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getIdempotencyKey() {
        return this.idempotencyKey;
    }

    public CourseOrder idempotencyKey(String idempotencyKey) {
        this.setIdempotencyKey(idempotencyKey);
        return this;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public Long getRowVersion() {
        return this.rowVersion;
    }

    public CourseOrder rowVersion(Long rowVersion) {
        this.setRowVersion(rowVersion);
        return this;
    }

    public void setRowVersion(Long rowVersion) {
        this.rowVersion = rowVersion;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public CourseOrder createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public User getStudent() {
        return this.student;
    }

    public void setStudent(User user) {
        this.student = user;
    }

    public CourseOrder student(User user) {
        this.setStudent(user);
        return this;
    }

    public Course getCourse() {
        return this.course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public CourseOrder course(Course course) {
        this.setCourse(course);
        return this;
    }

    public SystemPlan getPlan() {
        return this.plan;
    }

    public void setPlan(SystemPlan systemPlan) {
        this.plan = systemPlan;
    }

    public CourseOrder plan(SystemPlan systemPlan) {
        this.setPlan(systemPlan);
        return this;
    }

    public Coupon getCoupon() {
        return this.coupon;
    }

    public void setCoupon(Coupon coupon) {
        this.coupon = coupon;
    }

    public CourseOrder coupon(Coupon coupon) {
        this.setCoupon(coupon);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CourseOrder)) {
            return false;
        }
        return getId() != null && getId().equals(((CourseOrder) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CourseOrder{" +
            "id=" + getId() +
            ", originalPrice=" + getOriginalPrice() +
            ", discountAmount=" + getDiscountAmount() +
            ", finalPrice=" + getFinalPrice() +
            ", revenueSplitRate='" + getRevenueSplitRate() + "'" +
            ", status='" + getStatus() + "'" +
            ", idempotencyKey='" + getIdempotencyKey() + "'" +
            ", rowVersion=" + getRowVersion() +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
