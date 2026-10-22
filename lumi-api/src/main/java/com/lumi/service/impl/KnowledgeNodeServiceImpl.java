package com.lumi.service.impl;

import com.lumi.domain.KnowledgeNode;
import com.lumi.repository.KnowledgeNodeRepository;
import com.lumi.service.KnowledgeNodeService;
import com.lumi.service.dto.KnowledgeNodeDTO;
import com.lumi.service.mapper.KnowledgeNodeMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.KnowledgeNode}.
 */
@Service
@Transactional
public class KnowledgeNodeServiceImpl implements KnowledgeNodeService {

    private static final Logger LOG = LoggerFactory.getLogger(KnowledgeNodeServiceImpl.class);

    private final KnowledgeNodeRepository knowledgeNodeRepository;

    private final KnowledgeNodeMapper knowledgeNodeMapper;

    public KnowledgeNodeServiceImpl(KnowledgeNodeRepository knowledgeNodeRepository, KnowledgeNodeMapper knowledgeNodeMapper) {
        this.knowledgeNodeRepository = knowledgeNodeRepository;
        this.knowledgeNodeMapper = knowledgeNodeMapper;
    }

    @Override
    public KnowledgeNodeDTO save(KnowledgeNodeDTO knowledgeNodeDTO) {
        LOG.debug("Request to save KnowledgeNode : {}", knowledgeNodeDTO);
        KnowledgeNode knowledgeNode = knowledgeNodeMapper.toEntity(knowledgeNodeDTO);
        knowledgeNode = knowledgeNodeRepository.save(knowledgeNode);
        return knowledgeNodeMapper.toDto(knowledgeNode);
    }

    @Override
    public KnowledgeNodeDTO update(KnowledgeNodeDTO knowledgeNodeDTO) {
        LOG.debug("Request to update KnowledgeNode : {}", knowledgeNodeDTO);
        KnowledgeNode knowledgeNode = knowledgeNodeMapper.toEntity(knowledgeNodeDTO);
        knowledgeNode = knowledgeNodeRepository.save(knowledgeNode);
        return knowledgeNodeMapper.toDto(knowledgeNode);
    }

    @Override
    public Optional<KnowledgeNodeDTO> partialUpdate(KnowledgeNodeDTO knowledgeNodeDTO) {
        LOG.debug("Request to partially update KnowledgeNode : {}", knowledgeNodeDTO);

        return knowledgeNodeRepository
            .findById(knowledgeNodeDTO.getId())
            .map(existingKnowledgeNode -> {
                knowledgeNodeMapper.partialUpdate(existingKnowledgeNode, knowledgeNodeDTO);

                return existingKnowledgeNode;
            })
            .map(knowledgeNodeRepository::save)
            .map(knowledgeNodeMapper::toDto);
    }

    public Page<KnowledgeNodeDTO> findAllWithEagerRelationships(Pageable pageable) {
        return knowledgeNodeRepository.findAllWithEagerRelationships(pageable).map(knowledgeNodeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<KnowledgeNodeDTO> findOne(Long id) {
        LOG.debug("Request to get KnowledgeNode : {}", id);
        return knowledgeNodeRepository.findOneWithEagerRelationships(id).map(knowledgeNodeMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete KnowledgeNode : {}", id);
        knowledgeNodeRepository.deleteById(id);
    }
}
