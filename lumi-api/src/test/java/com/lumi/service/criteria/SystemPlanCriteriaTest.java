package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class SystemPlanCriteriaTest {

    @Test
    void newSystemPlanCriteriaHasAllFiltersNullTest() {
        var systemPlanCriteria = new SystemPlanCriteria();
        assertThat(systemPlanCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void systemPlanCriteriaFluentMethodsCreatesFiltersTest() {
        var systemPlanCriteria = new SystemPlanCriteria();

        setAllFilters(systemPlanCriteria);

        assertThat(systemPlanCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void systemPlanCriteriaCopyCreatesNullFilterTest() {
        var systemPlanCriteria = new SystemPlanCriteria();
        var copy = systemPlanCriteria.copy();

        assertThat(systemPlanCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(systemPlanCriteria)
        );
    }

    @Test
    void systemPlanCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var systemPlanCriteria = new SystemPlanCriteria();
        setAllFilters(systemPlanCriteria);

        var copy = systemPlanCriteria.copy();

        assertThat(systemPlanCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(systemPlanCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var systemPlanCriteria = new SystemPlanCriteria();

        assertThat(systemPlanCriteria).hasToString("SystemPlanCriteria{}");
    }

    private static void setAllFilters(SystemPlanCriteria systemPlanCriteria) {
        systemPlanCriteria.id();
        systemPlanCriteria.name();
        systemPlanCriteria.price();
        systemPlanCriteria.durationDays();
        systemPlanCriteria.storageQuotaMb();
        systemPlanCriteria.aiGenerationLimit();
        systemPlanCriteria.maxClasses();
        systemPlanCriteria.distinct();
    }

    private static Condition<SystemPlanCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getPrice()) &&
                condition.apply(criteria.getDurationDays()) &&
                condition.apply(criteria.getStorageQuotaMb()) &&
                condition.apply(criteria.getAiGenerationLimit()) &&
                condition.apply(criteria.getMaxClasses()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<SystemPlanCriteria> copyFiltersAre(SystemPlanCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getPrice(), copy.getPrice()) &&
                condition.apply(criteria.getDurationDays(), copy.getDurationDays()) &&
                condition.apply(criteria.getStorageQuotaMb(), copy.getStorageQuotaMb()) &&
                condition.apply(criteria.getAiGenerationLimit(), copy.getAiGenerationLimit()) &&
                condition.apply(criteria.getMaxClasses(), copy.getMaxClasses()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
