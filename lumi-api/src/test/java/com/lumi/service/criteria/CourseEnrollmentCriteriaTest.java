package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CourseEnrollmentCriteriaTest {

    @Test
    void newCourseEnrollmentCriteriaHasAllFiltersNullTest() {
        var courseEnrollmentCriteria = new CourseEnrollmentCriteria();
        assertThat(courseEnrollmentCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void courseEnrollmentCriteriaFluentMethodsCreatesFiltersTest() {
        var courseEnrollmentCriteria = new CourseEnrollmentCriteria();

        setAllFilters(courseEnrollmentCriteria);

        assertThat(courseEnrollmentCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void courseEnrollmentCriteriaCopyCreatesNullFilterTest() {
        var courseEnrollmentCriteria = new CourseEnrollmentCriteria();
        var copy = courseEnrollmentCriteria.copy();

        assertThat(courseEnrollmentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(courseEnrollmentCriteria)
        );
    }

    @Test
    void courseEnrollmentCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var courseEnrollmentCriteria = new CourseEnrollmentCriteria();
        setAllFilters(courseEnrollmentCriteria);

        var copy = courseEnrollmentCriteria.copy();

        assertThat(courseEnrollmentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(courseEnrollmentCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var courseEnrollmentCriteria = new CourseEnrollmentCriteria();

        assertThat(courseEnrollmentCriteria).hasToString("CourseEnrollmentCriteria{}");
    }

    private static void setAllFilters(CourseEnrollmentCriteria courseEnrollmentCriteria) {
        courseEnrollmentCriteria.id();
        courseEnrollmentCriteria.progressPercent();
        courseEnrollmentCriteria.status();
        courseEnrollmentCriteria.enrolledAt();
        courseEnrollmentCriteria.expiresAt();
        courseEnrollmentCriteria.updatedAt();
        courseEnrollmentCriteria.courseId();
        courseEnrollmentCriteria.studentId();
        courseEnrollmentCriteria.lastAccessedLessonId();
        courseEnrollmentCriteria.distinct();
    }

    private static Condition<CourseEnrollmentCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getProgressPercent()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getEnrolledAt()) &&
                condition.apply(criteria.getExpiresAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getCourseId()) &&
                condition.apply(criteria.getStudentId()) &&
                condition.apply(criteria.getLastAccessedLessonId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CourseEnrollmentCriteria> copyFiltersAre(
        CourseEnrollmentCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getProgressPercent(), copy.getProgressPercent()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getEnrolledAt(), copy.getEnrolledAt()) &&
                condition.apply(criteria.getExpiresAt(), copy.getExpiresAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getCourseId(), copy.getCourseId()) &&
                condition.apply(criteria.getStudentId(), copy.getStudentId()) &&
                condition.apply(criteria.getLastAccessedLessonId(), copy.getLastAccessedLessonId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
