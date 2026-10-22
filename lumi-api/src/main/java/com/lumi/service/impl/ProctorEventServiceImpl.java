package com.lumi.service.impl;

import com.lumi.domain.ProctorEvent;
import com.lumi.repository.ProctorEventRepository;
import com.lumi.service.ProctorEventService;
import com.lumi.service.dto.ProctorEventDTO;
import com.lumi.service.mapper.ProctorEventMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.ProctorEvent}.
 */
@Service
@Transactional
public class ProctorEventServiceImpl implements ProctorEventService {

    private static final Logger LOG = LoggerFactory.getLogger(ProctorEventServiceImpl.class);

    private final ProctorEventRepository proctorEventRepository;

    private final ProctorEventMapper proctorEventMapper;

    public ProctorEventServiceImpl(ProctorEventRepository proctorEventRepository, ProctorEventMapper proctorEventMapper) {
        this.proctorEventRepository = proctorEventRepository;
        this.proctorEventMapper = proctorEventMapper;
    }

    @Override
    public ProctorEventDTO save(ProctorEventDTO proctorEventDTO) {
        LOG.debug("Request to save ProctorEvent : {}", proctorEventDTO);
        ProctorEvent proctorEvent = proctorEventMapper.toEntity(proctorEventDTO);
        proctorEvent = proctorEventRepository.save(proctorEvent);
        return proctorEventMapper.toDto(proctorEvent);
    }

    @Override
    public ProctorEventDTO update(ProctorEventDTO proctorEventDTO) {
        LOG.debug("Request to update ProctorEvent : {}", proctorEventDTO);
        ProctorEvent proctorEvent = proctorEventMapper.toEntity(proctorEventDTO);
        proctorEvent = proctorEventRepository.save(proctorEvent);
        return proctorEventMapper.toDto(proctorEvent);
    }

    @Override
    public Optional<ProctorEventDTO> partialUpdate(ProctorEventDTO proctorEventDTO) {
        LOG.debug("Request to partially update ProctorEvent : {}", proctorEventDTO);

        return proctorEventRepository
            .findById(proctorEventDTO.getId())
            .map(existingProctorEvent -> {
                proctorEventMapper.partialUpdate(existingProctorEvent, proctorEventDTO);

                return existingProctorEvent;
            })
            .map(proctorEventRepository::save)
            .map(proctorEventMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProctorEventDTO> findOne(Long id) {
        LOG.debug("Request to get ProctorEvent : {}", id);
        return proctorEventRepository.findById(id).map(proctorEventMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ProctorEvent : {}", id);
        proctorEventRepository.deleteById(id);
    }
}
