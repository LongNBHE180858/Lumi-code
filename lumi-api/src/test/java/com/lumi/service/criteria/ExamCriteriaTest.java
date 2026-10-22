package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ExamCriteriaTest {

    @Test
    void newExamCriteriaHasAllFiltersNullTest() {
        var examCriteria = new ExamCriteria();
        assertThat(examCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void examCriteriaFluentMethodsCreatesFiltersTest() {
        var examCriteria = new ExamCriteria();

        setAllFilters(examCriteria);

        assertThat(examCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void examCriteriaCopyCreatesNullFilterTest() {
        var examCriteria = new ExamCriteria();
        var copy = examCriteria.copy();

        assertThat(examCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(examCriteria)
        );
    }

    @Test
    void examCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var examCriteria = new ExamCriteria();
        setAllFilters(examCriteria);

        var copy = examCriteria.copy();

        assertThat(examCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(examCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var examCriteria = new ExamCriteria();

        assertThat(examCriteria).hasToString("ExamCriteria{}");
    }

    private static void setAllFilters(ExamCriteria examCriteria) {
        examCriteria.id();
        examCriteria.title();
        examCriteria.durationMinutes();
        examCriteria.passScore();
        examCriteria.maxAttempts();
        examCriteria.shuffleQuestions();
        examCriteria.status();
        examCriteria.isDeleted();
        examCriteria.studyClassId();
        examCriteria.distinct();
    }

    private static Condition<ExamCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getTitle()) &&
                condition.apply(criteria.getDurationMinutes()) &&
                condition.apply(criteria.getPassScore()) &&
                condition.apply(criteria.getMaxAttempts()) &&
                condition.apply(criteria.getShuffleQuestions()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getIsDeleted()) &&
                condition.apply(criteria.getStudyClassId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ExamCriteria> copyFiltersAre(ExamCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getTitle(), copy.getTitle()) &&
                condition.apply(criteria.getDurationMinutes(), copy.getDurationMinutes()) &&
                condition.apply(criteria.getPassScore(), copy.getPassScore()) &&
                condition.apply(criteria.getMaxAttempts(), copy.getMaxAttempts()) &&
                condition.apply(criteria.getShuffleQuestions(), copy.getShuffleQuestions()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getIsDeleted(), copy.getIsDeleted()) &&
                condition.apply(criteria.getStudyClassId(), copy.getStudyClassId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
