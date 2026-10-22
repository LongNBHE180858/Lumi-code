package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class QuestionOptionCriteriaTest {

    @Test
    void newQuestionOptionCriteriaHasAllFiltersNullTest() {
        var questionOptionCriteria = new QuestionOptionCriteria();
        assertThat(questionOptionCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void questionOptionCriteriaFluentMethodsCreatesFiltersTest() {
        var questionOptionCriteria = new QuestionOptionCriteria();

        setAllFilters(questionOptionCriteria);

        assertThat(questionOptionCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void questionOptionCriteriaCopyCreatesNullFilterTest() {
        var questionOptionCriteria = new QuestionOptionCriteria();
        var copy = questionOptionCriteria.copy();

        assertThat(questionOptionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(questionOptionCriteria)
        );
    }

    @Test
    void questionOptionCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var questionOptionCriteria = new QuestionOptionCriteria();
        setAllFilters(questionOptionCriteria);

        var copy = questionOptionCriteria.copy();

        assertThat(questionOptionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(questionOptionCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var questionOptionCriteria = new QuestionOptionCriteria();

        assertThat(questionOptionCriteria).hasToString("QuestionOptionCriteria{}");
    }

    private static void setAllFilters(QuestionOptionCriteria questionOptionCriteria) {
        questionOptionCriteria.id();
        questionOptionCriteria.content();
        questionOptionCriteria.isCorrect();
        questionOptionCriteria.questionId();
        questionOptionCriteria.distinct();
    }

    private static Condition<QuestionOptionCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getContent()) &&
                condition.apply(criteria.getIsCorrect()) &&
                condition.apply(criteria.getQuestionId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<QuestionOptionCriteria> copyFiltersAre(
        QuestionOptionCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getContent(), copy.getContent()) &&
                condition.apply(criteria.getIsCorrect(), copy.getIsCorrect()) &&
                condition.apply(criteria.getQuestionId(), copy.getQuestionId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
