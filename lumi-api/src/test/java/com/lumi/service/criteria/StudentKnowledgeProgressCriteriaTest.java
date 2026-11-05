package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class StudentKnowledgeProgressCriteriaTest {

    @Test
    void newStudentKnowledgeProgressCriteriaHasAllFiltersNullTest() {
        var studentKnowledgeProgressCriteria = new StudentKnowledgeProgressCriteria();
        assertThat(studentKnowledgeProgressCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void studentKnowledgeProgressCriteriaFluentMethodsCreatesFiltersTest() {
        var studentKnowledgeProgressCriteria = new StudentKnowledgeProgressCriteria();

        setAllFilters(studentKnowledgeProgressCriteria);

        assertThat(studentKnowledgeProgressCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void studentKnowledgeProgressCriteriaCopyCreatesNullFilterTest() {
        var studentKnowledgeProgressCriteria = new StudentKnowledgeProgressCriteria();
        var copy = studentKnowledgeProgressCriteria.copy();

        assertThat(studentKnowledgeProgressCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(studentKnowledgeProgressCriteria)
        );
    }

    @Test
    void studentKnowledgeProgressCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var studentKnowledgeProgressCriteria = new StudentKnowledgeProgressCriteria();
        setAllFilters(studentKnowledgeProgressCriteria);

        var copy = studentKnowledgeProgressCriteria.copy();

        assertThat(studentKnowledgeProgressCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(studentKnowledgeProgressCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var studentKnowledgeProgressCriteria = new StudentKnowledgeProgressCriteria();

        assertThat(studentKnowledgeProgressCriteria).hasToString("StudentKnowledgeProgressCriteria{}");
    }

    private static void setAllFilters(StudentKnowledgeProgressCriteria studentKnowledgeProgressCriteria) {
        studentKnowledgeProgressCriteria.id();
        studentKnowledgeProgressCriteria.masteryScore();
        studentKnowledgeProgressCriteria.evidenceCount();
        studentKnowledgeProgressCriteria.calculationVersion();
        studentKnowledgeProgressCriteria.updatedAt();
        studentKnowledgeProgressCriteria.studentId();
        studentKnowledgeProgressCriteria.knowledgeNodeId();
        studentKnowledgeProgressCriteria.distinct();
    }

    private static Condition<StudentKnowledgeProgressCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getMasteryScore()) &&
                condition.apply(criteria.getEvidenceCount()) &&
                condition.apply(criteria.getCalculationVersion()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getStudentId()) &&
                condition.apply(criteria.getKnowledgeNodeId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<StudentKnowledgeProgressCriteria> copyFiltersAre(
        StudentKnowledgeProgressCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getMasteryScore(), copy.getMasteryScore()) &&
                condition.apply(criteria.getEvidenceCount(), copy.getEvidenceCount()) &&
                condition.apply(criteria.getCalculationVersion(), copy.getCalculationVersion()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getStudentId(), copy.getStudentId()) &&
                condition.apply(criteria.getKnowledgeNodeId(), copy.getKnowledgeNodeId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
