package com.lumi.service;

import com.lumi.service.dto.ProctorSessionDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.lumi.domain.ProctorSession}.
 */
public interface ProctorSessionService {
    /**
     * Save a proctorSession.
     *
     * @param proctorSessionDTO the entity to save.
     * @return the persisted entity.
     */
    ProctorSessionDTO save(ProctorSessionDTO proctorSessionDTO);

    /**
     * Updates a proctorSession.
     *
     * @param proctorSessionDTO the entity to update.
     * @return the persisted entity.
     */
    ProctorSessionDTO update(ProctorSessionDTO proctorSessionDTO);

    /**
     * Partially updates a proctorSession.
     *
     * @param proctorSessionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ProctorSessionDTO> partialUpdate(ProctorSessionDTO proctorSessionDTO);

    /**
     * Get the "id" proctorSession.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ProctorSessionDTO> findOne(Long id);

    /**
     * Delete the "id" proctorSession.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
