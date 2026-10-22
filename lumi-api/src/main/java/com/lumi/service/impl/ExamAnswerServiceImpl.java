package com.lumi.service.impl;

import com.lumi.domain.ExamAnswer;
import com.lumi.repository.ExamAnswerRepository;
import com.lumi.service.ExamAnswerService;
import com.lumi.service.dto.ExamAnswerDTO;
import com.lumi.service.mapper.ExamAnswerMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.ExamAnswer}.
 */
@Service
@Transactional
public class ExamAnswerServiceImpl implements ExamAnswerService {

    private static final Logger LOG = LoggerFactory.getLogger(ExamAnswerServiceImpl.class);

    private final ExamAnswerRepository examAnswerRepository;

    private final ExamAnswerMapper examAnswerMapper;

    public ExamAnswerServiceImpl(ExamAnswerRepository examAnswerRepository, ExamAnswerMapper examAnswerMapper) {
        this.examAnswerRepository = examAnswerRepository;
        this.examAnswerMapper = examAnswerMapper;
    }

    @Override
    public ExamAnswerDTO save(ExamAnswerDTO examAnswerDTO) {
        LOG.debug("Request to save ExamAnswer : {}", examAnswerDTO);
        ExamAnswer examAnswer = examAnswerMapper.toEntity(examAnswerDTO);
        examAnswer = examAnswerRepository.save(examAnswer);
        return examAnswerMapper.toDto(examAnswer);
    }

    @Override
    public ExamAnswerDTO update(ExamAnswerDTO examAnswerDTO) {
        LOG.debug("Request to update ExamAnswer : {}", examAnswerDTO);
        ExamAnswer examAnswer = examAnswerMapper.toEntity(examAnswerDTO);
        examAnswer = examAnswerRepository.save(examAnswer);
        return examAnswerMapper.toDto(examAnswer);
    }

    @Override
    public Optional<ExamAnswerDTO> partialUpdate(ExamAnswerDTO examAnswerDTO) {
        LOG.debug("Request to partially update ExamAnswer : {}", examAnswerDTO);

        return examAnswerRepository
            .findById(examAnswerDTO.getId())
            .map(existingExamAnswer -> {
                examAnswerMapper.partialUpdate(existingExamAnswer, examAnswerDTO);

                return existingExamAnswer;
            })
            .map(examAnswerRepository::save)
            .map(examAnswerMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ExamAnswerDTO> findOne(Long id) {
        LOG.debug("Request to get ExamAnswer : {}", id);
        return examAnswerRepository.findById(id).map(examAnswerMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ExamAnswer : {}", id);
        examAnswerRepository.deleteById(id);
    }
}
