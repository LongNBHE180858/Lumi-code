package com.lumi.service.dto;

import com.lumi.domain.enumeration.OrderStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.CourseOrder} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CourseOrderDTO implements Serializable {

    private Long id;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal originalPrice;

    @DecimalMin(value = "0")
    private BigDecimal discountAmount;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal finalPrice;

    @Size(max = 10)
    private String revenueSplitRate;

    private OrderStatus status;

    @Size(max = 200)
    private String idempotencyKey;

    @Min(value = 0L)
    private Long rowVersion;

    private Instant createdAt;

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
            ", student=" + getStudent() +
            ", course=" + getCourse() +
            ", plan=" + getPlan() +
            ", coupon=" + getCoupon() +
            "}";
    }
}
