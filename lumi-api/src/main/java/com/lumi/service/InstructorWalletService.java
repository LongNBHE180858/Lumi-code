package com.lumi.service;

import com.lumi.service.dto.InstructorWalletDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<InstructorWalletDTO> findAllWithEagerRelationships(Pageable pageable);

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
