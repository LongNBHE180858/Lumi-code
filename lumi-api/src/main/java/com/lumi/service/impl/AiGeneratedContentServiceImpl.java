package com.lumi.service.impl;

import com.lumi.domain.AiGeneratedContent;
import com.lumi.repository.AiGeneratedContentRepository;
import com.lumi.service.AiGeneratedContentService;
import com.lumi.service.dto.AiGeneratedContentDTO;
import com.lumi.service.mapper.AiGeneratedContentMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.AiGeneratedContent}.
 */
@Service
@Transactional
public class AiGeneratedContentServiceImpl implements AiGeneratedContentService {

    private static final Logger LOG = LoggerFactory.getLogger(AiGeneratedContentServiceImpl.class);

    private final AiGeneratedContentRepository aiGeneratedContentRepository;

    private final AiGeneratedContentMapper aiGeneratedContentMapper;

    public AiGeneratedContentServiceImpl(
        AiGeneratedContentRepository aiGeneratedContentRepository,
        AiGeneratedContentMapper aiGeneratedContentMapper
    ) {
        this.aiGeneratedContentRepository = aiGeneratedContentRepository;
        this.aiGeneratedContentMapper = aiGeneratedContentMapper;
    }

    @Override
    public AiGeneratedContentDTO save(AiGeneratedContentDTO aiGeneratedContentDTO) {
        LOG.debug("Request to save AiGeneratedContent : {}", aiGeneratedContentDTO);
        AiGeneratedContent aiGeneratedContent = aiGeneratedContentMapper.toEntity(aiGeneratedContentDTO);
        aiGeneratedContent = aiGeneratedContentRepository.save(aiGeneratedContent);
        return aiGeneratedContentMapper.toDto(aiGeneratedContent);
    }

    @Override
    public AiGeneratedContentDTO update(AiGeneratedContentDTO aiGeneratedContentDTO) {
        LOG.debug("Request to update AiGeneratedContent : {}", aiGeneratedContentDTO);
        AiGeneratedContent aiGeneratedContent = aiGeneratedContentMapper.toEntity(aiGeneratedContentDTO);
        aiGeneratedContent = aiGeneratedContentRepository.save(aiGeneratedContent);
        return aiGeneratedContentMapper.toDto(aiGeneratedContent);
    }

    @Override
    public Optional<AiGeneratedContentDTO> partialUpdate(AiGeneratedContentDTO aiGeneratedContentDTO) {
        LOG.debug("Request to partially update AiGeneratedContent : {}", aiGeneratedContentDTO);

        return aiGeneratedContentRepository
            .findById(aiGeneratedContentDTO.getId())
            .map(existingAiGeneratedContent -> {
                aiGeneratedContentMapper.partialUpdate(existingAiGeneratedContent, aiGeneratedContentDTO);

                return existingAiGeneratedContent;
            })
            .map(aiGeneratedContentRepository::save)
            .map(aiGeneratedContentMapper::toDto);
    }

    public Page<AiGeneratedContentDTO> findAllWithEagerRelationships(Pageable pageable) {
        return aiGeneratedContentRepository.findAllWithEagerRelationships(pageable).map(aiGeneratedContentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AiGeneratedContentDTO> findOne(Long id) {
        LOG.debug("Request to get AiGeneratedContent : {}", id);
        return aiGeneratedContentRepository.findOneWithEagerRelationships(id).map(aiGeneratedContentMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete AiGeneratedContent : {}", id);
        aiGeneratedContentRepository.deleteById(id);
    }
}
