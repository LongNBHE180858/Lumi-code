package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class AiMessageCriteriaTest {

    @Test
    void newAiMessageCriteriaHasAllFiltersNullTest() {
        var aiMessageCriteria = new AiMessageCriteria();
        assertThat(aiMessageCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void aiMessageCriteriaFluentMethodsCreatesFiltersTest() {
        var aiMessageCriteria = new AiMessageCriteria();

        setAllFilters(aiMessageCriteria);

        assertThat(aiMessageCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void aiMessageCriteriaCopyCreatesNullFilterTest() {
        var aiMessageCriteria = new AiMessageCriteria();
        var copy = aiMessageCriteria.copy();

        assertThat(aiMessageCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(aiMessageCriteria)
        );
    }

    @Test
    void aiMessageCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var aiMessageCriteria = new AiMessageCriteria();
        setAllFilters(aiMessageCriteria);

        var copy = aiMessageCriteria.copy();

        assertThat(aiMessageCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(aiMessageCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var aiMessageCriteria = new AiMessageCriteria();

        assertThat(aiMessageCriteria).hasToString("AiMessageCriteria{}");
    }

    private static void setAllFilters(AiMessageCriteria aiMessageCriteria) {
        aiMessageCriteria.id();
        aiMessageCriteria.senderRole();
        aiMessageCriteria.content();
        aiMessageCriteria.createdAt();
        aiMessageCriteria.conversationId();
        aiMessageCriteria.distinct();
    }

    private static Condition<AiMessageCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getSenderRole()) &&
                condition.apply(criteria.getContent()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getConversationId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<AiMessageCriteria> copyFiltersAre(AiMessageCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getSenderRole(), copy.getSenderRole()) &&
                condition.apply(criteria.getContent(), copy.getContent()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getConversationId(), copy.getConversationId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
