package com.lumi.service;

import com.lumi.service.dto.AssignmentSubmissionDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumi.domain.AssignmentSubmission}.
 */
public interface AssignmentSubmissionService {
    /**
     * Save a assignmentSubmission.
     *
     * @param assignmentSubmissionDTO the entity to save.
     * @return the persisted entity.
     */
    AssignmentSubmissionDTO save(AssignmentSubmissionDTO assignmentSubmissionDTO);

    /**
     * Updates a assignmentSubmission.
     *
     * @param assignmentSubmissionDTO the entity to update.
     * @return the persisted entity.
     */
    AssignmentSubmissionDTO update(AssignmentSubmissionDTO assignmentSubmissionDTO);

    /**
     * Partially updates a assignmentSubmission.
     *
     * @param assignmentSubmissionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<AssignmentSubmissionDTO> partialUpdate(AssignmentSubmissionDTO assignmentSubmissionDTO);

    /**
     * Get all the assignmentSubmissions with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<AssignmentSubmissionDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" assignmentSubmission.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<AssignmentSubmissionDTO> findOne(Long id);

    /**
     * Delete the "id" assignmentSubmission.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
