package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class FlashcardReviewCriteriaTest {

    @Test
    void newFlashcardReviewCriteriaHasAllFiltersNullTest() {
        var flashcardReviewCriteria = new FlashcardReviewCriteria();
        assertThat(flashcardReviewCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void flashcardReviewCriteriaFluentMethodsCreatesFiltersTest() {
        var flashcardReviewCriteria = new FlashcardReviewCriteria();

        setAllFilters(flashcardReviewCriteria);

        assertThat(flashcardReviewCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void flashcardReviewCriteriaCopyCreatesNullFilterTest() {
        var flashcardReviewCriteria = new FlashcardReviewCriteria();
        var copy = flashcardReviewCriteria.copy();

        assertThat(flashcardReviewCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(flashcardReviewCriteria)
        );
    }

    @Test
    void flashcardReviewCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var flashcardReviewCriteria = new FlashcardReviewCriteria();
        setAllFilters(flashcardReviewCriteria);

        var copy = flashcardReviewCriteria.copy();

        assertThat(flashcardReviewCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(flashcardReviewCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var flashcardReviewCriteria = new FlashcardReviewCriteria();

        assertThat(flashcardReviewCriteria).hasToString("FlashcardReviewCriteria{}");
    }

    private static void setAllFilters(FlashcardReviewCriteria flashcardReviewCriteria) {
        flashcardReviewCriteria.id();
        flashcardReviewCriteria.nextReviewAt();
        flashcardReviewCriteria.intervalDays();
        flashcardReviewCriteria.studentId();
        flashcardReviewCriteria.flashcardId();
        flashcardReviewCriteria.distinct();
    }

    private static Condition<FlashcardReviewCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getNextReviewAt()) &&
                condition.apply(criteria.getIntervalDays()) &&
                condition.apply(criteria.getStudentId()) &&
                condition.apply(criteria.getFlashcardId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<FlashcardReviewCriteria> copyFiltersAre(
        FlashcardReviewCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getNextReviewAt(), copy.getNextReviewAt()) &&
                condition.apply(criteria.getIntervalDays(), copy.getIntervalDays()) &&
                condition.apply(criteria.getStudentId(), copy.getStudentId()) &&
                condition.apply(criteria.getFlashcardId(), copy.getFlashcardId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
