package com.lumi.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.domain.KnowledgeEdge} entity. This class is used
 * in {@link com.lumi.web.rest.KnowledgeEdgeResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /knowledge-edges?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class KnowledgeEdgeCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter relationType;

    private LongFilter fromNodeId;

    private LongFilter toNodeId;

    private Boolean distinct;

    public KnowledgeEdgeCriteria() {}

    public KnowledgeEdgeCriteria(KnowledgeEdgeCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.relationType = other.optionalRelationType().map(StringFilter::copy).orElse(null);
        this.fromNodeId = other.optionalFromNodeId().map(LongFilter::copy).orElse(null);
        this.toNodeId = other.optionalToNodeId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public KnowledgeEdgeCriteria copy() {
        return new KnowledgeEdgeCriteria(this);
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

    public StringFilter getRelationType() {
        return relationType;
    }

    public Optional<StringFilter> optionalRelationType() {
        return Optional.ofNullable(relationType);
    }

    public StringFilter relationType() {
        if (relationType == null) {
            setRelationType(new StringFilter());
        }
        return relationType;
    }

    public void setRelationType(StringFilter relationType) {
        this.relationType = relationType;
    }

    public LongFilter getFromNodeId() {
        return fromNodeId;
    }

    public Optional<LongFilter> optionalFromNodeId() {
        return Optional.ofNullable(fromNodeId);
    }

    public LongFilter fromNodeId() {
        if (fromNodeId == null) {
            setFromNodeId(new LongFilter());
        }
        return fromNodeId;
    }

    public void setFromNodeId(LongFilter fromNodeId) {
        this.fromNodeId = fromNodeId;
    }

    public LongFilter getToNodeId() {
        return toNodeId;
    }

    public Optional<LongFilter> optionalToNodeId() {
        return Optional.ofNullable(toNodeId);
    }

    public LongFilter toNodeId() {
        if (toNodeId == null) {
            setToNodeId(new LongFilter());
        }
        return toNodeId;
    }

    public void setToNodeId(LongFilter toNodeId) {
        this.toNodeId = toNodeId;
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
        final KnowledgeEdgeCriteria that = (KnowledgeEdgeCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(relationType, that.relationType) &&
            Objects.equals(fromNodeId, that.fromNodeId) &&
            Objects.equals(toNodeId, that.toNodeId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, relationType, fromNodeId, toNodeId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "KnowledgeEdgeCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalRelationType().map(f -> "relationType=" + f + ", ").orElse("") +
            optionalFromNodeId().map(f -> "fromNodeId=" + f + ", ").orElse("") +
            optionalToNodeId().map(f -> "toNodeId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
