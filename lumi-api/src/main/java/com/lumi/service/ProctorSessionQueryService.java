package com.lumi.service;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.ProctorSession;
import com.lumi.repository.ProctorSessionRepository;
import com.lumi.service.criteria.ProctorSessionCriteria;
import com.lumi.service.dto.ProctorSessionDTO;
import com.lumi.service.mapper.ProctorSessionMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link ProctorSession} entities in the database.
 * The main input is a {@link ProctorSessionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ProctorSessionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProctorSessionQueryService extends QueryService<ProctorSession> {

    private static final Logger LOG = LoggerFactory.getLogger(ProctorSessionQueryService.class);

    private final ProctorSessionRepository proctorSessionRepository;

    private final ProctorSessionMapper proctorSessionMapper;

    public ProctorSessionQueryService(ProctorSessionRepository proctorSessionRepository, ProctorSessionMapper proctorSessionMapper) {
        this.proctorSessionRepository = proctorSessionRepository;
        this.proctorSessionMapper = proctorSessionMapper;
    }

    /**
     * Return a {@link List} of {@link ProctorSessionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ProctorSessionDTO> findByCriteria(ProctorSessionCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<ProctorSession> specification = createSpecification(criteria);
        return proctorSessionMapper.toDto(proctorSessionRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProctorSessionCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<ProctorSession> specification = createSpecification(criteria);
        return proctorSessionRepository.count(specification);
    }

    /**
     * Function to convert {@link ProctorSessionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ProctorSession> createSpecification(ProctorSessionCriteria criteria) {
        Specification<ProctorSession> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(ProctorSession_.attempt, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), ProctorSession_.id),
                    buildSpecification(criteria.getFaceVerified(), ProctorSession_.faceVerified),
                    buildRangeSpecification(criteria.getWarningCount(), ProctorSession_.warningCount),
                    buildStringSpecification(criteria.getStatus(), ProctorSession_.status),
                    buildSpecification(criteria.getAttemptId(), root ->
                        root.join(ProctorSession_.attempt, JoinType.LEFT).get(ExamAttempt_.id)
                    )
                )
            );
        }
        return specification;
    }
}
