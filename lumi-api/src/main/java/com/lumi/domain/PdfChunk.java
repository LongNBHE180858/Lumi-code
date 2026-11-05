package com.lumi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;

/**
 * Lưu đoạn văn bản và embedding được tách từ nguồn tài liệu.
 */
@Entity
@Table(name = "pdf_chunks")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PdfChunk implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "page_number")
    private Integer pageNumber;

    @Column(name = "chunk_text", nullable = false, columnDefinition = "text")
    private String chunkText;

    @Column(name = "embedding", columnDefinition = "text")
    private String embedding;

    @NotNull
    @Min(value = 0)
    @Column(name = "chunk_index", nullable = false)
    private Integer chunkIndex;

    @NotNull
    @Min(value = 1)
    @Column(name = "index_version", nullable = false)
    private Integer indexVersion;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "courseMaterial", "classMaterial", "uploadedBy", "subject", "grade" }, allowSetters = true)
    private LearningDocument material;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PdfChunk id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPageNumber() {
        return this.pageNumber;
    }

    public PdfChunk pageNumber(Integer pageNumber) {
        this.setPageNumber(pageNumber);
        return this;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getChunkText() {
        return this.chunkText;
    }

    public PdfChunk chunkText(String chunkText) {
        this.setChunkText(chunkText);
        return this;
    }

    public void setChunkText(String chunkText) {
        this.chunkText = chunkText;
    }

    public String getEmbedding() {
        return this.embedding;
    }

    public PdfChunk embedding(String embedding) {
        this.setEmbedding(embedding);
        return this;
    }

    public void setEmbedding(String embedding) {
        this.embedding = embedding;
    }

    public Integer getChunkIndex() {
        return this.chunkIndex;
    }

    public PdfChunk chunkIndex(Integer chunkIndex) {
        this.setChunkIndex(chunkIndex);
        return this;
    }

    public void setChunkIndex(Integer chunkIndex) {
        this.chunkIndex = chunkIndex;
    }

    public Integer getIndexVersion() {
        return this.indexVersion;
    }

    public PdfChunk indexVersion(Integer indexVersion) {
        this.setIndexVersion(indexVersion);
        return this;
    }

    public void setIndexVersion(Integer indexVersion) {
        this.indexVersion = indexVersion;
    }

    public LearningDocument getMaterial() {
        return this.material;
    }

    public void setMaterial(LearningDocument learningDocument) {
        this.material = learningDocument;
    }

    public PdfChunk material(LearningDocument learningDocument) {
        this.setMaterial(learningDocument);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PdfChunk)) {
            return false;
        }
        return getId() != null && getId().equals(((PdfChunk) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PdfChunk{" +
            "id=" + getId() +
            ", pageNumber=" + getPageNumber() +
            ", chunkText='" + getChunkText() + "'" +
            ", embedding='" + getEmbedding() + "'" +
            ", chunkIndex=" + getChunkIndex() +
            ", indexVersion=" + getIndexVersion() +
            "}";
    }
}
