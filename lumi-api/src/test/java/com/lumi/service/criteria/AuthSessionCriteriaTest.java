package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class AuthSessionCriteriaTest {

    @Test
    void newAuthSessionCriteriaHasAllFiltersNullTest() {
        var authSessionCriteria = new AuthSessionCriteria();
        assertThat(authSessionCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void authSessionCriteriaFluentMethodsCreatesFiltersTest() {
        var authSessionCriteria = new AuthSessionCriteria();

        setAllFilters(authSessionCriteria);

        assertThat(authSessionCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void authSessionCriteriaCopyCreatesNullFilterTest() {
        var authSessionCriteria = new AuthSessionCriteria();
        var copy = authSessionCriteria.copy();

        assertThat(authSessionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(authSessionCriteria)
        );
    }

    @Test
    void authSessionCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var authSessionCriteria = new AuthSessionCriteria();
        setAllFilters(authSessionCriteria);

        var copy = authSessionCriteria.copy();

        assertThat(authSessionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(authSessionCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var authSessionCriteria = new AuthSessionCriteria();

        assertThat(authSessionCriteria).hasToString("AuthSessionCriteria{}");
    }

    private static void setAllFilters(AuthSessionCriteria authSessionCriteria) {
        authSessionCriteria.id();
        authSessionCriteria.deviceInfo();
        authSessionCriteria.refreshToken();
        authSessionCriteria.expiresAt();
        authSessionCriteria.isRevoked();
        authSessionCriteria.createdAt();
        authSessionCriteria.refreshTokenHash();
        authSessionCriteria.userId();
        authSessionCriteria.distinct();
    }

    private static Condition<AuthSessionCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getDeviceInfo()) &&
                condition.apply(criteria.getRefreshToken()) &&
                condition.apply(criteria.getExpiresAt()) &&
                condition.apply(criteria.getIsRevoked()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getRefreshTokenHash()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<AuthSessionCriteria> copyFiltersAre(AuthSessionCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getDeviceInfo(), copy.getDeviceInfo()) &&
                condition.apply(criteria.getRefreshToken(), copy.getRefreshToken()) &&
                condition.apply(criteria.getExpiresAt(), copy.getExpiresAt()) &&
                condition.apply(criteria.getIsRevoked(), copy.getIsRevoked()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getRefreshTokenHash(), copy.getRefreshTokenHash()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
