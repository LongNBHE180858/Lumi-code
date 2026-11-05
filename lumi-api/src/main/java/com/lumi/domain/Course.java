package com.lumi.domain;

import com.lumi.domain.enumeration.AccessDurationUnit;
import com.lumi.domain.enumeration.RecordStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Lưu thông tin khóa học, giá, thời hạn truy cập và trạng thái xuất bản.
 */
@Entity
@Table(name = "courses")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Course implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 255)
    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Size(max = 1000)
    @Column(name = "thumbnail_url", length = 1000)
    private String thumbnailUrl;

    @Column(name = "price", precision = 21, scale = 2)
    private BigDecimal price;

    @Size(max = 1000)
    @Column(name = "short_description", length = 1000)
    private String shortDescription;

    @Size(max = 100)
    @Column(name = "book_series", length = 100)
    private String bookSeries;

    @Size(max = 1000)
    @Column(name = "target_audience", length = 1000)
    private String targetAudience;

    @Size(max = 1000)
    @Column(name = "prerequisites", length = 1000)
    private String prerequisites;

    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    @Column(name = "learning_objectives", columnDefinition = "jsonb")
    private List<String> learningObjectives;

    @Size(max = 1000)
    @Column(name = "intro_video_url", length = 1000)
    private String introVideoUrl;

    @Column(name = "valid_duration_days")
    private Integer validDurationDays;

    @Column(name = "access_duration_value")
    private Integer accessDurationValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_duration_unit", length = 10)
    private AccessDurationUnit accessDurationUnit;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RecordStatus status;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Min(value = 0L)
    @Column(name = "storage_limit_bytes")
    private Long storageLimitBytes;

    @ManyToOne(optional = false)
    @NotNull
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    private Grade grade;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Course id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Course title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public Course description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnailUrl() {
        return this.thumbnailUrl;
    }

    public Course thumbnailUrl(String thumbnailUrl) {
        this.setThumbnailUrl(thumbnailUrl);
        return this;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public Course price(BigDecimal price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getShortDescription() {
        return this.shortDescription;
    }

    public Course shortDescription(String shortDescription) {
        this.setShortDescription(shortDescription);
        return this;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getBookSeries() {
        return this.bookSeries;
    }

    public Course bookSeries(String bookSeries) {
        this.setBookSeries(bookSeries);
        return this;
    }

    public void setBookSeries(String bookSeries) {
        this.bookSeries = bookSeries;
    }

    public String getTargetAudience() {
        return this.targetAudience;
    }

    public Course targetAudience(String targetAudience) {
        this.setTargetAudience(targetAudience);
        return this;
    }

    public void setTargetAudience(String targetAudience) {
        this.targetAudience = targetAudience;
    }

    public String getPrerequisites() {
        return this.prerequisites;
    }

    public Course prerequisites(String prerequisites) {
        this.setPrerequisites(prerequisites);
        return this;
    }

    public void setPrerequisites(String prerequisites) {
        this.prerequisites = prerequisites;
    }

    public List<String> getLearningObjectives() {
        return this.learningObjectives;
    }

    public Course learningObjectives(List<String> learningObjectives) {
        this.setLearningObjectives(learningObjectives);
        return this;
    }

    public void setLearningObjectives(List<String> learningObjectives) {
        this.learningObjectives = learningObjectives;
    }

    public String getIntroVideoUrl() {
        return this.introVideoUrl;
    }

    public Course introVideoUrl(String introVideoUrl) {
        this.setIntroVideoUrl(introVideoUrl);
        return this;
    }

    public void setIntroVideoUrl(String introVideoUrl) {
        this.introVideoUrl = introVideoUrl;
    }

    public Integer getValidDurationDays() {
        return this.validDurationDays;
    }

    public Course validDurationDays(Integer validDurationDays) {
        this.setValidDurationDays(validDurationDays);
        return this;
    }

    public void setValidDurationDays(Integer validDurationDays) {
        this.validDurationDays = validDurationDays;
    }

    public Integer getAccessDurationValue() {
        return this.accessDurationValue;
    }

    public Course accessDurationValue(Integer accessDurationValue) {
        this.setAccessDurationValue(accessDurationValue);
        return this;
    }

    public void setAccessDurationValue(Integer accessDurationValue) {
        this.accessDurationValue = accessDurationValue;
    }

    public AccessDurationUnit getAccessDurationUnit() {
        return this.accessDurationUnit;
    }

    public Course accessDurationUnit(AccessDurationUnit accessDurationUnit) {
        this.setAccessDurationUnit(accessDurationUnit);
        return this;
    }

    public void setAccessDurationUnit(AccessDurationUnit accessDurationUnit) {
        this.accessDurationUnit = accessDurationUnit;
    }

    public Long getVersion() {
        return this.version;
    }

    public Course version(Long version) {
        this.setVersion(version);
        return this;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public RecordStatus getStatus() {
        return this.status;
    }

    public Course status(RecordStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(RecordStatus status) {
        this.status = status;
    }

    public Boolean getIsDeleted() {
        return this.isDeleted;
    }

    public Course isDeleted(Boolean isDeleted) {
        this.setIsDeleted(isDeleted);
        return this;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Course createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public Course updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getStorageLimitBytes() {
        return this.storageLimitBytes;
    }

    public Course storageLimitBytes(Long storageLimitBytes) {
        this.setStorageLimitBytes(storageLimitBytes);
        return this;
    }

    public void setStorageLimitBytes(Long storageLimitBytes) {
        this.storageLimitBytes = storageLimitBytes;
    }

    public User getAuthor() {
        return this.author;
    }

    public void setAuthor(User user) {
        this.author = user;
    }

    public Course author(User user) {
        this.setAuthor(user);
        return this;
    }

    public Subject getSubject() {
        return this.subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Course subject(Subject subject) {
        this.setSubject(subject);
        return this;
    }

    public Grade getGrade() {
        return this.grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    public Course grade(Grade grade) {
        this.setGrade(grade);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Course)) {
            return false;
        }
        return getId() != null && getId().equals(((Course) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Course{" +
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
            "}";
    }
}
