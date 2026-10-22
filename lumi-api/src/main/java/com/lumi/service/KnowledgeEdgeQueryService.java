package com.lumi.service;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.KnowledgeEdge;
import com.lumi.repository.KnowledgeEdgeRepository;
import com.lumi.service.criteria.KnowledgeEdgeCriteria;
import com.lumi.service.dto.KnowledgeEdgeDTO;
import com.lumi.service.mapper.KnowledgeEdgeMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link KnowledgeEdge} entities in the database.
 * The main input is a {@link KnowledgeEdgeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link KnowledgeEdgeDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class KnowledgeEdgeQueryService extends QueryService<KnowledgeEdge> {

    private static final Logger LOG = LoggerFactory.getLogger(KnowledgeEdgeQueryService.class);

    private final KnowledgeEdgeRepository knowledgeEdgeRepository;

    private final KnowledgeEdgeMapper knowledgeEdgeMapper;

    public KnowledgeEdgeQueryService(KnowledgeEdgeRepository knowledgeEdgeRepository, KnowledgeEdgeMapper knowledgeEdgeMapper) {
        this.knowledgeEdgeRepository = knowledgeEdgeRepository;
        this.knowledgeEdgeMapper = knowledgeEdgeMapper;
    }

    /**
     * Return a {@link List} of {@link KnowledgeEdgeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<KnowledgeEdgeDTO> findByCriteria(KnowledgeEdgeCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<KnowledgeEdge> specification = createSpecification(criteria);
        return knowledgeEdgeMapper.toDto(knowledgeEdgeRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(KnowledgeEdgeCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<KnowledgeEdge> specification = createSpecification(criteria);
        return knowledgeEdgeRepository.count(specification);
    }

    /**
     * Function to convert {@link KnowledgeEdgeCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<KnowledgeEdge> createSpecification(KnowledgeEdgeCriteria criteria) {
        Specification<KnowledgeEdge> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(KnowledgeEdge_.fromNode, JoinType.LEFT);
                root.fetch(KnowledgeEdge_.toNode, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), KnowledgeEdge_.id),
                    buildStringSpecification(criteria.getRelationType(), KnowledgeEdge_.relationType),
                    buildSpecification(criteria.getFromNodeId(), root ->
                        root.join(KnowledgeEdge_.fromNode, JoinType.LEFT).get(KnowledgeNode_.id)
                    ),
                    buildSpecification(criteria.getToNodeId(), root ->
                        root.join(KnowledgeEdge_.toNode, JoinType.LEFT).get(KnowledgeNode_.id)
                    )
                )
            );
        }
        return specification;
    }
}
