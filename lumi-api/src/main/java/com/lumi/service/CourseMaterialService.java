package com.lumi.service;

import com.lumi.service.dto.CourseMaterialDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.lumi.domain.CourseMaterial}.
 */
public interface CourseMaterialService {
    /**
     * Save a courseMaterial.
     *
     * @param courseMaterialDTO the entity to save.
     * @return the persisted entity.
     */
    CourseMaterialDTO save(CourseMaterialDTO courseMaterialDTO);

    /**
     * Updates a courseMaterial.
     *
     * @param courseMaterialDTO the entity to update.
     * @return the persisted entity.
     */
    CourseMaterialDTO update(CourseMaterialDTO courseMaterialDTO);

    /**
     * Partially updates a courseMaterial.
     *
     * @param courseMaterialDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CourseMaterialDTO> partialUpdate(CourseMaterialDTO courseMaterialDTO);

    /**
     * Get all the CourseMaterialDTO where LearningDocument is {@code null}.
     *
     * @return the {@link List} of entities.
     */
    List<CourseMaterialDTO> findAllWhereLearningDocumentIsNull();

    /**
     * Get the "id" courseMaterial.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CourseMaterialDTO> findOne(Long id);

    /**
     * Delete the "id" courseMaterial.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
