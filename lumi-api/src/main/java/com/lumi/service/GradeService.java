package com.lumi.service;

import com.lumi.service.dto.GradeDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.lumi.domain.Grade}.
 */
public interface GradeService {
    /**
     * Save a grade.
     *
     * @param gradeDTO the entity to save.
     * @return the persisted entity.
     */
    GradeDTO save(GradeDTO gradeDTO);

    /**
     * Updates a grade.
     *
     * @param gradeDTO the entity to update.
     * @return the persisted entity.
     */
    GradeDTO update(GradeDTO gradeDTO);

    /**
     * Partially updates a grade.
     *
     * @param gradeDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<GradeDTO> partialUpdate(GradeDTO gradeDTO);

    /**
     * Get the "id" grade.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<GradeDTO> findOne(Long id);

    /**
     * Delete the "id" grade.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
