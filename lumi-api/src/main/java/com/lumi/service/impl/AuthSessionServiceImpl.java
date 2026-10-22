package com.lumi.service.impl;

import com.lumi.domain.AuthSession;
import com.lumi.repository.AuthSessionRepository;
import com.lumi.service.AuthSessionService;
import com.lumi.service.dto.AuthSessionDTO;
import com.lumi.service.mapper.AuthSessionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.AuthSession}.
 */
@Service
@Transactional
public class AuthSessionServiceImpl implements AuthSessionService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthSessionServiceImpl.class);

    private final AuthSessionRepository authSessionRepository;

    private final AuthSessionMapper authSessionMapper;

    public AuthSessionServiceImpl(AuthSessionRepository authSessionRepository, AuthSessionMapper authSessionMapper) {
        this.authSessionRepository = authSessionRepository;
        this.authSessionMapper = authSessionMapper;
    }

    @Override
    public AuthSessionDTO save(AuthSessionDTO authSessionDTO) {
        LOG.debug("Request to save AuthSession : {}", authSessionDTO);
        AuthSession authSession = authSessionMapper.toEntity(authSessionDTO);
        authSession = authSessionRepository.save(authSession);
        return authSessionMapper.toDto(authSession);
    }

    @Override
    public AuthSessionDTO update(AuthSessionDTO authSessionDTO) {
        LOG.debug("Request to update AuthSession : {}", authSessionDTO);
        AuthSession authSession = authSessionMapper.toEntity(authSessionDTO);
        authSession = authSessionRepository.save(authSession);
        return authSessionMapper.toDto(authSession);
    }

    @Override
    public Optional<AuthSessionDTO> partialUpdate(AuthSessionDTO authSessionDTO) {
        LOG.debug("Request to partially update AuthSession : {}", authSessionDTO);

        return authSessionRepository
            .findById(authSessionDTO.getId())
            .map(existingAuthSession -> {
                authSessionMapper.partialUpdate(existingAuthSession, authSessionDTO);

                return existingAuthSession;
            })
            .map(authSessionRepository::save)
            .map(authSessionMapper::toDto);
    }

    public Page<AuthSessionDTO> findAllWithEagerRelationships(Pageable pageable) {
        return authSessionRepository.findAllWithEagerRelationships(pageable).map(authSessionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AuthSessionDTO> findOne(Long id) {
        LOG.debug("Request to get AuthSession : {}", id);
        return authSessionRepository.findOneWithEagerRelationships(id).map(authSessionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete AuthSession : {}", id);
        authSessionRepository.deleteById(id);
    }
}
