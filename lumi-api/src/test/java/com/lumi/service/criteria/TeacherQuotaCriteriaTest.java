package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class TeacherQuotaCriteriaTest {

    @Test
    void newTeacherQuotaCriteriaHasAllFiltersNullTest() {
        var teacherQuotaCriteria = new TeacherQuotaCriteria();
        assertThat(teacherQuotaCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void teacherQuotaCriteriaFluentMethodsCreatesFiltersTest() {
        var teacherQuotaCriteria = new TeacherQuotaCriteria();

        setAllFilters(teacherQuotaCriteria);

        assertThat(teacherQuotaCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void teacherQuotaCriteriaCopyCreatesNullFilterTest() {
        var teacherQuotaCriteria = new TeacherQuotaCriteria();
        var copy = teacherQuotaCriteria.copy();

        assertThat(teacherQuotaCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(teacherQuotaCriteria)
        );
    }

    @Test
    void teacherQuotaCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var teacherQuotaCriteria = new TeacherQuotaCriteria();
        setAllFilters(teacherQuotaCriteria);

        var copy = teacherQuotaCriteria.copy();

        assertThat(teacherQuotaCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(teacherQuotaCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var teacherQuotaCriteria = new TeacherQuotaCriteria();

        assertThat(teacherQuotaCriteria).hasToString("TeacherQuotaCriteria{}");
    }

    private static void setAllFilters(TeacherQuotaCriteria teacherQuotaCriteria) {
        teacherQuotaCriteria.id();
        teacherQuotaCriteria.planExpiresAt();
        teacherQuotaCriteria.usedStorageBytes();
        teacherQuotaCriteria.aiUsedCount();
        teacherQuotaCriteria.rowVersion();
        teacherQuotaCriteria.userId();
        teacherQuotaCriteria.currentPlanId();
        teacherQuotaCriteria.distinct();
    }

    private static Condition<TeacherQuotaCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getPlanExpiresAt()) &&
                condition.apply(criteria.getUsedStorageBytes()) &&
                condition.apply(criteria.getAiUsedCount()) &&
                condition.apply(criteria.getRowVersion()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getCurrentPlanId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<TeacherQuotaCriteria> copyFiltersAre(
        TeacherQuotaCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getPlanExpiresAt(), copy.getPlanExpiresAt()) &&
                condition.apply(criteria.getUsedStorageBytes(), copy.getUsedStorageBytes()) &&
                condition.apply(criteria.getAiUsedCount(), copy.getAiUsedCount()) &&
                condition.apply(criteria.getRowVersion(), copy.getRowVersion()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getCurrentPlanId(), copy.getCurrentPlanId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
