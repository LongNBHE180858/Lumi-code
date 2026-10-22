package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class TicketMessageCriteriaTest {

    @Test
    void newTicketMessageCriteriaHasAllFiltersNullTest() {
        var ticketMessageCriteria = new TicketMessageCriteria();
        assertThat(ticketMessageCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void ticketMessageCriteriaFluentMethodsCreatesFiltersTest() {
        var ticketMessageCriteria = new TicketMessageCriteria();

        setAllFilters(ticketMessageCriteria);

        assertThat(ticketMessageCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void ticketMessageCriteriaCopyCreatesNullFilterTest() {
        var ticketMessageCriteria = new TicketMessageCriteria();
        var copy = ticketMessageCriteria.copy();

        assertThat(ticketMessageCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(ticketMessageCriteria)
        );
    }

    @Test
    void ticketMessageCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var ticketMessageCriteria = new TicketMessageCriteria();
        setAllFilters(ticketMessageCriteria);

        var copy = ticketMessageCriteria.copy();

        assertThat(ticketMessageCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(ticketMessageCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var ticketMessageCriteria = new TicketMessageCriteria();

        assertThat(ticketMessageCriteria).hasToString("TicketMessageCriteria{}");
    }

    private static void setAllFilters(TicketMessageCriteria ticketMessageCriteria) {
        ticketMessageCriteria.id();
        ticketMessageCriteria.content();
        ticketMessageCriteria.createdAt();
        ticketMessageCriteria.senderId();
        ticketMessageCriteria.ticketId();
        ticketMessageCriteria.distinct();
    }

    private static Condition<TicketMessageCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getContent()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getSenderId()) &&
                condition.apply(criteria.getTicketId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<TicketMessageCriteria> copyFiltersAre(
        TicketMessageCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getContent(), copy.getContent()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getSenderId(), copy.getSenderId()) &&
                condition.apply(criteria.getTicketId(), copy.getTicketId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
