package com.lumi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * A ClassMaterial.
 */
@Entity
@Table(name = "class_material")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ClassMaterial implements Serializable {

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

    @NotNull
    @Size(max = 20)
    @Column(name = "file_type", length = 20, nullable = false)
    private String fileType;

    @NotNull
    @Size(max = 1000)
    @Column(name = "file_url", length = 1000, nullable = false)
    private String fileUrl;

    @NotNull
    @Min(value = 0L)
    @Column(name = "file_size_bytes", nullable = false)
    private Long fileSizeBytes;

    @Column(name = "uploaded_at")
    private Instant uploadedAt;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "instructor", "subject", "grade" }, allowSetters = true)
    private StudyClass studyClass;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ClassMaterial id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public ClassMaterial title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFileType() {
        return this.fileType;
    }

    public ClassMaterial fileType(String fileType) {
        this.setFileType(fileType);
        return this;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileUrl() {
        return this.fileUrl;
    }

    public ClassMaterial fileUrl(String fileUrl) {
        this.setFileUrl(fileUrl);
        return this;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Long getFileSizeBytes() {
        return this.fileSizeBytes;
    }

    public ClassMaterial fileSizeBytes(Long fileSizeBytes) {
        this.setFileSizeBytes(fileSizeBytes);
        return this;
    }

    public void setFileSizeBytes(Long fileSizeBytes) {
        this.fileSizeBytes = fileSizeBytes;
    }

    public Instant getUploadedAt() {
        return this.uploadedAt;
    }

    public ClassMaterial uploadedAt(Instant uploadedAt) {
        this.setUploadedAt(uploadedAt);
        return this;
    }

    public void setUploadedAt(Instant uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public StudyClass getStudyClass() {
        return this.studyClass;
    }

    public void setStudyClass(StudyClass studyClass) {
        this.studyClass = studyClass;
    }

    public ClassMaterial studyClass(StudyClass studyClass) {
        this.setStudyClass(studyClass);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClassMaterial)) {
            return false;
        }
        return getId() != null && getId().equals(((ClassMaterial) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClassMaterial{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", fileType='" + getFileType() + "'" +
            ", fileUrl='" + getFileUrl() + "'" +
            ", fileSizeBytes=" + getFileSizeBytes() +
            ", uploadedAt='" + getUploadedAt() + "'" +
            "}";
    }
}
