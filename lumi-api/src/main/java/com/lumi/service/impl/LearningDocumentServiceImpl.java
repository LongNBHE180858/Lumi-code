package com.lumi.service.impl;

import com.lumi.domain.LearningDocument;
import com.lumi.repository.LearningDocumentRepository;
import com.lumi.service.LearningDocumentService;
import com.lumi.service.dto.LearningDocumentDTO;
import com.lumi.service.mapper.LearningDocumentMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.LearningDocument}.
 */
@Service
@Transactional
public class LearningDocumentServiceImpl implements LearningDocumentService {

    private static final Logger LOG = LoggerFactory.getLogger(LearningDocumentServiceImpl.class);

    private final LearningDocumentRepository learningDocumentRepository;

    private final LearningDocumentMapper learningDocumentMapper;

    public LearningDocumentServiceImpl(
        LearningDocumentRepository learningDocumentRepository,
        LearningDocumentMapper learningDocumentMapper
    ) {
        this.learningDocumentRepository = learningDocumentRepository;
        this.learningDocumentMapper = learningDocumentMapper;
    }

    @Override
    public LearningDocumentDTO save(LearningDocumentDTO learningDocumentDTO) {
        LOG.debug("Request to save LearningDocument : {}", learningDocumentDTO);
        LearningDocument learningDocument = learningDocumentMapper.toEntity(learningDocumentDTO);
        learningDocument = learningDocumentRepository.save(learningDocument);
        return learningDocumentMapper.toDto(learningDocument);
    }

    @Override
    public LearningDocumentDTO update(LearningDocumentDTO learningDocumentDTO) {
        LOG.debug("Request to update LearningDocument : {}", learningDocumentDTO);
        LearningDocument learningDocument = learningDocumentMapper.toEntity(learningDocumentDTO);
        learningDocument = learningDocumentRepository.save(learningDocument);
        return learningDocumentMapper.toDto(learningDocument);
    }

    @Override
    public Optional<LearningDocumentDTO> partialUpdate(LearningDocumentDTO learningDocumentDTO) {
        LOG.debug("Request to partially update LearningDocument : {}", learningDocumentDTO);

        return learningDocumentRepository
            .findById(learningDocumentDTO.getId())
            .map(existingLearningDocument -> {
                learningDocumentMapper.partialUpdate(existingLearningDocument, learningDocumentDTO);

                return existingLearningDocument;
            })
            .map(learningDocumentRepository::save)
            .map(learningDocumentMapper::toDto);
    }

    public Page<LearningDocumentDTO> findAllWithEagerRelationships(Pageable pageable) {
        return learningDocumentRepository.findAllWithEagerRelationships(pageable).map(learningDocumentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LearningDocumentDTO> findOne(Long id) {
        LOG.debug("Request to get LearningDocument : {}", id);
        return learningDocumentRepository.findOneWithEagerRelationships(id).map(learningDocumentMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete LearningDocument : {}", id);
        learningDocumentRepository.deleteById(id);
    }
}
