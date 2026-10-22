package com.lumi.service.impl;

import com.lumi.domain.StudyClass;
import com.lumi.repository.StudyClassRepository;
import com.lumi.service.StudyClassService;
import com.lumi.service.dto.StudyClassDTO;
import com.lumi.service.mapper.StudyClassMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.StudyClass}.
 */
@Service
@Transactional
public class StudyClassServiceImpl implements StudyClassService {

    private static final Logger LOG = LoggerFactory.getLogger(StudyClassServiceImpl.class);

    private final StudyClassRepository studyClassRepository;

    private final StudyClassMapper studyClassMapper;

    public StudyClassServiceImpl(StudyClassRepository studyClassRepository, StudyClassMapper studyClassMapper) {
        this.studyClassRepository = studyClassRepository;
        this.studyClassMapper = studyClassMapper;
    }

    @Override
    public StudyClassDTO save(StudyClassDTO studyClassDTO) {
        LOG.debug("Request to save StudyClass : {}", studyClassDTO);
        StudyClass studyClass = studyClassMapper.toEntity(studyClassDTO);
        studyClass = studyClassRepository.save(studyClass);
        return studyClassMapper.toDto(studyClass);
    }

    @Override
    public StudyClassDTO update(StudyClassDTO studyClassDTO) {
        LOG.debug("Request to update StudyClass : {}", studyClassDTO);
        StudyClass studyClass = studyClassMapper.toEntity(studyClassDTO);
        studyClass = studyClassRepository.save(studyClass);
        return studyClassMapper.toDto(studyClass);
    }

    @Override
    public Optional<StudyClassDTO> partialUpdate(StudyClassDTO studyClassDTO) {
        LOG.debug("Request to partially update StudyClass : {}", studyClassDTO);

        return studyClassRepository
            .findById(studyClassDTO.getId())
            .map(existingStudyClass -> {
                studyClassMapper.partialUpdate(existingStudyClass, studyClassDTO);

                return existingStudyClass;
            })
            .map(studyClassRepository::save)
            .map(studyClassMapper::toDto);
    }

    public Page<StudyClassDTO> findAllWithEagerRelationships(Pageable pageable) {
        return studyClassRepository.findAllWithEagerRelationships(pageable).map(studyClassMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StudyClassDTO> findOne(Long id) {
        LOG.debug("Request to get StudyClass : {}", id);
        return studyClassRepository.findOneWithEagerRelationships(id).map(studyClassMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete StudyClass : {}", id);
        studyClassRepository.deleteById(id);
    }
}
