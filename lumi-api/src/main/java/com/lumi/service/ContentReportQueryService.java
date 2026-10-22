package com.lumi.service;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.ContentReport;
import com.lumi.repository.ContentReportRepository;
import com.lumi.service.criteria.ContentReportCriteria;
import com.lumi.service.dto.ContentReportDTO;
import com.lumi.service.mapper.ContentReportMapper;
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
 * Service for executing complex queries for {@link ContentReport} entities in the database.
 * The main input is a {@link ContentReportCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ContentReportDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ContentReportQueryService extends QueryService<ContentReport> {

    private static final Logger LOG = LoggerFactory.getLogger(ContentReportQueryService.class);

    private final ContentReportRepository contentReportRepository;

    private final ContentReportMapper contentReportMapper;

    public ContentReportQueryService(ContentReportRepository contentReportRepository, ContentReportMapper contentReportMapper) {
        this.contentReportRepository = contentReportRepository;
        this.contentReportMapper = contentReportMapper;
    }

    /**
     * Return a {@link Page} of {@link ContentReportDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ContentReportDTO> findByCriteria(ContentReportCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ContentReport> specification = createSpecification(criteria);
        return contentReportRepository.findAll(specification, page).map(contentReportMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ContentReportCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<ContentReport> specification = createSpecification(criteria);
        return contentReportRepository.count(specification);
    }

    /**
     * Function to convert {@link ContentReportCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ContentReport> createSpecification(ContentReportCriteria criteria) {
        Specification<ContentReport> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(ContentReport_.reporter, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), ContentReport_.id),
                    buildStringSpecification(criteria.getTargetType(), ContentReport_.targetType),
                    buildRangeSpecification(criteria.getTargetId(), ContentReport_.targetId),
                    buildStringSpecification(criteria.getReason(), ContentReport_.reason),
                    buildStringSpecification(criteria.getStatus(), ContentReport_.status),
                    buildRangeSpecification(criteria.getCreatedAt(), ContentReport_.createdAt),
                    buildSpecification(criteria.getReporterId(), root -> root.join(ContentReport_.reporter, JoinType.LEFT).get(User_.id))
                )
            );
        }
        return specification;
    }
}
