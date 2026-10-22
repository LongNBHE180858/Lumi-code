package com.lumi.service;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.Subject;
import com.lumi.repository.SubjectRepository;
import com.lumi.service.criteria.SubjectCriteria;
import com.lumi.service.dto.SubjectDTO;
import com.lumi.service.mapper.SubjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Subject} entities in the database.
 * The main input is a {@link SubjectCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link SubjectDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SubjectQueryService extends QueryService<Subject> {

    private static final Logger LOG = LoggerFactory.getLogger(SubjectQueryService.class);

    private final SubjectRepository subjectRepository;

    private final SubjectMapper subjectMapper;

    public SubjectQueryService(SubjectRepository subjectRepository, SubjectMapper subjectMapper) {
        this.subjectRepository = subjectRepository;
        this.subjectMapper = subjectMapper;
    }

    /**
     * Return a {@link Page} of {@link SubjectDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SubjectDTO> findByCriteria(SubjectCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Subject> specification = createSpecification(criteria).and((root, query, cb) -> cb.isTrue(root.get("active")));
        return subjectRepository.findAll(specification, page).map(subjectMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SubjectCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Subject> specification = createSpecification(criteria).and((root, query, cb) -> cb.isTrue(root.get("active")));
        return subjectRepository.count(specification);
    }

    /**
     * Function to convert {@link SubjectCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Subject> createSpecification(SubjectCriteria criteria) {
        Specification<Subject> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), Subject_.id),
                    buildStringSpecification(criteria.getName(), Subject_.name),
                    buildStringSpecification(criteria.getDescription(), Subject_.description)
                )
            );
        }
        return specification;
    }
}
