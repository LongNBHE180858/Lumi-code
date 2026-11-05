package com.lumi.service.impl;

import com.lumi.domain.QuestionKnowledgeLink;
import com.lumi.repository.QuestionKnowledgeLinkRepository;
import com.lumi.service.QuestionKnowledgeLinkService;
import com.lumi.service.dto.QuestionKnowledgeLinkDTO;
import com.lumi.service.mapper.QuestionKnowledgeLinkMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.QuestionKnowledgeLink}.
 */
@Service
@Transactional
public class QuestionKnowledgeLinkServiceImpl implements QuestionKnowledgeLinkService {

    private static final Logger LOG = LoggerFactory.getLogger(QuestionKnowledgeLinkServiceImpl.class);

    private final QuestionKnowledgeLinkRepository questionKnowledgeLinkRepository;

    private final QuestionKnowledgeLinkMapper questionKnowledgeLinkMapper;

    public QuestionKnowledgeLinkServiceImpl(
        QuestionKnowledgeLinkRepository questionKnowledgeLinkRepository,
        QuestionKnowledgeLinkMapper questionKnowledgeLinkMapper
    ) {
        this.questionKnowledgeLinkRepository = questionKnowledgeLinkRepository;
        this.questionKnowledgeLinkMapper = questionKnowledgeLinkMapper;
    }

    @Override
    public QuestionKnowledgeLinkDTO save(QuestionKnowledgeLinkDTO questionKnowledgeLinkDTO) {
        LOG.debug("Request to save QuestionKnowledgeLink : {}", questionKnowledgeLinkDTO);
        QuestionKnowledgeLink questionKnowledgeLink = questionKnowledgeLinkMapper.toEntity(questionKnowledgeLinkDTO);
        questionKnowledgeLink = questionKnowledgeLinkRepository.save(questionKnowledgeLink);
        return questionKnowledgeLinkMapper.toDto(questionKnowledgeLink);
    }

    @Override
    public QuestionKnowledgeLinkDTO update(QuestionKnowledgeLinkDTO questionKnowledgeLinkDTO) {
        LOG.debug("Request to update QuestionKnowledgeLink : {}", questionKnowledgeLinkDTO);
        QuestionKnowledgeLink questionKnowledgeLink = questionKnowledgeLinkMapper.toEntity(questionKnowledgeLinkDTO);
        questionKnowledgeLink = questionKnowledgeLinkRepository.save(questionKnowledgeLink);
        return questionKnowledgeLinkMapper.toDto(questionKnowledgeLink);
    }

    @Override
    public Optional<QuestionKnowledgeLinkDTO> partialUpdate(QuestionKnowledgeLinkDTO questionKnowledgeLinkDTO) {
        LOG.debug("Request to partially update QuestionKnowledgeLink : {}", questionKnowledgeLinkDTO);

        return questionKnowledgeLinkRepository
            .findById(questionKnowledgeLinkDTO.getId())
            .map(existingQuestionKnowledgeLink -> {
                questionKnowledgeLinkMapper.partialUpdate(existingQuestionKnowledgeLink, questionKnowledgeLinkDTO);

                return existingQuestionKnowledgeLink;
            })
            .map(questionKnowledgeLinkRepository::save)
            .map(questionKnowledgeLinkMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<QuestionKnowledgeLinkDTO> findOne(Long id) {
        LOG.debug("Request to get QuestionKnowledgeLink : {}", id);
        return questionKnowledgeLinkRepository.findById(id).map(questionKnowledgeLinkMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete QuestionKnowledgeLink : {}", id);
        questionKnowledgeLinkRepository.deleteById(id);
    }
}
