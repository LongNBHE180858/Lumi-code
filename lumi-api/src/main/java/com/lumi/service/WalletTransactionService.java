package com.lumi.service;

import com.lumi.service.dto.WalletTransactionDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.lumi.domain.WalletTransaction}.
 */
public interface WalletTransactionService {
    /**
     * Save a walletTransaction.
     *
     * @param walletTransactionDTO the entity to save.
     * @return the persisted entity.
     */
    WalletTransactionDTO save(WalletTransactionDTO walletTransactionDTO);

    /**
     * Updates a walletTransaction.
     *
     * @param walletTransactionDTO the entity to update.
     * @return the persisted entity.
     */
    WalletTransactionDTO update(WalletTransactionDTO walletTransactionDTO);

    /**
     * Partially updates a walletTransaction.
     *
     * @param walletTransactionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<WalletTransactionDTO> partialUpdate(WalletTransactionDTO walletTransactionDTO);

    /**
     * Get the "id" walletTransaction.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<WalletTransactionDTO> findOne(Long id);

    /**
     * Delete the "id" walletTransaction.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
