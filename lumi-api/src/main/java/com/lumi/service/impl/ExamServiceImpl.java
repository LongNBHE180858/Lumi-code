package com.lumi.service.impl;

import com.lumi.domain.Exam;
import com.lumi.repository.ExamRepository;
import com.lumi.service.ExamService;
import com.lumi.service.dto.ExamDTO;
import com.lumi.service.mapper.ExamMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.Exam}.
 */
@Service
@Transactional
public class ExamServiceImpl implements ExamService {

    private static final Logger LOG = LoggerFactory.getLogger(ExamServiceImpl.class);

    private final ExamRepository examRepository;

    private final ExamMapper examMapper;

    public ExamServiceImpl(ExamRepository examRepository, ExamMapper examMapper) {
        this.examRepository = examRepository;
        this.examMapper = examMapper;
    }

    @Override
    public ExamDTO save(ExamDTO examDTO) {
        LOG.debug("Request to save Exam : {}", examDTO);
        Exam exam = examMapper.toEntity(examDTO);
        exam = examRepository.save(exam);
        return examMapper.toDto(exam);
    }

    @Override
    public ExamDTO update(ExamDTO examDTO) {
        LOG.debug("Request to update Exam : {}", examDTO);
        Exam exam = examMapper.toEntity(examDTO);
        exam = examRepository.save(exam);
        return examMapper.toDto(exam);
    }

    @Override
    public Optional<ExamDTO> partialUpdate(ExamDTO examDTO) {
        LOG.debug("Request to partially update Exam : {}", examDTO);

        return examRepository
            .findById(examDTO.getId())
            .map(existingExam -> {
                examMapper.partialUpdate(existingExam, examDTO);

                return existingExam;
            })
            .map(examRepository::save)
            .map(examMapper::toDto);
    }

    public Page<ExamDTO> findAllWithEagerRelationships(Pageable pageable) {
        return examRepository.findAllWithEagerRelationships(pageable).map(examMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ExamDTO> findOne(Long id) {
        LOG.debug("Request to get Exam : {}", id);
        return examRepository.findOneWithEagerRelationships(id).map(examMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Exam : {}", id);
        examRepository.deleteById(id);
    }
}
