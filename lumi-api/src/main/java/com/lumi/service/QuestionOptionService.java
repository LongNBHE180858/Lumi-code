package com.lumi.service;

import com.lumi.service.dto.QuestionOptionDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.lumi.domain.QuestionOption}.
 */
public interface QuestionOptionService {
    /**
     * Save a questionOption.
     *
     * @param questionOptionDTO the entity to save.
     * @return the persisted entity.
     */
    QuestionOptionDTO save(QuestionOptionDTO questionOptionDTO);

    /**
     * Updates a questionOption.
     *
     * @param questionOptionDTO the entity to update.
     * @return the persisted entity.
     */
    QuestionOptionDTO update(QuestionOptionDTO questionOptionDTO);

    /**
     * Partially updates a questionOption.
     *
     * @param questionOptionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<QuestionOptionDTO> partialUpdate(QuestionOptionDTO questionOptionDTO);

    /**
     * Get the "id" questionOption.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<QuestionOptionDTO> findOne(Long id);

    /**
     * Delete the "id" questionOption.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
