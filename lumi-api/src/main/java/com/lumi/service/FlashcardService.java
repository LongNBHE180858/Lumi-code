package com.lumi.service;

import com.lumi.service.dto.FlashcardDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumi.domain.Flashcard}.
 */
public interface FlashcardService {
    /**
     * Save a flashcard.
     *
     * @param flashcardDTO the entity to save.
     * @return the persisted entity.
     */
    FlashcardDTO save(FlashcardDTO flashcardDTO);

    /**
     * Updates a flashcard.
     *
     * @param flashcardDTO the entity to update.
     * @return the persisted entity.
     */
    FlashcardDTO update(FlashcardDTO flashcardDTO);

    /**
     * Partially updates a flashcard.
     *
     * @param flashcardDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<FlashcardDTO> partialUpdate(FlashcardDTO flashcardDTO);

    /**
     * Get all the flashcards with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<FlashcardDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" flashcard.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<FlashcardDTO> findOne(Long id);

    /**
     * Delete the "id" flashcard.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
