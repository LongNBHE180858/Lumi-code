package com.lumi.service.query;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.StudentKnowledgeProgress;
import com.lumi.repository.StudentKnowledgeProgressRepository;
import com.lumi.service.criteria.StudentKnowledgeProgressCriteria;
import com.lumi.service.dto.StudentKnowledgeProgressDTO;
import com.lumi.service.mapper.StudentKnowledgeProgressMapper;
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
 * Service for executing complex queries for {@link StudentKnowledgeProgress} entities in the database.
 * The main input is a {@link StudentKnowledgeProgressCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link StudentKnowledgeProgressDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class StudentKnowledgeProgressQueryService extends QueryService<StudentKnowledgeProgress> {

    private static final Logger LOG = LoggerFactory.getLogger(StudentKnowledgeProgressQueryService.class);

    private final StudentKnowledgeProgressRepository studentKnowledgeProgressRepository;

    private final StudentKnowledgeProgressMapper studentKnowledgeProgressMapper;

    public StudentKnowledgeProgressQueryService(
        StudentKnowledgeProgressRepository studentKnowledgeProgressRepository,
        StudentKnowledgeProgressMapper studentKnowledgeProgressMapper
    ) {
        this.studentKnowledgeProgressRepository = studentKnowledgeProgressRepository;
        this.studentKnowledgeProgressMapper = studentKnowledgeProgressMapper;
    }

    /**
     * Return a {@link Page} of {@link StudentKnowledgeProgressDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<StudentKnowledgeProgressDTO> findByCriteria(StudentKnowledgeProgressCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<StudentKnowledgeProgress> specification = createSpecification(criteria);
        return studentKnowledgeProgressRepository.findAll(specification, page).map(studentKnowledgeProgressMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(StudentKnowledgeProgressCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<StudentKnowledgeProgress> specification = createSpecification(criteria);
        return studentKnowledgeProgressRepository.count(specification);
    }

    /**
     * Function to convert {@link StudentKnowledgeProgressCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<StudentKnowledgeProgress> createSpecification(StudentKnowledgeProgressCriteria criteria) {
        Specification<StudentKnowledgeProgress> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(StudentKnowledgeProgress_.student, JoinType.LEFT);
                root.fetch(StudentKnowledgeProgress_.knowledgeNode, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), StudentKnowledgeProgress_.id),
                    buildRangeSpecification(criteria.getMasteryScore(), StudentKnowledgeProgress_.masteryScore),
                    buildRangeSpecification(criteria.getEvidenceCount(), StudentKnowledgeProgress_.evidenceCount),
                    buildStringSpecification(criteria.getCalculationVersion(), StudentKnowledgeProgress_.calculationVersion),
                    buildRangeSpecification(criteria.getUpdatedAt(), StudentKnowledgeProgress_.updatedAt),
                    buildSpecification(criteria.getStudentId(), root ->
                        root.join(StudentKnowledgeProgress_.student, JoinType.LEFT).get(User_.id)
                    ),
                    buildSpecification(criteria.getKnowledgeNodeId(), root ->
                        root.join(StudentKnowledgeProgress_.knowledgeNode, JoinType.LEFT).get(KnowledgeNode_.id)
                    )
                )
            );
        }
        return specification;
    }
}
