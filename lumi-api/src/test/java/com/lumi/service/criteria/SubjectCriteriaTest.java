package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class SubjectCriteriaTest {

    @Test
    void newSubjectCriteriaHasAllFiltersNullTest() {
        var subjectCriteria = new SubjectCriteria();
        assertThat(subjectCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void subjectCriteriaFluentMethodsCreatesFiltersTest() {
        var subjectCriteria = new SubjectCriteria();

        setAllFilters(subjectCriteria);

        assertThat(subjectCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void subjectCriteriaCopyCreatesNullFilterTest() {
        var subjectCriteria = new SubjectCriteria();
        var copy = subjectCriteria.copy();

        assertThat(subjectCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(subjectCriteria)
        );
    }

    @Test
    void subjectCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var subjectCriteria = new SubjectCriteria();
        setAllFilters(subjectCriteria);

        var copy = subjectCriteria.copy();

        assertThat(subjectCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(subjectCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var subjectCriteria = new SubjectCriteria();

        assertThat(subjectCriteria).hasToString("SubjectCriteria{}");
    }

    private static void setAllFilters(SubjectCriteria subjectCriteria) {
        subjectCriteria.id();
        subjectCriteria.name();
        subjectCriteria.distinct();
    }

    private static Condition<SubjectCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria -> condition.apply(criteria.getId()) && condition.apply(criteria.getName()) && condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<SubjectCriteria> copyFiltersAre(SubjectCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
