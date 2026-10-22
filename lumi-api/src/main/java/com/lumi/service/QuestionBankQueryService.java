package com.lumi.service;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.QuestionBank;
import com.lumi.repository.QuestionBankRepository;
import com.lumi.service.criteria.QuestionBankCriteria;
import com.lumi.service.dto.QuestionBankDTO;
import com.lumi.service.mapper.QuestionBankMapper;
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
 * Service for executing complex queries for {@link QuestionBank} entities in the database.
 * The main input is a {@link QuestionBankCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link QuestionBankDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class QuestionBankQueryService extends QueryService<QuestionBank> {

    private static final Logger LOG = LoggerFactory.getLogger(QuestionBankQueryService.class);

    private final QuestionBankRepository questionBankRepository;

    private final QuestionBankMapper questionBankMapper;

    public QuestionBankQueryService(QuestionBankRepository questionBankRepository, QuestionBankMapper questionBankMapper) {
        this.questionBankRepository = questionBankRepository;
        this.questionBankMapper = questionBankMapper;
    }

    /**
     * Return a {@link Page} of {@link QuestionBankDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<QuestionBankDTO> findByCriteria(QuestionBankCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<QuestionBank> specification = createSpecification(criteria);
        return questionBankRepository.findAll(specification, page).map(questionBankMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(QuestionBankCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<QuestionBank> specification = createSpecification(criteria);
        return questionBankRepository.count(specification);
    }

    /**
     * Function to convert {@link QuestionBankCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<QuestionBank> createSpecification(QuestionBankCriteria criteria) {
        Specification<QuestionBank> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(QuestionBank_.instructor, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), QuestionBank_.id),
                    buildStringSpecification(criteria.getName(), QuestionBank_.name),
                    buildSpecification(criteria.getInstructorId(), root -> root.join(QuestionBank_.instructor, JoinType.LEFT).get(User_.id))
                )
            );
        }
        return specification;
    }
}
