package com.lumi.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.domain.AuditLog} entity. This class is used
 * in {@link com.lumi.web.rest.AuditLogResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /audit-logs?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AuditLogCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter action;

    private StringFilter targetEntity;

    private StringFilter targetId;

    private StringFilter ipAddress;

    private StringFilter details;

    private InstantFilter occurredAt;

    private LongFilter actorId;

    private Boolean distinct;

    public AuditLogCriteria() {}

    public AuditLogCriteria(AuditLogCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.action = other.optionalAction().map(StringFilter::copy).orElse(null);
        this.targetEntity = other.optionalTargetEntity().map(StringFilter::copy).orElse(null);
        this.targetId = other.optionalTargetId().map(StringFilter::copy).orElse(null);
        this.ipAddress = other.optionalIpAddress().map(StringFilter::copy).orElse(null);
        this.details = other.optionalDetails().map(StringFilter::copy).orElse(null);
        this.occurredAt = other.optionalOccurredAt().map(InstantFilter::copy).orElse(null);
        this.actorId = other.optionalActorId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public AuditLogCriteria copy() {
        return new AuditLogCriteria(this);
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

    public StringFilter getAction() {
        return action;
    }

    public Optional<StringFilter> optionalAction() {
        return Optional.ofNullable(action);
    }

    public StringFilter action() {
        if (action == null) {
            setAction(new StringFilter());
        }
        return action;
    }

    public void setAction(StringFilter action) {
        this.action = action;
    }

    public StringFilter getTargetEntity() {
        return targetEntity;
    }

    public Optional<StringFilter> optionalTargetEntity() {
        return Optional.ofNullable(targetEntity);
    }

    public StringFilter targetEntity() {
        if (targetEntity == null) {
            setTargetEntity(new StringFilter());
        }
        return targetEntity;
    }

    public void setTargetEntity(StringFilter targetEntity) {
        this.targetEntity = targetEntity;
    }

    public StringFilter getTargetId() {
        return targetId;
    }

    public Optional<StringFilter> optionalTargetId() {
        return Optional.ofNullable(targetId);
    }

    public StringFilter targetId() {
        if (targetId == null) {
            setTargetId(new StringFilter());
        }
        return targetId;
    }

    public void setTargetId(StringFilter targetId) {
        this.targetId = targetId;
    }

    public StringFilter getIpAddress() {
        return ipAddress;
    }

    public Optional<StringFilter> optionalIpAddress() {
        return Optional.ofNullable(ipAddress);
    }

    public StringFilter ipAddress() {
        if (ipAddress == null) {
            setIpAddress(new StringFilter());
        }
        return ipAddress;
    }

    public void setIpAddress(StringFilter ipAddress) {
        this.ipAddress = ipAddress;
    }

    public StringFilter getDetails() {
        return details;
    }

    public Optional<StringFilter> optionalDetails() {
        return Optional.ofNullable(details);
    }

    public StringFilter details() {
        if (details == null) {
            setDetails(new StringFilter());
        }
        return details;
    }

    public void setDetails(StringFilter details) {
        this.details = details;
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

    public LongFilter getActorId() {
        return actorId;
    }

    public Optional<LongFilter> optionalActorId() {
        return Optional.ofNullable(actorId);
    }

    public LongFilter actorId() {
        if (actorId == null) {
            setActorId(new LongFilter());
        }
        return actorId;
    }

    public void setActorId(LongFilter actorId) {
        this.actorId = actorId;
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
        final AuditLogCriteria that = (AuditLogCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(action, that.action) &&
            Objects.equals(targetEntity, that.targetEntity) &&
            Objects.equals(targetId, that.targetId) &&
            Objects.equals(ipAddress, that.ipAddress) &&
            Objects.equals(details, that.details) &&
            Objects.equals(occurredAt, that.occurredAt) &&
            Objects.equals(actorId, that.actorId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, action, targetEntity, targetId, ipAddress, details, occurredAt, actorId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AuditLogCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalAction().map(f -> "action=" + f + ", ").orElse("") +
            optionalTargetEntity().map(f -> "targetEntity=" + f + ", ").orElse("") +
            optionalTargetId().map(f -> "targetId=" + f + ", ").orElse("") +
            optionalIpAddress().map(f -> "ipAddress=" + f + ", ").orElse("") +
            optionalDetails().map(f -> "details=" + f + ", ").orElse("") +
            optionalOccurredAt().map(f -> "occurredAt=" + f + ", ").orElse("") +
            optionalActorId().map(f -> "actorId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
