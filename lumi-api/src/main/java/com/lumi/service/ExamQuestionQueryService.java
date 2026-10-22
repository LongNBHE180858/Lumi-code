package com.lumi.service;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.ExamQuestion;
import com.lumi.repository.ExamQuestionRepository;
import com.lumi.service.criteria.ExamQuestionCriteria;
import com.lumi.service.dto.ExamQuestionDTO;
import com.lumi.service.mapper.ExamQuestionMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link ExamQuestion} entities in the database.
 * The main input is a {@link ExamQuestionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ExamQuestionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ExamQuestionQueryService extends QueryService<ExamQuestion> {

    private static final Logger LOG = LoggerFactory.getLogger(ExamQuestionQueryService.class);

    private final ExamQuestionRepository examQuestionRepository;

    private final ExamQuestionMapper examQuestionMapper;

    public ExamQuestionQueryService(ExamQuestionRepository examQuestionRepository, ExamQuestionMapper examQuestionMapper) {
        this.examQuestionRepository = examQuestionRepository;
        this.examQuestionMapper = examQuestionMapper;
    }

    /**
     * Return a {@link List} of {@link ExamQuestionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ExamQuestionDTO> findByCriteria(ExamQuestionCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<ExamQuestion> specification = createSpecification(criteria);
        return examQuestionMapper.toDto(examQuestionRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ExamQuestionCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<ExamQuestion> specification = createSpecification(criteria);
        return examQuestionRepository.count(specification);
    }

    /**
     * Function to convert {@link ExamQuestionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ExamQuestion> createSpecification(ExamQuestionCriteria criteria) {
        Specification<ExamQuestion> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(ExamQuestion_.exam, JoinType.LEFT);
                root.fetch(ExamQuestion_.question, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), ExamQuestion_.id),
                    buildRangeSpecification(criteria.getOrderIndex(), ExamQuestion_.orderIndex),
                    buildRangeSpecification(criteria.getScore(), ExamQuestion_.score),
                    buildSpecification(criteria.getExamId(), root -> root.join(ExamQuestion_.exam, JoinType.LEFT).get(Exam_.id)),
                    buildSpecification(criteria.getQuestionId(), root -> root.join(ExamQuestion_.question, JoinType.LEFT).get(Question_.id))
                )
            );
        }
        return specification;
    }
}
