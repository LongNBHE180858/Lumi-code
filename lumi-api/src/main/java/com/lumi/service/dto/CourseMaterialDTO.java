package com.lumi.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.CourseMaterial} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CourseMaterialDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 20)
    private String fileType;

    @NotNull
    @Size(max = 1000)
    private String fileUrl;

    @Min(value = 0L)
    private Long fileSizeBytes;

    @NotNull
    private LessonDTO lesson;

    @Size(max = 255)
    private String fileName;

    @Size(max = 255)
    private String displayName;

    @Size(max = 100)
    private String mimeType;

    @Min(0)
    private Integer orderIndex;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LessonDTO getLesson() {
        return lesson;
    }

    public void setLesson(LessonDTO lesson) {
        this.lesson = lesson;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Integer getOrderIndex() {
        return this.orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
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
            ", fileType='" + getFileType() + "'" +
            ", fileUrl='" + getFileUrl() + "'" +
            ", fileSizeBytes=" + getFileSizeBytes() +
            ", lesson=" + getLesson() +
            "}";
    }
}
