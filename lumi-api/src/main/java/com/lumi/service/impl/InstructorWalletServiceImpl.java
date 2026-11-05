package com.lumi.service.impl;

import com.lumi.domain.InstructorWallet;
import com.lumi.repository.InstructorWalletRepository;
import com.lumi.service.InstructorWalletService;
import com.lumi.service.dto.InstructorWalletDTO;
import com.lumi.service.mapper.InstructorWalletMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.InstructorWallet}.
 */
@Service
@Transactional
public class InstructorWalletServiceImpl implements InstructorWalletService {

    private static final Logger LOG = LoggerFactory.getLogger(InstructorWalletServiceImpl.class);

    private final InstructorWalletRepository instructorWalletRepository;

    private final InstructorWalletMapper instructorWalletMapper;

    public InstructorWalletServiceImpl(
        InstructorWalletRepository instructorWalletRepository,
        InstructorWalletMapper instructorWalletMapper
    ) {
        this.instructorWalletRepository = instructorWalletRepository;
        this.instructorWalletMapper = instructorWalletMapper;
    }

    @Override
    public InstructorWalletDTO save(InstructorWalletDTO instructorWalletDTO) {
        LOG.debug("Request to save InstructorWallet : {}", instructorWalletDTO);
        InstructorWallet instructorWallet = instructorWalletMapper.toEntity(instructorWalletDTO);
        instructorWallet = instructorWalletRepository.save(instructorWallet);
        return instructorWalletMapper.toDto(instructorWallet);
    }

    @Override
    public InstructorWalletDTO update(InstructorWalletDTO instructorWalletDTO) {
        LOG.debug("Request to update InstructorWallet : {}", instructorWalletDTO);
        InstructorWallet instructorWallet = instructorWalletMapper.toEntity(instructorWalletDTO);
        instructorWallet = instructorWalletRepository.save(instructorWallet);
        return instructorWalletMapper.toDto(instructorWallet);
    }

    @Override
    public Optional<InstructorWalletDTO> partialUpdate(InstructorWalletDTO instructorWalletDTO) {
        LOG.debug("Request to partially update InstructorWallet : {}", instructorWalletDTO);

        return instructorWalletRepository
            .findById(instructorWalletDTO.getId())
            .map(existingInstructorWallet -> {
                instructorWalletMapper.partialUpdate(existingInstructorWallet, instructorWalletDTO);

                return existingInstructorWallet;
            })
            .map(instructorWalletRepository::save)
            .map(instructorWalletMapper::toDto);
    }

    public Page<InstructorWalletDTO> findAllWithEagerRelationships(Pageable pageable) {
        return instructorWalletRepository.findAllWithEagerRelationships(pageable).map(instructorWalletMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InstructorWalletDTO> findOne(Long id) {
        LOG.debug("Request to get InstructorWallet : {}", id);
        return instructorWalletRepository.findOneWithEagerRelationships(id).map(instructorWalletMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete InstructorWallet : {}", id);
        instructorWalletRepository.deleteById(id);
    }
}
