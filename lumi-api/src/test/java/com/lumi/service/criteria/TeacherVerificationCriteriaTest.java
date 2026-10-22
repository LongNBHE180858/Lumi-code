package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class TeacherVerificationCriteriaTest {

    @Test
    void newTeacherVerificationCriteriaHasAllFiltersNullTest() {
        var teacherVerificationCriteria = new TeacherVerificationCriteria();
        assertThat(teacherVerificationCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void teacherVerificationCriteriaFluentMethodsCreatesFiltersTest() {
        var teacherVerificationCriteria = new TeacherVerificationCriteria();

        setAllFilters(teacherVerificationCriteria);

        assertThat(teacherVerificationCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void teacherVerificationCriteriaCopyCreatesNullFilterTest() {
        var teacherVerificationCriteria = new TeacherVerificationCriteria();
        var copy = teacherVerificationCriteria.copy();

        assertThat(teacherVerificationCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(teacherVerificationCriteria)
        );
    }

    @Test
    void teacherVerificationCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var teacherVerificationCriteria = new TeacherVerificationCriteria();
        setAllFilters(teacherVerificationCriteria);

        var copy = teacherVerificationCriteria.copy();

        assertThat(teacherVerificationCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(teacherVerificationCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var teacherVerificationCriteria = new TeacherVerificationCriteria();

        assertThat(teacherVerificationCriteria).hasToString("TeacherVerificationCriteria{}");
    }

    private static void setAllFilters(TeacherVerificationCriteria teacherVerificationCriteria) {
        teacherVerificationCriteria.id();
        teacherVerificationCriteria.identityDocumentUrl();
        teacherVerificationCriteria.verificationStatus();
        teacherVerificationCriteria.notes();
        teacherVerificationCriteria.submittedAt();
        teacherVerificationCriteria.approvedAt();
        teacherVerificationCriteria.userId();
        teacherVerificationCriteria.distinct();
    }

    private static Condition<TeacherVerificationCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getIdentityDocumentUrl()) &&
                condition.apply(criteria.getVerificationStatus()) &&
                condition.apply(criteria.getNotes()) &&
                condition.apply(criteria.getSubmittedAt()) &&
                condition.apply(criteria.getApprovedAt()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<TeacherVerificationCriteria> copyFiltersAre(
        TeacherVerificationCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getIdentityDocumentUrl(), copy.getIdentityDocumentUrl()) &&
                condition.apply(criteria.getVerificationStatus(), copy.getVerificationStatus()) &&
                condition.apply(criteria.getNotes(), copy.getNotes()) &&
                condition.apply(criteria.getSubmittedAt(), copy.getSubmittedAt()) &&
                condition.apply(criteria.getApprovedAt(), copy.getApprovedAt()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
