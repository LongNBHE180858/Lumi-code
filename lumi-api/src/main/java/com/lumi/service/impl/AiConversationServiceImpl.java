package com.lumi.service.impl;

import com.lumi.domain.AiConversation;
import com.lumi.repository.AiConversationRepository;
import com.lumi.service.AiConversationService;
import com.lumi.service.dto.AiConversationDTO;
import com.lumi.service.mapper.AiConversationMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.AiConversation}.
 */
@Service
@Transactional
public class AiConversationServiceImpl implements AiConversationService {

    private static final Logger LOG = LoggerFactory.getLogger(AiConversationServiceImpl.class);

    private final AiConversationRepository aiConversationRepository;

    private final AiConversationMapper aiConversationMapper;

    public AiConversationServiceImpl(AiConversationRepository aiConversationRepository, AiConversationMapper aiConversationMapper) {
        this.aiConversationRepository = aiConversationRepository;
        this.aiConversationMapper = aiConversationMapper;
    }

    @Override
    public AiConversationDTO save(AiConversationDTO aiConversationDTO) {
        LOG.debug("Request to save AiConversation : {}", aiConversationDTO);
        AiConversation aiConversation = aiConversationMapper.toEntity(aiConversationDTO);
        aiConversation = aiConversationRepository.save(aiConversation);
        return aiConversationMapper.toDto(aiConversation);
    }

    @Override
    public AiConversationDTO update(AiConversationDTO aiConversationDTO) {
        LOG.debug("Request to update AiConversation : {}", aiConversationDTO);
        AiConversation aiConversation = aiConversationMapper.toEntity(aiConversationDTO);
        aiConversation = aiConversationRepository.save(aiConversation);
        return aiConversationMapper.toDto(aiConversation);
    }

    @Override
    public Optional<AiConversationDTO> partialUpdate(AiConversationDTO aiConversationDTO) {
        LOG.debug("Request to partially update AiConversation : {}", aiConversationDTO);

        return aiConversationRepository
            .findById(aiConversationDTO.getId())
            .map(existingAiConversation -> {
                aiConversationMapper.partialUpdate(existingAiConversation, aiConversationDTO);

                return existingAiConversation;
            })
            .map(aiConversationRepository::save)
            .map(aiConversationMapper::toDto);
    }

    public Page<AiConversationDTO> findAllWithEagerRelationships(Pageable pageable) {
        return aiConversationRepository.findAllWithEagerRelationships(pageable).map(aiConversationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AiConversationDTO> findOne(Long id) {
        LOG.debug("Request to get AiConversation : {}", id);
        return aiConversationRepository.findOneWithEagerRelationships(id).map(aiConversationMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete AiConversation : {}", id);
        aiConversationRepository.deleteById(id);
    }
}
