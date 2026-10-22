package com.lumi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;

/**
 * A PdfChunk.
 */
@Entity
@Table(name = "pdf_chunk")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PdfChunk implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Min(value = 1)
    @Column(name = "page_number")
    private Integer pageNumber;

    @NotNull
    @Size(max = 5000)
    @Column(name = "chunk_text", length = 5000, nullable = false)
    private String chunkText;

    @Size(max = 5000)
    @Column(name = "embedding", length = 5000)
    private String embedding;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "lesson" }, allowSetters = true)
    private CourseMaterial material;

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

    public CourseMaterial getMaterial() {
        return this.material;
    }

    public void setMaterial(CourseMaterial courseMaterial) {
        this.material = courseMaterial;
    }

    public PdfChunk material(CourseMaterial courseMaterial) {
        this.setMaterial(courseMaterial);
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
            "}";
    }
}
