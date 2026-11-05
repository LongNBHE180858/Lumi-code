package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ParentStudentLinkCriteriaTest {

    @Test
    void newParentStudentLinkCriteriaHasAllFiltersNullTest() {
        var parentStudentLinkCriteria = new ParentStudentLinkCriteria();
        assertThat(parentStudentLinkCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void parentStudentLinkCriteriaFluentMethodsCreatesFiltersTest() {
        var parentStudentLinkCriteria = new ParentStudentLinkCriteria();

        setAllFilters(parentStudentLinkCriteria);

        assertThat(parentStudentLinkCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void parentStudentLinkCriteriaCopyCreatesNullFilterTest() {
        var parentStudentLinkCriteria = new ParentStudentLinkCriteria();
        var copy = parentStudentLinkCriteria.copy();

        assertThat(parentStudentLinkCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(parentStudentLinkCriteria)
        );
    }

    @Test
    void parentStudentLinkCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var parentStudentLinkCriteria = new ParentStudentLinkCriteria();
        setAllFilters(parentStudentLinkCriteria);

        var copy = parentStudentLinkCriteria.copy();

        assertThat(parentStudentLinkCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(parentStudentLinkCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var parentStudentLinkCriteria = new ParentStudentLinkCriteria();

        assertThat(parentStudentLinkCriteria).hasToString("ParentStudentLinkCriteria{}");
    }

    private static void setAllFilters(ParentStudentLinkCriteria parentStudentLinkCriteria) {
        parentStudentLinkCriteria.id();
        parentStudentLinkCriteria.linkCode();
        parentStudentLinkCriteria.status();
        parentStudentLinkCriteria.linkedAt();
        parentStudentLinkCriteria.createdAt();
        parentStudentLinkCriteria.codeExpiresAt();
        parentStudentLinkCriteria.confirmedAt();
        parentStudentLinkCriteria.revokedAt();
        parentStudentLinkCriteria.relationshipType();
        parentStudentLinkCriteria.guardianVerificationStatus();
        parentStudentLinkCriteria.guardianVerifiedAt();
        parentStudentLinkCriteria.parentId();
        parentStudentLinkCriteria.studentId();
        parentStudentLinkCriteria.confirmedById();
        parentStudentLinkCriteria.guardianVerifiedById();
        parentStudentLinkCriteria.distinct();
    }

    private static Condition<ParentStudentLinkCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getLinkCode()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getLinkedAt()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getCodeExpiresAt()) &&
                condition.apply(criteria.getConfirmedAt()) &&
                condition.apply(criteria.getRevokedAt()) &&
                condition.apply(criteria.getRelationshipType()) &&
                condition.apply(criteria.getGuardianVerificationStatus()) &&
                condition.apply(criteria.getGuardianVerifiedAt()) &&
                condition.apply(criteria.getParentId()) &&
                condition.apply(criteria.getStudentId()) &&
                condition.apply(criteria.getConfirmedById()) &&
                condition.apply(criteria.getGuardianVerifiedById()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ParentStudentLinkCriteria> copyFiltersAre(
        ParentStudentLinkCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getLinkCode(), copy.getLinkCode()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getLinkedAt(), copy.getLinkedAt()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getCodeExpiresAt(), copy.getCodeExpiresAt()) &&
                condition.apply(criteria.getConfirmedAt(), copy.getConfirmedAt()) &&
                condition.apply(criteria.getRevokedAt(), copy.getRevokedAt()) &&
                condition.apply(criteria.getRelationshipType(), copy.getRelationshipType()) &&
                condition.apply(criteria.getGuardianVerificationStatus(), copy.getGuardianVerificationStatus()) &&
                condition.apply(criteria.getGuardianVerifiedAt(), copy.getGuardianVerifiedAt()) &&
                condition.apply(criteria.getParentId(), copy.getParentId()) &&
                condition.apply(criteria.getStudentId(), copy.getStudentId()) &&
                condition.apply(criteria.getConfirmedById(), copy.getConfirmedById()) &&
                condition.apply(criteria.getGuardianVerifiedById(), copy.getGuardianVerifiedById()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
