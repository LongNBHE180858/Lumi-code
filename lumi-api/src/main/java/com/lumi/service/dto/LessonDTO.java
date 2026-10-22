package com.lumi.service.dto;

import com.lumi.domain.enumeration.RecordStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.Lesson} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LessonDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String title;

    @NotNull
    @Size(max = 20)
    private String contentType;

    @NotNull
    @Min(value = 0)
    private Integer orderIndex;

    private Boolean isPreview;

    @Min(value = 0)
    private Integer durationMinutes;

    @Min(0)
    private Integer durationSeconds;

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    private RecordStatus status;

    @NotNull
    private ChapterDTO chapter;

    private String description;

    @Size(max = 1000)
    private String videoUrl;

    @Size(max = 255)
    private String videoFileName;

    @Min(0)
    private Long videoSizeBytes;

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

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
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

    public ChapterDTO getChapter() {
        return chapter;
    }

    public void setChapter(ChapterDTO chapter) {
        this.chapter = chapter;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoUrl() {
        return this.videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoFileName() {
        return this.videoFileName;
    }

    public void setVideoFileName(String videoFileName) {
        this.videoFileName = videoFileName;
    }

    public Long getVideoSizeBytes() {
        return this.videoSizeBytes;
    }

    public void setVideoSizeBytes(Long videoSizeBytes) {
        this.videoSizeBytes = videoSizeBytes;
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
            ", contentType='" + getContentType() + "'" +
            ", orderIndex=" + getOrderIndex() +
            ", isPreview='" + getIsPreview() + "'" +
            ", durationMinutes=" + getDurationMinutes() +
            ", status='" + getStatus() + "'" +
            ", chapter=" + getChapter() +
            "}";
    }
}
