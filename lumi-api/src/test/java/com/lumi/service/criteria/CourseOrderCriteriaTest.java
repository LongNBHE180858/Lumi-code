package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CourseOrderCriteriaTest {

    @Test
    void newCourseOrderCriteriaHasAllFiltersNullTest() {
        var courseOrderCriteria = new CourseOrderCriteria();
        assertThat(courseOrderCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void courseOrderCriteriaFluentMethodsCreatesFiltersTest() {
        var courseOrderCriteria = new CourseOrderCriteria();

        setAllFilters(courseOrderCriteria);

        assertThat(courseOrderCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void courseOrderCriteriaCopyCreatesNullFilterTest() {
        var courseOrderCriteria = new CourseOrderCriteria();
        var copy = courseOrderCriteria.copy();

        assertThat(courseOrderCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(courseOrderCriteria)
        );
    }

    @Test
    void courseOrderCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var courseOrderCriteria = new CourseOrderCriteria();
        setAllFilters(courseOrderCriteria);

        var copy = courseOrderCriteria.copy();

        assertThat(courseOrderCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(courseOrderCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var courseOrderCriteria = new CourseOrderCriteria();

        assertThat(courseOrderCriteria).hasToString("CourseOrderCriteria{}");
    }

    private static void setAllFilters(CourseOrderCriteria courseOrderCriteria) {
        courseOrderCriteria.id();
        courseOrderCriteria.originalPrice();
        courseOrderCriteria.discountAmount();
        courseOrderCriteria.finalPrice();
        courseOrderCriteria.revenueSplitRate();
        courseOrderCriteria.status();
        courseOrderCriteria.idempotencyKey();
        courseOrderCriteria.rowVersion();
        courseOrderCriteria.createdAt();
        courseOrderCriteria.studentId();
        courseOrderCriteria.courseId();
        courseOrderCriteria.planId();
        courseOrderCriteria.couponId();
        courseOrderCriteria.distinct();
    }

    private static Condition<CourseOrderCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getOriginalPrice()) &&
                condition.apply(criteria.getDiscountAmount()) &&
                condition.apply(criteria.getFinalPrice()) &&
                condition.apply(criteria.getRevenueSplitRate()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getIdempotencyKey()) &&
                condition.apply(criteria.getRowVersion()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getStudentId()) &&
                condition.apply(criteria.getCourseId()) &&
                condition.apply(criteria.getPlanId()) &&
                condition.apply(criteria.getCouponId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CourseOrderCriteria> copyFiltersAre(CourseOrderCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getOriginalPrice(), copy.getOriginalPrice()) &&
                condition.apply(criteria.getDiscountAmount(), copy.getDiscountAmount()) &&
                condition.apply(criteria.getFinalPrice(), copy.getFinalPrice()) &&
                condition.apply(criteria.getRevenueSplitRate(), copy.getRevenueSplitRate()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getIdempotencyKey(), copy.getIdempotencyKey()) &&
                condition.apply(criteria.getRowVersion(), copy.getRowVersion()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getStudentId(), copy.getStudentId()) &&
                condition.apply(criteria.getCourseId(), copy.getCourseId()) &&
                condition.apply(criteria.getPlanId(), copy.getPlanId()) &&
                condition.apply(criteria.getCouponId(), copy.getCouponId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
