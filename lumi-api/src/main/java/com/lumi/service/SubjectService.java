package com.lumi.service;

import com.lumi.service.dto.SubjectDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.lumi.domain.Subject}.
 */
public interface SubjectService {
    /**
     * Save a subject.
     *
     * @param subjectDTO the entity to save.
     * @return the persisted entity.
     */
    SubjectDTO save(SubjectDTO subjectDTO);

    /**
     * Updates a subject.
     *
     * @param subjectDTO the entity to update.
     * @return the persisted entity.
     */
    SubjectDTO update(SubjectDTO subjectDTO);

    /**
     * Partially updates a subject.
     *
     * @param subjectDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SubjectDTO> partialUpdate(SubjectDTO subjectDTO);

    /**
     * Get the "id" subject.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SubjectDTO> findOne(Long id);

    /**
     * Delete the "id" subject.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
