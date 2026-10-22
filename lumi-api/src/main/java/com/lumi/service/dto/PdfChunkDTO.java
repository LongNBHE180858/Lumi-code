package com.lumi.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.PdfChunk} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PdfChunkDTO implements Serializable {

    private Long id;

    @Min(value = 1)
    private Integer pageNumber;

    @NotNull
    @Size(max = 5000)
    private String chunkText;

    @Size(max = 5000)
    private String embedding;

    @NotNull
    private CourseMaterialDTO material;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getChunkText() {
        return chunkText;
    }

    public void setChunkText(String chunkText) {
        this.chunkText = chunkText;
    }

    public String getEmbedding() {
        return embedding;
    }

    public void setEmbedding(String embedding) {
        this.embedding = embedding;
    }

    public CourseMaterialDTO getMaterial() {
        return material;
    }

    public void setMaterial(CourseMaterialDTO material) {
        this.material = material;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PdfChunkDTO)) {
            return false;
        }

        PdfChunkDTO pdfChunkDTO = (PdfChunkDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, pdfChunkDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PdfChunkDTO{" +
            "id=" + getId() +
            ", pageNumber=" + getPageNumber() +
            ", chunkText='" + getChunkText() + "'" +
            ", embedding='" + getEmbedding() + "'" +
            ", material=" + getMaterial() +
            "}";
    }
}
