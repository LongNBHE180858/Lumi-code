package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CouponCriteriaTest {

    @Test
    void newCouponCriteriaHasAllFiltersNullTest() {
        var couponCriteria = new CouponCriteria();
        assertThat(couponCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void couponCriteriaFluentMethodsCreatesFiltersTest() {
        var couponCriteria = new CouponCriteria();

        setAllFilters(couponCriteria);

        assertThat(couponCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void couponCriteriaCopyCreatesNullFilterTest() {
        var couponCriteria = new CouponCriteria();
        var copy = couponCriteria.copy();

        assertThat(couponCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(couponCriteria)
        );
    }

    @Test
    void couponCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var couponCriteria = new CouponCriteria();
        setAllFilters(couponCriteria);

        var copy = couponCriteria.copy();

        assertThat(couponCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(couponCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var couponCriteria = new CouponCriteria();

        assertThat(couponCriteria).hasToString("CouponCriteria{}");
    }

    private static void setAllFilters(CouponCriteria couponCriteria) {
        couponCriteria.id();
        couponCriteria.code();
        couponCriteria.discountPercent();
        couponCriteria.maxUses();
        couponCriteria.usedCount();
        couponCriteria.validTo();
        couponCriteria.instructorId();
        couponCriteria.distinct();
    }

    private static Condition<CouponCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCode()) &&
                condition.apply(criteria.getDiscountPercent()) &&
                condition.apply(criteria.getMaxUses()) &&
                condition.apply(criteria.getUsedCount()) &&
                condition.apply(criteria.getValidTo()) &&
                condition.apply(criteria.getInstructorId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CouponCriteria> copyFiltersAre(CouponCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCode(), copy.getCode()) &&
                condition.apply(criteria.getDiscountPercent(), copy.getDiscountPercent()) &&
                condition.apply(criteria.getMaxUses(), copy.getMaxUses()) &&
                condition.apply(criteria.getUsedCount(), copy.getUsedCount()) &&
                condition.apply(criteria.getValidTo(), copy.getValidTo()) &&
                condition.apply(criteria.getInstructorId(), copy.getInstructorId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
