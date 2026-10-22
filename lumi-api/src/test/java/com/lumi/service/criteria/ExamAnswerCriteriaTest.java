package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ExamAnswerCriteriaTest {

    @Test
    void newExamAnswerCriteriaHasAllFiltersNullTest() {
        var examAnswerCriteria = new ExamAnswerCriteria();
        assertThat(examAnswerCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void examAnswerCriteriaFluentMethodsCreatesFiltersTest() {
        var examAnswerCriteria = new ExamAnswerCriteria();

        setAllFilters(examAnswerCriteria);

        assertThat(examAnswerCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void examAnswerCriteriaCopyCreatesNullFilterTest() {
        var examAnswerCriteria = new ExamAnswerCriteria();
        var copy = examAnswerCriteria.copy();

        assertThat(examAnswerCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(examAnswerCriteria)
        );
    }

    @Test
    void examAnswerCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var examAnswerCriteria = new ExamAnswerCriteria();
        setAllFilters(examAnswerCriteria);

        var copy = examAnswerCriteria.copy();

        assertThat(examAnswerCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(examAnswerCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var examAnswerCriteria = new ExamAnswerCriteria();

        assertThat(examAnswerCriteria).hasToString("ExamAnswerCriteria{}");
    }

    private static void setAllFilters(ExamAnswerCriteria examAnswerCriteria) {
        examAnswerCriteria.id();
        examAnswerCriteria.textAnswer();
        examAnswerCriteria.isCorrect();
        examAnswerCriteria.score();
        examAnswerCriteria.attemptId();
        examAnswerCriteria.questionId();
        examAnswerCriteria.optionId();
        examAnswerCriteria.distinct();
    }

    private static Condition<ExamAnswerCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getTextAnswer()) &&
                condition.apply(criteria.getIsCorrect()) &&
                condition.apply(criteria.getScore()) &&
                condition.apply(criteria.getAttemptId()) &&
                condition.apply(criteria.getQuestionId()) &&
                condition.apply(criteria.getOptionId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ExamAnswerCriteria> copyFiltersAre(ExamAnswerCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getTextAnswer(), copy.getTextAnswer()) &&
                condition.apply(criteria.getIsCorrect(), copy.getIsCorrect()) &&
                condition.apply(criteria.getScore(), copy.getScore()) &&
                condition.apply(criteria.getAttemptId(), copy.getAttemptId()) &&
                condition.apply(criteria.getQuestionId(), copy.getQuestionId()) &&
                condition.apply(criteria.getOptionId(), copy.getOptionId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
