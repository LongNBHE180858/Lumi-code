package com.lumi.service.dto;

import com.lumi.domain.enumeration.RecordStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.Course} entity.
 */
@Schema(description = "Lưu thông tin khóa học, giá, thời hạn truy cập và trạng thái xuất bản.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CourseDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String title;

    @Lob
    private String description;

    @Size(max = 1000)
    private String thumbnailUrl;

    private BigDecimal price;

    @Size(max = 1000)
    private String shortDescription;

    @Size(max = 100)
    private String bookSeries;

    @Size(max = 1000)
    private String targetAudience;

    @Size(max = 1000)
    private String prerequisites;

    private List<String> learningObjectives;

    @Size(max = 1000)
    private String introVideoUrl;

    @Min(value = 1)
    private Integer validDurationDays;

    private Integer accessDurationValue;

    @Size(max = 10)
    private String accessDurationUnit;

    @NotNull
    private Long version;

    private RecordStatus status;

    private Boolean isDeleted;

    private Instant createdAt;

    private Instant updatedAt;

    @Min(value = 0L)
    private Long storageLimitBytes;

    @NotNull
    private UserDTO author;

    private SubjectDTO subject;

    private GradeDTO grade;

    private String authorDisplayName;

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

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getBookSeries() {
        return bookSeries;
    }

    public void setBookSeries(String bookSeries) {
        this.bookSeries = bookSeries;
    }

    public String getTargetAudience() {
        return targetAudience;
    }

    public void setTargetAudience(String targetAudience) {
        this.targetAudience = targetAudience;
    }

    public String getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(String prerequisites) {
        this.prerequisites = prerequisites;
    }

    public List<String> getLearningObjectives() {
        return learningObjectives;
    }

    public void setLearningObjectives(List<String> learningObjectives) {
        this.learningObjectives = learningObjectives;
    }

    public String getIntroVideoUrl() {
        return introVideoUrl;
    }

    public void setIntroVideoUrl(String introVideoUrl) {
        this.introVideoUrl = introVideoUrl;
    }

    public Integer getValidDurationDays() {
        return validDurationDays;
    }

    public void setValidDurationDays(Integer validDurationDays) {
        this.validDurationDays = validDurationDays;
    }

    public Integer getAccessDurationValue() {
        return accessDurationValue;
    }

    public void setAccessDurationValue(Integer accessDurationValue) {
        this.accessDurationValue = accessDurationValue;
    }

    public String getAccessDurationUnit() {
        return accessDurationUnit;
    }

    public void setAccessDurationUnit(String accessDurationUnit) {
        this.accessDurationUnit = accessDurationUnit;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
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

    public Long getStorageLimitBytes() {
        return storageLimitBytes;
    }

    public void setStorageLimitBytes(Long storageLimitBytes) {
        this.storageLimitBytes = storageLimitBytes;
    }

    public UserDTO getAuthor() {
        return author;
    }

    public void setAuthor(UserDTO author) {
        this.author = author;
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

    public String getAuthorDisplayName() {
        return authorDisplayName;
    }

    public void setAuthorDisplayName(String authorDisplayName) {
        this.authorDisplayName = authorDisplayName;
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
            ", shortDescription='" + getShortDescription() + "'" +
            ", bookSeries='" + getBookSeries() + "'" +
            ", targetAudience='" + getTargetAudience() + "'" +
            ", prerequisites='" + getPrerequisites() + "'" +
            ", learningObjectives='" + getLearningObjectives() + "'" +
            ", introVideoUrl='" + getIntroVideoUrl() + "'" +
            ", validDurationDays=" + getValidDurationDays() +
            ", accessDurationValue=" + getAccessDurationValue() +
            ", accessDurationUnit='" + getAccessDurationUnit() + "'" +
            ", version=" + getVersion() +
            ", status='" + getStatus() + "'" +
            ", isDeleted='" + getIsDeleted() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", storageLimitBytes=" + getStorageLimitBytes() +
            ", author=" + getAuthor() +
            ", subject=" + getSubject() +
            ", grade=" + getGrade() +
            "}";
    }
}
