package com.lumi.service;

import com.lumi.service.dto.PayoutRequestDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumi.domain.PayoutRequest}.
 */
public interface PayoutRequestService {
    /**
     * Save a payoutRequest.
     *
     * @param payoutRequestDTO the entity to save.
     * @return the persisted entity.
     */
    PayoutRequestDTO save(PayoutRequestDTO payoutRequestDTO);

    /**
     * Updates a payoutRequest.
     *
     * @param payoutRequestDTO the entity to update.
     * @return the persisted entity.
     */
    PayoutRequestDTO update(PayoutRequestDTO payoutRequestDTO);

    /**
     * Partially updates a payoutRequest.
     *
     * @param payoutRequestDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<PayoutRequestDTO> partialUpdate(PayoutRequestDTO payoutRequestDTO);

    /**
     * Get all the payoutRequests with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PayoutRequestDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" payoutRequest.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PayoutRequestDTO> findOne(Long id);

    /**
     * Delete the "id" payoutRequest.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
