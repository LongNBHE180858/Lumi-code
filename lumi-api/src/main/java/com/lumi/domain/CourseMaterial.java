package com.lumi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;

/**
 * A CourseMaterial.
 */
@Entity
@Table(name = "course_material")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CourseMaterial implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 20)
    @Column(name = "file_type", length = 20, nullable = false)
    private String fileType;

    @NotNull
    @Size(max = 1000)
    @Column(name = "file_url", length = 1000, nullable = false)
    private String fileUrl;

    @Min(value = 0L)
    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "chapter" }, allowSetters = true)
    private Lesson lesson;

    @Size(max = 255)
    @Column(name = "file_name", length = 255)
    private String fileName;

    @Size(max = 255)
    @Column(name = "display_name", length = 255)
    private String displayName;

    @Size(max = 100)
    @Column(name = "mime_type", length = 100)
    private String mimeType;

    @Min(0)
    @Column(name = "order_index")
    private Integer orderIndex;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CourseMaterial id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileType() {
        return this.fileType;
    }

    public CourseMaterial fileType(String fileType) {
        this.setFileType(fileType);
        return this;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileUrl() {
        return this.fileUrl;
    }

    public CourseMaterial fileUrl(String fileUrl) {
        this.setFileUrl(fileUrl);
        return this;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Long getFileSizeBytes() {
        return this.fileSizeBytes;
    }

    public CourseMaterial fileSizeBytes(Long fileSizeBytes) {
        this.setFileSizeBytes(fileSizeBytes);
        return this;
    }

    public void setFileSizeBytes(Long fileSizeBytes) {
        this.fileSizeBytes = fileSizeBytes;
    }

    public Lesson getLesson() {
        return this.lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public CourseMaterial lesson(Lesson lesson) {
        this.setLesson(lesson);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public CourseMaterial fileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public CourseMaterial displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public CourseMaterial mimeType(String mimeType) {
        this.mimeType = mimeType;
        return this;
    }

    public Integer getOrderIndex() {
        return this.orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public CourseMaterial orderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CourseMaterial)) {
            return false;
        }
        return getId() != null && getId().equals(((CourseMaterial) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CourseMaterial{" +
            "id=" + getId() +
            ", fileType='" + getFileType() + "'" +
            ", fileUrl='" + getFileUrl() + "'" +
            ", fileSizeBytes=" + getFileSizeBytes() +
            "}";
    }
}
