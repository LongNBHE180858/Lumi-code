package com.lumi.service.dto;

import com.lumi.domain.enumeration.RecordStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.Course} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CourseDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String title;

    @Size(max = 5000)
    private String description;

    @Size(max = 1000)
    private String thumbnailUrl;

    @DecimalMin(value = "0")
    private BigDecimal price;

    @Min(value = 1)
    private Integer validDurationDays;

    private RecordStatus status;

    private Boolean isDeleted;

    private Instant createdAt;

    private Instant updatedAt;

    @NotNull
    private UserDTO author;

    private String authorDisplayName;

    public String getAuthorDisplayName() {
        return authorDisplayName;
    }

    public void setAuthorDisplayName(String authorDisplayName) {
        this.authorDisplayName = authorDisplayName;
    }

    private CategoryDTO category;

    private SubjectDTO subject;

    private GradeDTO grade;

    @Size(max = 1000)
    private String shortDescription;

    @Size(max = 100)
    private String bookSeries;

    @Size(max = 1000)
    private String targetAudience;

    @Size(max = 1000)
    private String prerequisites;

    private java.util.List<String> learningObjectives;

    @Size(max = 1000)
    private String introVideoUrl;

    @Min(1)
    private Integer accessDurationValue;

    private com.lumi.domain.enumeration.AccessDurationUnit accessDurationUnit;

    @Min(0)
    private Long version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getValidDurationDays() {
        return validDurationDays;
    }

    public void setValidDurationDays(Integer validDurationDays) {
        this.validDurationDays = validDurationDays;
    }

    public RecordStatus getStatus() {
        return status;
    }

    public void setStatus(RecordStatus status) {
        this.status = status;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UserDTO getAuthor() {
        return author;
    }

    public void setAuthor(UserDTO author) {
        this.author = author;
    }

    public CategoryDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryDTO category) {
        this.category = category;
    }

    public SubjectDTO getSubject() {
        return subject;
    }

    public void setSubject(SubjectDTO subject) {
        this.subject = subject;
    }

    public GradeDTO getGrade() {
        return grade;
    }

    public void setGrade(GradeDTO grade) {
        this.grade = grade;
    }

    public String getShortDescription() {
        return this.shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getBookSeries() {
        return this.bookSeries;
    }

    public void setBookSeries(String bookSeries) {
        this.bookSeries = bookSeries;
    }

    public String getTargetAudience() {
        return this.targetAudience;
    }

    public void setTargetAudience(String targetAudience) {
        this.targetAudience = targetAudience;
    }

    public String getPrerequisites() {
        return this.prerequisites;
    }

    public void setPrerequisites(String prerequisites) {
        this.prerequisites = prerequisites;
    }

    public java.util.List<String> getLearningObjectives() {
        return this.learningObjectives;
    }

    public void setLearningObjectives(java.util.List<String> learningObjectives) {
        this.learningObjectives = learningObjectives;
    }

    public String getIntroVideoUrl() {
        return this.introVideoUrl;
    }

    public void setIntroVideoUrl(String introVideoUrl) {
        this.introVideoUrl = introVideoUrl;
    }

    public Integer getAccessDurationValue() {
        return this.accessDurationValue;
    }

    public void setAccessDurationValue(Integer accessDurationValue) {
        this.accessDurationValue = accessDurationValue;
    }

    public com.lumi.domain.enumeration.AccessDurationUnit getAccessDurationUnit() {
        return this.accessDurationUnit;
    }

    public void setAccessDurationUnit(com.lumi.domain.enumeration.AccessDurationUnit accessDurationUnit) {
        this.accessDurationUnit = accessDurationUnit;
    }

    public Long getVersion() {
        return this.version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CourseDTO)) {
            return false;
        }

        CourseDTO courseDTO = (CourseDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, courseDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CourseDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", thumbnailUrl='" + getThumbnailUrl() + "'" +
            ", price=" + getPrice() +
            ", validDurationDays=" + getValidDurationDays() +
            ", status='" + getStatus() + "'" +
            ", isDeleted='" + getIsDeleted() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", author=" + getAuthor() +
            ", category=" + getCategory() +
            ", subject=" + getSubject() +
            ", grade=" + getGrade() +
            "}";
    }
}
