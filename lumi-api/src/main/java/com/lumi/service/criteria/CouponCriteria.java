package com.lumi.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.domain.Coupon} entity. This class is used
 * in {@link com.lumi.web.rest.CouponResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /coupons?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CouponCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter code;

    private IntegerFilter discountPercent;

    private IntegerFilter maxUses;

    private IntegerFilter usedCount;

    private InstantFilter validTo;

    private LongFilter instructorId;

    private Boolean distinct;

    public CouponCriteria() {}

    public CouponCriteria(CouponCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.code = other.optionalCode().map(StringFilter::copy).orElse(null);
        this.discountPercent = other.optionalDiscountPercent().map(IntegerFilter::copy).orElse(null);
        this.maxUses = other.optionalMaxUses().map(IntegerFilter::copy).orElse(null);
        this.usedCount = other.optionalUsedCount().map(IntegerFilter::copy).orElse(null);
        this.validTo = other.optionalValidTo().map(InstantFilter::copy).orElse(null);
        this.instructorId = other.optionalInstructorId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CouponCriteria copy() {
        return new CouponCriteria(this);
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

    public StringFilter getCode() {
        return code;
    }

    public Optional<StringFilter> optionalCode() {
        return Optional.ofNullable(code);
    }

    public StringFilter code() {
        if (code == null) {
            setCode(new StringFilter());
        }
        return code;
    }

    public void setCode(StringFilter code) {
        this.code = code;
    }

    public IntegerFilter getDiscountPercent() {
        return discountPercent;
    }

    public Optional<IntegerFilter> optionalDiscountPercent() {
        return Optional.ofNullable(discountPercent);
    }

    public IntegerFilter discountPercent() {
        if (discountPercent == null) {
            setDiscountPercent(new IntegerFilter());
        }
        return discountPercent;
    }

    public void setDiscountPercent(IntegerFilter discountPercent) {
        this.discountPercent = discountPercent;
    }

    public IntegerFilter getMaxUses() {
        return maxUses;
    }

    public Optional<IntegerFilter> optionalMaxUses() {
        return Optional.ofNullable(maxUses);
    }

    public IntegerFilter maxUses() {
        if (maxUses == null) {
            setMaxUses(new IntegerFilter());
        }
        return maxUses;
    }

    public void setMaxUses(IntegerFilter maxUses) {
        this.maxUses = maxUses;
    }

    public IntegerFilter getUsedCount() {
        return usedCount;
    }

    public Optional<IntegerFilter> optionalUsedCount() {
        return Optional.ofNullable(usedCount);
    }

    public IntegerFilter usedCount() {
        if (usedCount == null) {
            setUsedCount(new IntegerFilter());
        }
        return usedCount;
    }

    public void setUsedCount(IntegerFilter usedCount) {
        this.usedCount = usedCount;
    }

    public InstantFilter getValidTo() {
        return validTo;
    }

    public Optional<InstantFilter> optionalValidTo() {
        return Optional.ofNullable(validTo);
    }

    public InstantFilter validTo() {
        if (validTo == null) {
            setValidTo(new InstantFilter());
        }
        return validTo;
    }

    public void setValidTo(InstantFilter validTo) {
        this.validTo = validTo;
    }

    public LongFilter getInstructorId() {
        return instructorId;
    }

    public Optional<LongFilter> optionalInstructorId() {
        return Optional.ofNullable(instructorId);
    }

    public LongFilter instructorId() {
        if (instructorId == null) {
            setInstructorId(new LongFilter());
        }
        return instructorId;
    }

    public void setInstructorId(LongFilter instructorId) {
        this.instructorId = instructorId;
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
        final CouponCriteria that = (CouponCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(code, that.code) &&
            Objects.equals(discountPercent, that.discountPercent) &&
            Objects.equals(maxUses, that.maxUses) &&
            Objects.equals(usedCount, that.usedCount) &&
            Objects.equals(validTo, that.validTo) &&
            Objects.equals(instructorId, that.instructorId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, discountPercent, maxUses, usedCount, validTo, instructorId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CouponCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCode().map(f -> "code=" + f + ", ").orElse("") +
            optionalDiscountPercent().map(f -> "discountPercent=" + f + ", ").orElse("") +
            optionalMaxUses().map(f -> "maxUses=" + f + ", ").orElse("") +
            optionalUsedCount().map(f -> "usedCount=" + f + ", ").orElse("") +
            optionalValidTo().map(f -> "validTo=" + f + ", ").orElse("") +
            optionalInstructorId().map(f -> "instructorId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
