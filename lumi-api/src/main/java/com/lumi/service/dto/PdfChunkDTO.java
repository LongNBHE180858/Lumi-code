package com.lumi.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.PdfChunk} entity.
 */
@Schema(description = "Lưu đoạn văn bản và embedding được tách từ nguồn tài liệu.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PdfChunkDTO implements Serializable {

    private Long id;

    private Integer pageNumber;

    @Lob
    private String chunkText;

    @Lob
    private String embedding;

    @NotNull
    @Min(value = 0)
    private Integer chunkIndex;

    @NotNull
    @Min(value = 1)
    private Integer indexVersion;

    @NotNull
    private LearningDocumentDTO material;

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

    public Integer getChunkIndex() {
        return chunkIndex;
    }

    public void setChunkIndex(Integer chunkIndex) {
        this.chunkIndex = chunkIndex;
    }

    public Integer getIndexVersion() {
        return indexVersion;
    }

    public void setIndexVersion(Integer indexVersion) {
        this.indexVersion = indexVersion;
    }

    public LearningDocumentDTO getMaterial() {
        return material;
    }

    public void setMaterial(LearningDocumentDTO material) {
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
            ", chunkIndex=" + getChunkIndex() +
            ", indexVersion=" + getIndexVersion() +
            ", material=" + getMaterial() +
            "}";
    }
}
