package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class AssignmentSubmissionCriteriaTest {

    @Test
    void newAssignmentSubmissionCriteriaHasAllFiltersNullTest() {
        var assignmentSubmissionCriteria = new AssignmentSubmissionCriteria();
        assertThat(assignmentSubmissionCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void assignmentSubmissionCriteriaFluentMethodsCreatesFiltersTest() {
        var assignmentSubmissionCriteria = new AssignmentSubmissionCriteria();

        setAllFilters(assignmentSubmissionCriteria);

        assertThat(assignmentSubmissionCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void assignmentSubmissionCriteriaCopyCreatesNullFilterTest() {
        var assignmentSubmissionCriteria = new AssignmentSubmissionCriteria();
        var copy = assignmentSubmissionCriteria.copy();

        assertThat(assignmentSubmissionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(assignmentSubmissionCriteria)
        );
    }

    @Test
    void assignmentSubmissionCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var assignmentSubmissionCriteria = new AssignmentSubmissionCriteria();
        setAllFilters(assignmentSubmissionCriteria);

        var copy = assignmentSubmissionCriteria.copy();

        assertThat(assignmentSubmissionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(assignmentSubmissionCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var assignmentSubmissionCriteria = new AssignmentSubmissionCriteria();

        assertThat(assignmentSubmissionCriteria).hasToString("AssignmentSubmissionCriteria{}");
    }

    private static void setAllFilters(AssignmentSubmissionCriteria assignmentSubmissionCriteria) {
        assignmentSubmissionCriteria.id();
        assignmentSubmissionCriteria.content();
        assignmentSubmissionCriteria.score();
        assignmentSubmissionCriteria.teacherFeedback();
        assignmentSubmissionCriteria.status();
        assignmentSubmissionCriteria.submittedAt();
        assignmentSubmissionCriteria.studentId();
        assignmentSubmissionCriteria.assignmentId();
        assignmentSubmissionCriteria.distinct();
    }

    private static Condition<AssignmentSubmissionCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getContent()) &&
                condition.apply(criteria.getScore()) &&
                condition.apply(criteria.getTeacherFeedback()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getSubmittedAt()) &&
                condition.apply(criteria.getStudentId()) &&
                condition.apply(criteria.getAssignmentId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<AssignmentSubmissionCriteria> copyFiltersAre(
        AssignmentSubmissionCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getContent(), copy.getContent()) &&
                condition.apply(criteria.getScore(), copy.getScore()) &&
                condition.apply(criteria.getTeacherFeedback(), copy.getTeacherFeedback()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getSubmittedAt(), copy.getSubmittedAt()) &&
                condition.apply(criteria.getStudentId(), copy.getStudentId()) &&
                condition.apply(criteria.getAssignmentId(), copy.getAssignmentId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
