package com.lumi.service.impl;

import com.lumi.domain.AssignmentSubmission;
import com.lumi.repository.AssignmentSubmissionRepository;
import com.lumi.service.AssignmentSubmissionService;
import com.lumi.service.dto.AssignmentSubmissionDTO;
import com.lumi.service.mapper.AssignmentSubmissionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.AssignmentSubmission}.
 */
@Service
@Transactional
public class AssignmentSubmissionServiceImpl implements AssignmentSubmissionService {

    private static final Logger LOG = LoggerFactory.getLogger(AssignmentSubmissionServiceImpl.class);

    private final AssignmentSubmissionRepository assignmentSubmissionRepository;

    private final AssignmentSubmissionMapper assignmentSubmissionMapper;

    public AssignmentSubmissionServiceImpl(
        AssignmentSubmissionRepository assignmentSubmissionRepository,
        AssignmentSubmissionMapper assignmentSubmissionMapper
    ) {
        this.assignmentSubmissionRepository = assignmentSubmissionRepository;
        this.assignmentSubmissionMapper = assignmentSubmissionMapper;
    }

    @Override
    public AssignmentSubmissionDTO save(AssignmentSubmissionDTO assignmentSubmissionDTO) {
        LOG.debug("Request to save AssignmentSubmission : {}", assignmentSubmissionDTO);
        AssignmentSubmission assignmentSubmission = assignmentSubmissionMapper.toEntity(assignmentSubmissionDTO);
        assignmentSubmission = assignmentSubmissionRepository.save(assignmentSubmission);
        return assignmentSubmissionMapper.toDto(assignmentSubmission);
    }

    @Override
    public AssignmentSubmissionDTO update(AssignmentSubmissionDTO assignmentSubmissionDTO) {
        LOG.debug("Request to update AssignmentSubmission : {}", assignmentSubmissionDTO);
        AssignmentSubmission assignmentSubmission = assignmentSubmissionMapper.toEntity(assignmentSubmissionDTO);
        assignmentSubmission = assignmentSubmissionRepository.save(assignmentSubmission);
        return assignmentSubmissionMapper.toDto(assignmentSubmission);
    }

    @Override
    public Optional<AssignmentSubmissionDTO> partialUpdate(AssignmentSubmissionDTO assignmentSubmissionDTO) {
        LOG.debug("Request to partially update AssignmentSubmission : {}", assignmentSubmissionDTO);

        return assignmentSubmissionRepository
            .findById(assignmentSubmissionDTO.getId())
            .map(existingAssignmentSubmission -> {
                assignmentSubmissionMapper.partialUpdate(existingAssignmentSubmission, assignmentSubmissionDTO);

                return existingAssignmentSubmission;
            })
            .map(assignmentSubmissionRepository::save)
            .map(assignmentSubmissionMapper::toDto);
    }

    public Page<AssignmentSubmissionDTO> findAllWithEagerRelationships(Pageable pageable) {
        return assignmentSubmissionRepository.findAllWithEagerRelationships(pageable).map(assignmentSubmissionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AssignmentSubmissionDTO> findOne(Long id) {
        LOG.debug("Request to get AssignmentSubmission : {}", id);
        return assignmentSubmissionRepository.findOneWithEagerRelationships(id).map(assignmentSubmissionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete AssignmentSubmission : {}", id);
        assignmentSubmissionRepository.deleteById(id);
    }
}
