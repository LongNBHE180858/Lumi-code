package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class StudentStreakCriteriaTest {

    @Test
    void newStudentStreakCriteriaHasAllFiltersNullTest() {
        var studentStreakCriteria = new StudentStreakCriteria();
        assertThat(studentStreakCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void studentStreakCriteriaFluentMethodsCreatesFiltersTest() {
        var studentStreakCriteria = new StudentStreakCriteria();

        setAllFilters(studentStreakCriteria);

        assertThat(studentStreakCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void studentStreakCriteriaCopyCreatesNullFilterTest() {
        var studentStreakCriteria = new StudentStreakCriteria();
        var copy = studentStreakCriteria.copy();

        assertThat(studentStreakCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(studentStreakCriteria)
        );
    }

    @Test
    void studentStreakCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var studentStreakCriteria = new StudentStreakCriteria();
        setAllFilters(studentStreakCriteria);

        var copy = studentStreakCriteria.copy();

        assertThat(studentStreakCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(studentStreakCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var studentStreakCriteria = new StudentStreakCriteria();

        assertThat(studentStreakCriteria).hasToString("StudentStreakCriteria{}");
    }

    private static void setAllFilters(StudentStreakCriteria studentStreakCriteria) {
        studentStreakCriteria.id();
        studentStreakCriteria.currentStreak();
        studentStreakCriteria.longestStreak();
        studentStreakCriteria.gemsBalance();
        studentStreakCriteria.lastActiveDate();
        studentStreakCriteria.userId();
        studentStreakCriteria.distinct();
    }

    private static Condition<StudentStreakCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCurrentStreak()) &&
                condition.apply(criteria.getLongestStreak()) &&
                condition.apply(criteria.getGemsBalance()) &&
                condition.apply(criteria.getLastActiveDate()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<StudentStreakCriteria> copyFiltersAre(
        StudentStreakCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCurrentStreak(), copy.getCurrentStreak()) &&
                condition.apply(criteria.getLongestStreak(), copy.getLongestStreak()) &&
                condition.apply(criteria.getGemsBalance(), copy.getGemsBalance()) &&
                condition.apply(criteria.getLastActiveDate(), copy.getLastActiveDate()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
