package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class AiGeneratedContentCriteriaTest {

    @Test
    void newAiGeneratedContentCriteriaHasAllFiltersNullTest() {
        var aiGeneratedContentCriteria = new AiGeneratedContentCriteria();
        assertThat(aiGeneratedContentCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void aiGeneratedContentCriteriaFluentMethodsCreatesFiltersTest() {
        var aiGeneratedContentCriteria = new AiGeneratedContentCriteria();

        setAllFilters(aiGeneratedContentCriteria);

        assertThat(aiGeneratedContentCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void aiGeneratedContentCriteriaCopyCreatesNullFilterTest() {
        var aiGeneratedContentCriteria = new AiGeneratedContentCriteria();
        var copy = aiGeneratedContentCriteria.copy();

        assertThat(aiGeneratedContentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(aiGeneratedContentCriteria)
        );
    }

    @Test
    void aiGeneratedContentCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var aiGeneratedContentCriteria = new AiGeneratedContentCriteria();
        setAllFilters(aiGeneratedContentCriteria);

        var copy = aiGeneratedContentCriteria.copy();

        assertThat(aiGeneratedContentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(aiGeneratedContentCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var aiGeneratedContentCriteria = new AiGeneratedContentCriteria();

        assertThat(aiGeneratedContentCriteria).hasToString("AiGeneratedContentCriteria{}");
    }

    private static void setAllFilters(AiGeneratedContentCriteria aiGeneratedContentCriteria) {
        aiGeneratedContentCriteria.id();
        aiGeneratedContentCriteria.contentType();
        aiGeneratedContentCriteria.requestHash();
        aiGeneratedContentCriteria.sourceVersion();
        aiGeneratedContentCriteria.modelName();
        aiGeneratedContentCriteria.promptVersion();
        aiGeneratedContentCriteria.generationStatus();
        aiGeneratedContentCriteria.visibility();
        aiGeneratedContentCriteria.version();
        aiGeneratedContentCriteria.reviewStatus();
        aiGeneratedContentCriteria.reviewedAt();
        aiGeneratedContentCriteria.createdAt();
        aiGeneratedContentCriteria.updatedAt();
        aiGeneratedContentCriteria.sourceQuestionId();
        aiGeneratedContentCriteria.contentHash();
        aiGeneratedContentCriteria.createdById();
        aiGeneratedContentCriteria.documentId();
        aiGeneratedContentCriteria.lessonId();
        aiGeneratedContentCriteria.reviewedById();
        aiGeneratedContentCriteria.courseId();
        aiGeneratedContentCriteria.distinct();
    }

    private static Condition<AiGeneratedContentCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getContentType()) &&
                condition.apply(criteria.getRequestHash()) &&
                condition.apply(criteria.getSourceVersion()) &&
                condition.apply(criteria.getModelName()) &&
                condition.apply(criteria.getPromptVersion()) &&
                condition.apply(criteria.getGenerationStatus()) &&
                condition.apply(criteria.getVisibility()) &&
                condition.apply(criteria.getVersion()) &&
                condition.apply(criteria.getReviewStatus()) &&
                condition.apply(criteria.getReviewedAt()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getSourceQuestionId()) &&
                condition.apply(criteria.getContentHash()) &&
                condition.apply(criteria.getCreatedById()) &&
                condition.apply(criteria.getDocumentId()) &&
                condition.apply(criteria.getLessonId()) &&
                condition.apply(criteria.getReviewedById()) &&
                condition.apply(criteria.getCourseId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<AiGeneratedContentCriteria> copyFiltersAre(
        AiGeneratedContentCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getContentType(), copy.getContentType()) &&
                condition.apply(criteria.getRequestHash(), copy.getRequestHash()) &&
                condition.apply(criteria.getSourceVersion(), copy.getSourceVersion()) &&
                condition.apply(criteria.getModelName(), copy.getModelName()) &&
                condition.apply(criteria.getPromptVersion(), copy.getPromptVersion()) &&
                condition.apply(criteria.getGenerationStatus(), copy.getGenerationStatus()) &&
                condition.apply(criteria.getVisibility(), copy.getVisibility()) &&
                condition.apply(criteria.getVersion(), copy.getVersion()) &&
                condition.apply(criteria.getReviewStatus(), copy.getReviewStatus()) &&
                condition.apply(criteria.getReviewedAt(), copy.getReviewedAt()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getSourceQuestionId(), copy.getSourceQuestionId()) &&
                condition.apply(criteria.getContentHash(), copy.getContentHash()) &&
                condition.apply(criteria.getCreatedById(), copy.getCreatedById()) &&
                condition.apply(criteria.getDocumentId(), copy.getDocumentId()) &&
                condition.apply(criteria.getLessonId(), copy.getLessonId()) &&
                condition.apply(criteria.getReviewedById(), copy.getReviewedById()) &&
                condition.apply(criteria.getCourseId(), copy.getCourseId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
