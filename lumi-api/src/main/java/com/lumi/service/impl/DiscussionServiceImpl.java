package com.lumi.service.impl;

import com.lumi.domain.Discussion;
import com.lumi.repository.DiscussionRepository;
import com.lumi.service.DiscussionService;
import com.lumi.service.dto.DiscussionDTO;
import com.lumi.service.mapper.DiscussionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.Discussion}.
 */
@Service
@Transactional
public class DiscussionServiceImpl implements DiscussionService {

    private static final Logger LOG = LoggerFactory.getLogger(DiscussionServiceImpl.class);

    private final DiscussionRepository discussionRepository;

    private final DiscussionMapper discussionMapper;

    public DiscussionServiceImpl(DiscussionRepository discussionRepository, DiscussionMapper discussionMapper) {
        this.discussionRepository = discussionRepository;
        this.discussionMapper = discussionMapper;
    }

    @Override
    public DiscussionDTO save(DiscussionDTO discussionDTO) {
        LOG.debug("Request to save Discussion : {}", discussionDTO);
        Discussion discussion = discussionMapper.toEntity(discussionDTO);
        discussion = discussionRepository.save(discussion);
        return discussionMapper.toDto(discussion);
    }

    @Override
    public DiscussionDTO update(DiscussionDTO discussionDTO) {
        LOG.debug("Request to update Discussion : {}", discussionDTO);
        Discussion discussion = discussionMapper.toEntity(discussionDTO);
        discussion = discussionRepository.save(discussion);
        return discussionMapper.toDto(discussion);
    }

    @Override
    public Optional<DiscussionDTO> partialUpdate(DiscussionDTO discussionDTO) {
        LOG.debug("Request to partially update Discussion : {}", discussionDTO);

        return discussionRepository
            .findById(discussionDTO.getId())
            .map(existingDiscussion -> {
                discussionMapper.partialUpdate(existingDiscussion, discussionDTO);

                return existingDiscussion;
            })
            .map(discussionRepository::save)
            .map(discussionMapper::toDto);
    }

    public Page<DiscussionDTO> findAllWithEagerRelationships(Pageable pageable) {
        return discussionRepository.findAllWithEagerRelationships(pageable).map(discussionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DiscussionDTO> findOne(Long id) {
        LOG.debug("Request to get Discussion : {}", id);
        return discussionRepository.findOneWithEagerRelationships(id).map(discussionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Discussion : {}", id);
        discussionRepository.deleteById(id);
    }
}
