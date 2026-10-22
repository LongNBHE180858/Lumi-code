package com.lumi.service.impl;

import com.lumi.domain.ProctorSession;
import com.lumi.repository.ProctorSessionRepository;
import com.lumi.service.ProctorSessionService;
import com.lumi.service.dto.ProctorSessionDTO;
import com.lumi.service.mapper.ProctorSessionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.ProctorSession}.
 */
@Service
@Transactional
public class ProctorSessionServiceImpl implements ProctorSessionService {

    private static final Logger LOG = LoggerFactory.getLogger(ProctorSessionServiceImpl.class);

    private final ProctorSessionRepository proctorSessionRepository;

    private final ProctorSessionMapper proctorSessionMapper;

    public ProctorSessionServiceImpl(ProctorSessionRepository proctorSessionRepository, ProctorSessionMapper proctorSessionMapper) {
        this.proctorSessionRepository = proctorSessionRepository;
        this.proctorSessionMapper = proctorSessionMapper;
    }

    @Override
    public ProctorSessionDTO save(ProctorSessionDTO proctorSessionDTO) {
        LOG.debug("Request to save ProctorSession : {}", proctorSessionDTO);
        ProctorSession proctorSession = proctorSessionMapper.toEntity(proctorSessionDTO);
        proctorSession = proctorSessionRepository.save(proctorSession);
        return proctorSessionMapper.toDto(proctorSession);
    }

    @Override
    public ProctorSessionDTO update(ProctorSessionDTO proctorSessionDTO) {
        LOG.debug("Request to update ProctorSession : {}", proctorSessionDTO);
        ProctorSession proctorSession = proctorSessionMapper.toEntity(proctorSessionDTO);
        proctorSession = proctorSessionRepository.save(proctorSession);
        return proctorSessionMapper.toDto(proctorSession);
    }

    @Override
    public Optional<ProctorSessionDTO> partialUpdate(ProctorSessionDTO proctorSessionDTO) {
        LOG.debug("Request to partially update ProctorSession : {}", proctorSessionDTO);

        return proctorSessionRepository
            .findById(proctorSessionDTO.getId())
            .map(existingProctorSession -> {
                proctorSessionMapper.partialUpdate(existingProctorSession, proctorSessionDTO);

                return existingProctorSession;
            })
            .map(proctorSessionRepository::save)
            .map(proctorSessionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProctorSessionDTO> findOne(Long id) {
        LOG.debug("Request to get ProctorSession : {}", id);
        return proctorSessionRepository.findById(id).map(proctorSessionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ProctorSession : {}", id);
        proctorSessionRepository.deleteById(id);
    }
}
