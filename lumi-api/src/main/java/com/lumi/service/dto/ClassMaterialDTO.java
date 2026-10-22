package com.lumi.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.ClassMaterial} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ClassMaterialDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String title;

    @NotNull
    @Size(max = 20)
    private String fileType;

    @NotNull
    @Size(max = 1000)
    private String fileUrl;

    @NotNull
    @Min(value = 0L)
    private Long fileSizeBytes;

    private Instant uploadedAt;

    @NotNull
    private StudyClassDTO studyClass;

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

    public Instant getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Instant uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public StudyClassDTO getStudyClass() {
        return studyClass;
    }

    public void setStudyClass(StudyClassDTO studyClass) {
        this.studyClass = studyClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClassMaterialDTO)) {
            return false;
        }

        ClassMaterialDTO classMaterialDTO = (ClassMaterialDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, classMaterialDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClassMaterialDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", fileType='" + getFileType() + "'" +
            ", fileUrl='" + getFileUrl() + "'" +
            ", fileSizeBytes=" + getFileSizeBytes() +
            ", uploadedAt='" + getUploadedAt() + "'" +
            ", studyClass=" + getStudyClass() +
            "}";
    }
}
