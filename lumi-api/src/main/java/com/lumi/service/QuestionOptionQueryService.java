package com.lumi.service;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.QuestionOption;
import com.lumi.repository.QuestionOptionRepository;
import com.lumi.service.criteria.QuestionOptionCriteria;
import com.lumi.service.dto.QuestionOptionDTO;
import com.lumi.service.mapper.QuestionOptionMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link QuestionOption} entities in the database.
 * The main input is a {@link QuestionOptionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link QuestionOptionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class QuestionOptionQueryService extends QueryService<QuestionOption> {

    private static final Logger LOG = LoggerFactory.getLogger(QuestionOptionQueryService.class);

    private final QuestionOptionRepository questionOptionRepository;

    private final QuestionOptionMapper questionOptionMapper;

    public QuestionOptionQueryService(QuestionOptionRepository questionOptionRepository, QuestionOptionMapper questionOptionMapper) {
        this.questionOptionRepository = questionOptionRepository;
        this.questionOptionMapper = questionOptionMapper;
    }

    /**
     * Return a {@link List} of {@link QuestionOptionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<QuestionOptionDTO> findByCriteria(QuestionOptionCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<QuestionOption> specification = createSpecification(criteria);
        return questionOptionMapper.toDto(questionOptionRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(QuestionOptionCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<QuestionOption> specification = createSpecification(criteria);
        return questionOptionRepository.count(specification);
    }

    /**
     * Function to convert {@link QuestionOptionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<QuestionOption> createSpecification(QuestionOptionCriteria criteria) {
        Specification<QuestionOption> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(QuestionOption_.question, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), QuestionOption_.id),
                    buildStringSpecification(criteria.getContent(), QuestionOption_.content),
                    buildSpecification(criteria.getIsCorrect(), QuestionOption_.isCorrect),
                    buildSpecification(criteria.getQuestionId(), root ->
                        root.join(QuestionOption_.question, JoinType.LEFT).get(Question_.id)
                    )
                )
            );
        }
        return specification;
    }
}
