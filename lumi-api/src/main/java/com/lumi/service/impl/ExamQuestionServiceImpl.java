package com.lumi.service.impl;

import com.lumi.domain.ExamQuestion;
import com.lumi.repository.ExamQuestionRepository;
import com.lumi.service.ExamQuestionService;
import com.lumi.service.dto.ExamQuestionDTO;
import com.lumi.service.mapper.ExamQuestionMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.ExamQuestion}.
 */
@Service
@Transactional
public class ExamQuestionServiceImpl implements ExamQuestionService {

    private static final Logger LOG = LoggerFactory.getLogger(ExamQuestionServiceImpl.class);

    private final ExamQuestionRepository examQuestionRepository;

    private final ExamQuestionMapper examQuestionMapper;

    public ExamQuestionServiceImpl(ExamQuestionRepository examQuestionRepository, ExamQuestionMapper examQuestionMapper) {
        this.examQuestionRepository = examQuestionRepository;
        this.examQuestionMapper = examQuestionMapper;
    }

    @Override
    public ExamQuestionDTO save(ExamQuestionDTO examQuestionDTO) {
        LOG.debug("Request to save ExamQuestion : {}", examQuestionDTO);
        ExamQuestion examQuestion = examQuestionMapper.toEntity(examQuestionDTO);
        examQuestion = examQuestionRepository.save(examQuestion);
        return examQuestionMapper.toDto(examQuestion);
    }

    @Override
    public ExamQuestionDTO update(ExamQuestionDTO examQuestionDTO) {
        LOG.debug("Request to update ExamQuestion : {}", examQuestionDTO);
        ExamQuestion examQuestion = examQuestionMapper.toEntity(examQuestionDTO);
        examQuestion = examQuestionRepository.save(examQuestion);
        return examQuestionMapper.toDto(examQuestion);
    }

    @Override
    public Optional<ExamQuestionDTO> partialUpdate(ExamQuestionDTO examQuestionDTO) {
        LOG.debug("Request to partially update ExamQuestion : {}", examQuestionDTO);

        return examQuestionRepository
            .findById(examQuestionDTO.getId())
            .map(existingExamQuestion -> {
                examQuestionMapper.partialUpdate(existingExamQuestion, examQuestionDTO);

                return existingExamQuestion;
            })
            .map(examQuestionRepository::save)
            .map(examQuestionMapper::toDto);
    }

    public List<ExamQuestionDTO> findAllWithEagerRelationships() {
        return examQuestionRepository
            .findAllWithEagerRelationships()
            .stream()
            .map(examQuestionMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ExamQuestionDTO> findOne(Long id) {
        LOG.debug("Request to get ExamQuestion : {}", id);
        return examQuestionRepository.findOneWithEagerRelationships(id).map(examQuestionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ExamQuestion : {}", id);
        examQuestionRepository.deleteById(id);
    }
}
