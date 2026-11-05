package com.lumi.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.CourseMaterial} entity.
 */
@Schema(description = "Lưu tài nguyên và đường dẫn tài liệu đính kèm bài học.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CourseMaterialDTO implements Serializable {

    private Long id;

    @Size(max = 255)
    private String fileName;

    @Size(max = 255)
    private String displayName;

    @Size(max = 100)
    private String mimeType;

    private Integer orderIndex;

    @NotNull
    @Size(max = 20)
    private String fileType;

    @NotNull
    @Size(max = 1000)
    private String fileUrl;

    private Long fileSizeBytes;

    @Size(max = 1000)
    private String fileObjectKey;

    @NotNull
    private Instant updatedAt;

    @NotNull
    private LessonDTO lesson;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Long getFileSizeBytes() {
        return fileSizeBytes;
    }

    public void setFileSizeBytes(Long fileSizeBytes) {
        this.fileSizeBytes = fileSizeBytes;
    }

    public String getFileObjectKey() {
        return fileObjectKey;
    }

    public void setFileObjectKey(String fileObjectKey) {
        this.fileObjectKey = fileObjectKey;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LessonDTO getLesson() {
        return lesson;
    }

    public void setLesson(LessonDTO lesson) {
        this.lesson = lesson;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CourseMaterialDTO)) {
            return false;
        }

        CourseMaterialDTO courseMaterialDTO = (CourseMaterialDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, courseMaterialDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CourseMaterialDTO{" +
            "id=" + getId() +
            ", fileName='" + getFileName() + "'" +
            ", displayName='" + getDisplayName() + "'" +
            ", mimeType='" + getMimeType() + "'" +
            ", orderIndex=" + getOrderIndex() +
            ", fileType='" + getFileType() + "'" +
            ", fileUrl='" + getFileUrl() + "'" +
            ", fileSizeBytes=" + getFileSizeBytes() +
            ", fileObjectKey='" + getFileObjectKey() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", lesson=" + getLesson() +
            "}";
    }
}
