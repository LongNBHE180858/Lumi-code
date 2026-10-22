package com.lumi.service;

import com.lumi.service.dto.ExamDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumi.domain.Exam}.
 */
public interface ExamService {
    /**
     * Save a exam.
     *
     * @param examDTO the entity to save.
     * @return the persisted entity.
     */
    ExamDTO save(ExamDTO examDTO);

    /**
     * Updates a exam.
     *
     * @param examDTO the entity to update.
     * @return the persisted entity.
     */
    ExamDTO update(ExamDTO examDTO);

    /**
     * Partially updates a exam.
     *
     * @param examDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ExamDTO> partialUpdate(ExamDTO examDTO);

    /**
     * Get all the exams with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ExamDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" exam.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ExamDTO> findOne(Long id);

    /**
     * Delete the "id" exam.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
