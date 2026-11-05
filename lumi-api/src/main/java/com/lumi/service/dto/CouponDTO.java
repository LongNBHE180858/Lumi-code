package com.lumi.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.Coupon} entity.
 */
@Schema(description = "Lưu mã giảm giá, điều kiện sử dụng và tỷ lệ giáo viên chịu giảm giá.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CouponDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 50)
    private String code;

    @Min(value = 1)
    @Max(value = 100)
    private Integer discountPercent;

    private Integer maxUses;

    private Integer usedCount;

    private Instant validTo;

    private Instant validFrom;

    @NotNull
    private Boolean isActive;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal minOrderAmount;

    @Min(value = 1)
    private Integer maxUsesPerUser;

    @DecimalMin(value = "0")
    @DecimalMax(value = "100")
    private BigDecimal instructorDiscountSharePercent;

    private UserDTO instructor;

    private CourseDTO course;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(Integer discountPercent) {
        this.discountPercent = discountPercent;
    }

    public Integer getMaxUses() {
        return maxUses;
    }

    public void setMaxUses(Integer maxUses) {
        this.maxUses = maxUses;
    }

    public Integer getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(Integer usedCount) {
        this.usedCount = usedCount;
    }

    public Instant getValidTo() {
        return validTo;
    }

    public void setValidTo(Instant validTo) {
        this.validTo = validTo;
    }

    public Instant getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Instant validFrom) {
        this.validFrom = validFrom;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public BigDecimal getMinOrderAmount() {
        return minOrderAmount;
    }

    public void setMinOrderAmount(BigDecimal minOrderAmount) {
        this.minOrderAmount = minOrderAmount;
    }

    public Integer getMaxUsesPerUser() {
        return maxUsesPerUser;
    }

    public void setMaxUsesPerUser(Integer maxUsesPerUser) {
        this.maxUsesPerUser = maxUsesPerUser;
    }

    public BigDecimal getInstructorDiscountSharePercent() {
        return instructorDiscountSharePercent;
    }

    public void setInstructorDiscountSharePercent(BigDecimal instructorDiscountSharePercent) {
        this.instructorDiscountSharePercent = instructorDiscountSharePercent;
    }

    public UserDTO getInstructor() {
        return instructor;
    }

    public void setInstructor(UserDTO instructor) {
        this.instructor = instructor;
    }

    public CourseDTO getCourse() {
        return course;
    }

    public void setCourse(CourseDTO course) {
        this.course = course;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CouponDTO)) {
            return false;
        }

        CouponDTO couponDTO = (CouponDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, couponDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CouponDTO{" +
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
            ", instructor=" + getInstructor() +
            ", course=" + getCourse() +
            "}";
    }
}
