package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class DiscussionReplyCriteriaTest {

    @Test
    void newDiscussionReplyCriteriaHasAllFiltersNullTest() {
        var discussionReplyCriteria = new DiscussionReplyCriteria();
        assertThat(discussionReplyCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void discussionReplyCriteriaFluentMethodsCreatesFiltersTest() {
        var discussionReplyCriteria = new DiscussionReplyCriteria();

        setAllFilters(discussionReplyCriteria);

        assertThat(discussionReplyCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void discussionReplyCriteriaCopyCreatesNullFilterTest() {
        var discussionReplyCriteria = new DiscussionReplyCriteria();
        var copy = discussionReplyCriteria.copy();

        assertThat(discussionReplyCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(discussionReplyCriteria)
        );
    }

    @Test
    void discussionReplyCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var discussionReplyCriteria = new DiscussionReplyCriteria();
        setAllFilters(discussionReplyCriteria);

        var copy = discussionReplyCriteria.copy();

        assertThat(discussionReplyCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(discussionReplyCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var discussionReplyCriteria = new DiscussionReplyCriteria();

        assertThat(discussionReplyCriteria).hasToString("DiscussionReplyCriteria{}");
    }

    private static void setAllFilters(DiscussionReplyCriteria discussionReplyCriteria) {
        discussionReplyCriteria.id();
        discussionReplyCriteria.content();
        discussionReplyCriteria.createdAt();
        discussionReplyCriteria.userId();
        discussionReplyCriteria.discussionId();
        discussionReplyCriteria.parentId();
        discussionReplyCriteria.distinct();
    }

    private static Condition<DiscussionReplyCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getContent()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getDiscussionId()) &&
                condition.apply(criteria.getParentId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<DiscussionReplyCriteria> copyFiltersAre(
        DiscussionReplyCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getContent(), copy.getContent()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getDiscussionId(), copy.getDiscussionId()) &&
                condition.apply(criteria.getParentId(), copy.getParentId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
