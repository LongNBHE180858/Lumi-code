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
 * Lưu đơn mua khóa học hoặc gói dịch vụ cùng quyền lợi và chính sách giá tại lúc mua.
 */
@Entity
@Table(name = "orders")
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
    @Column(name = "original_price", precision = 21, scale = 2, nullable = false)
    private BigDecimal originalPrice;

    @Column(name = "discount_amount", precision = 21, scale = 2)
    private BigDecimal discountAmount;

    @NotNull
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

    @Column(name = "row_version")
    private Long rowVersion;

    @Column(name = "created_at")
    private Instant createdAt;

    @DecimalMin(value = "0")
    @DecimalMax(value = "100")
    @Column(name = "instructor_share_percent", precision = 21, scale = 2)
    private BigDecimal instructorSharePercent;

    @DecimalMin(value = "0")
    @Column(name = "instructor_amount", precision = 21, scale = 2)
    private BigDecimal instructorAmount;

    @DecimalMin(value = "0")
    @Column(name = "platform_amount", precision = 21, scale = 2)
    private BigDecimal platformAmount;

    @NotNull
    @Size(max = 20)
    @Column(name = "order_type", length = 20, nullable = false)
    private String orderType;

    @NotNull
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Min(value = 1)
    @Column(name = "access_duration_value")
    private Integer accessDurationValue;

    @Size(max = 10)
    @Column(name = "access_duration_unit", length = 10)
    private String accessDurationUnit;

    @Column(name = "entitlement_starts_at")
    private Instant entitlementStartsAt;

    @Column(name = "entitlement_expires_at")
    private Instant entitlementExpiresAt;

    @Size(max = 20)
    @Column(name = "entitlement_status", length = 20)
    private String entitlementStatus;

    @Column(name = "plan_snapshot", columnDefinition = "text")
    private String planSnapshot;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "pricing_snapshot", columnDefinition = "text")
    private String pricingSnapshot;

    @ManyToOne(optional = false)
    @NotNull
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "author", "subject", "grade" }, allowSetters = true)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    private SystemPlan plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "instructor", "course" }, allowSetters = true)
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

    public BigDecimal getInstructorSharePercent() {
        return this.instructorSharePercent;
    }

    public CourseOrder instructorSharePercent(BigDecimal instructorSharePercent) {
        this.setInstructorSharePercent(instructorSharePercent);
        return this;
    }

    public void setInstructorSharePercent(BigDecimal instructorSharePercent) {
        this.instructorSharePercent = instructorSharePercent;
    }

    public BigDecimal getInstructorAmount() {
        return this.instructorAmount;
    }

    public CourseOrder instructorAmount(BigDecimal instructorAmount) {
        this.setInstructorAmount(instructorAmount);
        return this;
    }

    public void setInstructorAmount(BigDecimal instructorAmount) {
        this.instructorAmount = instructorAmount;
    }

    public BigDecimal getPlatformAmount() {
        return this.platformAmount;
    }

    public CourseOrder platformAmount(BigDecimal platformAmount) {
        this.setPlatformAmount(platformAmount);
        return this;
    }

    public void setPlatformAmount(BigDecimal platformAmount) {
        this.platformAmount = platformAmount;
    }

    public String getOrderType() {
        return this.orderType;
    }

    public CourseOrder orderType(String orderType) {
        this.setOrderType(orderType);
        return this;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public Instant getExpiresAt() {
        return this.expiresAt;
    }

    public CourseOrder expiresAt(Instant expiresAt) {
        this.setExpiresAt(expiresAt);
        return this;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Instant getPaidAt() {
        return this.paidAt;
    }

    public CourseOrder paidAt(Instant paidAt) {
        this.setPaidAt(paidAt);
        return this;
    }

    public void setPaidAt(Instant paidAt) {
        this.paidAt = paidAt;
    }

    public Integer getAccessDurationValue() {
        return this.accessDurationValue;
    }

    public CourseOrder accessDurationValue(Integer accessDurationValue) {
        this.setAccessDurationValue(accessDurationValue);
        return this;
    }

    public void setAccessDurationValue(Integer accessDurationValue) {
        this.accessDurationValue = accessDurationValue;
    }

    public String getAccessDurationUnit() {
        return this.accessDurationUnit;
    }

    public CourseOrder accessDurationUnit(String accessDurationUnit) {
        this.setAccessDurationUnit(accessDurationUnit);
        return this;
    }

    public void setAccessDurationUnit(String accessDurationUnit) {
        this.accessDurationUnit = accessDurationUnit;
    }

    public Instant getEntitlementStartsAt() {
        return this.entitlementStartsAt;
    }

    public CourseOrder entitlementStartsAt(Instant entitlementStartsAt) {
        this.setEntitlementStartsAt(entitlementStartsAt);
        return this;
    }

    public void setEntitlementStartsAt(Instant entitlementStartsAt) {
        this.entitlementStartsAt = entitlementStartsAt;
    }

    public Instant getEntitlementExpiresAt() {
        return this.entitlementExpiresAt;
    }

    public CourseOrder entitlementExpiresAt(Instant entitlementExpiresAt) {
        this.setEntitlementExpiresAt(entitlementExpiresAt);
        return this;
    }

    public void setEntitlementExpiresAt(Instant entitlementExpiresAt) {
        this.entitlementExpiresAt = entitlementExpiresAt;
    }

    public String getEntitlementStatus() {
        return this.entitlementStatus;
    }

    public CourseOrder entitlementStatus(String entitlementStatus) {
        this.setEntitlementStatus(entitlementStatus);
        return this;
    }

    public void setEntitlementStatus(String entitlementStatus) {
        this.entitlementStatus = entitlementStatus;
    }

    public String getPlanSnapshot() {
        return this.planSnapshot;
    }

    public CourseOrder planSnapshot(String planSnapshot) {
        this.setPlanSnapshot(planSnapshot);
        return this;
    }

    public void setPlanSnapshot(String planSnapshot) {
        this.planSnapshot = planSnapshot;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public CourseOrder updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPricingSnapshot() {
        return this.pricingSnapshot;
    }

    public CourseOrder pricingSnapshot(String pricingSnapshot) {
        this.setPricingSnapshot(pricingSnapshot);
        return this;
    }

    public void setPricingSnapshot(String pricingSnapshot) {
        this.pricingSnapshot = pricingSnapshot;
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
            ", instructorSharePercent=" + getInstructorSharePercent() +
            ", instructorAmount=" + getInstructorAmount() +
            ", platformAmount=" + getPlatformAmount() +
            ", orderType='" + getOrderType() + "'" +
            ", expiresAt='" + getExpiresAt() + "'" +
            ", paidAt='" + getPaidAt() + "'" +
            ", accessDurationValue=" + getAccessDurationValue() +
            ", accessDurationUnit='" + getAccessDurationUnit() + "'" +
            ", entitlementStartsAt='" + getEntitlementStartsAt() + "'" +
            ", entitlementExpiresAt='" + getEntitlementExpiresAt() + "'" +
            ", entitlementStatus='" + getEntitlementStatus() + "'" +
            ", planSnapshot='" + getPlanSnapshot() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", pricingSnapshot='" + getPricingSnapshot() + "'" +
            "}";
    }
}
