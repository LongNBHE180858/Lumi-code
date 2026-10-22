package com.lumi.service;

import com.lumi.service.dto.InstructorWalletDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.lumi.domain.InstructorWallet}.
 */
public interface InstructorWalletService {
    /**
     * Save a instructorWallet.
     *
     * @param instructorWalletDTO the entity to save.
     * @return the persisted entity.
     */
    InstructorWalletDTO save(InstructorWalletDTO instructorWalletDTO);

    /**
     * Updates a instructorWallet.
     *
     * @param instructorWalletDTO the entity to update.
     * @return the persisted entity.
     */
    InstructorWalletDTO update(InstructorWalletDTO instructorWalletDTO);

    /**
     * Partially updates a instructorWallet.
     *
     * @param instructorWalletDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<InstructorWalletDTO> partialUpdate(InstructorWalletDTO instructorWalletDTO);

    /**
     * Get all the instructorWallets with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    List<InstructorWalletDTO> findAllWithEagerRelationships();

    /**
     * Get the "id" instructorWallet.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<InstructorWalletDTO> findOne(Long id);

    /**
     * Delete the "id" instructorWallet.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
