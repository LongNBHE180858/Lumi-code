package com.lumi.service;

import com.lumi.service.dto.ProctorEventDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.lumi.domain.ProctorEvent}.
 */
public interface ProctorEventService {
    /**
     * Save a proctorEvent.
     *
     * @param proctorEventDTO the entity to save.
     * @return the persisted entity.
     */
    ProctorEventDTO save(ProctorEventDTO proctorEventDTO);

    /**
     * Updates a proctorEvent.
     *
     * @param proctorEventDTO the entity to update.
     * @return the persisted entity.
     */
    ProctorEventDTO update(ProctorEventDTO proctorEventDTO);

    /**
     * Partially updates a proctorEvent.
     *
     * @param proctorEventDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ProctorEventDTO> partialUpdate(ProctorEventDTO proctorEventDTO);

    /**
     * Get the "id" proctorEvent.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ProctorEventDTO> findOne(Long id);

    /**
     * Delete the "id" proctorEvent.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
