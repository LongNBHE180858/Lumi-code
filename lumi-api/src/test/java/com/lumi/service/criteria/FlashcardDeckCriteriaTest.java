package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class FlashcardDeckCriteriaTest {

    @Test
    void newFlashcardDeckCriteriaHasAllFiltersNullTest() {
        var flashcardDeckCriteria = new FlashcardDeckCriteria();
        assertThat(flashcardDeckCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void flashcardDeckCriteriaFluentMethodsCreatesFiltersTest() {
        var flashcardDeckCriteria = new FlashcardDeckCriteria();

        setAllFilters(flashcardDeckCriteria);

        assertThat(flashcardDeckCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void flashcardDeckCriteriaCopyCreatesNullFilterTest() {
        var flashcardDeckCriteria = new FlashcardDeckCriteria();
        var copy = flashcardDeckCriteria.copy();

        assertThat(flashcardDeckCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(flashcardDeckCriteria)
        );
    }

    @Test
    void flashcardDeckCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var flashcardDeckCriteria = new FlashcardDeckCriteria();
        setAllFilters(flashcardDeckCriteria);

        var copy = flashcardDeckCriteria.copy();

        assertThat(flashcardDeckCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(flashcardDeckCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var flashcardDeckCriteria = new FlashcardDeckCriteria();

        assertThat(flashcardDeckCriteria).hasToString("FlashcardDeckCriteria{}");
    }

    private static void setAllFilters(FlashcardDeckCriteria flashcardDeckCriteria) {
        flashcardDeckCriteria.id();
        flashcardDeckCriteria.title();
        flashcardDeckCriteria.isPublic();
        flashcardDeckCriteria.ownerId();
        flashcardDeckCriteria.subjectId();
        flashcardDeckCriteria.distinct();
    }

    private static Condition<FlashcardDeckCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getTitle()) &&
                condition.apply(criteria.getIsPublic()) &&
                condition.apply(criteria.getOwnerId()) &&
                condition.apply(criteria.getSubjectId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<FlashcardDeckCriteria> copyFiltersAre(
        FlashcardDeckCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getTitle(), copy.getTitle()) &&
                condition.apply(criteria.getIsPublic(), copy.getIsPublic()) &&
                condition.apply(criteria.getOwnerId(), copy.getOwnerId()) &&
                condition.apply(criteria.getSubjectId(), copy.getSubjectId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
