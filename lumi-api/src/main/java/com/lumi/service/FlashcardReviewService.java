package com.lumi.service;

import com.lumi.service.dto.FlashcardReviewDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.lumi.domain.FlashcardReview}.
 */
public interface FlashcardReviewService {
    /**
     * Save a flashcardReview.
     *
     * @param flashcardReviewDTO the entity to save.
     * @return the persisted entity.
     */
    FlashcardReviewDTO save(FlashcardReviewDTO flashcardReviewDTO);

    /**
     * Updates a flashcardReview.
     *
     * @param flashcardReviewDTO the entity to update.
     * @return the persisted entity.
     */
    FlashcardReviewDTO update(FlashcardReviewDTO flashcardReviewDTO);

    /**
     * Partially updates a flashcardReview.
     *
     * @param flashcardReviewDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<FlashcardReviewDTO> partialUpdate(FlashcardReviewDTO flashcardReviewDTO);

    /**
     * Get all the flashcardReviews with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    List<FlashcardReviewDTO> findAllWithEagerRelationships();

    /**
     * Get the "id" flashcardReview.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<FlashcardReviewDTO> findOne(Long id);

    /**
     * Delete the "id" flashcardReview.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
