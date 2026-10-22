package com.lumi.service.impl;

import com.lumi.domain.FlashcardReview;
import com.lumi.repository.FlashcardReviewRepository;
import com.lumi.service.FlashcardReviewService;
import com.lumi.service.dto.FlashcardReviewDTO;
import com.lumi.service.mapper.FlashcardReviewMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.FlashcardReview}.
 */
@Service
@Transactional
public class FlashcardReviewServiceImpl implements FlashcardReviewService {

    private static final Logger LOG = LoggerFactory.getLogger(FlashcardReviewServiceImpl.class);

    private final FlashcardReviewRepository flashcardReviewRepository;

    private final FlashcardReviewMapper flashcardReviewMapper;

    public FlashcardReviewServiceImpl(FlashcardReviewRepository flashcardReviewRepository, FlashcardReviewMapper flashcardReviewMapper) {
        this.flashcardReviewRepository = flashcardReviewRepository;
        this.flashcardReviewMapper = flashcardReviewMapper;
    }

    @Override
    public FlashcardReviewDTO save(FlashcardReviewDTO flashcardReviewDTO) {
        LOG.debug("Request to save FlashcardReview : {}", flashcardReviewDTO);
        FlashcardReview flashcardReview = flashcardReviewMapper.toEntity(flashcardReviewDTO);
        flashcardReview = flashcardReviewRepository.save(flashcardReview);
        return flashcardReviewMapper.toDto(flashcardReview);
    }

    @Override
    public FlashcardReviewDTO update(FlashcardReviewDTO flashcardReviewDTO) {
        LOG.debug("Request to update FlashcardReview : {}", flashcardReviewDTO);
        FlashcardReview flashcardReview = flashcardReviewMapper.toEntity(flashcardReviewDTO);
        flashcardReview = flashcardReviewRepository.save(flashcardReview);
        return flashcardReviewMapper.toDto(flashcardReview);
    }

    @Override
    public Optional<FlashcardReviewDTO> partialUpdate(FlashcardReviewDTO flashcardReviewDTO) {
        LOG.debug("Request to partially update FlashcardReview : {}", flashcardReviewDTO);

        return flashcardReviewRepository
            .findById(flashcardReviewDTO.getId())
            .map(existingFlashcardReview -> {
                flashcardReviewMapper.partialUpdate(existingFlashcardReview, flashcardReviewDTO);

                return existingFlashcardReview;
            })
            .map(flashcardReviewRepository::save)
            .map(flashcardReviewMapper::toDto);
    }

    public List<FlashcardReviewDTO> findAllWithEagerRelationships() {
        return flashcardReviewRepository
            .findAllWithEagerRelationships()
            .stream()
            .map(flashcardReviewMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FlashcardReviewDTO> findOne(Long id) {
        LOG.debug("Request to get FlashcardReview : {}", id);
        return flashcardReviewRepository.findOneWithEagerRelationships(id).map(flashcardReviewMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete FlashcardReview : {}", id);
        flashcardReviewRepository.deleteById(id);
    }
}
