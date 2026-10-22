package com.lumi.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.domain.ParentStudentLink} entity. This class is used
 * in {@link com.lumi.web.rest.ParentStudentLinkResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /parent-student-links?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ParentStudentLinkCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter linkCode;

    private StringFilter status;

    private InstantFilter linkedAt;

    private LongFilter parentId;

    private LongFilter studentId;

    private Boolean distinct;

    public ParentStudentLinkCriteria() {}

    public ParentStudentLinkCriteria(ParentStudentLinkCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.linkCode = other.optionalLinkCode().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(StringFilter::copy).orElse(null);
        this.linkedAt = other.optionalLinkedAt().map(InstantFilter::copy).orElse(null);
        this.parentId = other.optionalParentId().map(LongFilter::copy).orElse(null);
        this.studentId = other.optionalStudentId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ParentStudentLinkCriteria copy() {
        return new ParentStudentLinkCriteria(this);
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

    public StringFilter getLinkCode() {
        return linkCode;
    }

    public Optional<StringFilter> optionalLinkCode() {
        return Optional.ofNullable(linkCode);
    }

    public StringFilter linkCode() {
        if (linkCode == null) {
            setLinkCode(new StringFilter());
        }
        return linkCode;
    }

    public void setLinkCode(StringFilter linkCode) {
        this.linkCode = linkCode;
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

    public InstantFilter getLinkedAt() {
        return linkedAt;
    }

    public Optional<InstantFilter> optionalLinkedAt() {
        return Optional.ofNullable(linkedAt);
    }

    public InstantFilter linkedAt() {
        if (linkedAt == null) {
            setLinkedAt(new InstantFilter());
        }
        return linkedAt;
    }

    public void setLinkedAt(InstantFilter linkedAt) {
        this.linkedAt = linkedAt;
    }

    public LongFilter getParentId() {
        return parentId;
    }

    public Optional<LongFilter> optionalParentId() {
        return Optional.ofNullable(parentId);
    }

    public LongFilter parentId() {
        if (parentId == null) {
            setParentId(new LongFilter());
        }
        return parentId;
    }

    public void setParentId(LongFilter parentId) {
        this.parentId = parentId;
    }

    public LongFilter getStudentId() {
        return studentId;
    }

    public Optional<LongFilter> optionalStudentId() {
        return Optional.ofNullable(studentId);
    }

    public LongFilter studentId() {
        if (studentId == null) {
            setStudentId(new LongFilter());
        }
        return studentId;
    }

    public void setStudentId(LongFilter studentId) {
        this.studentId = studentId;
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
        final ParentStudentLinkCriteria that = (ParentStudentLinkCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(linkCode, that.linkCode) &&
            Objects.equals(status, that.status) &&
            Objects.equals(linkedAt, that.linkedAt) &&
            Objects.equals(parentId, that.parentId) &&
            Objects.equals(studentId, that.studentId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, linkCode, status, linkedAt, parentId, studentId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ParentStudentLinkCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalLinkCode().map(f -> "linkCode=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalLinkedAt().map(f -> "linkedAt=" + f + ", ").orElse("") +
            optionalParentId().map(f -> "parentId=" + f + ", ").orElse("") +
            optionalStudentId().map(f -> "studentId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
