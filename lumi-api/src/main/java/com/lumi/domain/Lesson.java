package com.lumi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lumi.domain.enumeration.RecordStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * Lưu bài học, video và thứ tự bài trong chương.
 */
@Entity
@Table(name = "lessons")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Lesson implements Serializable {

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

    @NotNull
    @Size(max = 20)
    @Column(name = "content_type", length = 20, nullable = false)
    private String contentType;

    @Size(max = 1000)
    @Column(name = "video_url", length = 1000)
    private String videoUrl;

    @Size(max = 255)
    @Column(name = "video_file_name", length = 255)
    private String videoFileName;

    @Column(name = "video_size_bytes")
    private Long videoSizeBytes;

    @NotNull
    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @Column(name = "is_preview")
    private Boolean isPreview;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RecordStatus status;

    @Min(value = 0)
    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Size(max = 1000)
    @Column(name = "video_object_key", length = 1000)
    private String videoObjectKey;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "course" }, allowSetters = true)
    private Chapter chapter;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Lesson id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Lesson title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public Lesson description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContentType() {
        return this.contentType;
    }

    public Lesson contentType(String contentType) {
        this.setContentType(contentType);
        return this;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getVideoUrl() {
        return this.videoUrl;
    }

    public Lesson videoUrl(String videoUrl) {
        this.setVideoUrl(videoUrl);
        return this;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoFileName() {
        return this.videoFileName;
    }

    public Lesson videoFileName(String videoFileName) {
        this.setVideoFileName(videoFileName);
        return this;
    }

    public void setVideoFileName(String videoFileName) {
        this.videoFileName = videoFileName;
    }

    public Long getVideoSizeBytes() {
        return this.videoSizeBytes;
    }

    public Lesson videoSizeBytes(Long videoSizeBytes) {
        this.setVideoSizeBytes(videoSizeBytes);
        return this;
    }

    public void setVideoSizeBytes(Long videoSizeBytes) {
        this.videoSizeBytes = videoSizeBytes;
    }

    public Integer getOrderIndex() {
        return this.orderIndex;
    }

    public Lesson orderIndex(Integer orderIndex) {
        this.setOrderIndex(orderIndex);
        return this;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public Boolean getIsPreview() {
        return this.isPreview;
    }

    public Lesson isPreview(Boolean isPreview) {
        this.setIsPreview(isPreview);
        return this;
    }

    public void setIsPreview(Boolean isPreview) {
        this.isPreview = isPreview;
    }

    public Integer getDurationMinutes() {
        return this.durationMinutes;
    }

    public Lesson durationMinutes(Integer durationMinutes) {
        this.setDurationMinutes(durationMinutes);
        return this;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public RecordStatus getStatus() {
        return this.status;
    }

    public Lesson status(RecordStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(RecordStatus status) {
        this.status = status;
    }

    public Integer getDurationSeconds() {
        return this.durationSeconds;
    }

    public Lesson durationSeconds(Integer durationSeconds) {
        this.setDurationSeconds(durationSeconds);
        return this;
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public String getVideoObjectKey() {
        return this.videoObjectKey;
    }

    public Lesson videoObjectKey(String videoObjectKey) {
        this.setVideoObjectKey(videoObjectKey);
        return this;
    }

    public void setVideoObjectKey(String videoObjectKey) {
        this.videoObjectKey = videoObjectKey;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public Lesson updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Chapter getChapter() {
        return this.chapter;
    }

    public void setChapter(Chapter chapter) {
        this.chapter = chapter;
    }

    public Lesson chapter(Chapter chapter) {
        this.setChapter(chapter);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Lesson)) {
            return false;
        }
        return getId() != null && getId().equals(((Lesson) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Lesson{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", contentType='" + getContentType() + "'" +
            ", videoUrl='" + getVideoUrl() + "'" +
            ", videoFileName='" + getVideoFileName() + "'" +
            ", videoSizeBytes=" + getVideoSizeBytes() +
            ", orderIndex=" + getOrderIndex() +
            ", isPreview='" + getIsPreview() + "'" +
            ", durationMinutes=" + getDurationMinutes() +
            ", status='" + getStatus() + "'" +
            ", durationSeconds=" + getDurationSeconds() +
            ", videoObjectKey='" + getVideoObjectKey() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
