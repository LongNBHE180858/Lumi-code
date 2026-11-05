package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ClassMaterialCriteriaTest {

    @Test
    void newClassMaterialCriteriaHasAllFiltersNullTest() {
        var classMaterialCriteria = new ClassMaterialCriteria();
        assertThat(classMaterialCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void classMaterialCriteriaFluentMethodsCreatesFiltersTest() {
        var classMaterialCriteria = new ClassMaterialCriteria();

        setAllFilters(classMaterialCriteria);

        assertThat(classMaterialCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void classMaterialCriteriaCopyCreatesNullFilterTest() {
        var classMaterialCriteria = new ClassMaterialCriteria();
        var copy = classMaterialCriteria.copy();

        assertThat(classMaterialCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(classMaterialCriteria)
        );
    }

    @Test
    void classMaterialCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var classMaterialCriteria = new ClassMaterialCriteria();
        setAllFilters(classMaterialCriteria);

        var copy = classMaterialCriteria.copy();

        assertThat(classMaterialCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(classMaterialCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var classMaterialCriteria = new ClassMaterialCriteria();

        assertThat(classMaterialCriteria).hasToString("ClassMaterialCriteria{}");
    }

    private static void setAllFilters(ClassMaterialCriteria classMaterialCriteria) {
        classMaterialCriteria.id();
        classMaterialCriteria.title();
        classMaterialCriteria.fileType();
        classMaterialCriteria.fileUrl();
        classMaterialCriteria.fileSizeBytes();
        classMaterialCriteria.uploadedAt();
        classMaterialCriteria.fileObjectKey();
        classMaterialCriteria.updatedAt();
        classMaterialCriteria.studyClassId();
        classMaterialCriteria.learningDocumentId();
        classMaterialCriteria.distinct();
    }

    private static Condition<ClassMaterialCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getTitle()) &&
                condition.apply(criteria.getFileType()) &&
                condition.apply(criteria.getFileUrl()) &&
                condition.apply(criteria.getFileSizeBytes()) &&
                condition.apply(criteria.getUploadedAt()) &&
                condition.apply(criteria.getFileObjectKey()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getStudyClassId()) &&
                condition.apply(criteria.getLearningDocumentId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ClassMaterialCriteria> copyFiltersAre(
        ClassMaterialCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getTitle(), copy.getTitle()) &&
                condition.apply(criteria.getFileType(), copy.getFileType()) &&
                condition.apply(criteria.getFileUrl(), copy.getFileUrl()) &&
                condition.apply(criteria.getFileSizeBytes(), copy.getFileSizeBytes()) &&
                condition.apply(criteria.getUploadedAt(), copy.getUploadedAt()) &&
                condition.apply(criteria.getFileObjectKey(), copy.getFileObjectKey()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getStudyClassId(), copy.getStudyClassId()) &&
                condition.apply(criteria.getLearningDocumentId(), copy.getLearningDocumentId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
