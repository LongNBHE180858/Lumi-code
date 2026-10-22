package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class FlashcardCriteriaTest {

    @Test
    void newFlashcardCriteriaHasAllFiltersNullTest() {
        var flashcardCriteria = new FlashcardCriteria();
        assertThat(flashcardCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void flashcardCriteriaFluentMethodsCreatesFiltersTest() {
        var flashcardCriteria = new FlashcardCriteria();

        setAllFilters(flashcardCriteria);

        assertThat(flashcardCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void flashcardCriteriaCopyCreatesNullFilterTest() {
        var flashcardCriteria = new FlashcardCriteria();
        var copy = flashcardCriteria.copy();

        assertThat(flashcardCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(flashcardCriteria)
        );
    }

    @Test
    void flashcardCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var flashcardCriteria = new FlashcardCriteria();
        setAllFilters(flashcardCriteria);

        var copy = flashcardCriteria.copy();

        assertThat(flashcardCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(flashcardCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var flashcardCriteria = new FlashcardCriteria();

        assertThat(flashcardCriteria).hasToString("FlashcardCriteria{}");
    }

    private static void setAllFilters(FlashcardCriteria flashcardCriteria) {
        flashcardCriteria.id();
        flashcardCriteria.frontText();
        flashcardCriteria.backText();
        flashcardCriteria.deckId();
        flashcardCriteria.distinct();
    }

    private static Condition<FlashcardCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getFrontText()) &&
                condition.apply(criteria.getBackText()) &&
                condition.apply(criteria.getDeckId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<FlashcardCriteria> copyFiltersAre(FlashcardCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getFrontText(), copy.getFrontText()) &&
                condition.apply(criteria.getBackText(), copy.getBackText()) &&
                condition.apply(criteria.getDeckId(), copy.getDeckId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
