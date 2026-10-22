package com.lumi.service;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.KnowledgeNode;
import com.lumi.repository.KnowledgeNodeRepository;
import com.lumi.service.criteria.KnowledgeNodeCriteria;
import com.lumi.service.dto.KnowledgeNodeDTO;
import com.lumi.service.mapper.KnowledgeNodeMapper;
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
 * Service for executing complex queries for {@link KnowledgeNode} entities in the database.
 * The main input is a {@link KnowledgeNodeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link KnowledgeNodeDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class KnowledgeNodeQueryService extends QueryService<KnowledgeNode> {

    private static final Logger LOG = LoggerFactory.getLogger(KnowledgeNodeQueryService.class);

    private final KnowledgeNodeRepository knowledgeNodeRepository;

    private final KnowledgeNodeMapper knowledgeNodeMapper;

    public KnowledgeNodeQueryService(KnowledgeNodeRepository knowledgeNodeRepository, KnowledgeNodeMapper knowledgeNodeMapper) {
        this.knowledgeNodeRepository = knowledgeNodeRepository;
        this.knowledgeNodeMapper = knowledgeNodeMapper;
    }

    /**
     * Return a {@link Page} of {@link KnowledgeNodeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<KnowledgeNodeDTO> findByCriteria(KnowledgeNodeCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<KnowledgeNode> specification = createSpecification(criteria);
        return knowledgeNodeRepository.findAll(specification, page).map(knowledgeNodeMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(KnowledgeNodeCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<KnowledgeNode> specification = createSpecification(criteria);
        return knowledgeNodeRepository.count(specification);
    }

    /**
     * Function to convert {@link KnowledgeNodeCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<KnowledgeNode> createSpecification(KnowledgeNodeCriteria criteria) {
        Specification<KnowledgeNode> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(KnowledgeNode_.subject, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), KnowledgeNode_.id),
                    buildStringSpecification(criteria.getTitle(), KnowledgeNode_.title),
                    buildStringSpecification(criteria.getType(), KnowledgeNode_.type),
                    buildSpecification(criteria.getSubjectId(), root -> root.join(KnowledgeNode_.subject, JoinType.LEFT).get(Subject_.id))
                )
            );
        }
        return specification;
    }
}
