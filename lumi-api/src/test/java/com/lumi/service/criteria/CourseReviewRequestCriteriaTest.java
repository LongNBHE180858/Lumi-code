package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CourseReviewRequestCriteriaTest {

    @Test
    void newCourseReviewRequestCriteriaHasAllFiltersNullTest() {
        var courseReviewRequestCriteria = new CourseReviewRequestCriteria();
        assertThat(courseReviewRequestCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void courseReviewRequestCriteriaFluentMethodsCreatesFiltersTest() {
        var courseReviewRequestCriteria = new CourseReviewRequestCriteria();

        setAllFilters(courseReviewRequestCriteria);

        assertThat(courseReviewRequestCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void courseReviewRequestCriteriaCopyCreatesNullFilterTest() {
        var courseReviewRequestCriteria = new CourseReviewRequestCriteria();
        var copy = courseReviewRequestCriteria.copy();

        assertThat(courseReviewRequestCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(courseReviewRequestCriteria)
        );
    }

    @Test
    void courseReviewRequestCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var courseReviewRequestCriteria = new CourseReviewRequestCriteria();
        setAllFilters(courseReviewRequestCriteria);

        var copy = courseReviewRequestCriteria.copy();

        assertThat(courseReviewRequestCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(courseReviewRequestCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var courseReviewRequestCriteria = new CourseReviewRequestCriteria();

        assertThat(courseReviewRequestCriteria).hasToString("CourseReviewRequestCriteria{}");
    }

    private static void setAllFilters(CourseReviewRequestCriteria courseReviewRequestCriteria) {
        courseReviewRequestCriteria.id();
        courseReviewRequestCriteria.courseVersion();
        courseReviewRequestCriteria.status();
        courseReviewRequestCriteria.submissionNote();
        courseReviewRequestCriteria.rightsConfirmedAt();
        courseReviewRequestCriteria.submittedAt();
        courseReviewRequestCriteria.reviewedAt();
        courseReviewRequestCriteria.reviewerFeedback();
        courseReviewRequestCriteria.courseId();
        courseReviewRequestCriteria.submittedById();
        courseReviewRequestCriteria.reviewedById();
        courseReviewRequestCriteria.distinct();
    }

    private static Condition<CourseReviewRequestCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCourseVersion()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getSubmissionNote()) &&
                condition.apply(criteria.getRightsConfirmedAt()) &&
                condition.apply(criteria.getSubmittedAt()) &&
                condition.apply(criteria.getReviewedAt()) &&
                condition.apply(criteria.getReviewerFeedback()) &&
                condition.apply(criteria.getCourseId()) &&
                condition.apply(criteria.getSubmittedById()) &&
                condition.apply(criteria.getReviewedById()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CourseReviewRequestCriteria> copyFiltersAre(
        CourseReviewRequestCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCourseVersion(), copy.getCourseVersion()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getSubmissionNote(), copy.getSubmissionNote()) &&
                condition.apply(criteria.getRightsConfirmedAt(), copy.getRightsConfirmedAt()) &&
                condition.apply(criteria.getSubmittedAt(), copy.getSubmittedAt()) &&
                condition.apply(criteria.getReviewedAt(), copy.getReviewedAt()) &&
                condition.apply(criteria.getReviewerFeedback(), copy.getReviewerFeedback()) &&
                condition.apply(criteria.getCourseId(), copy.getCourseId()) &&
                condition.apply(criteria.getSubmittedById(), copy.getSubmittedById()) &&
                condition.apply(criteria.getReviewedById(), copy.getReviewedById()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
