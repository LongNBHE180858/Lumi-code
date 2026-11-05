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
        contentReportCriteria.status();
        contentReportCriteria.createdAt();
        contentReportCriteria.resolvedAt();
        contentReportCriteria.targetVersion();
        contentReportCriteria.reporterId();
        contentReportCriteria.assignedToId();
        contentReportCriteria.resolvedById();
        contentReportCriteria.distinct();
    }

    private static Condition<ContentReportCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getTargetType()) &&
                condition.apply(criteria.getTargetId()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getResolvedAt()) &&
                condition.apply(criteria.getTargetVersion()) &&
                condition.apply(criteria.getReporterId()) &&
                condition.apply(criteria.getAssignedToId()) &&
                condition.apply(criteria.getResolvedById()) &&
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
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getResolvedAt(), copy.getResolvedAt()) &&
                condition.apply(criteria.getTargetVersion(), copy.getTargetVersion()) &&
                condition.apply(criteria.getReporterId(), copy.getReporterId()) &&
                condition.apply(criteria.getAssignedToId(), copy.getAssignedToId()) &&
                condition.apply(criteria.getResolvedById(), copy.getResolvedById()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
