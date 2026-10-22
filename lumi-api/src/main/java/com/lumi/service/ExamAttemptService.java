package com.lumi.service;

import com.lumi.service.dto.ExamAttemptDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumi.domain.ExamAttempt}.
 */
public interface ExamAttemptService {
    /**
     * Save a examAttempt.
     *
     * @param examAttemptDTO the entity to save.
     * @return the persisted entity.
     */
    ExamAttemptDTO save(ExamAttemptDTO examAttemptDTO);

    /**
     * Updates a examAttempt.
     *
     * @param examAttemptDTO the entity to update.
     * @return the persisted entity.
     */
    ExamAttemptDTO update(ExamAttemptDTO examAttemptDTO);

    /**
     * Partially updates a examAttempt.
     *
     * @param examAttemptDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ExamAttemptDTO> partialUpdate(ExamAttemptDTO examAttemptDTO);

    /**
     * Get all the ExamAttemptDTO where ProctorSession is {@code null}.
     *
     * @return the {@link List} of entities.
     */
    List<ExamAttemptDTO> findAllWhereProctorSessionIsNull();

    /**
     * Get all the examAttempts with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ExamAttemptDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" examAttempt.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ExamAttemptDTO> findOne(Long id);

    /**
     * Delete the "id" examAttempt.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
