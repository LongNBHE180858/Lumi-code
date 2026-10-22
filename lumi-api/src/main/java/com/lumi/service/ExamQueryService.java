package com.lumi.service;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.Exam;
import com.lumi.repository.ExamRepository;
import com.lumi.service.criteria.ExamCriteria;
import com.lumi.service.dto.ExamDTO;
import com.lumi.service.mapper.ExamMapper;
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
 * Service for executing complex queries for {@link Exam} entities in the database.
 * The main input is a {@link ExamCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ExamDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ExamQueryService extends QueryService<Exam> {

    private static final Logger LOG = LoggerFactory.getLogger(ExamQueryService.class);

    private final ExamRepository examRepository;

    private final ExamMapper examMapper;

    public ExamQueryService(ExamRepository examRepository, ExamMapper examMapper) {
        this.examRepository = examRepository;
        this.examMapper = examMapper;
    }

    /**
     * Return a {@link Page} of {@link ExamDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ExamDTO> findByCriteria(ExamCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Exam> specification = createSpecification(criteria);
        return examRepository.findAll(specification, page).map(examMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ExamCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Exam> specification = createSpecification(criteria);
        return examRepository.count(specification);
    }

    /**
     * Function to convert {@link ExamCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Exam> createSpecification(ExamCriteria criteria) {
        Specification<Exam> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(Exam_.studyClass, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), Exam_.id),
                    buildStringSpecification(criteria.getTitle(), Exam_.title),
                    buildRangeSpecification(criteria.getDurationMinutes(), Exam_.durationMinutes),
                    buildRangeSpecification(criteria.getPassScore(), Exam_.passScore),
                    buildRangeSpecification(criteria.getMaxAttempts(), Exam_.maxAttempts),
                    buildSpecification(criteria.getShuffleQuestions(), Exam_.shuffleQuestions),
                    buildSpecification(criteria.getStatus(), Exam_.status),
                    buildSpecification(criteria.getIsDeleted(), Exam_.isDeleted),
                    buildSpecification(criteria.getStudyClassId(), root -> root.join(Exam_.studyClass, JoinType.LEFT).get(StudyClass_.id))
                )
            );
        }
        return specification;
    }
}
