package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class SupportTicketCriteriaTest {

    @Test
    void newSupportTicketCriteriaHasAllFiltersNullTest() {
        var supportTicketCriteria = new SupportTicketCriteria();
        assertThat(supportTicketCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void supportTicketCriteriaFluentMethodsCreatesFiltersTest() {
        var supportTicketCriteria = new SupportTicketCriteria();

        setAllFilters(supportTicketCriteria);

        assertThat(supportTicketCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void supportTicketCriteriaCopyCreatesNullFilterTest() {
        var supportTicketCriteria = new SupportTicketCriteria();
        var copy = supportTicketCriteria.copy();

        assertThat(supportTicketCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(supportTicketCriteria)
        );
    }

    @Test
    void supportTicketCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var supportTicketCriteria = new SupportTicketCriteria();
        setAllFilters(supportTicketCriteria);

        var copy = supportTicketCriteria.copy();

        assertThat(supportTicketCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(supportTicketCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var supportTicketCriteria = new SupportTicketCriteria();

        assertThat(supportTicketCriteria).hasToString("SupportTicketCriteria{}");
    }

    private static void setAllFilters(SupportTicketCriteria supportTicketCriteria) {
        supportTicketCriteria.id();
        supportTicketCriteria.type();
        supportTicketCriteria.subject();
        supportTicketCriteria.status();
        supportTicketCriteria.createdAt();
        supportTicketCriteria.userId();
        supportTicketCriteria.distinct();
    }

    private static Condition<SupportTicketCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getType()) &&
                condition.apply(criteria.getSubject()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<SupportTicketCriteria> copyFiltersAre(
        SupportTicketCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getType(), copy.getType()) &&
                condition.apply(criteria.getSubject(), copy.getSubject()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
