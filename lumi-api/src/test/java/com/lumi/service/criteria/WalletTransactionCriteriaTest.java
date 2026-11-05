package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class WalletTransactionCriteriaTest {

    @Test
    void newWalletTransactionCriteriaHasAllFiltersNullTest() {
        var walletTransactionCriteria = new WalletTransactionCriteria();
        assertThat(walletTransactionCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void walletTransactionCriteriaFluentMethodsCreatesFiltersTest() {
        var walletTransactionCriteria = new WalletTransactionCriteria();

        setAllFilters(walletTransactionCriteria);

        assertThat(walletTransactionCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void walletTransactionCriteriaCopyCreatesNullFilterTest() {
        var walletTransactionCriteria = new WalletTransactionCriteria();
        var copy = walletTransactionCriteria.copy();

        assertThat(walletTransactionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(walletTransactionCriteria)
        );
    }

    @Test
    void walletTransactionCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var walletTransactionCriteria = new WalletTransactionCriteria();
        setAllFilters(walletTransactionCriteria);

        var copy = walletTransactionCriteria.copy();

        assertThat(walletTransactionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(walletTransactionCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var walletTransactionCriteria = new WalletTransactionCriteria();

        assertThat(walletTransactionCriteria).hasToString("WalletTransactionCriteria{}");
    }

    private static void setAllFilters(WalletTransactionCriteria walletTransactionCriteria) {
        walletTransactionCriteria.id();
        walletTransactionCriteria.type();
        walletTransactionCriteria.amount();
        walletTransactionCriteria.status();
        walletTransactionCriteria.idempotencyKey();
        walletTransactionCriteria.occurredAt();
        walletTransactionCriteria.balanceBucket();
        walletTransactionCriteria.walletId();
        walletTransactionCriteria.orderId();
        walletTransactionCriteria.payoutRequestId();
        walletTransactionCriteria.refundId();
        walletTransactionCriteria.distinct();
    }

    private static Condition<WalletTransactionCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getType()) &&
                condition.apply(criteria.getAmount()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getIdempotencyKey()) &&
                condition.apply(criteria.getOccurredAt()) &&
                condition.apply(criteria.getBalanceBucket()) &&
                condition.apply(criteria.getWalletId()) &&
                condition.apply(criteria.getOrderId()) &&
                condition.apply(criteria.getPayoutRequestId()) &&
                condition.apply(criteria.getRefundId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<WalletTransactionCriteria> copyFiltersAre(
        WalletTransactionCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getType(), copy.getType()) &&
                condition.apply(criteria.getAmount(), copy.getAmount()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getIdempotencyKey(), copy.getIdempotencyKey()) &&
                condition.apply(criteria.getOccurredAt(), copy.getOccurredAt()) &&
                condition.apply(criteria.getBalanceBucket(), copy.getBalanceBucket()) &&
                condition.apply(criteria.getWalletId(), copy.getWalletId()) &&
                condition.apply(criteria.getOrderId(), copy.getOrderId()) &&
                condition.apply(criteria.getPayoutRequestId(), copy.getPayoutRequestId()) &&
                condition.apply(criteria.getRefundId(), copy.getRefundId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
