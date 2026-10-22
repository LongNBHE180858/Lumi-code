package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CourseMaterialCriteriaTest {

    @Test
    void newCourseMaterialCriteriaHasAllFiltersNullTest() {
        var courseMaterialCriteria = new CourseMaterialCriteria();
        assertThat(courseMaterialCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void courseMaterialCriteriaFluentMethodsCreatesFiltersTest() {
        var courseMaterialCriteria = new CourseMaterialCriteria();

        setAllFilters(courseMaterialCriteria);

        assertThat(courseMaterialCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void courseMaterialCriteriaCopyCreatesNullFilterTest() {
        var courseMaterialCriteria = new CourseMaterialCriteria();
        var copy = courseMaterialCriteria.copy();

        assertThat(courseMaterialCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(courseMaterialCriteria)
        );
    }

    @Test
    void courseMaterialCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var courseMaterialCriteria = new CourseMaterialCriteria();
        setAllFilters(courseMaterialCriteria);

        var copy = courseMaterialCriteria.copy();

        assertThat(courseMaterialCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(courseMaterialCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var courseMaterialCriteria = new CourseMaterialCriteria();

        assertThat(courseMaterialCriteria).hasToString("CourseMaterialCriteria{}");
    }

    private static void setAllFilters(CourseMaterialCriteria courseMaterialCriteria) {
        courseMaterialCriteria.id();
        courseMaterialCriteria.fileType();
        courseMaterialCriteria.fileUrl();
        courseMaterialCriteria.fileSizeBytes();
        courseMaterialCriteria.lessonId();
        courseMaterialCriteria.distinct();
    }

    private static Condition<CourseMaterialCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getFileType()) &&
                condition.apply(criteria.getFileUrl()) &&
                condition.apply(criteria.getFileSizeBytes()) &&
                condition.apply(criteria.getLessonId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CourseMaterialCriteria> copyFiltersAre(
        CourseMaterialCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getFileType(), copy.getFileType()) &&
                condition.apply(criteria.getFileUrl(), copy.getFileUrl()) &&
                condition.apply(criteria.getFileSizeBytes(), copy.getFileSizeBytes()) &&
                condition.apply(criteria.getLessonId(), copy.getLessonId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
