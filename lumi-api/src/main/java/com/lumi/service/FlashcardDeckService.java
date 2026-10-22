package com.lumi.service;

import com.lumi.service.dto.FlashcardDeckDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumi.domain.FlashcardDeck}.
 */
public interface FlashcardDeckService {
    /**
     * Save a flashcardDeck.
     *
     * @param flashcardDeckDTO the entity to save.
     * @return the persisted entity.
     */
    FlashcardDeckDTO save(FlashcardDeckDTO flashcardDeckDTO);

    /**
     * Updates a flashcardDeck.
     *
     * @param flashcardDeckDTO the entity to update.
     * @return the persisted entity.
     */
    FlashcardDeckDTO update(FlashcardDeckDTO flashcardDeckDTO);

    /**
     * Partially updates a flashcardDeck.
     *
     * @param flashcardDeckDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<FlashcardDeckDTO> partialUpdate(FlashcardDeckDTO flashcardDeckDTO);

    /**
     * Get all the flashcardDecks with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<FlashcardDeckDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" flashcardDeck.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<FlashcardDeckDTO> findOne(Long id);

    /**
     * Delete the "id" flashcardDeck.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
