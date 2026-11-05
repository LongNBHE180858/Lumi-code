package com.lumi.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.domain.ContentReport} entity. This class is used
 * in {@link com.lumi.web.rest.ContentReportResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /content-reports?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ContentReportCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter targetType;

    private LongFilter targetId;

    private StringFilter status;

    private InstantFilter createdAt;

    private InstantFilter resolvedAt;

    private StringFilter targetVersion;

    private LongFilter reporterId;

    private LongFilter assignedToId;

    private LongFilter resolvedById;

    private Boolean distinct;

    public ContentReportCriteria() {}

    public ContentReportCriteria(ContentReportCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.targetType = other.optionalTargetType().map(StringFilter::copy).orElse(null);
        this.targetId = other.optionalTargetId().map(LongFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(StringFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.resolvedAt = other.optionalResolvedAt().map(InstantFilter::copy).orElse(null);
        this.targetVersion = other.optionalTargetVersion().map(StringFilter::copy).orElse(null);
        this.reporterId = other.optionalReporterId().map(LongFilter::copy).orElse(null);
        this.assignedToId = other.optionalAssignedToId().map(LongFilter::copy).orElse(null);
        this.resolvedById = other.optionalResolvedById().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ContentReportCriteria copy() {
        return new ContentReportCriteria(this);
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

    public StringFilter getTargetType() {
        return targetType;
    }

    public Optional<StringFilter> optionalTargetType() {
        return Optional.ofNullable(targetType);
    }

    public StringFilter targetType() {
        if (targetType == null) {
            setTargetType(new StringFilter());
        }
        return targetType;
    }

    public void setTargetType(StringFilter targetType) {
        this.targetType = targetType;
    }

    public LongFilter getTargetId() {
        return targetId;
    }

    public Optional<LongFilter> optionalTargetId() {
        return Optional.ofNullable(targetId);
    }

    public LongFilter targetId() {
        if (targetId == null) {
            setTargetId(new LongFilter());
        }
        return targetId;
    }

    public void setTargetId(LongFilter targetId) {
        this.targetId = targetId;
    }

    public StringFilter getStatus() {
        return status;
    }

    public Optional<StringFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public StringFilter status() {
        if (status == null) {
            setStatus(new StringFilter());
        }
        return status;
    }

    public void setStatus(StringFilter status) {
        this.status = status;
    }

    public InstantFilter getCreatedAt() {
        return createdAt;
    }

    public Optional<InstantFilter> optionalCreatedAt() {
        return Optional.ofNullable(createdAt);
    }

    public InstantFilter createdAt() {
        if (createdAt == null) {
            setCreatedAt(new InstantFilter());
        }
        return createdAt;
    }

    public void setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
    }

    public InstantFilter getResolvedAt() {
        return resolvedAt;
    }

    public Optional<InstantFilter> optionalResolvedAt() {
        return Optional.ofNullable(resolvedAt);
    }

    public InstantFilter resolvedAt() {
        if (resolvedAt == null) {
            setResolvedAt(new InstantFilter());
        }
        return resolvedAt;
    }

    public void setResolvedAt(InstantFilter resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public StringFilter getTargetVersion() {
        return targetVersion;
    }

    public Optional<StringFilter> optionalTargetVersion() {
        return Optional.ofNullable(targetVersion);
    }

    public StringFilter targetVersion() {
        if (targetVersion == null) {
            setTargetVersion(new StringFilter());
        }
        return targetVersion;
    }

    public void setTargetVersion(StringFilter targetVersion) {
        this.targetVersion = targetVersion;
    }

    public LongFilter getReporterId() {
        return reporterId;
    }

    public Optional<LongFilter> optionalReporterId() {
        return Optional.ofNullable(reporterId);
    }

    public LongFilter reporterId() {
        if (reporterId == null) {
            setReporterId(new LongFilter());
        }
        return reporterId;
    }

    public void setReporterId(LongFilter reporterId) {
        this.reporterId = reporterId;
    }

    public LongFilter getAssignedToId() {
        return assignedToId;
    }

    public Optional<LongFilter> optionalAssignedToId() {
        return Optional.ofNullable(assignedToId);
    }

    public LongFilter assignedToId() {
        if (assignedToId == null) {
            setAssignedToId(new LongFilter());
        }
        return assignedToId;
    }

    public void setAssignedToId(LongFilter assignedToId) {
        this.assignedToId = assignedToId;
    }

    public LongFilter getResolvedById() {
        return resolvedById;
    }

    public Optional<LongFilter> optionalResolvedById() {
        return Optional.ofNullable(resolvedById);
    }

    public LongFilter resolvedById() {
        if (resolvedById == null) {
            setResolvedById(new LongFilter());
        }
        return resolvedById;
    }

    public void setResolvedById(LongFilter resolvedById) {
        this.resolvedById = resolvedById;
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
        final ContentReportCriteria that = (ContentReportCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(targetType, that.targetType) &&
            Objects.equals(targetId, that.targetId) &&
            Objects.equals(status, that.status) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(resolvedAt, that.resolvedAt) &&
            Objects.equals(targetVersion, that.targetVersion) &&
            Objects.equals(reporterId, that.reporterId) &&
            Objects.equals(assignedToId, that.assignedToId) &&
            Objects.equals(resolvedById, that.resolvedById) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            targetType,
            targetId,
            status,
            createdAt,
            resolvedAt,
            targetVersion,
            reporterId,
            assignedToId,
            resolvedById,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ContentReportCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalTargetType().map(f -> "targetType=" + f + ", ").orElse("") +
            optionalTargetId().map(f -> "targetId=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalResolvedAt().map(f -> "resolvedAt=" + f + ", ").orElse("") +
            optionalTargetVersion().map(f -> "targetVersion=" + f + ", ").orElse("") +
            optionalReporterId().map(f -> "reporterId=" + f + ", ").orElse("") +
            optionalAssignedToId().map(f -> "assignedToId=" + f + ", ").orElse("") +
            optionalResolvedById().map(f -> "resolvedById=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
