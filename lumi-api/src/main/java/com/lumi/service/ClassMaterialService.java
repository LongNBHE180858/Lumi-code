package com.lumi.service;

import com.lumi.service.dto.ClassMaterialDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumi.domain.ClassMaterial}.
 */
public interface ClassMaterialService {
    /**
     * Save a classMaterial.
     *
     * @param classMaterialDTO the entity to save.
     * @return the persisted entity.
     */
    ClassMaterialDTO save(ClassMaterialDTO classMaterialDTO);

    /**
     * Updates a classMaterial.
     *
     * @param classMaterialDTO the entity to update.
     * @return the persisted entity.
     */
    ClassMaterialDTO update(ClassMaterialDTO classMaterialDTO);

    /**
     * Partially updates a classMaterial.
     *
     * @param classMaterialDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ClassMaterialDTO> partialUpdate(ClassMaterialDTO classMaterialDTO);

    /**
     * Get all the classMaterials with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ClassMaterialDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" classMaterial.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ClassMaterialDTO> findOne(Long id);

    /**
     * Delete the "id" classMaterial.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
