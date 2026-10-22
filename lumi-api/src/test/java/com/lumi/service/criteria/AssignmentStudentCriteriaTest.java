package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class AssignmentStudentCriteriaTest {

    @Test
    void newAssignmentStudentCriteriaHasAllFiltersNullTest() {
        var assignmentStudentCriteria = new AssignmentStudentCriteria();
        assertThat(assignmentStudentCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void assignmentStudentCriteriaFluentMethodsCreatesFiltersTest() {
        var assignmentStudentCriteria = new AssignmentStudentCriteria();

        setAllFilters(assignmentStudentCriteria);

        assertThat(assignmentStudentCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void assignmentStudentCriteriaCopyCreatesNullFilterTest() {
        var assignmentStudentCriteria = new AssignmentStudentCriteria();
        var copy = assignmentStudentCriteria.copy();

        assertThat(assignmentStudentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(assignmentStudentCriteria)
        );
    }

    @Test
    void assignmentStudentCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var assignmentStudentCriteria = new AssignmentStudentCriteria();
        setAllFilters(assignmentStudentCriteria);

        var copy = assignmentStudentCriteria.copy();

        assertThat(assignmentStudentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(assignmentStudentCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var assignmentStudentCriteria = new AssignmentStudentCriteria();

        assertThat(assignmentStudentCriteria).hasToString("AssignmentStudentCriteria{}");
    }

    private static void setAllFilters(AssignmentStudentCriteria assignmentStudentCriteria) {
        assignmentStudentCriteria.id();
        assignmentStudentCriteria.studentId();
        assignmentStudentCriteria.assignmentId();
        assignmentStudentCriteria.distinct();
    }

    private static Condition<AssignmentStudentCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getStudentId()) &&
                condition.apply(criteria.getAssignmentId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<AssignmentStudentCriteria> copyFiltersAre(
        AssignmentStudentCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getStudentId(), copy.getStudentId()) &&
                condition.apply(criteria.getAssignmentId(), copy.getAssignmentId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
