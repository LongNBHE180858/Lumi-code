package com.lumi.service.dto;

import com.lumi.domain.enumeration.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.CourseOrder} entity.
 */
@Schema(description = "Lưu đơn mua khóa học hoặc gói dịch vụ cùng quyền lợi và chính sách giá tại lúc mua.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CourseOrderDTO implements Serializable {

    private Long id;

    @NotNull
    private BigDecimal originalPrice;

    private BigDecimal discountAmount;

    @NotNull
    private BigDecimal finalPrice;

    @Size(max = 10)
    private String revenueSplitRate;

    private OrderStatus status;

    @Size(max = 200)
    private String idempotencyKey;

    private Long rowVersion;

    private Instant createdAt;

    @DecimalMin(value = "0")
    @DecimalMax(value = "100")
    private BigDecimal instructorSharePercent;

    @DecimalMin(value = "0")
    private BigDecimal instructorAmount;

    @DecimalMin(value = "0")
    private BigDecimal platformAmount;

    @NotNull
    @Size(max = 20)
    private String orderType;

    @NotNull
    private Instant expiresAt;

    private Instant paidAt;

    @Min(value = 1)
    private Integer accessDurationValue;

    @Size(max = 10)
    private String accessDurationUnit;

    private Instant entitlementStartsAt;

    private Instant entitlementExpiresAt;

    @Size(max = 20)
    private String entitlementStatus;

    @Lob
    private String planSnapshot;

    @NotNull
    private Instant updatedAt;

    @Lob
    private String pricingSnapshot;

    @NotNull
    private UserDTO student;

    private CourseDTO course;

    private SystemPlanDTO plan;

    private CouponDTO coupon;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(BigDecimal finalPrice) {
        this.finalPrice = finalPrice;
    }

    public String getRevenueSplitRate() {
        return revenueSplitRate;
    }

    public void setRevenueSplitRate(String revenueSplitRate) {
        this.revenueSplitRate = revenueSplitRate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public Long getRowVersion() {
        return rowVersion;
    }

    public void setRowVersion(Long rowVersion) {
        this.rowVersion = rowVersion;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public BigDecimal getInstructorSharePercent() {
        return instructorSharePercent;
    }

    public void setInstructorSharePercent(BigDecimal instructorSharePercent) {
        this.instructorSharePercent = instructorSharePercent;
    }

    public BigDecimal getInstructorAmount() {
        return instructorAmount;
    }

    public void setInstructorAmount(BigDecimal instructorAmount) {
        this.instructorAmount = instructorAmount;
    }

    public BigDecimal getPlatformAmount() {
        return platformAmount;
    }

    public void setPlatformAmount(BigDecimal platformAmount) {
        this.platformAmount = platformAmount;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Instant getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(Instant paidAt) {
        this.paidAt = paidAt;
    }

    public Integer getAccessDurationValue() {
        return accessDurationValue;
    }

    public void setAccessDurationValue(Integer accessDurationValue) {
        this.accessDurationValue = accessDurationValue;
    }

    public String getAccessDurationUnit() {
        return accessDurationUnit;
    }

    public void setAccessDurationUnit(String accessDurationUnit) {
        this.accessDurationUnit = accessDurationUnit;
    }

    public Instant getEntitlementStartsAt() {
        return entitlementStartsAt;
    }

    public void setEntitlementStartsAt(Instant entitlementStartsAt) {
        this.entitlementStartsAt = entitlementStartsAt;
    }

    public Instant getEntitlementExpiresAt() {
        return entitlementExpiresAt;
    }

    public void setEntitlementExpiresAt(Instant entitlementExpiresAt) {
        this.entitlementExpiresAt = entitlementExpiresAt;
    }

    public String getEntitlementStatus() {
        return entitlementStatus;
    }

    public void setEntitlementStatus(String entitlementStatus) {
        this.entitlementStatus = entitlementStatus;
    }

    public String getPlanSnapshot() {
        return planSnapshot;
    }

    public void setPlanSnapshot(String planSnapshot) {
        this.planSnapshot = planSnapshot;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPricingSnapshot() {
        return pricingSnapshot;
    }

    public void setPricingSnapshot(String pricingSnapshot) {
        this.pricingSnapshot = pricingSnapshot;
    }

    public UserDTO getStudent() {
        return student;
    }

    public void setStudent(UserDTO student) {
        this.student = student;
    }

    public CourseDTO getCourse() {
        return course;
    }

    public void setCourse(CourseDTO course) {
        this.course = course;
    }

    public SystemPlanDTO getPlan() {
        return plan;
    }

    public void setPlan(SystemPlanDTO plan) {
        this.plan = plan;
    }

    public CouponDTO getCoupon() {
        return coupon;
    }

    public void setCoupon(CouponDTO coupon) {
        this.coupon = coupon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CourseOrderDTO)) {
            return false;
        }

        CourseOrderDTO courseOrderDTO = (CourseOrderDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, courseOrderDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CourseOrderDTO{" +
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
            ", student=" + getStudent() +
            ", course=" + getCourse() +
            ", plan=" + getPlan() +
            ", coupon=" + getCoupon() +
            "}";
    }
}
