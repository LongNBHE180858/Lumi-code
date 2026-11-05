package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ClassMemberCriteriaTest {

    @Test
    void newClassMemberCriteriaHasAllFiltersNullTest() {
        var classMemberCriteria = new ClassMemberCriteria();
        assertThat(classMemberCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void classMemberCriteriaFluentMethodsCreatesFiltersTest() {
        var classMemberCriteria = new ClassMemberCriteria();

        setAllFilters(classMemberCriteria);

        assertThat(classMemberCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void classMemberCriteriaCopyCreatesNullFilterTest() {
        var classMemberCriteria = new ClassMemberCriteria();
        var copy = classMemberCriteria.copy();

        assertThat(classMemberCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(classMemberCriteria)
        );
    }

    @Test
    void classMemberCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var classMemberCriteria = new ClassMemberCriteria();
        setAllFilters(classMemberCriteria);

        var copy = classMemberCriteria.copy();

        assertThat(classMemberCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(classMemberCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var classMemberCriteria = new ClassMemberCriteria();

        assertThat(classMemberCriteria).hasToString("ClassMemberCriteria{}");
    }

    private static void setAllFilters(ClassMemberCriteria classMemberCriteria) {
        classMemberCriteria.id();
        classMemberCriteria.status();
        classMemberCriteria.joinedAt();
        classMemberCriteria.requestedAt();
        classMemberCriteria.leftAt();
        classMemberCriteria.updatedAt();
        classMemberCriteria.studyClassId();
        classMemberCriteria.studentId();
        classMemberCriteria.distinct();
    }

    private static Condition<ClassMemberCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getJoinedAt()) &&
                condition.apply(criteria.getRequestedAt()) &&
                condition.apply(criteria.getLeftAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getStudyClassId()) &&
                condition.apply(criteria.getStudentId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ClassMemberCriteria> copyFiltersAre(ClassMemberCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getJoinedAt(), copy.getJoinedAt()) &&
                condition.apply(criteria.getRequestedAt(), copy.getRequestedAt()) &&
                condition.apply(criteria.getLeftAt(), copy.getLeftAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getStudyClassId(), copy.getStudyClassId()) &&
                condition.apply(criteria.getStudentId(), copy.getStudentId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
