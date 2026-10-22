package com.lumi.service.impl;

import com.lumi.domain.AiMessage;
import com.lumi.repository.AiMessageRepository;
import com.lumi.service.AiMessageService;
import com.lumi.service.dto.AiMessageDTO;
import com.lumi.service.mapper.AiMessageMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.AiMessage}.
 */
@Service
@Transactional
public class AiMessageServiceImpl implements AiMessageService {

    private static final Logger LOG = LoggerFactory.getLogger(AiMessageServiceImpl.class);

    private final AiMessageRepository aiMessageRepository;

    private final AiMessageMapper aiMessageMapper;

    public AiMessageServiceImpl(AiMessageRepository aiMessageRepository, AiMessageMapper aiMessageMapper) {
        this.aiMessageRepository = aiMessageRepository;
        this.aiMessageMapper = aiMessageMapper;
    }

    @Override
    public AiMessageDTO save(AiMessageDTO aiMessageDTO) {
        LOG.debug("Request to save AiMessage : {}", aiMessageDTO);
        AiMessage aiMessage = aiMessageMapper.toEntity(aiMessageDTO);
        aiMessage = aiMessageRepository.save(aiMessage);
        return aiMessageMapper.toDto(aiMessage);
    }

    @Override
    public AiMessageDTO update(AiMessageDTO aiMessageDTO) {
        LOG.debug("Request to update AiMessage : {}", aiMessageDTO);
        AiMessage aiMessage = aiMessageMapper.toEntity(aiMessageDTO);
        aiMessage = aiMessageRepository.save(aiMessage);
        return aiMessageMapper.toDto(aiMessage);
    }

    @Override
    public Optional<AiMessageDTO> partialUpdate(AiMessageDTO aiMessageDTO) {
        LOG.debug("Request to partially update AiMessage : {}", aiMessageDTO);

        return aiMessageRepository
            .findById(aiMessageDTO.getId())
            .map(existingAiMessage -> {
                aiMessageMapper.partialUpdate(existingAiMessage, aiMessageDTO);

                return existingAiMessage;
            })
            .map(aiMessageRepository::save)
            .map(aiMessageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AiMessageDTO> findOne(Long id) {
        LOG.debug("Request to get AiMessage : {}", id);
        return aiMessageRepository.findById(id).map(aiMessageMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete AiMessage : {}", id);
        aiMessageRepository.deleteById(id);
    }
}
