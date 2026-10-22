package com.lumi.service;

import com.lumi.service.dto.PdfChunkDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.lumi.domain.PdfChunk}.
 */
public interface PdfChunkService {
    /**
     * Save a pdfChunk.
     *
     * @param pdfChunkDTO the entity to save.
     * @return the persisted entity.
     */
    PdfChunkDTO save(PdfChunkDTO pdfChunkDTO);

    /**
     * Updates a pdfChunk.
     *
     * @param pdfChunkDTO the entity to update.
     * @return the persisted entity.
     */
    PdfChunkDTO update(PdfChunkDTO pdfChunkDTO);

    /**
     * Partially updates a pdfChunk.
     *
     * @param pdfChunkDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<PdfChunkDTO> partialUpdate(PdfChunkDTO pdfChunkDTO);

    /**
     * Get the "id" pdfChunk.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PdfChunkDTO> findOne(Long id);

    /**
     * Delete the "id" pdfChunk.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
