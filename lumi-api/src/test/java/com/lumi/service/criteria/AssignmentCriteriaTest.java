package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class AssignmentCriteriaTest {

    @Test
    void newAssignmentCriteriaHasAllFiltersNullTest() {
        var assignmentCriteria = new AssignmentCriteria();
        assertThat(assignmentCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void assignmentCriteriaFluentMethodsCreatesFiltersTest() {
        var assignmentCriteria = new AssignmentCriteria();

        setAllFilters(assignmentCriteria);

        assertThat(assignmentCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void assignmentCriteriaCopyCreatesNullFilterTest() {
        var assignmentCriteria = new AssignmentCriteria();
        var copy = assignmentCriteria.copy();

        assertThat(assignmentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(assignmentCriteria)
        );
    }

    @Test
    void assignmentCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var assignmentCriteria = new AssignmentCriteria();
        setAllFilters(assignmentCriteria);

        var copy = assignmentCriteria.copy();

        assertThat(assignmentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(assignmentCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var assignmentCriteria = new AssignmentCriteria();

        assertThat(assignmentCriteria).hasToString("AssignmentCriteria{}");
    }

    private static void setAllFilters(AssignmentCriteria assignmentCriteria) {
        assignmentCriteria.id();
        assignmentCriteria.title();
        assignmentCriteria.instructions();
        assignmentCriteria.dueAt();
        assignmentCriteria.maxScore();
        assignmentCriteria.allowLate();
        assignmentCriteria.isDeleted();
        assignmentCriteria.studyClassId();
        assignmentCriteria.distinct();
    }

    private static Condition<AssignmentCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getTitle()) &&
                condition.apply(criteria.getInstructions()) &&
                condition.apply(criteria.getDueAt()) &&
                condition.apply(criteria.getMaxScore()) &&
                condition.apply(criteria.getAllowLate()) &&
                condition.apply(criteria.getIsDeleted()) &&
                condition.apply(criteria.getStudyClassId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<AssignmentCriteria> copyFiltersAre(AssignmentCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getTitle(), copy.getTitle()) &&
                condition.apply(criteria.getInstructions(), copy.getInstructions()) &&
                condition.apply(criteria.getDueAt(), copy.getDueAt()) &&
                condition.apply(criteria.getMaxScore(), copy.getMaxScore()) &&
                condition.apply(criteria.getAllowLate(), copy.getAllowLate()) &&
                condition.apply(criteria.getIsDeleted(), copy.getIsDeleted()) &&
                condition.apply(criteria.getStudyClassId(), copy.getStudyClassId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
