package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class LessonProgressCriteriaTest {

    @Test
    void newLessonProgressCriteriaHasAllFiltersNullTest() {
        var lessonProgressCriteria = new LessonProgressCriteria();
        assertThat(lessonProgressCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void lessonProgressCriteriaFluentMethodsCreatesFiltersTest() {
        var lessonProgressCriteria = new LessonProgressCriteria();

        setAllFilters(lessonProgressCriteria);

        assertThat(lessonProgressCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void lessonProgressCriteriaCopyCreatesNullFilterTest() {
        var lessonProgressCriteria = new LessonProgressCriteria();
        var copy = lessonProgressCriteria.copy();

        assertThat(lessonProgressCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(lessonProgressCriteria)
        );
    }

    @Test
    void lessonProgressCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var lessonProgressCriteria = new LessonProgressCriteria();
        setAllFilters(lessonProgressCriteria);

        var copy = lessonProgressCriteria.copy();

        assertThat(lessonProgressCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(lessonProgressCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var lessonProgressCriteria = new LessonProgressCriteria();

        assertThat(lessonProgressCriteria).hasToString("LessonProgressCriteria{}");
    }

    private static void setAllFilters(LessonProgressCriteria lessonProgressCriteria) {
        lessonProgressCriteria.id();
        lessonProgressCriteria.status();
        lessonProgressCriteria.lastPositionSeconds();
        lessonProgressCriteria.lastAccessedAt();
        lessonProgressCriteria.completedAt();
        lessonProgressCriteria.rowVersion();
        lessonProgressCriteria.enrollmentId();
        lessonProgressCriteria.lessonId();
        lessonProgressCriteria.distinct();
    }

    private static Condition<LessonProgressCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getLastPositionSeconds()) &&
                condition.apply(criteria.getLastAccessedAt()) &&
                condition.apply(criteria.getCompletedAt()) &&
                condition.apply(criteria.getRowVersion()) &&
                condition.apply(criteria.getEnrollmentId()) &&
                condition.apply(criteria.getLessonId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<LessonProgressCriteria> copyFiltersAre(
        LessonProgressCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getLastPositionSeconds(), copy.getLastPositionSeconds()) &&
                condition.apply(criteria.getLastAccessedAt(), copy.getLastAccessedAt()) &&
                condition.apply(criteria.getCompletedAt(), copy.getCompletedAt()) &&
                condition.apply(criteria.getRowVersion(), copy.getRowVersion()) &&
                condition.apply(criteria.getEnrollmentId(), copy.getEnrollmentId()) &&
                condition.apply(criteria.getLessonId(), copy.getLessonId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
