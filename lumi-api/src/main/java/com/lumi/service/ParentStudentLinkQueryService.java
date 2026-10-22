package com.lumi.service;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.ParentStudentLink;
import com.lumi.repository.ParentStudentLinkRepository;
import com.lumi.service.criteria.ParentStudentLinkCriteria;
import com.lumi.service.dto.ParentStudentLinkDTO;
import com.lumi.service.mapper.ParentStudentLinkMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link ParentStudentLink} entities in the database.
 * The main input is a {@link ParentStudentLinkCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ParentStudentLinkDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ParentStudentLinkQueryService extends QueryService<ParentStudentLink> {

    private static final Logger LOG = LoggerFactory.getLogger(ParentStudentLinkQueryService.class);

    private final ParentStudentLinkRepository parentStudentLinkRepository;

    private final ParentStudentLinkMapper parentStudentLinkMapper;

    public ParentStudentLinkQueryService(
        ParentStudentLinkRepository parentStudentLinkRepository,
        ParentStudentLinkMapper parentStudentLinkMapper
    ) {
        this.parentStudentLinkRepository = parentStudentLinkRepository;
        this.parentStudentLinkMapper = parentStudentLinkMapper;
    }

    /**
     * Return a {@link List} of {@link ParentStudentLinkDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ParentStudentLinkDTO> findByCriteria(ParentStudentLinkCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<ParentStudentLink> specification = createSpecification(criteria);
        return parentStudentLinkMapper.toDto(parentStudentLinkRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ParentStudentLinkCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<ParentStudentLink> specification = createSpecification(criteria);
        return parentStudentLinkRepository.count(specification);
    }

    /**
     * Function to convert {@link ParentStudentLinkCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ParentStudentLink> createSpecification(ParentStudentLinkCriteria criteria) {
        Specification<ParentStudentLink> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(ParentStudentLink_.parent, JoinType.LEFT);
                root.fetch(ParentStudentLink_.student, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), ParentStudentLink_.id),
                    buildStringSpecification(criteria.getLinkCode(), ParentStudentLink_.linkCode),
                    buildStringSpecification(criteria.getStatus(), ParentStudentLink_.status),
                    buildRangeSpecification(criteria.getLinkedAt(), ParentStudentLink_.linkedAt),
                    buildSpecification(criteria.getParentId(), root -> root.join(ParentStudentLink_.parent, JoinType.LEFT).get(User_.id)),
                    buildSpecification(criteria.getStudentId(), root -> root.join(ParentStudentLink_.student, JoinType.LEFT).get(User_.id))
                )
            );
        }
        return specification;
    }
}
