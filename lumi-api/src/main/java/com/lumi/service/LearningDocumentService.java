package com.lumi.service;

import com.lumi.service.dto.LearningDocumentDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumi.domain.LearningDocument}.
 */
public interface LearningDocumentService {
    /**
     * Save a learningDocument.
     *
     * @param learningDocumentDTO the entity to save.
     * @return the persisted entity.
     */
    LearningDocumentDTO save(LearningDocumentDTO learningDocumentDTO);

    /**
     * Updates a learningDocument.
     *
     * @param learningDocumentDTO the entity to update.
     * @return the persisted entity.
     */
    LearningDocumentDTO update(LearningDocumentDTO learningDocumentDTO);

    /**
     * Partially updates a learningDocument.
     *
     * @param learningDocumentDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<LearningDocumentDTO> partialUpdate(LearningDocumentDTO learningDocumentDTO);

    /**
     * Get all the learningDocuments with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<LearningDocumentDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" learningDocument.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<LearningDocumentDTO> findOne(Long id);

    /**
     * Delete the "id" learningDocument.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
