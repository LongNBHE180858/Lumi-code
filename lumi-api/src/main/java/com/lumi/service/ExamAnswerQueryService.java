package com.lumi.service;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.ExamAnswer;
import com.lumi.repository.ExamAnswerRepository;
import com.lumi.service.criteria.ExamAnswerCriteria;
import com.lumi.service.dto.ExamAnswerDTO;
import com.lumi.service.mapper.ExamAnswerMapper;
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
 * Service for executing complex queries for {@link ExamAnswer} entities in the database.
 * The main input is a {@link ExamAnswerCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ExamAnswerDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ExamAnswerQueryService extends QueryService<ExamAnswer> {

    private static final Logger LOG = LoggerFactory.getLogger(ExamAnswerQueryService.class);

    private final ExamAnswerRepository examAnswerRepository;

    private final ExamAnswerMapper examAnswerMapper;

    public ExamAnswerQueryService(ExamAnswerRepository examAnswerRepository, ExamAnswerMapper examAnswerMapper) {
        this.examAnswerRepository = examAnswerRepository;
        this.examAnswerMapper = examAnswerMapper;
    }

    /**
     * Return a {@link Page} of {@link ExamAnswerDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ExamAnswerDTO> findByCriteria(ExamAnswerCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ExamAnswer> specification = createSpecification(criteria);
        return examAnswerRepository.findAll(specification, page).map(examAnswerMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ExamAnswerCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<ExamAnswer> specification = createSpecification(criteria);
        return examAnswerRepository.count(specification);
    }

    /**
     * Function to convert {@link ExamAnswerCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ExamAnswer> createSpecification(ExamAnswerCriteria criteria) {
        Specification<ExamAnswer> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(ExamAnswer_.attempt, JoinType.LEFT);
                root.fetch(ExamAnswer_.question, JoinType.LEFT);
                root.fetch(ExamAnswer_.option, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), ExamAnswer_.id),
                    buildStringSpecification(criteria.getTextAnswer(), ExamAnswer_.textAnswer),
                    buildSpecification(criteria.getIsCorrect(), ExamAnswer_.isCorrect),
                    buildRangeSpecification(criteria.getScore(), ExamAnswer_.score),
                    buildSpecification(criteria.getAttemptId(), root -> root.join(ExamAnswer_.attempt, JoinType.LEFT).get(ExamAttempt_.id)),
                    buildSpecification(criteria.getQuestionId(), root -> root.join(ExamAnswer_.question, JoinType.LEFT).get(Question_.id)),
                    buildSpecification(criteria.getOptionId(), root -> root.join(ExamAnswer_.option, JoinType.LEFT).get(QuestionOption_.id))
                )
            );
        }
        return specification;
    }
}
