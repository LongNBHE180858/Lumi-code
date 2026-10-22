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
 * Criteria class for the {@link com.lumi.domain.Course} entity. This class is used
 * in {@link com.lumi.web.rest.CourseResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /courses?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CourseCriteria implements Serializable, Criteria {

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

    private StringFilter description;

    private StringFilter thumbnailUrl;

    private BigDecimalFilter price;

    private IntegerFilter validDurationDays;

    private RecordStatusFilter status;

    private BooleanFilter isDeleted;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    private LongFilter authorId;

    private LongFilter categoryId;

    private LongFilter subjectId;

    private LongFilter gradeId;

    private Boolean distinct;

    public CourseCriteria() {}

    public CourseCriteria(CourseCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.title = other.optionalTitle().map(StringFilter::copy).orElse(null);
        this.description = other.optionalDescription().map(StringFilter::copy).orElse(null);
        this.thumbnailUrl = other.optionalThumbnailUrl().map(StringFilter::copy).orElse(null);
        this.price = other.optionalPrice().map(BigDecimalFilter::copy).orElse(null);
        this.validDurationDays = other.optionalValidDurationDays().map(IntegerFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(RecordStatusFilter::copy).orElse(null);
        this.isDeleted = other.optionalIsDeleted().map(BooleanFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.authorId = other.optionalAuthorId().map(LongFilter::copy).orElse(null);
        this.categoryId = other.optionalCategoryId().map(LongFilter::copy).orElse(null);
        this.subjectId = other.optionalSubjectId().map(LongFilter::copy).orElse(null);
        this.gradeId = other.optionalGradeId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CourseCriteria copy() {
        return new CourseCriteria(this);
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

    public StringFilter getDescription() {
        return description;
    }

    public Optional<StringFilter> optionalDescription() {
        return Optional.ofNullable(description);
    }

    public StringFilter description() {
        if (description == null) {
            setDescription(new StringFilter());
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public StringFilter getThumbnailUrl() {
        return thumbnailUrl;
    }

    public Optional<StringFilter> optionalThumbnailUrl() {
        return Optional.ofNullable(thumbnailUrl);
    }

    public StringFilter thumbnailUrl() {
        if (thumbnailUrl == null) {
            setThumbnailUrl(new StringFilter());
        }
        return thumbnailUrl;
    }

    public void setThumbnailUrl(StringFilter thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public BigDecimalFilter getPrice() {
        return price;
    }

    public Optional<BigDecimalFilter> optionalPrice() {
        return Optional.ofNullable(price);
    }

    public BigDecimalFilter price() {
        if (price == null) {
            setPrice(new BigDecimalFilter());
        }
        return price;
    }

    public void setPrice(BigDecimalFilter price) {
        this.price = price;
    }

    public IntegerFilter getValidDurationDays() {
        return validDurationDays;
    }

    public Optional<IntegerFilter> optionalValidDurationDays() {
        return Optional.ofNullable(validDurationDays);
    }

    public IntegerFilter validDurationDays() {
        if (validDurationDays == null) {
            setValidDurationDays(new IntegerFilter());
        }
        return validDurationDays;
    }

    public void setValidDurationDays(IntegerFilter validDurationDays) {
        this.validDurationDays = validDurationDays;
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

    public BooleanFilter getIsDeleted() {
        return isDeleted;
    }

    public Optional<BooleanFilter> optionalIsDeleted() {
        return Optional.ofNullable(isDeleted);
    }

    public BooleanFilter isDeleted() {
        if (isDeleted == null) {
            setIsDeleted(new BooleanFilter());
        }
        return isDeleted;
    }

    public void setIsDeleted(BooleanFilter isDeleted) {
        this.isDeleted = isDeleted;
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

    public InstantFilter getUpdatedAt() {
        return updatedAt;
    }

    public Optional<InstantFilter> optionalUpdatedAt() {
        return Optional.ofNullable(updatedAt);
    }

    public InstantFilter updatedAt() {
        if (updatedAt == null) {
            setUpdatedAt(new InstantFilter());
        }
        return updatedAt;
    }

    public void setUpdatedAt(InstantFilter updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LongFilter getAuthorId() {
        return authorId;
    }

    public Optional<LongFilter> optionalAuthorId() {
        return Optional.ofNullable(authorId);
    }

    public LongFilter authorId() {
        if (authorId == null) {
            setAuthorId(new LongFilter());
        }
        return authorId;
    }

    public void setAuthorId(LongFilter authorId) {
        this.authorId = authorId;
    }

    public LongFilter getCategoryId() {
        return categoryId;
    }

    public Optional<LongFilter> optionalCategoryId() {
        return Optional.ofNullable(categoryId);
    }

    public LongFilter categoryId() {
        if (categoryId == null) {
            setCategoryId(new LongFilter());
        }
        return categoryId;
    }

    public void setCategoryId(LongFilter categoryId) {
        this.categoryId = categoryId;
    }

    public LongFilter getSubjectId() {
        return subjectId;
    }

    public Optional<LongFilter> optionalSubjectId() {
        return Optional.ofNullable(subjectId);
    }

    public LongFilter subjectId() {
        if (subjectId == null) {
            setSubjectId(new LongFilter());
        }
        return subjectId;
    }

    public void setSubjectId(LongFilter subjectId) {
        this.subjectId = subjectId;
    }

    public LongFilter getGradeId() {
        return gradeId;
    }

    public Optional<LongFilter> optionalGradeId() {
        return Optional.ofNullable(gradeId);
    }

    public LongFilter gradeId() {
        if (gradeId == null) {
            setGradeId(new LongFilter());
        }
        return gradeId;
    }

    public void setGradeId(LongFilter gradeId) {
        this.gradeId = gradeId;
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
        final CourseCriteria that = (CourseCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(title, that.title) &&
            Objects.equals(description, that.description) &&
            Objects.equals(thumbnailUrl, that.thumbnailUrl) &&
            Objects.equals(price, that.price) &&
            Objects.equals(validDurationDays, that.validDurationDays) &&
            Objects.equals(status, that.status) &&
            Objects.equals(isDeleted, that.isDeleted) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(authorId, that.authorId) &&
            Objects.equals(categoryId, that.categoryId) &&
            Objects.equals(subjectId, that.subjectId) &&
            Objects.equals(gradeId, that.gradeId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            title,
            description,
            thumbnailUrl,
            price,
            validDurationDays,
            status,
            isDeleted,
            createdAt,
            updatedAt,
            authorId,
            categoryId,
            subjectId,
            gradeId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CourseCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalTitle().map(f -> "title=" + f + ", ").orElse("") +
            optionalDescription().map(f -> "description=" + f + ", ").orElse("") +
            optionalThumbnailUrl().map(f -> "thumbnailUrl=" + f + ", ").orElse("") +
            optionalPrice().map(f -> "price=" + f + ", ").orElse("") +
            optionalValidDurationDays().map(f -> "validDurationDays=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalIsDeleted().map(f -> "isDeleted=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalAuthorId().map(f -> "authorId=" + f + ", ").orElse("") +
            optionalCategoryId().map(f -> "categoryId=" + f + ", ").orElse("") +
            optionalSubjectId().map(f -> "subjectId=" + f + ", ").orElse("") +
            optionalGradeId().map(f -> "gradeId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
