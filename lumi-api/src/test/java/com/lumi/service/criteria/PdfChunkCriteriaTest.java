package com.lumi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class PdfChunkCriteriaTest {

    @Test
    void newPdfChunkCriteriaHasAllFiltersNullTest() {
        var pdfChunkCriteria = new PdfChunkCriteria();
        assertThat(pdfChunkCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void pdfChunkCriteriaFluentMethodsCreatesFiltersTest() {
        var pdfChunkCriteria = new PdfChunkCriteria();

        setAllFilters(pdfChunkCriteria);

        assertThat(pdfChunkCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void pdfChunkCriteriaCopyCreatesNullFilterTest() {
        var pdfChunkCriteria = new PdfChunkCriteria();
        var copy = pdfChunkCriteria.copy();

        assertThat(pdfChunkCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(pdfChunkCriteria)
        );
    }

    @Test
    void pdfChunkCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var pdfChunkCriteria = new PdfChunkCriteria();
        setAllFilters(pdfChunkCriteria);

        var copy = pdfChunkCriteria.copy();

        assertThat(pdfChunkCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(pdfChunkCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var pdfChunkCriteria = new PdfChunkCriteria();

        assertThat(pdfChunkCriteria).hasToString("PdfChunkCriteria{}");
    }

    private static void setAllFilters(PdfChunkCriteria pdfChunkCriteria) {
        pdfChunkCriteria.id();
        pdfChunkCriteria.pageNumber();
        pdfChunkCriteria.chunkText();
        pdfChunkCriteria.embedding();
        pdfChunkCriteria.materialId();
        pdfChunkCriteria.distinct();
    }

    private static Condition<PdfChunkCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getPageNumber()) &&
                condition.apply(criteria.getChunkText()) &&
                condition.apply(criteria.getEmbedding()) &&
                condition.apply(criteria.getMaterialId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<PdfChunkCriteria> copyFiltersAre(PdfChunkCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getPageNumber(), copy.getPageNumber()) &&
                condition.apply(criteria.getChunkText(), copy.getChunkText()) &&
                condition.apply(criteria.getEmbedding(), copy.getEmbedding()) &&
                condition.apply(criteria.getMaterialId(), copy.getMaterialId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
