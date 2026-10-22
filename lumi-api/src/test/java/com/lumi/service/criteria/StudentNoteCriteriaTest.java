package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class StudentNoteCriteriaTest {

    @Test
    void newStudentNoteCriteriaHasAllFiltersNullTest() {
        var studentNoteCriteria = new StudentNoteCriteria();
        assertThat(studentNoteCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void studentNoteCriteriaFluentMethodsCreatesFiltersTest() {
        var studentNoteCriteria = new StudentNoteCriteria();

        setAllFilters(studentNoteCriteria);

        assertThat(studentNoteCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void studentNoteCriteriaCopyCreatesNullFilterTest() {
        var studentNoteCriteria = new StudentNoteCriteria();
        var copy = studentNoteCriteria.copy();

        assertThat(studentNoteCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(studentNoteCriteria)
        );
    }

    @Test
    void studentNoteCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var studentNoteCriteria = new StudentNoteCriteria();
        setAllFilters(studentNoteCriteria);

        var copy = studentNoteCriteria.copy();

        assertThat(studentNoteCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(studentNoteCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var studentNoteCriteria = new StudentNoteCriteria();

        assertThat(studentNoteCriteria).hasToString("StudentNoteCriteria{}");
    }

    private static void setAllFilters(StudentNoteCriteria studentNoteCriteria) {
        studentNoteCriteria.id();
        studentNoteCriteria.videoTimestamp();
        studentNoteCriteria.content();
        studentNoteCriteria.createdAt();
        studentNoteCriteria.studentId();
        studentNoteCriteria.lessonId();
        studentNoteCriteria.distinct();
    }

    private static Condition<StudentNoteCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getVideoTimestamp()) &&
                condition.apply(criteria.getContent()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getStudentId()) &&
                condition.apply(criteria.getLessonId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<StudentNoteCriteria> copyFiltersAre(StudentNoteCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getVideoTimestamp(), copy.getVideoTimestamp()) &&
                condition.apply(criteria.getContent(), copy.getContent()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getStudentId(), copy.getStudentId()) &&
                condition.apply(criteria.getLessonId(), copy.getLessonId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
