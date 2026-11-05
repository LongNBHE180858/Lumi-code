package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class QuestionKnowledgeLinkCriteriaTest {

    @Test
    void newQuestionKnowledgeLinkCriteriaHasAllFiltersNullTest() {
        var questionKnowledgeLinkCriteria = new QuestionKnowledgeLinkCriteria();
        assertThat(questionKnowledgeLinkCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void questionKnowledgeLinkCriteriaFluentMethodsCreatesFiltersTest() {
        var questionKnowledgeLinkCriteria = new QuestionKnowledgeLinkCriteria();

        setAllFilters(questionKnowledgeLinkCriteria);

        assertThat(questionKnowledgeLinkCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void questionKnowledgeLinkCriteriaCopyCreatesNullFilterTest() {
        var questionKnowledgeLinkCriteria = new QuestionKnowledgeLinkCriteria();
        var copy = questionKnowledgeLinkCriteria.copy();

        assertThat(questionKnowledgeLinkCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(questionKnowledgeLinkCriteria)
        );
    }

    @Test
    void questionKnowledgeLinkCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var questionKnowledgeLinkCriteria = new QuestionKnowledgeLinkCriteria();
        setAllFilters(questionKnowledgeLinkCriteria);

        var copy = questionKnowledgeLinkCriteria.copy();

        assertThat(questionKnowledgeLinkCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(questionKnowledgeLinkCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var questionKnowledgeLinkCriteria = new QuestionKnowledgeLinkCriteria();

        assertThat(questionKnowledgeLinkCriteria).hasToString("QuestionKnowledgeLinkCriteria{}");
    }

    private static void setAllFilters(QuestionKnowledgeLinkCriteria questionKnowledgeLinkCriteria) {
        questionKnowledgeLinkCriteria.id();
        questionKnowledgeLinkCriteria.weight();
        questionKnowledgeLinkCriteria.questionId();
        questionKnowledgeLinkCriteria.knowledgeNodeId();
        questionKnowledgeLinkCriteria.distinct();
    }

    private static Condition<QuestionKnowledgeLinkCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getWeight()) &&
                condition.apply(criteria.getQuestionId()) &&
                condition.apply(criteria.getKnowledgeNodeId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<QuestionKnowledgeLinkCriteria> copyFiltersAre(
        QuestionKnowledgeLinkCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getWeight(), copy.getWeight()) &&
                condition.apply(criteria.getQuestionId(), copy.getQuestionId()) &&
                condition.apply(criteria.getKnowledgeNodeId(), copy.getKnowledgeNodeId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
