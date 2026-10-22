package com.lumi.service;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.AiConversation;
import com.lumi.repository.AiConversationRepository;
import com.lumi.service.criteria.AiConversationCriteria;
import com.lumi.service.dto.AiConversationDTO;
import com.lumi.service.mapper.AiConversationMapper;
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
 * Service for executing complex queries for {@link AiConversation} entities in the database.
 * The main input is a {@link AiConversationCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link AiConversationDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AiConversationQueryService extends QueryService<AiConversation> {

    private static final Logger LOG = LoggerFactory.getLogger(AiConversationQueryService.class);

    private final AiConversationRepository aiConversationRepository;

    private final AiConversationMapper aiConversationMapper;

    public AiConversationQueryService(AiConversationRepository aiConversationRepository, AiConversationMapper aiConversationMapper) {
        this.aiConversationRepository = aiConversationRepository;
        this.aiConversationMapper = aiConversationMapper;
    }

    /**
     * Return a {@link Page} of {@link AiConversationDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<AiConversationDTO> findByCriteria(AiConversationCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<AiConversation> specification = createSpecification(criteria);
        return aiConversationRepository.findAll(specification, page).map(aiConversationMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AiConversationCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<AiConversation> specification = createSpecification(criteria);
        return aiConversationRepository.count(specification);
    }

    /**
     * Function to convert {@link AiConversationCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<AiConversation> createSpecification(AiConversationCriteria criteria) {
        Specification<AiConversation> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(AiConversation_.user, JoinType.LEFT);
                root.fetch(AiConversation_.lesson, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), AiConversation_.id),
                    buildStringSpecification(criteria.getType(), AiConversation_.type),
                    buildRangeSpecification(criteria.getCreatedAt(), AiConversation_.createdAt),
                    buildSpecification(criteria.getUserId(), root -> root.join(AiConversation_.user, JoinType.LEFT).get(User_.id)),
                    buildSpecification(criteria.getLessonId(), root -> root.join(AiConversation_.lesson, JoinType.LEFT).get(Lesson_.id))
                )
            );
        }
        return specification;
    }
}
