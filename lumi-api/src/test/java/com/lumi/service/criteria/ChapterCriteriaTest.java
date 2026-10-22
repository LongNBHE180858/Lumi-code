package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ChapterCriteriaTest {

    @Test
    void newChapterCriteriaHasAllFiltersNullTest() {
        var chapterCriteria = new ChapterCriteria();
        assertThat(chapterCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void chapterCriteriaFluentMethodsCreatesFiltersTest() {
        var chapterCriteria = new ChapterCriteria();

        setAllFilters(chapterCriteria);

        assertThat(chapterCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void chapterCriteriaCopyCreatesNullFilterTest() {
        var chapterCriteria = new ChapterCriteria();
        var copy = chapterCriteria.copy();

        assertThat(chapterCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(chapterCriteria)
        );
    }

    @Test
    void chapterCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var chapterCriteria = new ChapterCriteria();
        setAllFilters(chapterCriteria);

        var copy = chapterCriteria.copy();

        assertThat(chapterCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(chapterCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var chapterCriteria = new ChapterCriteria();

        assertThat(chapterCriteria).hasToString("ChapterCriteria{}");
    }

    private static void setAllFilters(ChapterCriteria chapterCriteria) {
        chapterCriteria.id();
        chapterCriteria.title();
        chapterCriteria.orderIndex();
        chapterCriteria.courseId();
        chapterCriteria.distinct();
    }

    private static Condition<ChapterCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getTitle()) &&
                condition.apply(criteria.getOrderIndex()) &&
                condition.apply(criteria.getCourseId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ChapterCriteria> copyFiltersAre(ChapterCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getTitle(), copy.getTitle()) &&
                condition.apply(criteria.getOrderIndex(), copy.getOrderIndex()) &&
                condition.apply(criteria.getCourseId(), copy.getCourseId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
