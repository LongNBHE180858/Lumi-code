package com.lumi.service.impl;

import com.lumi.domain.ExamAttempt;
import com.lumi.repository.ExamAttemptRepository;
import com.lumi.service.ExamAttemptService;
import com.lumi.service.dto.ExamAttemptDTO;
import com.lumi.service.mapper.ExamAttemptMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.ExamAttempt}.
 */
@Service
@Transactional
public class ExamAttemptServiceImpl implements ExamAttemptService {

    private static final Logger LOG = LoggerFactory.getLogger(ExamAttemptServiceImpl.class);

    private final ExamAttemptRepository examAttemptRepository;

    private final ExamAttemptMapper examAttemptMapper;

    public ExamAttemptServiceImpl(ExamAttemptRepository examAttemptRepository, ExamAttemptMapper examAttemptMapper) {
        this.examAttemptRepository = examAttemptRepository;
        this.examAttemptMapper = examAttemptMapper;
    }

    @Override
    public ExamAttemptDTO save(ExamAttemptDTO examAttemptDTO) {
        LOG.debug("Request to save ExamAttempt : {}", examAttemptDTO);
        ExamAttempt examAttempt = examAttemptMapper.toEntity(examAttemptDTO);
        examAttempt = examAttemptRepository.save(examAttempt);
        return examAttemptMapper.toDto(examAttempt);
    }

    @Override
    public ExamAttemptDTO update(ExamAttemptDTO examAttemptDTO) {
        LOG.debug("Request to update ExamAttempt : {}", examAttemptDTO);
        ExamAttempt examAttempt = examAttemptMapper.toEntity(examAttemptDTO);
        examAttempt = examAttemptRepository.save(examAttempt);
        return examAttemptMapper.toDto(examAttempt);
    }

    @Override
    public Optional<ExamAttemptDTO> partialUpdate(ExamAttemptDTO examAttemptDTO) {
        LOG.debug("Request to partially update ExamAttempt : {}", examAttemptDTO);

        return examAttemptRepository
            .findById(examAttemptDTO.getId())
            .map(existingExamAttempt -> {
                examAttemptMapper.partialUpdate(existingExamAttempt, examAttemptDTO);

                return existingExamAttempt;
            })
            .map(examAttemptRepository::save)
            .map(examAttemptMapper::toDto);
    }

    public Page<ExamAttemptDTO> findAllWithEagerRelationships(Pageable pageable) {
        return examAttemptRepository.findAllWithEagerRelationships(pageable).map(examAttemptMapper::toDto);
    }

    /**
     *  Get all the examAttempts where ProctorSession is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ExamAttemptDTO> findAllWhereProctorSessionIsNull() {
        LOG.debug("Request to get all examAttempts where ProctorSession is null");
        return StreamSupport.stream(examAttemptRepository.findAll().spliterator(), false)
            .filter(examAttempt -> examAttempt.getProctorSession() == null)
            .map(examAttemptMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ExamAttemptDTO> findOne(Long id) {
        LOG.debug("Request to get ExamAttempt : {}", id);
        return examAttemptRepository.findOneWithEagerRelationships(id).map(examAttemptMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ExamAttempt : {}", id);
        examAttemptRepository.deleteById(id);
    }
}
