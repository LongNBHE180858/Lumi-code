package com.lumi.service.query;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.QuestionKnowledgeLink;
import com.lumi.repository.QuestionKnowledgeLinkRepository;
import com.lumi.service.criteria.QuestionKnowledgeLinkCriteria;
import com.lumi.service.dto.QuestionKnowledgeLinkDTO;
import com.lumi.service.mapper.QuestionKnowledgeLinkMapper;
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
 * Service for executing complex queries for {@link QuestionKnowledgeLink} entities in the database.
 * The main input is a {@link QuestionKnowledgeLinkCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link QuestionKnowledgeLinkDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class QuestionKnowledgeLinkQueryService extends QueryService<QuestionKnowledgeLink> {

    private static final Logger LOG = LoggerFactory.getLogger(QuestionKnowledgeLinkQueryService.class);

    private final QuestionKnowledgeLinkRepository questionKnowledgeLinkRepository;

    private final QuestionKnowledgeLinkMapper questionKnowledgeLinkMapper;

    public QuestionKnowledgeLinkQueryService(
        QuestionKnowledgeLinkRepository questionKnowledgeLinkRepository,
        QuestionKnowledgeLinkMapper questionKnowledgeLinkMapper
    ) {
        this.questionKnowledgeLinkRepository = questionKnowledgeLinkRepository;
        this.questionKnowledgeLinkMapper = questionKnowledgeLinkMapper;
    }

    /**
     * Return a {@link Page} of {@link QuestionKnowledgeLinkDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<QuestionKnowledgeLinkDTO> findByCriteria(QuestionKnowledgeLinkCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<QuestionKnowledgeLink> specification = createSpecification(criteria);
        return questionKnowledgeLinkRepository.findAll(specification, page).map(questionKnowledgeLinkMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(QuestionKnowledgeLinkCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<QuestionKnowledgeLink> specification = createSpecification(criteria);
        return questionKnowledgeLinkRepository.count(specification);
    }

    /**
     * Function to convert {@link QuestionKnowledgeLinkCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<QuestionKnowledgeLink> createSpecification(QuestionKnowledgeLinkCriteria criteria) {
        Specification<QuestionKnowledgeLink> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(QuestionKnowledgeLink_.question, JoinType.LEFT);
                root.fetch(QuestionKnowledgeLink_.knowledgeNode, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), QuestionKnowledgeLink_.id),
                    buildRangeSpecification(criteria.getWeight(), QuestionKnowledgeLink_.weight),
                    buildSpecification(criteria.getQuestionId(), root ->
                        root.join(QuestionKnowledgeLink_.question, JoinType.LEFT).get(Question_.id)
                    ),
                    buildSpecification(criteria.getKnowledgeNodeId(), root ->
                        root.join(QuestionKnowledgeLink_.knowledgeNode, JoinType.LEFT).get(KnowledgeNode_.id)
                    )
                )
            );
        }
        return specification;
    }
}
