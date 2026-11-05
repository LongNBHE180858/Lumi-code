package com.lumi.service;

import com.lumi.service.dto.AiGeneratedContentDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumi.domain.AiGeneratedContent}.
 */
public interface AiGeneratedContentService {
    /**
     * Save a aiGeneratedContent.
     *
     * @param aiGeneratedContentDTO the entity to save.
     * @return the persisted entity.
     */
    AiGeneratedContentDTO save(AiGeneratedContentDTO aiGeneratedContentDTO);

    /**
     * Updates a aiGeneratedContent.
     *
     * @param aiGeneratedContentDTO the entity to update.
     * @return the persisted entity.
     */
    AiGeneratedContentDTO update(AiGeneratedContentDTO aiGeneratedContentDTO);

    /**
     * Partially updates a aiGeneratedContent.
     *
     * @param aiGeneratedContentDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<AiGeneratedContentDTO> partialUpdate(AiGeneratedContentDTO aiGeneratedContentDTO);

    /**
     * Get all the aiGeneratedContents with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<AiGeneratedContentDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" aiGeneratedContent.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<AiGeneratedContentDTO> findOne(Long id);

    /**
     * Delete the "id" aiGeneratedContent.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
