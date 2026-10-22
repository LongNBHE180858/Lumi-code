package com.lumi.service.impl;

import com.lumi.domain.TeacherQuota;
import com.lumi.repository.TeacherQuotaRepository;
import com.lumi.service.TeacherQuotaService;
import com.lumi.service.dto.TeacherQuotaDTO;
import com.lumi.service.mapper.TeacherQuotaMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.TeacherQuota}.
 */
@Service
@Transactional
public class TeacherQuotaServiceImpl implements TeacherQuotaService {

    private static final Logger LOG = LoggerFactory.getLogger(TeacherQuotaServiceImpl.class);

    private final TeacherQuotaRepository teacherQuotaRepository;

    private final TeacherQuotaMapper teacherQuotaMapper;

    public TeacherQuotaServiceImpl(TeacherQuotaRepository teacherQuotaRepository, TeacherQuotaMapper teacherQuotaMapper) {
        this.teacherQuotaRepository = teacherQuotaRepository;
        this.teacherQuotaMapper = teacherQuotaMapper;
    }

    @Override
    public TeacherQuotaDTO save(TeacherQuotaDTO teacherQuotaDTO) {
        LOG.debug("Request to save TeacherQuota : {}", teacherQuotaDTO);
        TeacherQuota teacherQuota = teacherQuotaMapper.toEntity(teacherQuotaDTO);
        teacherQuota = teacherQuotaRepository.save(teacherQuota);
        return teacherQuotaMapper.toDto(teacherQuota);
    }

    @Override
    public TeacherQuotaDTO update(TeacherQuotaDTO teacherQuotaDTO) {
        LOG.debug("Request to update TeacherQuota : {}", teacherQuotaDTO);
        TeacherQuota teacherQuota = teacherQuotaMapper.toEntity(teacherQuotaDTO);
        teacherQuota = teacherQuotaRepository.save(teacherQuota);
        return teacherQuotaMapper.toDto(teacherQuota);
    }

    @Override
    public Optional<TeacherQuotaDTO> partialUpdate(TeacherQuotaDTO teacherQuotaDTO) {
        LOG.debug("Request to partially update TeacherQuota : {}", teacherQuotaDTO);

        return teacherQuotaRepository
            .findById(teacherQuotaDTO.getId())
            .map(existingTeacherQuota -> {
                teacherQuotaMapper.partialUpdate(existingTeacherQuota, teacherQuotaDTO);

                return existingTeacherQuota;
            })
            .map(teacherQuotaRepository::save)
            .map(teacherQuotaMapper::toDto);
    }

    public List<TeacherQuotaDTO> findAllWithEagerRelationships() {
        return teacherQuotaRepository
            .findAllWithEagerRelationships()
            .stream()
            .map(teacherQuotaMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TeacherQuotaDTO> findOne(Long id) {
        LOG.debug("Request to get TeacherQuota : {}", id);
        return teacherQuotaRepository.findOneWithEagerRelationships(id).map(teacherQuotaMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete TeacherQuota : {}", id);
        teacherQuotaRepository.deleteById(id);
    }
}
