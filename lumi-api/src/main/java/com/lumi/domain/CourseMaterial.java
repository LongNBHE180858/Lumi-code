package com.lumi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * Lưu tài nguyên và đường dẫn tài liệu đính kèm bài học.
 */
@Entity
@Table(name = "course_materials")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CourseMaterial implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Size(max = 255)
    @Column(name = "file_name", length = 255)
    private String fileName;

    @Size(max = 255)
    @Column(name = "display_name", length = 255)
    private String displayName;

    @Size(max = 100)
    @Column(name = "mime_type", length = 100)
    private String mimeType;

    @Column(name = "order_index")
    private Integer orderIndex;

    @NotNull
    @Size(max = 20)
    @Column(name = "file_type", length = 20, nullable = false)
    private String fileType;

    @NotNull
    @Size(max = 1000)
    @Column(name = "file_url", length = 1000, nullable = false)
    private String fileUrl;

    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;

    @Size(max = 1000)
    @Column(name = "file_object_key", length = 1000)
    private String fileObjectKey;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "chapter" }, allowSetters = true)
    private Lesson lesson;

    @JsonIgnoreProperties(value = { "courseMaterial", "classMaterial", "uploadedBy", "subject", "grade" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "courseMaterial")
    private LearningDocument learningDocument;

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

    public String getFileName() {
        return this.fileName;
    }

    public CourseMaterial fileName(String fileName) {
        this.setFileName(fileName);
        return this;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public CourseMaterial displayName(String displayName) {
        this.setDisplayName(displayName);
        return this;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public CourseMaterial mimeType(String mimeType) {
        this.setMimeType(mimeType);
        return this;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Integer getOrderIndex() {
        return this.orderIndex;
    }

    public CourseMaterial orderIndex(Integer orderIndex) {
        this.setOrderIndex(orderIndex);
        return this;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
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

    public String getFileObjectKey() {
        return this.fileObjectKey;
    }

    public CourseMaterial fileObjectKey(String fileObjectKey) {
        this.setFileObjectKey(fileObjectKey);
        return this;
    }

    public void setFileObjectKey(String fileObjectKey) {
        this.fileObjectKey = fileObjectKey;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public CourseMaterial updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
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

    public LearningDocument getLearningDocument() {
        return this.learningDocument;
    }

    public void setLearningDocument(LearningDocument learningDocument) {
        if (this.learningDocument != null) {
            this.learningDocument.setCourseMaterial(null);
        }
        if (learningDocument != null) {
            learningDocument.setCourseMaterial(this);
        }
        this.learningDocument = learningDocument;
    }

    public CourseMaterial learningDocument(LearningDocument learningDocument) {
        this.setLearningDocument(learningDocument);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

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
            ", fileName='" + getFileName() + "'" +
            ", displayName='" + getDisplayName() + "'" +
            ", mimeType='" + getMimeType() + "'" +
            ", orderIndex=" + getOrderIndex() +
            ", fileType='" + getFileType() + "'" +
            ", fileUrl='" + getFileUrl() + "'" +
            ", fileSizeBytes=" + getFileSizeBytes() +
            ", fileObjectKey='" + getFileObjectKey() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
