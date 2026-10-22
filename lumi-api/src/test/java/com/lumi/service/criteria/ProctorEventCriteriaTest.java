package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ProctorEventCriteriaTest {

    @Test
    void newProctorEventCriteriaHasAllFiltersNullTest() {
        var proctorEventCriteria = new ProctorEventCriteria();
        assertThat(proctorEventCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void proctorEventCriteriaFluentMethodsCreatesFiltersTest() {
        var proctorEventCriteria = new ProctorEventCriteria();

        setAllFilters(proctorEventCriteria);

        assertThat(proctorEventCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void proctorEventCriteriaCopyCreatesNullFilterTest() {
        var proctorEventCriteria = new ProctorEventCriteria();
        var copy = proctorEventCriteria.copy();

        assertThat(proctorEventCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(proctorEventCriteria)
        );
    }

    @Test
    void proctorEventCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var proctorEventCriteria = new ProctorEventCriteria();
        setAllFilters(proctorEventCriteria);

        var copy = proctorEventCriteria.copy();

        assertThat(proctorEventCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(proctorEventCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var proctorEventCriteria = new ProctorEventCriteria();

        assertThat(proctorEventCriteria).hasToString("ProctorEventCriteria{}");
    }

    private static void setAllFilters(ProctorEventCriteria proctorEventCriteria) {
        proctorEventCriteria.id();
        proctorEventCriteria.violationType();
        proctorEventCriteria.photoUrl();
        proctorEventCriteria.occurredAt();
        proctorEventCriteria.sessionId();
        proctorEventCriteria.distinct();
    }

    private static Condition<ProctorEventCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getViolationType()) &&
                condition.apply(criteria.getPhotoUrl()) &&
                condition.apply(criteria.getOccurredAt()) &&
                condition.apply(criteria.getSessionId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ProctorEventCriteria> copyFiltersAre(
        ProctorEventCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getViolationType(), copy.getViolationType()) &&
                condition.apply(criteria.getPhotoUrl(), copy.getPhotoUrl()) &&
                condition.apply(criteria.getOccurredAt(), copy.getOccurredAt()) &&
                condition.apply(criteria.getSessionId(), copy.getSessionId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
