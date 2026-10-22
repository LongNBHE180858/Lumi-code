package com.lumi.service.impl;

import com.lumi.domain.TeacherVerification;
import com.lumi.repository.TeacherVerificationRepository;
import com.lumi.service.TeacherVerificationService;
import com.lumi.service.dto.TeacherVerificationDTO;
import com.lumi.service.mapper.TeacherVerificationMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.TeacherVerification}.
 */
@Service
@Transactional
public class TeacherVerificationServiceImpl implements TeacherVerificationService {

    private static final Logger LOG = LoggerFactory.getLogger(TeacherVerificationServiceImpl.class);

    private final TeacherVerificationRepository teacherVerificationRepository;

    private final TeacherVerificationMapper teacherVerificationMapper;

    public TeacherVerificationServiceImpl(
        TeacherVerificationRepository teacherVerificationRepository,
        TeacherVerificationMapper teacherVerificationMapper
    ) {
        this.teacherVerificationRepository = teacherVerificationRepository;
        this.teacherVerificationMapper = teacherVerificationMapper;
    }

    @Override
    public TeacherVerificationDTO save(TeacherVerificationDTO teacherVerificationDTO) {
        LOG.debug("Request to save TeacherVerification : {}", teacherVerificationDTO);
        TeacherVerification teacherVerification = teacherVerificationMapper.toEntity(teacherVerificationDTO);
        teacherVerification = teacherVerificationRepository.save(teacherVerification);
        return teacherVerificationMapper.toDto(teacherVerification);
    }

    @Override
    public TeacherVerificationDTO update(TeacherVerificationDTO teacherVerificationDTO) {
        LOG.debug("Request to update TeacherVerification : {}", teacherVerificationDTO);
        TeacherVerification teacherVerification = teacherVerificationMapper.toEntity(teacherVerificationDTO);
        teacherVerification = teacherVerificationRepository.save(teacherVerification);
        return teacherVerificationMapper.toDto(teacherVerification);
    }

    @Override
    public Optional<TeacherVerificationDTO> partialUpdate(TeacherVerificationDTO teacherVerificationDTO) {
        LOG.debug("Request to partially update TeacherVerification : {}", teacherVerificationDTO);

        return teacherVerificationRepository
            .findById(teacherVerificationDTO.getId())
            .map(existingTeacherVerification -> {
                teacherVerificationMapper.partialUpdate(existingTeacherVerification, teacherVerificationDTO);

                return existingTeacherVerification;
            })
            .map(teacherVerificationRepository::save)
            .map(teacherVerificationMapper::toDto);
    }

    public Page<TeacherVerificationDTO> findAllWithEagerRelationships(Pageable pageable) {
        return teacherVerificationRepository.findAllWithEagerRelationships(pageable).map(teacherVerificationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TeacherVerificationDTO> findOne(Long id) {
        LOG.debug("Request to get TeacherVerification : {}", id);
        return teacherVerificationRepository.findOneWithEagerRelationships(id).map(teacherVerificationMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete TeacherVerification : {}", id);
        teacherVerificationRepository.deleteById(id);
    }
}
