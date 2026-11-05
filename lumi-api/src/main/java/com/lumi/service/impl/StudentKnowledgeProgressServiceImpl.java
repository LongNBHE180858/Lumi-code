package com.lumi.service.impl;

import com.lumi.domain.StudentKnowledgeProgress;
import com.lumi.repository.StudentKnowledgeProgressRepository;
import com.lumi.service.StudentKnowledgeProgressService;
import com.lumi.service.dto.StudentKnowledgeProgressDTO;
import com.lumi.service.mapper.StudentKnowledgeProgressMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.StudentKnowledgeProgress}.
 */
@Service
@Transactional
public class StudentKnowledgeProgressServiceImpl implements StudentKnowledgeProgressService {

    private static final Logger LOG = LoggerFactory.getLogger(StudentKnowledgeProgressServiceImpl.class);

    private final StudentKnowledgeProgressRepository studentKnowledgeProgressRepository;

    private final StudentKnowledgeProgressMapper studentKnowledgeProgressMapper;

    public StudentKnowledgeProgressServiceImpl(
        StudentKnowledgeProgressRepository studentKnowledgeProgressRepository,
        StudentKnowledgeProgressMapper studentKnowledgeProgressMapper
    ) {
        this.studentKnowledgeProgressRepository = studentKnowledgeProgressRepository;
        this.studentKnowledgeProgressMapper = studentKnowledgeProgressMapper;
    }

    @Override
    public StudentKnowledgeProgressDTO save(StudentKnowledgeProgressDTO studentKnowledgeProgressDTO) {
        LOG.debug("Request to save StudentKnowledgeProgress : {}", studentKnowledgeProgressDTO);
        StudentKnowledgeProgress studentKnowledgeProgress = studentKnowledgeProgressMapper.toEntity(studentKnowledgeProgressDTO);
        studentKnowledgeProgress = studentKnowledgeProgressRepository.save(studentKnowledgeProgress);
        return studentKnowledgeProgressMapper.toDto(studentKnowledgeProgress);
    }

    @Override
    public StudentKnowledgeProgressDTO update(StudentKnowledgeProgressDTO studentKnowledgeProgressDTO) {
        LOG.debug("Request to update StudentKnowledgeProgress : {}", studentKnowledgeProgressDTO);
        StudentKnowledgeProgress studentKnowledgeProgress = studentKnowledgeProgressMapper.toEntity(studentKnowledgeProgressDTO);
        studentKnowledgeProgress = studentKnowledgeProgressRepository.save(studentKnowledgeProgress);
        return studentKnowledgeProgressMapper.toDto(studentKnowledgeProgress);
    }

    @Override
    public Optional<StudentKnowledgeProgressDTO> partialUpdate(StudentKnowledgeProgressDTO studentKnowledgeProgressDTO) {
        LOG.debug("Request to partially update StudentKnowledgeProgress : {}", studentKnowledgeProgressDTO);

        return studentKnowledgeProgressRepository
            .findById(studentKnowledgeProgressDTO.getId())
            .map(existingStudentKnowledgeProgress -> {
                studentKnowledgeProgressMapper.partialUpdate(existingStudentKnowledgeProgress, studentKnowledgeProgressDTO);

                return existingStudentKnowledgeProgress;
            })
            .map(studentKnowledgeProgressRepository::save)
            .map(studentKnowledgeProgressMapper::toDto);
    }

    public Page<StudentKnowledgeProgressDTO> findAllWithEagerRelationships(Pageable pageable) {
        return studentKnowledgeProgressRepository.findAllWithEagerRelationships(pageable).map(studentKnowledgeProgressMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StudentKnowledgeProgressDTO> findOne(Long id) {
        LOG.debug("Request to get StudentKnowledgeProgress : {}", id);
        return studentKnowledgeProgressRepository.findOneWithEagerRelationships(id).map(studentKnowledgeProgressMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete StudentKnowledgeProgress : {}", id);
        studentKnowledgeProgressRepository.deleteById(id);
    }
}
