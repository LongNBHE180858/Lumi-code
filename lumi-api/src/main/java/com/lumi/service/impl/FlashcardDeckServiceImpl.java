package com.lumi.service.impl;

import com.lumi.domain.FlashcardDeck;
import com.lumi.repository.FlashcardDeckRepository;
import com.lumi.service.FlashcardDeckService;
import com.lumi.service.dto.FlashcardDeckDTO;
import com.lumi.service.mapper.FlashcardDeckMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.FlashcardDeck}.
 */
@Service
@Transactional
public class FlashcardDeckServiceImpl implements FlashcardDeckService {

    private static final Logger LOG = LoggerFactory.getLogger(FlashcardDeckServiceImpl.class);

    private final FlashcardDeckRepository flashcardDeckRepository;

    private final FlashcardDeckMapper flashcardDeckMapper;

    public FlashcardDeckServiceImpl(FlashcardDeckRepository flashcardDeckRepository, FlashcardDeckMapper flashcardDeckMapper) {
        this.flashcardDeckRepository = flashcardDeckRepository;
        this.flashcardDeckMapper = flashcardDeckMapper;
    }

    @Override
    public FlashcardDeckDTO save(FlashcardDeckDTO flashcardDeckDTO) {
        LOG.debug("Request to save FlashcardDeck : {}", flashcardDeckDTO);
        FlashcardDeck flashcardDeck = flashcardDeckMapper.toEntity(flashcardDeckDTO);
        flashcardDeck = flashcardDeckRepository.save(flashcardDeck);
        return flashcardDeckMapper.toDto(flashcardDeck);
    }

    @Override
    public FlashcardDeckDTO update(FlashcardDeckDTO flashcardDeckDTO) {
        LOG.debug("Request to update FlashcardDeck : {}", flashcardDeckDTO);
        FlashcardDeck flashcardDeck = flashcardDeckMapper.toEntity(flashcardDeckDTO);
        flashcardDeck = flashcardDeckRepository.save(flashcardDeck);
        return flashcardDeckMapper.toDto(flashcardDeck);
    }

    @Override
    public Optional<FlashcardDeckDTO> partialUpdate(FlashcardDeckDTO flashcardDeckDTO) {
        LOG.debug("Request to partially update FlashcardDeck : {}", flashcardDeckDTO);

        return flashcardDeckRepository
            .findById(flashcardDeckDTO.getId())
            .map(existingFlashcardDeck -> {
                flashcardDeckMapper.partialUpdate(existingFlashcardDeck, flashcardDeckDTO);

                return existingFlashcardDeck;
            })
            .map(flashcardDeckRepository::save)
            .map(flashcardDeckMapper::toDto);
    }

    public Page<FlashcardDeckDTO> findAllWithEagerRelationships(Pageable pageable) {
        return flashcardDeckRepository.findAllWithEagerRelationships(pageable).map(flashcardDeckMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FlashcardDeckDTO> findOne(Long id) {
        LOG.debug("Request to get FlashcardDeck : {}", id);
        return flashcardDeckRepository.findOneWithEagerRelationships(id).map(flashcardDeckMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete FlashcardDeck : {}", id);
        flashcardDeckRepository.deleteById(id);
    }
}
