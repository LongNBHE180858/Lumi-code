package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class AiConversationCriteriaTest {

    @Test
    void newAiConversationCriteriaHasAllFiltersNullTest() {
        var aiConversationCriteria = new AiConversationCriteria();
        assertThat(aiConversationCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void aiConversationCriteriaFluentMethodsCreatesFiltersTest() {
        var aiConversationCriteria = new AiConversationCriteria();

        setAllFilters(aiConversationCriteria);

        assertThat(aiConversationCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void aiConversationCriteriaCopyCreatesNullFilterTest() {
        var aiConversationCriteria = new AiConversationCriteria();
        var copy = aiConversationCriteria.copy();

        assertThat(aiConversationCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(aiConversationCriteria)
        );
    }

    @Test
    void aiConversationCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var aiConversationCriteria = new AiConversationCriteria();
        setAllFilters(aiConversationCriteria);

        var copy = aiConversationCriteria.copy();

        assertThat(aiConversationCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(aiConversationCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var aiConversationCriteria = new AiConversationCriteria();

        assertThat(aiConversationCriteria).hasToString("AiConversationCriteria{}");
    }

    private static void setAllFilters(AiConversationCriteria aiConversationCriteria) {
        aiConversationCriteria.id();
        aiConversationCriteria.type();
        aiConversationCriteria.createdAt();
        aiConversationCriteria.userId();
        aiConversationCriteria.lessonId();
        aiConversationCriteria.distinct();
    }

    private static Condition<AiConversationCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getType()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getLessonId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<AiConversationCriteria> copyFiltersAre(
        AiConversationCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getType(), copy.getType()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getLessonId(), copy.getLessonId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
