package com.lumi.service;

import com.lumi.service.dto.QuestionKnowledgeLinkDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.lumi.domain.QuestionKnowledgeLink}.
 */
public interface QuestionKnowledgeLinkService {
    /**
     * Save a questionKnowledgeLink.
     *
     * @param questionKnowledgeLinkDTO the entity to save.
     * @return the persisted entity.
     */
    QuestionKnowledgeLinkDTO save(QuestionKnowledgeLinkDTO questionKnowledgeLinkDTO);

    /**
     * Updates a questionKnowledgeLink.
     *
     * @param questionKnowledgeLinkDTO the entity to update.
     * @return the persisted entity.
     */
    QuestionKnowledgeLinkDTO update(QuestionKnowledgeLinkDTO questionKnowledgeLinkDTO);

    /**
     * Partially updates a questionKnowledgeLink.
     *
     * @param questionKnowledgeLinkDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<QuestionKnowledgeLinkDTO> partialUpdate(QuestionKnowledgeLinkDTO questionKnowledgeLinkDTO);

    /**
     * Get the "id" questionKnowledgeLink.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<QuestionKnowledgeLinkDTO> findOne(Long id);

    /**
     * Delete the "id" questionKnowledgeLink.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
