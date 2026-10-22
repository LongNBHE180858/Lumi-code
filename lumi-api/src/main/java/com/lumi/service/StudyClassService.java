package com.lumi.service;

import com.lumi.service.dto.StudyClassDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumi.domain.StudyClass}.
 */
public interface StudyClassService {
    /**
     * Save a studyClass.
     *
     * @param studyClassDTO the entity to save.
     * @return the persisted entity.
     */
    StudyClassDTO save(StudyClassDTO studyClassDTO);

    /**
     * Updates a studyClass.
     *
     * @param studyClassDTO the entity to update.
     * @return the persisted entity.
     */
    StudyClassDTO update(StudyClassDTO studyClassDTO);

    /**
     * Partially updates a studyClass.
     *
     * @param studyClassDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<StudyClassDTO> partialUpdate(StudyClassDTO studyClassDTO);

    /**
     * Get all the studyClasses with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<StudyClassDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" studyClass.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<StudyClassDTO> findOne(Long id);

    /**
     * Delete the "id" studyClass.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
