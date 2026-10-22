package com.lumi.service.impl;

import com.lumi.domain.KnowledgeEdge;
import com.lumi.repository.KnowledgeEdgeRepository;
import com.lumi.service.KnowledgeEdgeService;
import com.lumi.service.dto.KnowledgeEdgeDTO;
import com.lumi.service.mapper.KnowledgeEdgeMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.KnowledgeEdge}.
 */
@Service
@Transactional
public class KnowledgeEdgeServiceImpl implements KnowledgeEdgeService {

    private static final Logger LOG = LoggerFactory.getLogger(KnowledgeEdgeServiceImpl.class);

    private final KnowledgeEdgeRepository knowledgeEdgeRepository;

    private final KnowledgeEdgeMapper knowledgeEdgeMapper;

    public KnowledgeEdgeServiceImpl(KnowledgeEdgeRepository knowledgeEdgeRepository, KnowledgeEdgeMapper knowledgeEdgeMapper) {
        this.knowledgeEdgeRepository = knowledgeEdgeRepository;
        this.knowledgeEdgeMapper = knowledgeEdgeMapper;
    }

    @Override
    public KnowledgeEdgeDTO save(KnowledgeEdgeDTO knowledgeEdgeDTO) {
        LOG.debug("Request to save KnowledgeEdge : {}", knowledgeEdgeDTO);
        KnowledgeEdge knowledgeEdge = knowledgeEdgeMapper.toEntity(knowledgeEdgeDTO);
        knowledgeEdge = knowledgeEdgeRepository.save(knowledgeEdge);
        return knowledgeEdgeMapper.toDto(knowledgeEdge);
    }

    @Override
    public KnowledgeEdgeDTO update(KnowledgeEdgeDTO knowledgeEdgeDTO) {
        LOG.debug("Request to update KnowledgeEdge : {}", knowledgeEdgeDTO);
        KnowledgeEdge knowledgeEdge = knowledgeEdgeMapper.toEntity(knowledgeEdgeDTO);
        knowledgeEdge = knowledgeEdgeRepository.save(knowledgeEdge);
        return knowledgeEdgeMapper.toDto(knowledgeEdge);
    }

    @Override
    public Optional<KnowledgeEdgeDTO> partialUpdate(KnowledgeEdgeDTO knowledgeEdgeDTO) {
        LOG.debug("Request to partially update KnowledgeEdge : {}", knowledgeEdgeDTO);

        return knowledgeEdgeRepository
            .findById(knowledgeEdgeDTO.getId())
            .map(existingKnowledgeEdge -> {
                knowledgeEdgeMapper.partialUpdate(existingKnowledgeEdge, knowledgeEdgeDTO);

                return existingKnowledgeEdge;
            })
            .map(knowledgeEdgeRepository::save)
            .map(knowledgeEdgeMapper::toDto);
    }

    public List<KnowledgeEdgeDTO> findAllWithEagerRelationships() {
        return knowledgeEdgeRepository
            .findAllWithEagerRelationships()
            .stream()
            .map(knowledgeEdgeMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<KnowledgeEdgeDTO> findOne(Long id) {
        LOG.debug("Request to get KnowledgeEdge : {}", id);
        return knowledgeEdgeRepository.findOneWithEagerRelationships(id).map(knowledgeEdgeMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete KnowledgeEdge : {}", id);
        knowledgeEdgeRepository.deleteById(id);
    }
}
