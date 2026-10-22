package com.lumi.service.impl;

import com.lumi.domain.QuestionBank;
import com.lumi.repository.QuestionBankRepository;
import com.lumi.service.QuestionBankService;
import com.lumi.service.dto.QuestionBankDTO;
import com.lumi.service.mapper.QuestionBankMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.QuestionBank}.
 */
@Service
@Transactional
public class QuestionBankServiceImpl implements QuestionBankService {

    private static final Logger LOG = LoggerFactory.getLogger(QuestionBankServiceImpl.class);

    private final QuestionBankRepository questionBankRepository;

    private final QuestionBankMapper questionBankMapper;

    public QuestionBankServiceImpl(QuestionBankRepository questionBankRepository, QuestionBankMapper questionBankMapper) {
        this.questionBankRepository = questionBankRepository;
        this.questionBankMapper = questionBankMapper;
    }

    @Override
    public QuestionBankDTO save(QuestionBankDTO questionBankDTO) {
        LOG.debug("Request to save QuestionBank : {}", questionBankDTO);
        QuestionBank questionBank = questionBankMapper.toEntity(questionBankDTO);
        questionBank = questionBankRepository.save(questionBank);
        return questionBankMapper.toDto(questionBank);
    }

    @Override
    public QuestionBankDTO update(QuestionBankDTO questionBankDTO) {
        LOG.debug("Request to update QuestionBank : {}", questionBankDTO);
        QuestionBank questionBank = questionBankMapper.toEntity(questionBankDTO);
        questionBank = questionBankRepository.save(questionBank);
        return questionBankMapper.toDto(questionBank);
    }

    @Override
    public Optional<QuestionBankDTO> partialUpdate(QuestionBankDTO questionBankDTO) {
        LOG.debug("Request to partially update QuestionBank : {}", questionBankDTO);

        return questionBankRepository
            .findById(questionBankDTO.getId())
            .map(existingQuestionBank -> {
                questionBankMapper.partialUpdate(existingQuestionBank, questionBankDTO);

                return existingQuestionBank;
            })
            .map(questionBankRepository::save)
            .map(questionBankMapper::toDto);
    }

    public Page<QuestionBankDTO> findAllWithEagerRelationships(Pageable pageable) {
        return questionBankRepository.findAllWithEagerRelationships(pageable).map(questionBankMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<QuestionBankDTO> findOne(Long id) {
        LOG.debug("Request to get QuestionBank : {}", id);
        return questionBankRepository.findOneWithEagerRelationships(id).map(questionBankMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete QuestionBank : {}", id);
        questionBankRepository.deleteById(id);
    }
}
