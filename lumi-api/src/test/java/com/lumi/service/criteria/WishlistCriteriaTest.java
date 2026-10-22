package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class WishlistCriteriaTest {

    @Test
    void newWishlistCriteriaHasAllFiltersNullTest() {
        var wishlistCriteria = new WishlistCriteria();
        assertThat(wishlistCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void wishlistCriteriaFluentMethodsCreatesFiltersTest() {
        var wishlistCriteria = new WishlistCriteria();

        setAllFilters(wishlistCriteria);

        assertThat(wishlistCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void wishlistCriteriaCopyCreatesNullFilterTest() {
        var wishlistCriteria = new WishlistCriteria();
        var copy = wishlistCriteria.copy();

        assertThat(wishlistCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(wishlistCriteria)
        );
    }

    @Test
    void wishlistCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var wishlistCriteria = new WishlistCriteria();
        setAllFilters(wishlistCriteria);

        var copy = wishlistCriteria.copy();

        assertThat(wishlistCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(wishlistCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var wishlistCriteria = new WishlistCriteria();

        assertThat(wishlistCriteria).hasToString("WishlistCriteria{}");
    }

    private static void setAllFilters(WishlistCriteria wishlistCriteria) {
        wishlistCriteria.id();
        wishlistCriteria.createdAt();
        wishlistCriteria.studentId();
        wishlistCriteria.courseId();
        wishlistCriteria.distinct();
    }

    private static Condition<WishlistCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getStudentId()) &&
                condition.apply(criteria.getCourseId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<WishlistCriteria> copyFiltersAre(WishlistCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getStudentId(), copy.getStudentId()) &&
                condition.apply(criteria.getCourseId(), copy.getCourseId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
