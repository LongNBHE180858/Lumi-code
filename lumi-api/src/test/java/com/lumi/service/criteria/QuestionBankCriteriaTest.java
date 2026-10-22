package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class QuestionBankCriteriaTest {

    @Test
    void newQuestionBankCriteriaHasAllFiltersNullTest() {
        var questionBankCriteria = new QuestionBankCriteria();
        assertThat(questionBankCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void questionBankCriteriaFluentMethodsCreatesFiltersTest() {
        var questionBankCriteria = new QuestionBankCriteria();

        setAllFilters(questionBankCriteria);

        assertThat(questionBankCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void questionBankCriteriaCopyCreatesNullFilterTest() {
        var questionBankCriteria = new QuestionBankCriteria();
        var copy = questionBankCriteria.copy();

        assertThat(questionBankCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(questionBankCriteria)
        );
    }

    @Test
    void questionBankCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var questionBankCriteria = new QuestionBankCriteria();
        setAllFilters(questionBankCriteria);

        var copy = questionBankCriteria.copy();

        assertThat(questionBankCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(questionBankCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var questionBankCriteria = new QuestionBankCriteria();

        assertThat(questionBankCriteria).hasToString("QuestionBankCriteria{}");
    }

    private static void setAllFilters(QuestionBankCriteria questionBankCriteria) {
        questionBankCriteria.id();
        questionBankCriteria.name();
        questionBankCriteria.instructorId();
        questionBankCriteria.distinct();
    }

    private static Condition<QuestionBankCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getInstructorId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<QuestionBankCriteria> copyFiltersAre(
        QuestionBankCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getInstructorId(), copy.getInstructorId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
