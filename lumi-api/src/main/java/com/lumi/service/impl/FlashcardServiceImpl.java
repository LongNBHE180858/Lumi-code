package com.lumi.service.impl;

import com.lumi.domain.Flashcard;
import com.lumi.repository.FlashcardRepository;
import com.lumi.service.FlashcardService;
import com.lumi.service.dto.FlashcardDTO;
import com.lumi.service.mapper.FlashcardMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.Flashcard}.
 */
@Service
@Transactional
public class FlashcardServiceImpl implements FlashcardService {

    private static final Logger LOG = LoggerFactory.getLogger(FlashcardServiceImpl.class);

    private final FlashcardRepository flashcardRepository;

    private final FlashcardMapper flashcardMapper;

    public FlashcardServiceImpl(FlashcardRepository flashcardRepository, FlashcardMapper flashcardMapper) {
        this.flashcardRepository = flashcardRepository;
        this.flashcardMapper = flashcardMapper;
    }

    @Override
    public FlashcardDTO save(FlashcardDTO flashcardDTO) {
        LOG.debug("Request to save Flashcard : {}", flashcardDTO);
        Flashcard flashcard = flashcardMapper.toEntity(flashcardDTO);
        flashcard = flashcardRepository.save(flashcard);
        return flashcardMapper.toDto(flashcard);
    }

    @Override
    public FlashcardDTO update(FlashcardDTO flashcardDTO) {
        LOG.debug("Request to update Flashcard : {}", flashcardDTO);
        Flashcard flashcard = flashcardMapper.toEntity(flashcardDTO);
        flashcard = flashcardRepository.save(flashcard);
        return flashcardMapper.toDto(flashcard);
    }

    @Override
    public Optional<FlashcardDTO> partialUpdate(FlashcardDTO flashcardDTO) {
        LOG.debug("Request to partially update Flashcard : {}", flashcardDTO);

        return flashcardRepository
            .findById(flashcardDTO.getId())
            .map(existingFlashcard -> {
                flashcardMapper.partialUpdate(existingFlashcard, flashcardDTO);

                return existingFlashcard;
            })
            .map(flashcardRepository::save)
            .map(flashcardMapper::toDto);
    }

    public Page<FlashcardDTO> findAllWithEagerRelationships(Pageable pageable) {
        return flashcardRepository.findAllWithEagerRelationships(pageable).map(flashcardMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FlashcardDTO> findOne(Long id) {
        LOG.debug("Request to get Flashcard : {}", id);
        return flashcardRepository.findOneWithEagerRelationships(id).map(flashcardMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Flashcard : {}", id);
        flashcardRepository.deleteById(id);
    }
}
