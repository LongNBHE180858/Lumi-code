package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class LessonCriteriaTest {

    @Test
    void newLessonCriteriaHasAllFiltersNullTest() {
        var lessonCriteria = new LessonCriteria();
        assertThat(lessonCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void lessonCriteriaFluentMethodsCreatesFiltersTest() {
        var lessonCriteria = new LessonCriteria();

        setAllFilters(lessonCriteria);

        assertThat(lessonCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void lessonCriteriaCopyCreatesNullFilterTest() {
        var lessonCriteria = new LessonCriteria();
        var copy = lessonCriteria.copy();

        assertThat(lessonCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(lessonCriteria)
        );
    }

    @Test
    void lessonCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var lessonCriteria = new LessonCriteria();
        setAllFilters(lessonCriteria);

        var copy = lessonCriteria.copy();

        assertThat(lessonCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(lessonCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var lessonCriteria = new LessonCriteria();

        assertThat(lessonCriteria).hasToString("LessonCriteria{}");
    }

    private static void setAllFilters(LessonCriteria lessonCriteria) {
        lessonCriteria.id();
        lessonCriteria.title();
        lessonCriteria.contentType();
        lessonCriteria.orderIndex();
        lessonCriteria.isPreview();
        lessonCriteria.durationMinutes();
        lessonCriteria.status();
        lessonCriteria.chapterId();
        lessonCriteria.distinct();
    }

    private static Condition<LessonCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getTitle()) &&
                condition.apply(criteria.getContentType()) &&
                condition.apply(criteria.getOrderIndex()) &&
                condition.apply(criteria.getIsPreview()) &&
                condition.apply(criteria.getDurationMinutes()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getChapterId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<LessonCriteria> copyFiltersAre(LessonCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getTitle(), copy.getTitle()) &&
                condition.apply(criteria.getContentType(), copy.getContentType()) &&
                condition.apply(criteria.getOrderIndex(), copy.getOrderIndex()) &&
                condition.apply(criteria.getIsPreview(), copy.getIsPreview()) &&
                condition.apply(criteria.getDurationMinutes(), copy.getDurationMinutes()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getChapterId(), copy.getChapterId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
