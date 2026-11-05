package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class PayoutRequestCriteriaTest {

    @Test
    void newPayoutRequestCriteriaHasAllFiltersNullTest() {
        var payoutRequestCriteria = new PayoutRequestCriteria();
        assertThat(payoutRequestCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void payoutRequestCriteriaFluentMethodsCreatesFiltersTest() {
        var payoutRequestCriteria = new PayoutRequestCriteria();

        setAllFilters(payoutRequestCriteria);

        assertThat(payoutRequestCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void payoutRequestCriteriaCopyCreatesNullFilterTest() {
        var payoutRequestCriteria = new PayoutRequestCriteria();
        var copy = payoutRequestCriteria.copy();

        assertThat(payoutRequestCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(payoutRequestCriteria)
        );
    }

    @Test
    void payoutRequestCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var payoutRequestCriteria = new PayoutRequestCriteria();
        setAllFilters(payoutRequestCriteria);

        var copy = payoutRequestCriteria.copy();

        assertThat(payoutRequestCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(payoutRequestCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var payoutRequestCriteria = new PayoutRequestCriteria();

        assertThat(payoutRequestCriteria).hasToString("PayoutRequestCriteria{}");
    }

    private static void setAllFilters(PayoutRequestCriteria payoutRequestCriteria) {
        payoutRequestCriteria.id();
        payoutRequestCriteria.amount();
        payoutRequestCriteria.bankCode();
        payoutRequestCriteria.bankAccount();
        payoutRequestCriteria.status();
        payoutRequestCriteria.createdAt();
        payoutRequestCriteria.accountHolderName();
        payoutRequestCriteria.processedAt();
        payoutRequestCriteria.transferReference();
        payoutRequestCriteria.updatedAt();
        payoutRequestCriteria.walletId();
        payoutRequestCriteria.processedById();
        payoutRequestCriteria.distinct();
    }

    private static Condition<PayoutRequestCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getAmount()) &&
                condition.apply(criteria.getBankCode()) &&
                condition.apply(criteria.getBankAccount()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getAccountHolderName()) &&
                condition.apply(criteria.getProcessedAt()) &&
                condition.apply(criteria.getTransferReference()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getWalletId()) &&
                condition.apply(criteria.getProcessedById()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<PayoutRequestCriteria> copyFiltersAre(
        PayoutRequestCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getAmount(), copy.getAmount()) &&
                condition.apply(criteria.getBankCode(), copy.getBankCode()) &&
                condition.apply(criteria.getBankAccount(), copy.getBankAccount()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getAccountHolderName(), copy.getAccountHolderName()) &&
                condition.apply(criteria.getProcessedAt(), copy.getProcessedAt()) &&
                condition.apply(criteria.getTransferReference(), copy.getTransferReference()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getWalletId(), copy.getWalletId()) &&
                condition.apply(criteria.getProcessedById(), copy.getProcessedById()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
