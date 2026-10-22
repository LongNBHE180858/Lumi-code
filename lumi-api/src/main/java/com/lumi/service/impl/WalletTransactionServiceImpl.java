package com.lumi.service.impl;

import com.lumi.domain.WalletTransaction;
import com.lumi.repository.WalletTransactionRepository;
import com.lumi.service.WalletTransactionService;
import com.lumi.service.dto.WalletTransactionDTO;
import com.lumi.service.mapper.WalletTransactionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.WalletTransaction}.
 */
@Service
@Transactional
public class WalletTransactionServiceImpl implements WalletTransactionService {

    private static final Logger LOG = LoggerFactory.getLogger(WalletTransactionServiceImpl.class);

    private final WalletTransactionRepository walletTransactionRepository;

    private final WalletTransactionMapper walletTransactionMapper;

    public WalletTransactionServiceImpl(
        WalletTransactionRepository walletTransactionRepository,
        WalletTransactionMapper walletTransactionMapper
    ) {
        this.walletTransactionRepository = walletTransactionRepository;
        this.walletTransactionMapper = walletTransactionMapper;
    }

    @Override
    public WalletTransactionDTO save(WalletTransactionDTO walletTransactionDTO) {
        LOG.debug("Request to save WalletTransaction : {}", walletTransactionDTO);
        WalletTransaction walletTransaction = walletTransactionMapper.toEntity(walletTransactionDTO);
        walletTransaction = walletTransactionRepository.save(walletTransaction);
        return walletTransactionMapper.toDto(walletTransaction);
    }

    @Override
    public WalletTransactionDTO update(WalletTransactionDTO walletTransactionDTO) {
        LOG.debug("Request to update WalletTransaction : {}", walletTransactionDTO);
        WalletTransaction walletTransaction = walletTransactionMapper.toEntity(walletTransactionDTO);
        walletTransaction = walletTransactionRepository.save(walletTransaction);
        return walletTransactionMapper.toDto(walletTransaction);
    }

    @Override
    public Optional<WalletTransactionDTO> partialUpdate(WalletTransactionDTO walletTransactionDTO) {
        LOG.debug("Request to partially update WalletTransaction : {}", walletTransactionDTO);

        return walletTransactionRepository
            .findById(walletTransactionDTO.getId())
            .map(existingWalletTransaction -> {
                walletTransactionMapper.partialUpdate(existingWalletTransaction, walletTransactionDTO);

                return existingWalletTransaction;
            })
            .map(walletTransactionRepository::save)
            .map(walletTransactionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WalletTransactionDTO> findOne(Long id) {
        LOG.debug("Request to get WalletTransaction : {}", id);
        return walletTransactionRepository.findById(id).map(walletTransactionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete WalletTransaction : {}", id);
        walletTransactionRepository.deleteById(id);
    }
}
