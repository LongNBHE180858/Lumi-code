package com.lumi.service.impl;

import com.lumi.domain.DiscussionReply;
import com.lumi.repository.DiscussionReplyRepository;
import com.lumi.service.DiscussionReplyService;
import com.lumi.service.dto.DiscussionReplyDTO;
import com.lumi.service.mapper.DiscussionReplyMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.DiscussionReply}.
 */
@Service
@Transactional
public class DiscussionReplyServiceImpl implements DiscussionReplyService {

    private static final Logger LOG = LoggerFactory.getLogger(DiscussionReplyServiceImpl.class);

    private final DiscussionReplyRepository discussionReplyRepository;

    private final DiscussionReplyMapper discussionReplyMapper;

    public DiscussionReplyServiceImpl(DiscussionReplyRepository discussionReplyRepository, DiscussionReplyMapper discussionReplyMapper) {
        this.discussionReplyRepository = discussionReplyRepository;
        this.discussionReplyMapper = discussionReplyMapper;
    }

    @Override
    public DiscussionReplyDTO save(DiscussionReplyDTO discussionReplyDTO) {
        LOG.debug("Request to save DiscussionReply : {}", discussionReplyDTO);
        DiscussionReply discussionReply = discussionReplyMapper.toEntity(discussionReplyDTO);
        discussionReply = discussionReplyRepository.save(discussionReply);
        return discussionReplyMapper.toDto(discussionReply);
    }

    @Override
    public DiscussionReplyDTO update(DiscussionReplyDTO discussionReplyDTO) {
        LOG.debug("Request to update DiscussionReply : {}", discussionReplyDTO);
        DiscussionReply discussionReply = discussionReplyMapper.toEntity(discussionReplyDTO);
        discussionReply = discussionReplyRepository.save(discussionReply);
        return discussionReplyMapper.toDto(discussionReply);
    }

    @Override
    public Optional<DiscussionReplyDTO> partialUpdate(DiscussionReplyDTO discussionReplyDTO) {
        LOG.debug("Request to partially update DiscussionReply : {}", discussionReplyDTO);

        return discussionReplyRepository
            .findById(discussionReplyDTO.getId())
            .map(existingDiscussionReply -> {
                discussionReplyMapper.partialUpdate(existingDiscussionReply, discussionReplyDTO);

                return existingDiscussionReply;
            })
            .map(discussionReplyRepository::save)
            .map(discussionReplyMapper::toDto);
    }

    public Page<DiscussionReplyDTO> findAllWithEagerRelationships(Pageable pageable) {
        return discussionReplyRepository.findAllWithEagerRelationships(pageable).map(discussionReplyMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DiscussionReplyDTO> findOne(Long id) {
        LOG.debug("Request to get DiscussionReply : {}", id);
        return discussionReplyRepository.findOneWithEagerRelationships(id).map(discussionReplyMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete DiscussionReply : {}", id);
        discussionReplyRepository.deleteById(id);
    }
}
