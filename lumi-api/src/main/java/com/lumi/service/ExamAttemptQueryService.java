package com.lumi.service;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.ExamAttempt;
import com.lumi.repository.ExamAttemptRepository;
import com.lumi.service.criteria.ExamAttemptCriteria;
import com.lumi.service.dto.ExamAttemptDTO;
import com.lumi.service.mapper.ExamAttemptMapper;
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
 * Service for executing complex queries for {@link ExamAttempt} entities in the database.
 * The main input is a {@link ExamAttemptCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ExamAttemptDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ExamAttemptQueryService extends QueryService<ExamAttempt> {

    private static final Logger LOG = LoggerFactory.getLogger(ExamAttemptQueryService.class);

    private final ExamAttemptRepository examAttemptRepository;

    private final ExamAttemptMapper examAttemptMapper;

    public ExamAttemptQueryService(ExamAttemptRepository examAttemptRepository, ExamAttemptMapper examAttemptMapper) {
        this.examAttemptRepository = examAttemptRepository;
        this.examAttemptMapper = examAttemptMapper;
    }

    /**
     * Return a {@link Page} of {@link ExamAttemptDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ExamAttemptDTO> findByCriteria(ExamAttemptCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ExamAttempt> specification = createSpecification(criteria);
        return examAttemptRepository.findAll(specification, page).map(examAttemptMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ExamAttemptCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<ExamAttempt> specification = createSpecification(criteria);
        return examAttemptRepository.count(specification);
    }

    /**
     * Function to convert {@link ExamAttemptCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ExamAttempt> createSpecification(ExamAttemptCriteria criteria) {
        Specification<ExamAttempt> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(ExamAttempt_.student, JoinType.LEFT);
                root.fetch(ExamAttempt_.exam, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), ExamAttempt_.id),
                    buildRangeSpecification(criteria.getStartTime(), ExamAttempt_.startTime),
                    buildRangeSpecification(criteria.getEndTime(), ExamAttempt_.endTime),
                    buildRangeSpecification(criteria.getScore(), ExamAttempt_.score),
                    buildStringSpecification(criteria.getStatus(), ExamAttempt_.status),
                    buildRangeSpecification(criteria.getRowVersion(), ExamAttempt_.rowVersion),
                    buildSpecification(criteria.getStudentId(), root -> root.join(ExamAttempt_.student, JoinType.LEFT).get(User_.id)),
                    buildSpecification(criteria.getExamId(), root -> root.join(ExamAttempt_.exam, JoinType.LEFT).get(Exam_.id)),
                    buildSpecification(criteria.getProctorSessionId(), root ->
                        root.join(ExamAttempt_.proctorSession, JoinType.LEFT).get(ProctorSession_.id)
                    )
                )
            );
        }
        return specification;
    }
}
