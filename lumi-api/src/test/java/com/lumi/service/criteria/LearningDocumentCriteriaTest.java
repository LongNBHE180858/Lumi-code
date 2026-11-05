package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class LearningDocumentCriteriaTest {

    @Test
    void newLearningDocumentCriteriaHasAllFiltersNullTest() {
        var learningDocumentCriteria = new LearningDocumentCriteria();
        assertThat(learningDocumentCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void learningDocumentCriteriaFluentMethodsCreatesFiltersTest() {
        var learningDocumentCriteria = new LearningDocumentCriteria();

        setAllFilters(learningDocumentCriteria);

        assertThat(learningDocumentCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void learningDocumentCriteriaCopyCreatesNullFilterTest() {
        var learningDocumentCriteria = new LearningDocumentCriteria();
        var copy = learningDocumentCriteria.copy();

        assertThat(learningDocumentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(learningDocumentCriteria)
        );
    }

    @Test
    void learningDocumentCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var learningDocumentCriteria = new LearningDocumentCriteria();
        setAllFilters(learningDocumentCriteria);

        var copy = learningDocumentCriteria.copy();

        assertThat(learningDocumentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(learningDocumentCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var learningDocumentCriteria = new LearningDocumentCriteria();

        assertThat(learningDocumentCriteria).hasToString("LearningDocumentCriteria{}");
    }

    private static void setAllFilters(LearningDocumentCriteria learningDocumentCriteria) {
        learningDocumentCriteria.id();
        learningDocumentCriteria.sourceType();
        learningDocumentCriteria.title();
        learningDocumentCriteria.fileObjectKey();
        learningDocumentCriteria.contentHash();
        learningDocumentCriteria.sourceVersion();
        learningDocumentCriteria.processingStatus();
        learningDocumentCriteria.indexStatus();
        learningDocumentCriteria.extractedTextObjectKey();
        learningDocumentCriteria.embeddingModel();
        learningDocumentCriteria.createdAt();
        learningDocumentCriteria.updatedAt();
        learningDocumentCriteria.bookSeries();
        learningDocumentCriteria.courseMaterialId();
        learningDocumentCriteria.classMaterialId();
        learningDocumentCriteria.uploadedById();
        learningDocumentCriteria.subjectId();
        learningDocumentCriteria.gradeId();
        learningDocumentCriteria.distinct();
    }

    private static Condition<LearningDocumentCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getSourceType()) &&
                condition.apply(criteria.getTitle()) &&
                condition.apply(criteria.getFileObjectKey()) &&
                condition.apply(criteria.getContentHash()) &&
                condition.apply(criteria.getSourceVersion()) &&
                condition.apply(criteria.getProcessingStatus()) &&
                condition.apply(criteria.getIndexStatus()) &&
                condition.apply(criteria.getExtractedTextObjectKey()) &&
                condition.apply(criteria.getEmbeddingModel()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getBookSeries()) &&
                condition.apply(criteria.getCourseMaterialId()) &&
                condition.apply(criteria.getClassMaterialId()) &&
                condition.apply(criteria.getUploadedById()) &&
                condition.apply(criteria.getSubjectId()) &&
                condition.apply(criteria.getGradeId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<LearningDocumentCriteria> copyFiltersAre(
        LearningDocumentCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getSourceType(), copy.getSourceType()) &&
                condition.apply(criteria.getTitle(), copy.getTitle()) &&
                condition.apply(criteria.getFileObjectKey(), copy.getFileObjectKey()) &&
                condition.apply(criteria.getContentHash(), copy.getContentHash()) &&
                condition.apply(criteria.getSourceVersion(), copy.getSourceVersion()) &&
                condition.apply(criteria.getProcessingStatus(), copy.getProcessingStatus()) &&
                condition.apply(criteria.getIndexStatus(), copy.getIndexStatus()) &&
                condition.apply(criteria.getExtractedTextObjectKey(), copy.getExtractedTextObjectKey()) &&
                condition.apply(criteria.getEmbeddingModel(), copy.getEmbeddingModel()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getBookSeries(), copy.getBookSeries()) &&
                condition.apply(criteria.getCourseMaterialId(), copy.getCourseMaterialId()) &&
                condition.apply(criteria.getClassMaterialId(), copy.getClassMaterialId()) &&
                condition.apply(criteria.getUploadedById(), copy.getUploadedById()) &&
                condition.apply(criteria.getSubjectId(), copy.getSubjectId()) &&
                condition.apply(criteria.getGradeId(), copy.getGradeId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
