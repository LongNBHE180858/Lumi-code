package com.lumi.service;

import com.lumi.service.dto.CourseReviewRequestDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumi.domain.CourseReviewRequest}.
 */
public interface CourseReviewRequestService {
    /**
     * Save a courseReviewRequest.
     *
     * @param courseReviewRequestDTO the entity to save.
     * @return the persisted entity.
     */
    CourseReviewRequestDTO save(CourseReviewRequestDTO courseReviewRequestDTO);

    /**
     * Updates a courseReviewRequest.
     *
     * @param courseReviewRequestDTO the entity to update.
     * @return the persisted entity.
     */
    CourseReviewRequestDTO update(CourseReviewRequestDTO courseReviewRequestDTO);

    /**
     * Partially updates a courseReviewRequest.
     *
     * @param courseReviewRequestDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CourseReviewRequestDTO> partialUpdate(CourseReviewRequestDTO courseReviewRequestDTO);

    /**
     * Get all the courseReviewRequests with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CourseReviewRequestDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" courseReviewRequest.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CourseReviewRequestDTO> findOne(Long id);

    /**
     * Delete the "id" courseReviewRequest.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
