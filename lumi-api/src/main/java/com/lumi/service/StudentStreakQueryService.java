package com.lumi.service;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.StudentStreak;
import com.lumi.repository.StudentStreakRepository;
import com.lumi.service.criteria.StudentStreakCriteria;
import com.lumi.service.dto.StudentStreakDTO;
import com.lumi.service.mapper.StudentStreakMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link StudentStreak} entities in the database.
 * The main input is a {@link StudentStreakCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link StudentStreakDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class StudentStreakQueryService extends QueryService<StudentStreak> {

    private static final Logger LOG = LoggerFactory.getLogger(StudentStreakQueryService.class);

    private final StudentStreakRepository studentStreakRepository;

    private final StudentStreakMapper studentStreakMapper;

    public StudentStreakQueryService(StudentStreakRepository studentStreakRepository, StudentStreakMapper studentStreakMapper) {
        this.studentStreakRepository = studentStreakRepository;
        this.studentStreakMapper = studentStreakMapper;
    }

    /**
     * Return a {@link List} of {@link StudentStreakDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<StudentStreakDTO> findByCriteria(StudentStreakCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<StudentStreak> specification = createSpecification(criteria);
        return studentStreakMapper.toDto(studentStreakRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(StudentStreakCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<StudentStreak> specification = createSpecification(criteria);
        return studentStreakRepository.count(specification);
    }

    /**
     * Function to convert {@link StudentStreakCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<StudentStreak> createSpecification(StudentStreakCriteria criteria) {
        Specification<StudentStreak> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(StudentStreak_.user, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), StudentStreak_.id),
                    buildRangeSpecification(criteria.getCurrentStreak(), StudentStreak_.currentStreak),
                    buildRangeSpecification(criteria.getLongestStreak(), StudentStreak_.longestStreak),
                    buildRangeSpecification(criteria.getGemsBalance(), StudentStreak_.gemsBalance),
                    buildRangeSpecification(criteria.getLastActiveDate(), StudentStreak_.lastActiveDate),
                    buildSpecification(criteria.getUserId(), root -> root.join(StudentStreak_.user, JoinType.LEFT).get(User_.id))
                )
            );
        }
        return specification;
    }
}
