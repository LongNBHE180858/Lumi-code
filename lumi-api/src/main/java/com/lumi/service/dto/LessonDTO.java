package com.lumi.service.dto;

import com.lumi.domain.enumeration.RecordStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.Lesson} entity.
 */
@Schema(description = "Lưu bài học, video và thứ tự bài trong chương.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LessonDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String title;

    @Lob
    private String description;

    @NotNull
    @Size(max = 20)
    private String contentType;

    @Size(max = 1000)
    private String videoUrl;

    @Size(max = 255)
    private String videoFileName;

    private Long videoSizeBytes;

    @NotNull
    private Integer orderIndex;

    private Boolean isPreview;

    private Integer durationMinutes;

    private RecordStatus status;

    @Min(value = 0)
    private Integer durationSeconds;

    @Size(max = 1000)
    private String videoObjectKey;

    @NotNull
    private Instant updatedAt;

    @NotNull
    private ChapterDTO chapter;

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

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoFileName() {
        return videoFileName;
    }

    public void setVideoFileName(String videoFileName) {
        this.videoFileName = videoFileName;
    }

    public Long getVideoSizeBytes() {
        return videoSizeBytes;
    }

    public void setVideoSizeBytes(Long videoSizeBytes) {
        this.videoSizeBytes = videoSizeBytes;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public Boolean getIsPreview() {
        return isPreview;
    }

    public void setIsPreview(Boolean isPreview) {
        this.isPreview = isPreview;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public RecordStatus getStatus() {
        return status;
    }

    public void setStatus(RecordStatus status) {
        this.status = status;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public String getVideoObjectKey() {
        return videoObjectKey;
    }

    public void setVideoObjectKey(String videoObjectKey) {
        this.videoObjectKey = videoObjectKey;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ChapterDTO getChapter() {
        return chapter;
    }

    public void setChapter(ChapterDTO chapter) {
        this.chapter = chapter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LessonDTO)) {
            return false;
        }

        LessonDTO lessonDTO = (LessonDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, lessonDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LessonDTO{" +
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
            ", chapter=" + getChapter() +
            "}";
    }
}
