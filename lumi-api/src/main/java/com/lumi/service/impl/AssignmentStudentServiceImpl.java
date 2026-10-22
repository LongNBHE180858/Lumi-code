package com.lumi.service.impl;

import com.lumi.domain.AssignmentStudent;
import com.lumi.repository.AssignmentStudentRepository;
import com.lumi.service.AssignmentStudentService;
import com.lumi.service.dto.AssignmentStudentDTO;
import com.lumi.service.mapper.AssignmentStudentMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.AssignmentStudent}.
 */
@Service
@Transactional
public class AssignmentStudentServiceImpl implements AssignmentStudentService {

    private static final Logger LOG = LoggerFactory.getLogger(AssignmentStudentServiceImpl.class);

    private final AssignmentStudentRepository assignmentStudentRepository;

    private final AssignmentStudentMapper assignmentStudentMapper;

    public AssignmentStudentServiceImpl(
        AssignmentStudentRepository assignmentStudentRepository,
        AssignmentStudentMapper assignmentStudentMapper
    ) {
        this.assignmentStudentRepository = assignmentStudentRepository;
        this.assignmentStudentMapper = assignmentStudentMapper;
    }

    @Override
    public AssignmentStudentDTO save(AssignmentStudentDTO assignmentStudentDTO) {
        LOG.debug("Request to save AssignmentStudent : {}", assignmentStudentDTO);
        AssignmentStudent assignmentStudent = assignmentStudentMapper.toEntity(assignmentStudentDTO);
        assignmentStudent = assignmentStudentRepository.save(assignmentStudent);
        return assignmentStudentMapper.toDto(assignmentStudent);
    }

    @Override
    public AssignmentStudentDTO update(AssignmentStudentDTO assignmentStudentDTO) {
        LOG.debug("Request to update AssignmentStudent : {}", assignmentStudentDTO);
        AssignmentStudent assignmentStudent = assignmentStudentMapper.toEntity(assignmentStudentDTO);
        assignmentStudent = assignmentStudentRepository.save(assignmentStudent);
        return assignmentStudentMapper.toDto(assignmentStudent);
    }

    @Override
    public Optional<AssignmentStudentDTO> partialUpdate(AssignmentStudentDTO assignmentStudentDTO) {
        LOG.debug("Request to partially update AssignmentStudent : {}", assignmentStudentDTO);

        return assignmentStudentRepository
            .findById(assignmentStudentDTO.getId())
            .map(existingAssignmentStudent -> {
                assignmentStudentMapper.partialUpdate(existingAssignmentStudent, assignmentStudentDTO);

                return existingAssignmentStudent;
            })
            .map(assignmentStudentRepository::save)
            .map(assignmentStudentMapper::toDto);
    }

    public Page<AssignmentStudentDTO> findAllWithEagerRelationships(Pageable pageable) {
        return assignmentStudentRepository.findAllWithEagerRelationships(pageable).map(assignmentStudentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AssignmentStudentDTO> findOne(Long id) {
        LOG.debug("Request to get AssignmentStudent : {}", id);
        return assignmentStudentRepository.findOneWithEagerRelationships(id).map(assignmentStudentMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete AssignmentStudent : {}", id);
        assignmentStudentRepository.deleteById(id);
    }
}
