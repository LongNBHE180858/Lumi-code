package com.lumi.service;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.Question;
import com.lumi.repository.QuestionRepository;
import com.lumi.service.criteria.QuestionCriteria;
import com.lumi.service.dto.QuestionDTO;
import com.lumi.service.mapper.QuestionMapper;
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
 * Service for executing complex queries for {@link Question} entities in the database.
 * The main input is a {@link QuestionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link QuestionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class QuestionQueryService extends QueryService<Question> {

    private static final Logger LOG = LoggerFactory.getLogger(QuestionQueryService.class);

    private final QuestionRepository questionRepository;

    private final QuestionMapper questionMapper;

    public QuestionQueryService(QuestionRepository questionRepository, QuestionMapper questionMapper) {
        this.questionRepository = questionRepository;
        this.questionMapper = questionMapper;
    }

    /**
     * Return a {@link Page} of {@link QuestionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<QuestionDTO> findByCriteria(QuestionCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Question> specification = createSpecification(criteria);
        return questionRepository.findAll(specification, page).map(questionMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(QuestionCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Question> specification = createSpecification(criteria);
        return questionRepository.count(specification);
    }

    /**
     * Function to convert {@link QuestionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Question> createSpecification(QuestionCriteria criteria) {
        Specification<Question> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(Question_.bank, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), Question_.id),
                    buildStringSpecification(criteria.getContent(), Question_.content),
                    buildStringSpecification(criteria.getType(), Question_.type),
                    buildRangeSpecification(criteria.getDifficulty(), Question_.difficulty),
                    buildStringSpecification(criteria.getExplanation(), Question_.explanation),
                    buildSpecification(criteria.getIsDeleted(), Question_.isDeleted),
                    buildSpecification(criteria.getBankId(), root -> root.join(Question_.bank, JoinType.LEFT).get(QuestionBank_.id))
                )
            );
        }
        return specification;
    }
}
