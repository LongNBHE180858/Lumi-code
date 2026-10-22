package com.lumi.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.domain.AssignmentStudent} entity. This class is used
 * in {@link com.lumi.web.rest.AssignmentStudentResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /assignment-students?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AssignmentStudentCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter studentId;

    private LongFilter assignmentId;

    private Boolean distinct;

    public AssignmentStudentCriteria() {}

    public AssignmentStudentCriteria(AssignmentStudentCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.studentId = other.optionalStudentId().map(LongFilter::copy).orElse(null);
        this.assignmentId = other.optionalAssignmentId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public AssignmentStudentCriteria copy() {
        return new AssignmentStudentCriteria(this);
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

    public LongFilter getAssignmentId() {
        return assignmentId;
    }

    public Optional<LongFilter> optionalAssignmentId() {
        return Optional.ofNullable(assignmentId);
    }

    public LongFilter assignmentId() {
        if (assignmentId == null) {
            setAssignmentId(new LongFilter());
        }
        return assignmentId;
    }

    public void setAssignmentId(LongFilter assignmentId) {
        this.assignmentId = assignmentId;
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
        final AssignmentStudentCriteria that = (AssignmentStudentCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(studentId, that.studentId) &&
            Objects.equals(assignmentId, that.assignmentId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, studentId, assignmentId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AssignmentStudentCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalStudentId().map(f -> "studentId=" + f + ", ").orElse("") +
            optionalAssignmentId().map(f -> "assignmentId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
