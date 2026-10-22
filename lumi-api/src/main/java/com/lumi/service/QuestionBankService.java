package com.lumi.service;

import com.lumi.service.dto.QuestionBankDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumi.domain.QuestionBank}.
 */
public interface QuestionBankService {
    /**
     * Save a questionBank.
     *
     * @param questionBankDTO the entity to save.
     * @return the persisted entity.
     */
    QuestionBankDTO save(QuestionBankDTO questionBankDTO);

    /**
     * Updates a questionBank.
     *
     * @param questionBankDTO the entity to update.
     * @return the persisted entity.
     */
    QuestionBankDTO update(QuestionBankDTO questionBankDTO);

    /**
     * Partially updates a questionBank.
     *
     * @param questionBankDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<QuestionBankDTO> partialUpdate(QuestionBankDTO questionBankDTO);

    /**
     * Get all the questionBanks with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<QuestionBankDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" questionBank.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<QuestionBankDTO> findOne(Long id);

    /**
     * Delete the "id" questionBank.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
