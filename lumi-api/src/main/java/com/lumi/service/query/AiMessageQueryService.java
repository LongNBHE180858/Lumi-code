package com.lumi.service.query;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.AiMessage;
import com.lumi.repository.AiMessageRepository;
import com.lumi.service.criteria.AiMessageCriteria;
import com.lumi.service.dto.AiMessageDTO;
import com.lumi.service.mapper.AiMessageMapper;
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
 * Service for executing complex queries for {@link AiMessage} entities in the database.
 * The main input is a {@link AiMessageCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link AiMessageDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AiMessageQueryService extends QueryService<AiMessage> {

    private static final Logger LOG = LoggerFactory.getLogger(AiMessageQueryService.class);

    private final AiMessageRepository aiMessageRepository;

    private final AiMessageMapper aiMessageMapper;

    public AiMessageQueryService(AiMessageRepository aiMessageRepository, AiMessageMapper aiMessageMapper) {
        this.aiMessageRepository = aiMessageRepository;
        this.aiMessageMapper = aiMessageMapper;
    }

    /**
     * Return a {@link Page} of {@link AiMessageDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<AiMessageDTO> findByCriteria(AiMessageCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<AiMessage> specification = createSpecification(criteria);
        return aiMessageRepository.findAll(specification, page).map(aiMessageMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AiMessageCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<AiMessage> specification = createSpecification(criteria);
        return aiMessageRepository.count(specification);
    }

    /**
     * Function to convert {@link AiMessageCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<AiMessage> createSpecification(AiMessageCriteria criteria) {
        Specification<AiMessage> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(AiMessage_.conversation, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), AiMessage_.id),
                    buildStringSpecification(criteria.getSenderRole(), AiMessage_.senderRole),
                    buildRangeSpecification(criteria.getCreatedAt(), AiMessage_.createdAt),
                    buildStringSpecification(criteria.getGenerationStatus(), AiMessage_.generationStatus),
                    buildStringSpecification(criteria.getModelName(), AiMessage_.modelName),
                    buildRangeSpecification(criteria.getFeedbackRating(), AiMessage_.feedbackRating),
                    buildRangeSpecification(criteria.getFeedbackAt(), AiMessage_.feedbackAt),
                    buildSpecification(criteria.getConversationId(), root ->
                        root.join(AiMessage_.conversation, JoinType.LEFT).get(AiConversation_.id)
                    )
                )
            );
        }
        return specification;
    }
}
