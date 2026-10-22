package com.lumi.service.impl;

import com.lumi.domain.QuestionOption;
import com.lumi.repository.QuestionOptionRepository;
import com.lumi.service.QuestionOptionService;
import com.lumi.service.dto.QuestionOptionDTO;
import com.lumi.service.mapper.QuestionOptionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.QuestionOption}.
 */
@Service
@Transactional
public class QuestionOptionServiceImpl implements QuestionOptionService {

    private static final Logger LOG = LoggerFactory.getLogger(QuestionOptionServiceImpl.class);

    private final QuestionOptionRepository questionOptionRepository;

    private final QuestionOptionMapper questionOptionMapper;

    public QuestionOptionServiceImpl(QuestionOptionRepository questionOptionRepository, QuestionOptionMapper questionOptionMapper) {
        this.questionOptionRepository = questionOptionRepository;
        this.questionOptionMapper = questionOptionMapper;
    }

    @Override
    public QuestionOptionDTO save(QuestionOptionDTO questionOptionDTO) {
        LOG.debug("Request to save QuestionOption : {}", questionOptionDTO);
        QuestionOption questionOption = questionOptionMapper.toEntity(questionOptionDTO);
        questionOption = questionOptionRepository.save(questionOption);
        return questionOptionMapper.toDto(questionOption);
    }

    @Override
    public QuestionOptionDTO update(QuestionOptionDTO questionOptionDTO) {
        LOG.debug("Request to update QuestionOption : {}", questionOptionDTO);
        QuestionOption questionOption = questionOptionMapper.toEntity(questionOptionDTO);
        questionOption = questionOptionRepository.save(questionOption);
        return questionOptionMapper.toDto(questionOption);
    }

    @Override
    public Optional<QuestionOptionDTO> partialUpdate(QuestionOptionDTO questionOptionDTO) {
        LOG.debug("Request to partially update QuestionOption : {}", questionOptionDTO);

        return questionOptionRepository
            .findById(questionOptionDTO.getId())
            .map(existingQuestionOption -> {
                questionOptionMapper.partialUpdate(existingQuestionOption, questionOptionDTO);

                return existingQuestionOption;
            })
            .map(questionOptionRepository::save)
            .map(questionOptionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<QuestionOptionDTO> findOne(Long id) {
        LOG.debug("Request to get QuestionOption : {}", id);
        return questionOptionRepository.findById(id).map(questionOptionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete QuestionOption : {}", id);
        questionOptionRepository.deleteById(id);
    }
}
