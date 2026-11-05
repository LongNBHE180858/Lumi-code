package com.lumi.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.domain.PdfChunk} entity. This class is used
 * in {@link com.lumi.web.rest.PdfChunkResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /pdf-chunks?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PdfChunkCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter pageNumber;

    private IntegerFilter chunkIndex;

    private IntegerFilter indexVersion;

    private LongFilter materialId;

    private Boolean distinct;

    public PdfChunkCriteria() {}

    public PdfChunkCriteria(PdfChunkCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.pageNumber = other.optionalPageNumber().map(IntegerFilter::copy).orElse(null);
        this.chunkIndex = other.optionalChunkIndex().map(IntegerFilter::copy).orElse(null);
        this.indexVersion = other.optionalIndexVersion().map(IntegerFilter::copy).orElse(null);
        this.materialId = other.optionalMaterialId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public PdfChunkCriteria copy() {
        return new PdfChunkCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public IntegerFilter getPageNumber() {
        return pageNumber;
    }

    public Optional<IntegerFilter> optionalPageNumber() {
        return Optional.ofNullable(pageNumber);
    }

    public IntegerFilter pageNumber() {
        if (pageNumber == null) {
            setPageNumber(new IntegerFilter());
        }
        return pageNumber;
    }

    public void setPageNumber(IntegerFilter pageNumber) {
        this.pageNumber = pageNumber;
    }

    public IntegerFilter getChunkIndex() {
        return chunkIndex;
    }

    public Optional<IntegerFilter> optionalChunkIndex() {
        return Optional.ofNullable(chunkIndex);
    }

    public IntegerFilter chunkIndex() {
        if (chunkIndex == null) {
            setChunkIndex(new IntegerFilter());
        }
        return chunkIndex;
    }

    public void setChunkIndex(IntegerFilter chunkIndex) {
        this.chunkIndex = chunkIndex;
    }

    public IntegerFilter getIndexVersion() {
        return indexVersion;
    }

    public Optional<IntegerFilter> optionalIndexVersion() {
        return Optional.ofNullable(indexVersion);
    }

    public IntegerFilter indexVersion() {
        if (indexVersion == null) {
            setIndexVersion(new IntegerFilter());
        }
        return indexVersion;
    }

    public void setIndexVersion(IntegerFilter indexVersion) {
        this.indexVersion = indexVersion;
    }

    public LongFilter getMaterialId() {
        return materialId;
    }

    public Optional<LongFilter> optionalMaterialId() {
        return Optional.ofNullable(materialId);
    }

    public LongFilter materialId() {
        if (materialId == null) {
            setMaterialId(new LongFilter());
        }
        return materialId;
    }

    public void setMaterialId(LongFilter materialId) {
        this.materialId = materialId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PdfChunkCriteria that = (PdfChunkCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(pageNumber, that.pageNumber) &&
            Objects.equals(chunkIndex, that.chunkIndex) &&
            Objects.equals(indexVersion, that.indexVersion) &&
            Objects.equals(materialId, that.materialId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, pageNumber, chunkIndex, indexVersion, materialId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PdfChunkCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalPageNumber().map(f -> "pageNumber=" + f + ", ").orElse("") +
            optionalChunkIndex().map(f -> "chunkIndex=" + f + ", ").orElse("") +
            optionalIndexVersion().map(f -> "indexVersion=" + f + ", ").orElse("") +
            optionalMaterialId().map(f -> "materialId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
