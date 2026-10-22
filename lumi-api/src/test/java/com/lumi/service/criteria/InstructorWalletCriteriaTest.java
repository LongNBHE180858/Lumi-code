package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class InstructorWalletCriteriaTest {

    @Test
    void newInstructorWalletCriteriaHasAllFiltersNullTest() {
        var instructorWalletCriteria = new InstructorWalletCriteria();
        assertThat(instructorWalletCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void instructorWalletCriteriaFluentMethodsCreatesFiltersTest() {
        var instructorWalletCriteria = new InstructorWalletCriteria();

        setAllFilters(instructorWalletCriteria);

        assertThat(instructorWalletCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void instructorWalletCriteriaCopyCreatesNullFilterTest() {
        var instructorWalletCriteria = new InstructorWalletCriteria();
        var copy = instructorWalletCriteria.copy();

        assertThat(instructorWalletCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(instructorWalletCriteria)
        );
    }

    @Test
    void instructorWalletCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var instructorWalletCriteria = new InstructorWalletCriteria();
        setAllFilters(instructorWalletCriteria);

        var copy = instructorWalletCriteria.copy();

        assertThat(instructorWalletCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(instructorWalletCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var instructorWalletCriteria = new InstructorWalletCriteria();

        assertThat(instructorWalletCriteria).hasToString("InstructorWalletCriteria{}");
    }

    private static void setAllFilters(InstructorWalletCriteria instructorWalletCriteria) {
        instructorWalletCriteria.id();
        instructorWalletCriteria.pendingAmount();
        instructorWalletCriteria.availableAmount();
        instructorWalletCriteria.rowVersion();
        instructorWalletCriteria.userId();
        instructorWalletCriteria.distinct();
    }

    private static Condition<InstructorWalletCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getPendingAmount()) &&
                condition.apply(criteria.getAvailableAmount()) &&
                condition.apply(criteria.getRowVersion()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<InstructorWalletCriteria> copyFiltersAre(
        InstructorWalletCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getPendingAmount(), copy.getPendingAmount()) &&
                condition.apply(criteria.getAvailableAmount(), copy.getAvailableAmount()) &&
                condition.apply(criteria.getRowVersion(), copy.getRowVersion()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
