package com.lumi.service.query;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.StudyClass;
import com.lumi.repository.StudyClassRepository;
import com.lumi.service.criteria.StudyClassCriteria;
import com.lumi.service.dto.StudyClassDTO;
import com.lumi.service.mapper.StudyClassMapper;
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
 * Service for executing complex queries for {@link StudyClass} entities in the database.
 * The main input is a {@link StudyClassCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link StudyClassDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class StudyClassQueryService extends QueryService<StudyClass> {

    private static final Logger LOG = LoggerFactory.getLogger(StudyClassQueryService.class);

    private final StudyClassRepository studyClassRepository;

    private final StudyClassMapper studyClassMapper;

    public StudyClassQueryService(StudyClassRepository studyClassRepository, StudyClassMapper studyClassMapper) {
        this.studyClassRepository = studyClassRepository;
        this.studyClassMapper = studyClassMapper;
    }

    /**
     * Return a {@link Page} of {@link StudyClassDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<StudyClassDTO> findByCriteria(StudyClassCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<StudyClass> specification = createSpecification(criteria);
        return studyClassRepository.findAll(specification, page).map(studyClassMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(StudyClassCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<StudyClass> specification = createSpecification(criteria);
        return studyClassRepository.count(specification);
    }

    /**
     * Function to convert {@link StudyClassCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<StudyClass> createSpecification(StudyClassCriteria criteria) {
        Specification<StudyClass> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(StudyClass_.instructor, JoinType.LEFT);
                root.fetch(StudyClass_.subject, JoinType.LEFT);
                root.fetch(StudyClass_.grade, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), StudyClass_.id),
                    buildStringSpecification(criteria.getName(), StudyClass_.name),
                    buildStringSpecification(criteria.getJoinCode(), StudyClass_.joinCode),
                    buildSpecification(criteria.getJoinMode(), StudyClass_.joinMode),
                    buildRangeSpecification(criteria.getMaxStudents(), StudyClass_.maxStudents),
                    buildRangeSpecification(criteria.getCurrentStudents(), StudyClass_.currentStudents),
                    buildSpecification(criteria.getStatus(), StudyClass_.status),
                    buildSpecification(criteria.getIsDeleted(), StudyClass_.isDeleted),
                    buildRangeSpecification(criteria.getCreatedAt(), StudyClass_.createdAt),
                    buildRangeSpecification(criteria.getJoinCodeExpiresAt(), StudyClass_.joinCodeExpiresAt),
                    buildRangeSpecification(criteria.getUpdatedAt(), StudyClass_.updatedAt),
                    buildSpecification(criteria.getInstructorId(), root -> root.join(StudyClass_.instructor, JoinType.LEFT).get(User_.id)),
                    buildSpecification(criteria.getSubjectId(), root -> root.join(StudyClass_.subject, JoinType.LEFT).get(Subject_.id)),
                    buildSpecification(criteria.getGradeId(), root -> root.join(StudyClass_.grade, JoinType.LEFT).get(Grade_.id))
                )
            );
        }
        return specification;
    }
}
