package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class KnowledgeEdgeCriteriaTest {

    @Test
    void newKnowledgeEdgeCriteriaHasAllFiltersNullTest() {
        var knowledgeEdgeCriteria = new KnowledgeEdgeCriteria();
        assertThat(knowledgeEdgeCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void knowledgeEdgeCriteriaFluentMethodsCreatesFiltersTest() {
        var knowledgeEdgeCriteria = new KnowledgeEdgeCriteria();

        setAllFilters(knowledgeEdgeCriteria);

        assertThat(knowledgeEdgeCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void knowledgeEdgeCriteriaCopyCreatesNullFilterTest() {
        var knowledgeEdgeCriteria = new KnowledgeEdgeCriteria();
        var copy = knowledgeEdgeCriteria.copy();

        assertThat(knowledgeEdgeCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(knowledgeEdgeCriteria)
        );
    }

    @Test
    void knowledgeEdgeCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var knowledgeEdgeCriteria = new KnowledgeEdgeCriteria();
        setAllFilters(knowledgeEdgeCriteria);

        var copy = knowledgeEdgeCriteria.copy();

        assertThat(knowledgeEdgeCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(knowledgeEdgeCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var knowledgeEdgeCriteria = new KnowledgeEdgeCriteria();

        assertThat(knowledgeEdgeCriteria).hasToString("KnowledgeEdgeCriteria{}");
    }

    private static void setAllFilters(KnowledgeEdgeCriteria knowledgeEdgeCriteria) {
        knowledgeEdgeCriteria.id();
        knowledgeEdgeCriteria.relationType();
        knowledgeEdgeCriteria.fromNodeId();
        knowledgeEdgeCriteria.toNodeId();
        knowledgeEdgeCriteria.distinct();
    }

    private static Condition<KnowledgeEdgeCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getRelationType()) &&
                condition.apply(criteria.getFromNodeId()) &&
                condition.apply(criteria.getToNodeId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<KnowledgeEdgeCriteria> copyFiltersAre(
        KnowledgeEdgeCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getRelationType(), copy.getRelationType()) &&
                condition.apply(criteria.getFromNodeId(), copy.getFromNodeId()) &&
                condition.apply(criteria.getToNodeId(), copy.getToNodeId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
