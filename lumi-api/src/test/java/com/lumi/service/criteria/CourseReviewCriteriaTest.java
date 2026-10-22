package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CourseReviewCriteriaTest {

    @Test
    void newCourseReviewCriteriaHasAllFiltersNullTest() {
        var courseReviewCriteria = new CourseReviewCriteria();
        assertThat(courseReviewCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void courseReviewCriteriaFluentMethodsCreatesFiltersTest() {
        var courseReviewCriteria = new CourseReviewCriteria();

        setAllFilters(courseReviewCriteria);

        assertThat(courseReviewCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void courseReviewCriteriaCopyCreatesNullFilterTest() {
        var courseReviewCriteria = new CourseReviewCriteria();
        var copy = courseReviewCriteria.copy();

        assertThat(courseReviewCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(courseReviewCriteria)
        );
    }

    @Test
    void courseReviewCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var courseReviewCriteria = new CourseReviewCriteria();
        setAllFilters(courseReviewCriteria);

        var copy = courseReviewCriteria.copy();

        assertThat(courseReviewCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(courseReviewCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var courseReviewCriteria = new CourseReviewCriteria();

        assertThat(courseReviewCriteria).hasToString("CourseReviewCriteria{}");
    }

    private static void setAllFilters(CourseReviewCriteria courseReviewCriteria) {
        courseReviewCriteria.id();
        courseReviewCriteria.rating();
        courseReviewCriteria.content();
        courseReviewCriteria.createdAt();
        courseReviewCriteria.studentId();
        courseReviewCriteria.courseId();
        courseReviewCriteria.distinct();
    }

    private static Condition<CourseReviewCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getRating()) &&
                condition.apply(criteria.getContent()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getStudentId()) &&
                condition.apply(criteria.getCourseId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CourseReviewCriteria> copyFiltersAre(
        CourseReviewCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getRating(), copy.getRating()) &&
                condition.apply(criteria.getContent(), copy.getContent()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getStudentId(), copy.getStudentId()) &&
                condition.apply(criteria.getCourseId(), copy.getCourseId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
