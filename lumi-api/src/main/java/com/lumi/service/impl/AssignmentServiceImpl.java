package com.lumi.service.impl;

import com.lumi.domain.Assignment;
import com.lumi.repository.AssignmentRepository;
import com.lumi.service.AssignmentService;
import com.lumi.service.dto.AssignmentDTO;
import com.lumi.service.mapper.AssignmentMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.Assignment}.
 */
@Service
@Transactional
public class AssignmentServiceImpl implements AssignmentService {

    private static final Logger LOG = LoggerFactory.getLogger(AssignmentServiceImpl.class);

    private final AssignmentRepository assignmentRepository;

    private final AssignmentMapper assignmentMapper;

    public AssignmentServiceImpl(AssignmentRepository assignmentRepository, AssignmentMapper assignmentMapper) {
        this.assignmentRepository = assignmentRepository;
        this.assignmentMapper = assignmentMapper;
    }

    @Override
    public AssignmentDTO save(AssignmentDTO assignmentDTO) {
        LOG.debug("Request to save Assignment : {}", assignmentDTO);
        Assignment assignment = assignmentMapper.toEntity(assignmentDTO);
        assignment = assignmentRepository.save(assignment);
        return assignmentMapper.toDto(assignment);
    }

    @Override
    public AssignmentDTO update(AssignmentDTO assignmentDTO) {
        LOG.debug("Request to update Assignment : {}", assignmentDTO);
        Assignment assignment = assignmentMapper.toEntity(assignmentDTO);
        assignment = assignmentRepository.save(assignment);
        return assignmentMapper.toDto(assignment);
    }

    @Override
    public Optional<AssignmentDTO> partialUpdate(AssignmentDTO assignmentDTO) {
        LOG.debug("Request to partially update Assignment : {}", assignmentDTO);

        return assignmentRepository
            .findById(assignmentDTO.getId())
            .map(existingAssignment -> {
                assignmentMapper.partialUpdate(existingAssignment, assignmentDTO);

                return existingAssignment;
            })
            .map(assignmentRepository::save)
            .map(assignmentMapper::toDto);
    }

    public Page<AssignmentDTO> findAllWithEagerRelationships(Pageable pageable) {
        return assignmentRepository.findAllWithEagerRelationships(pageable).map(assignmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AssignmentDTO> findOne(Long id) {
        LOG.debug("Request to get Assignment : {}", id);
        return assignmentRepository.findOneWithEagerRelationships(id).map(assignmentMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Assignment : {}", id);
        assignmentRepository.deleteById(id);
    }
}
