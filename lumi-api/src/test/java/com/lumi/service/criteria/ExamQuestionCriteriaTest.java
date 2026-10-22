package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ExamQuestionCriteriaTest {

    @Test
    void newExamQuestionCriteriaHasAllFiltersNullTest() {
        var examQuestionCriteria = new ExamQuestionCriteria();
        assertThat(examQuestionCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void examQuestionCriteriaFluentMethodsCreatesFiltersTest() {
        var examQuestionCriteria = new ExamQuestionCriteria();

        setAllFilters(examQuestionCriteria);

        assertThat(examQuestionCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void examQuestionCriteriaCopyCreatesNullFilterTest() {
        var examQuestionCriteria = new ExamQuestionCriteria();
        var copy = examQuestionCriteria.copy();

        assertThat(examQuestionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(examQuestionCriteria)
        );
    }

    @Test
    void examQuestionCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var examQuestionCriteria = new ExamQuestionCriteria();
        setAllFilters(examQuestionCriteria);

        var copy = examQuestionCriteria.copy();

        assertThat(examQuestionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(examQuestionCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var examQuestionCriteria = new ExamQuestionCriteria();

        assertThat(examQuestionCriteria).hasToString("ExamQuestionCriteria{}");
    }

    private static void setAllFilters(ExamQuestionCriteria examQuestionCriteria) {
        examQuestionCriteria.id();
        examQuestionCriteria.orderIndex();
        examQuestionCriteria.score();
        examQuestionCriteria.examId();
        examQuestionCriteria.questionId();
        examQuestionCriteria.distinct();
    }

    private static Condition<ExamQuestionCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getOrderIndex()) &&
                condition.apply(criteria.getScore()) &&
                condition.apply(criteria.getExamId()) &&
                condition.apply(criteria.getQuestionId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ExamQuestionCriteria> copyFiltersAre(
        ExamQuestionCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getOrderIndex(), copy.getOrderIndex()) &&
                condition.apply(criteria.getScore(), copy.getScore()) &&
                condition.apply(criteria.getExamId(), copy.getExamId()) &&
                condition.apply(criteria.getQuestionId(), copy.getQuestionId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
