package com.lumi.service;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.DiscussionReply;
import com.lumi.repository.DiscussionReplyRepository;
import com.lumi.service.criteria.DiscussionReplyCriteria;
import com.lumi.service.dto.DiscussionReplyDTO;
import com.lumi.service.mapper.DiscussionReplyMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link DiscussionReply} entities in the database.
 * The main input is a {@link DiscussionReplyCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link DiscussionReplyDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DiscussionReplyQueryService extends QueryService<DiscussionReply> {

    private static final Logger LOG = LoggerFactory.getLogger(DiscussionReplyQueryService.class);

    private final DiscussionReplyRepository discussionReplyRepository;

    private final DiscussionReplyMapper discussionReplyMapper;

    public DiscussionReplyQueryService(DiscussionReplyRepository discussionReplyRepository, DiscussionReplyMapper discussionReplyMapper) {
        this.discussionReplyRepository = discussionReplyRepository;
        this.discussionReplyMapper = discussionReplyMapper;
    }

    /**
     * Return a {@link Page} of {@link DiscussionReplyDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<DiscussionReplyDTO> findByCriteria(DiscussionReplyCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<DiscussionReply> specification = createSpecification(criteria);
        return discussionReplyRepository.findAll(specification, page).map(discussionReplyMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DiscussionReplyCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<DiscussionReply> specification = createSpecification(criteria);
        return discussionReplyRepository.count(specification);
    }

    /**
     * Function to convert {@link DiscussionReplyCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<DiscussionReply> createSpecification(DiscussionReplyCriteria criteria) {
        Specification<DiscussionReply> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(DiscussionReply_.user, JoinType.LEFT);
                root.fetch(DiscussionReply_.discussion, JoinType.LEFT);
                root.fetch(DiscussionReply_.parent, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), DiscussionReply_.id),
                    buildStringSpecification(criteria.getContent(), DiscussionReply_.content),
                    buildRangeSpecification(criteria.getCreatedAt(), DiscussionReply_.createdAt),
                    buildSpecification(criteria.getUserId(), root -> root.join(DiscussionReply_.user, JoinType.LEFT).get(User_.id)),
                    buildSpecification(criteria.getDiscussionId(), root ->
                        root.join(DiscussionReply_.discussion, JoinType.LEFT).get(Discussion_.id)
                    ),
                    buildSpecification(criteria.getParentId(), root ->
                        root.join(DiscussionReply_.parent, JoinType.LEFT).get(DiscussionReply_.id)
                    )
                )
            );
        }
        return specification;
    }
}
