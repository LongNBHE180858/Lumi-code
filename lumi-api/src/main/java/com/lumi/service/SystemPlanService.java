package com.lumi.service;

import com.lumi.service.dto.SystemPlanDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.lumi.domain.SystemPlan}.
 */
public interface SystemPlanService {
    /**
     * Save a systemPlan.
     *
     * @param systemPlanDTO the entity to save.
     * @return the persisted entity.
     */
    SystemPlanDTO save(SystemPlanDTO systemPlanDTO);

    /**
     * Updates a systemPlan.
     *
     * @param systemPlanDTO the entity to update.
     * @return the persisted entity.
     */
    SystemPlanDTO update(SystemPlanDTO systemPlanDTO);

    /**
     * Partially updates a systemPlan.
     *
     * @param systemPlanDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SystemPlanDTO> partialUpdate(SystemPlanDTO systemPlanDTO);

    /**
     * Get the "id" systemPlan.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SystemPlanDTO> findOne(Long id);

    /**
     * Delete the "id" systemPlan.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
