package com.lumi.service;

import com.lumi.service.dto.CourseReviewDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumi.domain.CourseReview}.
 */
public interface CourseReviewService {
    /**
     * Save a courseReview.
     *
     * @param courseReviewDTO the entity to save.
     * @return the persisted entity.
     */
    CourseReviewDTO save(CourseReviewDTO courseReviewDTO);

    /**
     * Updates a courseReview.
     *
     * @param courseReviewDTO the entity to update.
     * @return the persisted entity.
     */
    CourseReviewDTO update(CourseReviewDTO courseReviewDTO);

    /**
     * Partially updates a courseReview.
     *
     * @param courseReviewDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CourseReviewDTO> partialUpdate(CourseReviewDTO courseReviewDTO);

    /**
     * Get all the courseReviews with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CourseReviewDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" courseReview.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CourseReviewDTO> findOne(Long id);

    /**
     * Delete the "id" courseReview.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
