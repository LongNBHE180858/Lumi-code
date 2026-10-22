package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ExamAttemptCriteriaTest {

    @Test
    void newExamAttemptCriteriaHasAllFiltersNullTest() {
        var examAttemptCriteria = new ExamAttemptCriteria();
        assertThat(examAttemptCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void examAttemptCriteriaFluentMethodsCreatesFiltersTest() {
        var examAttemptCriteria = new ExamAttemptCriteria();

        setAllFilters(examAttemptCriteria);

        assertThat(examAttemptCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void examAttemptCriteriaCopyCreatesNullFilterTest() {
        var examAttemptCriteria = new ExamAttemptCriteria();
        var copy = examAttemptCriteria.copy();

        assertThat(examAttemptCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(examAttemptCriteria)
        );
    }

    @Test
    void examAttemptCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var examAttemptCriteria = new ExamAttemptCriteria();
        setAllFilters(examAttemptCriteria);

        var copy = examAttemptCriteria.copy();

        assertThat(examAttemptCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(examAttemptCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var examAttemptCriteria = new ExamAttemptCriteria();

        assertThat(examAttemptCriteria).hasToString("ExamAttemptCriteria{}");
    }

    private static void setAllFilters(ExamAttemptCriteria examAttemptCriteria) {
        examAttemptCriteria.id();
        examAttemptCriteria.startTime();
        examAttemptCriteria.endTime();
        examAttemptCriteria.score();
        examAttemptCriteria.status();
        examAttemptCriteria.rowVersion();
        examAttemptCriteria.studentId();
        examAttemptCriteria.examId();
        examAttemptCriteria.proctorSessionId();
        examAttemptCriteria.distinct();
    }

    private static Condition<ExamAttemptCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getStartTime()) &&
                condition.apply(criteria.getEndTime()) &&
                condition.apply(criteria.getScore()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getRowVersion()) &&
                condition.apply(criteria.getStudentId()) &&
                condition.apply(criteria.getExamId()) &&
                condition.apply(criteria.getProctorSessionId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ExamAttemptCriteria> copyFiltersAre(ExamAttemptCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getStartTime(), copy.getStartTime()) &&
                condition.apply(criteria.getEndTime(), copy.getEndTime()) &&
                condition.apply(criteria.getScore(), copy.getScore()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getRowVersion(), copy.getRowVersion()) &&
                condition.apply(criteria.getStudentId(), copy.getStudentId()) &&
                condition.apply(criteria.getExamId(), copy.getExamId()) &&
                condition.apply(criteria.getProctorSessionId(), copy.getProctorSessionId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
