package com.lumi.service.impl;

import com.lumi.domain.SystemPlan;
import com.lumi.repository.SystemPlanRepository;
import com.lumi.service.SystemPlanService;
import com.lumi.service.dto.SystemPlanDTO;
import com.lumi.service.mapper.SystemPlanMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.SystemPlan}.
 */
@Service
@Transactional
public class SystemPlanServiceImpl implements SystemPlanService {

    private static final Logger LOG = LoggerFactory.getLogger(SystemPlanServiceImpl.class);

    private final SystemPlanRepository systemPlanRepository;

    private final SystemPlanMapper systemPlanMapper;

    public SystemPlanServiceImpl(SystemPlanRepository systemPlanRepository, SystemPlanMapper systemPlanMapper) {
        this.systemPlanRepository = systemPlanRepository;
        this.systemPlanMapper = systemPlanMapper;
    }

    @Override
    public SystemPlanDTO save(SystemPlanDTO systemPlanDTO) {
        LOG.debug("Request to save SystemPlan : {}", systemPlanDTO);
        SystemPlan systemPlan = systemPlanMapper.toEntity(systemPlanDTO);
        systemPlan = systemPlanRepository.save(systemPlan);
        return systemPlanMapper.toDto(systemPlan);
    }

    @Override
    public SystemPlanDTO update(SystemPlanDTO systemPlanDTO) {
        LOG.debug("Request to update SystemPlan : {}", systemPlanDTO);
        SystemPlan systemPlan = systemPlanMapper.toEntity(systemPlanDTO);
        systemPlan = systemPlanRepository.save(systemPlan);
        return systemPlanMapper.toDto(systemPlan);
    }

    @Override
    public Optional<SystemPlanDTO> partialUpdate(SystemPlanDTO systemPlanDTO) {
        LOG.debug("Request to partially update SystemPlan : {}", systemPlanDTO);

        return systemPlanRepository
            .findById(systemPlanDTO.getId())
            .map(existingSystemPlan -> {
                systemPlanMapper.partialUpdate(existingSystemPlan, systemPlanDTO);

                return existingSystemPlan;
            })
            .map(systemPlanRepository::save)
            .map(systemPlanMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SystemPlanDTO> findOne(Long id) {
        LOG.debug("Request to get SystemPlan : {}", id);
        return systemPlanRepository.findById(id).map(systemPlanMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete SystemPlan : {}", id);
        systemPlanRepository.deleteById(id);
    }
}
