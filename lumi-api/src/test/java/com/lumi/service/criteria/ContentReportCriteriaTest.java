package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ContentReportCriteriaTest {

    @Test
    void newContentReportCriteriaHasAllFiltersNullTest() {
        var contentReportCriteria = new ContentReportCriteria();
        assertThat(contentReportCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void contentReportCriteriaFluentMethodsCreatesFiltersTest() {
        var contentReportCriteria = new ContentReportCriteria();

        setAllFilters(contentReportCriteria);

        assertThat(contentReportCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void contentReportCriteriaCopyCreatesNullFilterTest() {
        var contentReportCriteria = new ContentReportCriteria();
        var copy = contentReportCriteria.copy();

        assertThat(contentReportCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(contentReportCriteria)
        );
    }

    @Test
    void contentReportCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var contentReportCriteria = new ContentReportCriteria();
        setAllFilters(contentReportCriteria);

        var copy = contentReportCriteria.copy();

        assertThat(contentReportCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(contentReportCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var contentReportCriteria = new ContentReportCriteria();

        assertThat(contentReportCriteria).hasToString("ContentReportCriteria{}");
    }

    private static void setAllFilters(ContentReportCriteria contentReportCriteria) {
        contentReportCriteria.id();
        contentReportCriteria.targetType();
        contentReportCriteria.targetId();
        contentReportCriteria.reason();
        contentReportCriteria.status();
        contentReportCriteria.createdAt();
        contentReportCriteria.reporterId();
        contentReportCriteria.distinct();
    }

    private static Condition<ContentReportCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getTargetType()) &&
                condition.apply(criteria.getTargetId()) &&
                condition.apply(criteria.getReason()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getReporterId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ContentReportCriteria> copyFiltersAre(
        ContentReportCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getTargetType(), copy.getTargetType()) &&
                condition.apply(criteria.getTargetId(), copy.getTargetId()) &&
                condition.apply(criteria.getReason(), copy.getReason()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getReporterId(), copy.getReporterId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
