package com.lumi.service.impl;

import com.lumi.domain.Question;
import com.lumi.repository.QuestionRepository;
import com.lumi.service.QuestionService;
import com.lumi.service.dto.QuestionDTO;
import com.lumi.service.mapper.QuestionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.Question}.
 */
@Service
@Transactional
public class QuestionServiceImpl implements QuestionService {

    private static final Logger LOG = LoggerFactory.getLogger(QuestionServiceImpl.class);

    private final QuestionRepository questionRepository;

    private final QuestionMapper questionMapper;

    public QuestionServiceImpl(QuestionRepository questionRepository, QuestionMapper questionMapper) {
        this.questionRepository = questionRepository;
        this.questionMapper = questionMapper;
    }

    @Override
    public QuestionDTO save(QuestionDTO questionDTO) {
        LOG.debug("Request to save Question : {}", questionDTO);
        Question question = questionMapper.toEntity(questionDTO);
        question = questionRepository.save(question);
        return questionMapper.toDto(question);
    }

    @Override
    public QuestionDTO update(QuestionDTO questionDTO) {
        LOG.debug("Request to update Question : {}", questionDTO);
        Question question = questionMapper.toEntity(questionDTO);
        question = questionRepository.save(question);
        return questionMapper.toDto(question);
    }

    @Override
    public Optional<QuestionDTO> partialUpdate(QuestionDTO questionDTO) {
        LOG.debug("Request to partially update Question : {}", questionDTO);

        return questionRepository
            .findById(questionDTO.getId())
            .map(existingQuestion -> {
                questionMapper.partialUpdate(existingQuestion, questionDTO);

                return existingQuestion;
            })
            .map(questionRepository::save)
            .map(questionMapper::toDto);
    }

    public Page<QuestionDTO> findAllWithEagerRelationships(Pageable pageable) {
        return questionRepository.findAllWithEagerRelationships(pageable).map(questionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<QuestionDTO> findOne(Long id) {
        LOG.debug("Request to get Question : {}", id);
        return questionRepository.findOneWithEagerRelationships(id).map(questionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Question : {}", id);
        questionRepository.deleteById(id);
    }
}
