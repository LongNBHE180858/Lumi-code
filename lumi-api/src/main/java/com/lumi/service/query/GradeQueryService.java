package com.lumi.service.query;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.Grade;
import com.lumi.repository.GradeRepository;
import com.lumi.service.criteria.GradeCriteria;
import com.lumi.service.dto.GradeDTO;
import com.lumi.service.mapper.GradeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Grade} entities in the database.
 * The main input is a {@link GradeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link GradeDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class GradeQueryService extends QueryService<Grade> {

    private static final Logger LOG = LoggerFactory.getLogger(GradeQueryService.class);

    private final GradeRepository gradeRepository;

    private final GradeMapper gradeMapper;

    public GradeQueryService(GradeRepository gradeRepository, GradeMapper gradeMapper) {
        this.gradeRepository = gradeRepository;
        this.gradeMapper = gradeMapper;
    }

    /**
     * Return a {@link Page} of {@link GradeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<GradeDTO> findByCriteria(GradeCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Grade> specification = createSpecification(criteria);
        return gradeRepository.findAll(specification, page).map(gradeMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(GradeCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Grade> specification = createSpecification(criteria);
        return gradeRepository.count(specification);
    }

    /**
     * Function to convert {@link GradeCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Grade> createSpecification(GradeCriteria criteria) {
        Specification<Grade> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), Grade_.id),
                    buildRangeSpecification(criteria.getLevel(), Grade_.level),
                    buildStringSpecification(criteria.getName(), Grade_.name)
                )
            );
        }
        return specification;
    }
}
