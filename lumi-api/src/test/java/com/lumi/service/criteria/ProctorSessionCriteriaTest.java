package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ProctorSessionCriteriaTest {

    @Test
    void newProctorSessionCriteriaHasAllFiltersNullTest() {
        var proctorSessionCriteria = new ProctorSessionCriteria();
        assertThat(proctorSessionCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void proctorSessionCriteriaFluentMethodsCreatesFiltersTest() {
        var proctorSessionCriteria = new ProctorSessionCriteria();

        setAllFilters(proctorSessionCriteria);

        assertThat(proctorSessionCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void proctorSessionCriteriaCopyCreatesNullFilterTest() {
        var proctorSessionCriteria = new ProctorSessionCriteria();
        var copy = proctorSessionCriteria.copy();

        assertThat(proctorSessionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(proctorSessionCriteria)
        );
    }

    @Test
    void proctorSessionCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var proctorSessionCriteria = new ProctorSessionCriteria();
        setAllFilters(proctorSessionCriteria);

        var copy = proctorSessionCriteria.copy();

        assertThat(proctorSessionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(proctorSessionCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var proctorSessionCriteria = new ProctorSessionCriteria();

        assertThat(proctorSessionCriteria).hasToString("ProctorSessionCriteria{}");
    }

    private static void setAllFilters(ProctorSessionCriteria proctorSessionCriteria) {
        proctorSessionCriteria.id();
        proctorSessionCriteria.faceVerified();
        proctorSessionCriteria.warningCount();
        proctorSessionCriteria.status();
        proctorSessionCriteria.attemptId();
        proctorSessionCriteria.distinct();
    }

    private static Condition<ProctorSessionCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getFaceVerified()) &&
                condition.apply(criteria.getWarningCount()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getAttemptId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ProctorSessionCriteria> copyFiltersAre(
        ProctorSessionCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getFaceVerified(), copy.getFaceVerified()) &&
                condition.apply(criteria.getWarningCount(), copy.getWarningCount()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getAttemptId(), copy.getAttemptId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
