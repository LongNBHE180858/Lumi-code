package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class StudyClassCriteriaTest {

    @Test
    void newStudyClassCriteriaHasAllFiltersNullTest() {
        var studyClassCriteria = new StudyClassCriteria();
        assertThat(studyClassCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void studyClassCriteriaFluentMethodsCreatesFiltersTest() {
        var studyClassCriteria = new StudyClassCriteria();

        setAllFilters(studyClassCriteria);

        assertThat(studyClassCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void studyClassCriteriaCopyCreatesNullFilterTest() {
        var studyClassCriteria = new StudyClassCriteria();
        var copy = studyClassCriteria.copy();

        assertThat(studyClassCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(studyClassCriteria)
        );
    }

    @Test
    void studyClassCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var studyClassCriteria = new StudyClassCriteria();
        setAllFilters(studyClassCriteria);

        var copy = studyClassCriteria.copy();

        assertThat(studyClassCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(studyClassCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var studyClassCriteria = new StudyClassCriteria();

        assertThat(studyClassCriteria).hasToString("StudyClassCriteria{}");
    }

    private static void setAllFilters(StudyClassCriteria studyClassCriteria) {
        studyClassCriteria.id();
        studyClassCriteria.name();
        studyClassCriteria.joinCode();
        studyClassCriteria.joinMode();
        studyClassCriteria.maxStudents();
        studyClassCriteria.currentStudents();
        studyClassCriteria.status();
        studyClassCriteria.isDeleted();
        studyClassCriteria.createdAt();
        studyClassCriteria.joinCodeExpiresAt();
        studyClassCriteria.updatedAt();
        studyClassCriteria.instructorId();
        studyClassCriteria.subjectId();
        studyClassCriteria.gradeId();
        studyClassCriteria.distinct();
    }

    private static Condition<StudyClassCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getJoinCode()) &&
                condition.apply(criteria.getJoinMode()) &&
                condition.apply(criteria.getMaxStudents()) &&
                condition.apply(criteria.getCurrentStudents()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getIsDeleted()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getJoinCodeExpiresAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getInstructorId()) &&
                condition.apply(criteria.getSubjectId()) &&
                condition.apply(criteria.getGradeId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<StudyClassCriteria> copyFiltersAre(StudyClassCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getJoinCode(), copy.getJoinCode()) &&
                condition.apply(criteria.getJoinMode(), copy.getJoinMode()) &&
                condition.apply(criteria.getMaxStudents(), copy.getMaxStudents()) &&
                condition.apply(criteria.getCurrentStudents(), copy.getCurrentStudents()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getIsDeleted(), copy.getIsDeleted()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getJoinCodeExpiresAt(), copy.getJoinCodeExpiresAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getInstructorId(), copy.getInstructorId()) &&
                condition.apply(criteria.getSubjectId(), copy.getSubjectId()) &&
                condition.apply(criteria.getGradeId(), copy.getGradeId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
