package com.lumi.service.criteria;

import com.lumi.domain.enumeration.RecordStatus;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.domain.Lesson} entity. This class is used
 * in {@link com.lumi.web.rest.LessonResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /lessons?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LessonCriteria implements Serializable, Criteria {

    /**
     * Class for filtering RecordStatus
     */
    public static class RecordStatusFilter extends Filter<RecordStatus> {

        public RecordStatusFilter() {}

        public RecordStatusFilter(RecordStatusFilter filter) {
            super(filter);
        }

        @Override
        public RecordStatusFilter copy() {
            return new RecordStatusFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter title;

    private StringFilter contentType;

    private IntegerFilter orderIndex;

    private BooleanFilter isPreview;

    private IntegerFilter durationMinutes;

    private RecordStatusFilter status;

    private LongFilter chapterId;

    private Boolean distinct;

    public LessonCriteria() {}

    public LessonCriteria(LessonCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.title = other.optionalTitle().map(StringFilter::copy).orElse(null);
        this.contentType = other.optionalContentType().map(StringFilter::copy).orElse(null);
        this.orderIndex = other.optionalOrderIndex().map(IntegerFilter::copy).orElse(null);
        this.isPreview = other.optionalIsPreview().map(BooleanFilter::copy).orElse(null);
        this.durationMinutes = other.optionalDurationMinutes().map(IntegerFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(RecordStatusFilter::copy).orElse(null);
        this.chapterId = other.optionalChapterId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public LessonCriteria copy() {
        return new LessonCriteria(this);
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

    public StringFilter getTitle() {
        return title;
    }

    public Optional<StringFilter> optionalTitle() {
        return Optional.ofNullable(title);
    }

    public StringFilter title() {
        if (title == null) {
            setTitle(new StringFilter());
        }
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
    }

    public StringFilter getContentType() {
        return contentType;
    }

    public Optional<StringFilter> optionalContentType() {
        return Optional.ofNullable(contentType);
    }

    public StringFilter contentType() {
        if (contentType == null) {
            setContentType(new StringFilter());
        }
        return contentType;
    }

    public void setContentType(StringFilter contentType) {
        this.contentType = contentType;
    }

    public IntegerFilter getOrderIndex() {
        return orderIndex;
    }

    public Optional<IntegerFilter> optionalOrderIndex() {
        return Optional.ofNullable(orderIndex);
    }

    public IntegerFilter orderIndex() {
        if (orderIndex == null) {
            setOrderIndex(new IntegerFilter());
        }
        return orderIndex;
    }

    public void setOrderIndex(IntegerFilter orderIndex) {
        this.orderIndex = orderIndex;
    }

    public BooleanFilter getIsPreview() {
        return isPreview;
    }

    public Optional<BooleanFilter> optionalIsPreview() {
        return Optional.ofNullable(isPreview);
    }

    public BooleanFilter isPreview() {
        if (isPreview == null) {
            setIsPreview(new BooleanFilter());
        }
        return isPreview;
    }

    public void setIsPreview(BooleanFilter isPreview) {
        this.isPreview = isPreview;
    }

    public IntegerFilter getDurationMinutes() {
        return durationMinutes;
    }

    public Optional<IntegerFilter> optionalDurationMinutes() {
        return Optional.ofNullable(durationMinutes);
    }

    public IntegerFilter durationMinutes() {
        if (durationMinutes == null) {
            setDurationMinutes(new IntegerFilter());
        }
        return durationMinutes;
    }

    public void setDurationMinutes(IntegerFilter durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public RecordStatusFilter getStatus() {
        return status;
    }

    public Optional<RecordStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public RecordStatusFilter status() {
        if (status == null) {
            setStatus(new RecordStatusFilter());
        }
        return status;
    }

    public void setStatus(RecordStatusFilter status) {
        this.status = status;
    }

    public LongFilter getChapterId() {
        return chapterId;
    }

    public Optional<LongFilter> optionalChapterId() {
        return Optional.ofNullable(chapterId);
    }

    public LongFilter chapterId() {
        if (chapterId == null) {
            setChapterId(new LongFilter());
        }
        return chapterId;
    }

    public void setChapterId(LongFilter chapterId) {
        this.chapterId = chapterId;
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
        final LessonCriteria that = (LessonCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(title, that.title) &&
            Objects.equals(contentType, that.contentType) &&
            Objects.equals(orderIndex, that.orderIndex) &&
            Objects.equals(isPreview, that.isPreview) &&
            Objects.equals(durationMinutes, that.durationMinutes) &&
            Objects.equals(status, that.status) &&
            Objects.equals(chapterId, that.chapterId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, contentType, orderIndex, isPreview, durationMinutes, status, chapterId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LessonCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalTitle().map(f -> "title=" + f + ", ").orElse("") +
            optionalContentType().map(f -> "contentType=" + f + ", ").orElse("") +
            optionalOrderIndex().map(f -> "orderIndex=" + f + ", ").orElse("") +
            optionalIsPreview().map(f -> "isPreview=" + f + ", ").orElse("") +
            optionalDurationMinutes().map(f -> "durationMinutes=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalChapterId().map(f -> "chapterId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
