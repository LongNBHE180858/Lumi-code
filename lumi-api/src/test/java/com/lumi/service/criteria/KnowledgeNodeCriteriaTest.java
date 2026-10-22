package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class KnowledgeNodeCriteriaTest {

    @Test
    void newKnowledgeNodeCriteriaHasAllFiltersNullTest() {
        var knowledgeNodeCriteria = new KnowledgeNodeCriteria();
        assertThat(knowledgeNodeCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void knowledgeNodeCriteriaFluentMethodsCreatesFiltersTest() {
        var knowledgeNodeCriteria = new KnowledgeNodeCriteria();

        setAllFilters(knowledgeNodeCriteria);

        assertThat(knowledgeNodeCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void knowledgeNodeCriteriaCopyCreatesNullFilterTest() {
        var knowledgeNodeCriteria = new KnowledgeNodeCriteria();
        var copy = knowledgeNodeCriteria.copy();

        assertThat(knowledgeNodeCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(knowledgeNodeCriteria)
        );
    }

    @Test
    void knowledgeNodeCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var knowledgeNodeCriteria = new KnowledgeNodeCriteria();
        setAllFilters(knowledgeNodeCriteria);

        var copy = knowledgeNodeCriteria.copy();

        assertThat(knowledgeNodeCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(knowledgeNodeCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var knowledgeNodeCriteria = new KnowledgeNodeCriteria();

        assertThat(knowledgeNodeCriteria).hasToString("KnowledgeNodeCriteria{}");
    }

    private static void setAllFilters(KnowledgeNodeCriteria knowledgeNodeCriteria) {
        knowledgeNodeCriteria.id();
        knowledgeNodeCriteria.title();
        knowledgeNodeCriteria.type();
        knowledgeNodeCriteria.subjectId();
        knowledgeNodeCriteria.distinct();
    }

    private static Condition<KnowledgeNodeCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getTitle()) &&
                condition.apply(criteria.getType()) &&
                condition.apply(criteria.getSubjectId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<KnowledgeNodeCriteria> copyFiltersAre(
        KnowledgeNodeCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getTitle(), copy.getTitle()) &&
                condition.apply(criteria.getType(), copy.getType()) &&
                condition.apply(criteria.getSubjectId(), copy.getSubjectId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
