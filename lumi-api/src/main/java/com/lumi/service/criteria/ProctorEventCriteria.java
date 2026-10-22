package com.lumi.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.domain.ProctorEvent} entity. This class is used
 * in {@link com.lumi.web.rest.ProctorEventResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /proctor-events?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProctorEventCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter violationType;

    private StringFilter photoUrl;

    private InstantFilter occurredAt;

    private LongFilter sessionId;

    private Boolean distinct;

    public ProctorEventCriteria() {}

    public ProctorEventCriteria(ProctorEventCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.violationType = other.optionalViolationType().map(StringFilter::copy).orElse(null);
        this.photoUrl = other.optionalPhotoUrl().map(StringFilter::copy).orElse(null);
        this.occurredAt = other.optionalOccurredAt().map(InstantFilter::copy).orElse(null);
        this.sessionId = other.optionalSessionId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ProctorEventCriteria copy() {
        return new ProctorEventCriteria(this);
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

    public StringFilter getViolationType() {
        return violationType;
    }

    public Optional<StringFilter> optionalViolationType() {
        return Optional.ofNullable(violationType);
    }

    public StringFilter violationType() {
        if (violationType == null) {
            setViolationType(new StringFilter());
        }
        return violationType;
    }

    public void setViolationType(StringFilter violationType) {
        this.violationType = violationType;
    }

    public StringFilter getPhotoUrl() {
        return photoUrl;
    }

    public Optional<StringFilter> optionalPhotoUrl() {
        return Optional.ofNullable(photoUrl);
    }

    public StringFilter photoUrl() {
        if (photoUrl == null) {
            setPhotoUrl(new StringFilter());
        }
        return photoUrl;
    }

    public void setPhotoUrl(StringFilter photoUrl) {
        this.photoUrl = photoUrl;
    }

    public InstantFilter getOccurredAt() {
        return occurredAt;
    }

    public Optional<InstantFilter> optionalOccurredAt() {
        return Optional.ofNullable(occurredAt);
    }

    public InstantFilter occurredAt() {
        if (occurredAt == null) {
            setOccurredAt(new InstantFilter());
        }
        return occurredAt;
    }

    public void setOccurredAt(InstantFilter occurredAt) {
        this.occurredAt = occurredAt;
    }

    public LongFilter getSessionId() {
        return sessionId;
    }

    public Optional<LongFilter> optionalSessionId() {
        return Optional.ofNullable(sessionId);
    }

    public LongFilter sessionId() {
        if (sessionId == null) {
            setSessionId(new LongFilter());
        }
        return sessionId;
    }

    public void setSessionId(LongFilter sessionId) {
        this.sessionId = sessionId;
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
        final ProctorEventCriteria that = (ProctorEventCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(violationType, that.violationType) &&
            Objects.equals(photoUrl, that.photoUrl) &&
            Objects.equals(occurredAt, that.occurredAt) &&
            Objects.equals(sessionId, that.sessionId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, violationType, photoUrl, occurredAt, sessionId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProctorEventCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalViolationType().map(f -> "violationType=" + f + ", ").orElse("") +
            optionalPhotoUrl().map(f -> "photoUrl=" + f + ", ").orElse("") +
            optionalOccurredAt().map(f -> "occurredAt=" + f + ", ").orElse("") +
            optionalSessionId().map(f -> "sessionId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
