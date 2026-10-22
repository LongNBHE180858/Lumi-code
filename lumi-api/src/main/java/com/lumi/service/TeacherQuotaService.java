package com.lumi.service;

import com.lumi.service.dto.TeacherQuotaDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.lumi.domain.TeacherQuota}.
 */
public interface TeacherQuotaService {
    /**
     * Save a teacherQuota.
     *
     * @param teacherQuotaDTO the entity to save.
     * @return the persisted entity.
     */
    TeacherQuotaDTO save(TeacherQuotaDTO teacherQuotaDTO);

    /**
     * Updates a teacherQuota.
     *
     * @param teacherQuotaDTO the entity to update.
     * @return the persisted entity.
     */
    TeacherQuotaDTO update(TeacherQuotaDTO teacherQuotaDTO);

    /**
     * Partially updates a teacherQuota.
     *
     * @param teacherQuotaDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TeacherQuotaDTO> partialUpdate(TeacherQuotaDTO teacherQuotaDTO);

    /**
     * Get all the teacherQuotas with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    List<TeacherQuotaDTO> findAllWithEagerRelationships();

    /**
     * Get the "id" teacherQuota.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TeacherQuotaDTO> findOne(Long id);

    /**
     * Delete the "id" teacherQuota.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
