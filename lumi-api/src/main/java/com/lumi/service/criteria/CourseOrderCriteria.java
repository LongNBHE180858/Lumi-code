package com.lumi.service.criteria;

import com.lumi.domain.enumeration.OrderStatus;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.domain.CourseOrder} entity. This class is used
 * in {@link com.lumi.web.rest.CourseOrderResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /course-orders?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CourseOrderCriteria implements Serializable, Criteria {

    /**
     * Class for filtering OrderStatus
     */
    public static class OrderStatusFilter extends Filter<OrderStatus> {

        public OrderStatusFilter() {}

        public OrderStatusFilter(OrderStatusFilter filter) {
            super(filter);
        }

        @Override
        public OrderStatusFilter copy() {
            return new OrderStatusFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private BigDecimalFilter originalPrice;

    private BigDecimalFilter discountAmount;

    private BigDecimalFilter finalPrice;

    private StringFilter revenueSplitRate;

    private OrderStatusFilter status;

    private StringFilter idempotencyKey;

    private LongFilter rowVersion;

    private InstantFilter createdAt;

    private LongFilter studentId;

    private LongFilter courseId;

    private LongFilter planId;

    private LongFilter couponId;

    private Boolean distinct;

    public CourseOrderCriteria() {}

    public CourseOrderCriteria(CourseOrderCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.originalPrice = other.optionalOriginalPrice().map(BigDecimalFilter::copy).orElse(null);
        this.discountAmount = other.optionalDiscountAmount().map(BigDecimalFilter::copy).orElse(null);
        this.finalPrice = other.optionalFinalPrice().map(BigDecimalFilter::copy).orElse(null);
        this.revenueSplitRate = other.optionalRevenueSplitRate().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(OrderStatusFilter::copy).orElse(null);
        this.idempotencyKey = other.optionalIdempotencyKey().map(StringFilter::copy).orElse(null);
        this.rowVersion = other.optionalRowVersion().map(LongFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.studentId = other.optionalStudentId().map(LongFilter::copy).orElse(null);
        this.courseId = other.optionalCourseId().map(LongFilter::copy).orElse(null);
        this.planId = other.optionalPlanId().map(LongFilter::copy).orElse(null);
        this.couponId = other.optionalCouponId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CourseOrderCriteria copy() {
        return new CourseOrderCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public BigDecimalFilter getOriginalPrice() {
        return originalPrice;
    }

    public Optional<BigDecimalFilter> optionalOriginalPrice() {
        return Optional.ofNullable(originalPrice);
    }

    public BigDecimalFilter originalPrice() {
        if (originalPrice == null) {
            setOriginalPrice(new BigDecimalFilter());
        }
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimalFilter originalPrice) {
        this.originalPrice = originalPrice;
    }

    public BigDecimalFilter getDiscountAmount() {
        return discountAmount;
    }

    public Optional<BigDecimalFilter> optionalDiscountAmount() {
        return Optional.ofNullable(discountAmount);
    }

    public BigDecimalFilter discountAmount() {
        if (discountAmount == null) {
            setDiscountAmount(new BigDecimalFilter());
        }
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimalFilter discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimalFilter getFinalPrice() {
        return finalPrice;
    }

    public Optional<BigDecimalFilter> optionalFinalPrice() {
        return Optional.ofNullable(finalPrice);
    }

    public BigDecimalFilter finalPrice() {
        if (finalPrice == null) {
            setFinalPrice(new BigDecimalFilter());
        }
        return finalPrice;
    }

    public void setFinalPrice(BigDecimalFilter finalPrice) {
        this.finalPrice = finalPrice;
    }

    public StringFilter getRevenueSplitRate() {
        return revenueSplitRate;
    }

    public Optional<StringFilter> optionalRevenueSplitRate() {
        return Optional.ofNullable(revenueSplitRate);
    }

    public StringFilter revenueSplitRate() {
        if (revenueSplitRate == null) {
            setRevenueSplitRate(new StringFilter());
        }
        return revenueSplitRate;
    }

    public void setRevenueSplitRate(StringFilter revenueSplitRate) {
        this.revenueSplitRate = revenueSplitRate;
    }

    public OrderStatusFilter getStatus() {
        return status;
    }

    public Optional<OrderStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public OrderStatusFilter status() {
        if (status == null) {
            setStatus(new OrderStatusFilter());
        }
        return status;
    }

    public void setStatus(OrderStatusFilter status) {
        this.status = status;
    }

    public StringFilter getIdempotencyKey() {
        return idempotencyKey;
    }

    public Optional<StringFilter> optionalIdempotencyKey() {
        return Optional.ofNullable(idempotencyKey);
    }

    public StringFilter idempotencyKey() {
        if (idempotencyKey == null) {
            setIdempotencyKey(new StringFilter());
        }
        return idempotencyKey;
    }

    public void setIdempotencyKey(StringFilter idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public LongFilter getRowVersion() {
        return rowVersion;
    }

    public Optional<LongFilter> optionalRowVersion() {
        return Optional.ofNullable(rowVersion);
    }

    public LongFilter rowVersion() {
        if (rowVersion == null) {
            setRowVersion(new LongFilter());
        }
        return rowVersion;
    }

    public void setRowVersion(LongFilter rowVersion) {
        this.rowVersion = rowVersion;
    }

    public InstantFilter getCreatedAt() {
        return createdAt;
    }

    public Optional<InstantFilter> optionalCreatedAt() {
        return Optional.ofNullable(createdAt);
    }

    public InstantFilter createdAt() {
        if (createdAt == null) {
            setCreatedAt(new InstantFilter());
        }
        return createdAt;
    }

    public void setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
    }

    public LongFilter getStudentId() {
        return studentId;
    }

    public Optional<LongFilter> optionalStudentId() {
        return Optional.ofNullable(studentId);
    }

    public LongFilter studentId() {
        if (studentId == null) {
            setStudentId(new LongFilter());
        }
        return studentId;
    }

    public void setStudentId(LongFilter studentId) {
        this.studentId = studentId;
    }

    public LongFilter getCourseId() {
        return courseId;
    }

    public Optional<LongFilter> optionalCourseId() {
        return Optional.ofNullable(courseId);
    }

    public LongFilter courseId() {
        if (courseId == null) {
            setCourseId(new LongFilter());
        }
        return courseId;
    }

    public void setCourseId(LongFilter courseId) {
        this.courseId = courseId;
    }

    public LongFilter getPlanId() {
        return planId;
    }

    public Optional<LongFilter> optionalPlanId() {
        return Optional.ofNullable(planId);
    }

    public LongFilter planId() {
        if (planId == null) {
            setPlanId(new LongFilter());
        }
        return planId;
    }

    public void setPlanId(LongFilter planId) {
        this.planId = planId;
    }

    public LongFilter getCouponId() {
        return couponId;
    }

    public Optional<LongFilter> optionalCouponId() {
        return Optional.ofNullable(couponId);
    }

    public LongFilter couponId() {
        if (couponId == null) {
            setCouponId(new LongFilter());
        }
        return couponId;
    }

    public void setCouponId(LongFilter couponId) {
        this.couponId = couponId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CourseOrderCriteria that = (CourseOrderCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(originalPrice, that.originalPrice) &&
            Objects.equals(discountAmount, that.discountAmount) &&
            Objects.equals(finalPrice, that.finalPrice) &&
            Objects.equals(revenueSplitRate, that.revenueSplitRate) &&
            Objects.equals(status, that.status) &&
            Objects.equals(idempotencyKey, that.idempotencyKey) &&
            Objects.equals(rowVersion, that.rowVersion) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(studentId, that.studentId) &&
            Objects.equals(courseId, that.courseId) &&
            Objects.equals(planId, that.planId) &&
            Objects.equals(couponId, that.couponId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            originalPrice,
            discountAmount,
            finalPrice,
            revenueSplitRate,
            status,
            idempotencyKey,
            rowVersion,
            createdAt,
            studentId,
            courseId,
            planId,
            couponId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CourseOrderCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalOriginalPrice().map(f -> "originalPrice=" + f + ", ").orElse("") +
            optionalDiscountAmount().map(f -> "discountAmount=" + f + ", ").orElse("") +
            optionalFinalPrice().map(f -> "finalPrice=" + f + ", ").orElse("") +
            optionalRevenueSplitRate().map(f -> "revenueSplitRate=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalIdempotencyKey().map(f -> "idempotencyKey=" + f + ", ").orElse("") +
            optionalRowVersion().map(f -> "rowVersion=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalStudentId().map(f -> "studentId=" + f + ", ").orElse("") +
            optionalCourseId().map(f -> "courseId=" + f + ", ").orElse("") +
            optionalPlanId().map(f -> "planId=" + f + ", ").orElse("") +
            optionalCouponId().map(f -> "couponId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
