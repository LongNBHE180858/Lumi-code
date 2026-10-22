package com.lumi.service;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.ClassMember;
import com.lumi.repository.ClassMemberRepository;
import com.lumi.service.criteria.ClassMemberCriteria;
import com.lumi.service.dto.ClassMemberDTO;
import com.lumi.service.mapper.ClassMemberMapper;
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
 * Service for executing complex queries for {@link ClassMember} entities in the database.
 * The main input is a {@link ClassMemberCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ClassMemberDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ClassMemberQueryService extends QueryService<ClassMember> {

    private static final Logger LOG = LoggerFactory.getLogger(ClassMemberQueryService.class);

    private final ClassMemberRepository classMemberRepository;

    private final ClassMemberMapper classMemberMapper;

    public ClassMemberQueryService(ClassMemberRepository classMemberRepository, ClassMemberMapper classMemberMapper) {
        this.classMemberRepository = classMemberRepository;
        this.classMemberMapper = classMemberMapper;
    }

    /**
     * Return a {@link Page} of {@link ClassMemberDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ClassMemberDTO> findByCriteria(ClassMemberCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ClassMember> specification = createSpecification(criteria);
        return classMemberRepository.findAll(specification, page).map(classMemberMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ClassMemberCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<ClassMember> specification = createSpecification(criteria);
        return classMemberRepository.count(specification);
    }

    /**
     * Function to convert {@link ClassMemberCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ClassMember> createSpecification(ClassMemberCriteria criteria) {
        Specification<ClassMember> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(ClassMember_.student, JoinType.LEFT);
                root.fetch(ClassMember_.studyClass, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), ClassMember_.id),
                    buildStringSpecification(criteria.getStatus(), ClassMember_.status),
                    buildRangeSpecification(criteria.getJoinedAt(), ClassMember_.joinedAt),
                    buildSpecification(criteria.getStudentId(), root -> root.join(ClassMember_.student, JoinType.LEFT).get(User_.id)),
                    buildSpecification(criteria.getStudyClassId(), root ->
                        root.join(ClassMember_.studyClass, JoinType.LEFT).get(StudyClass_.id)
                    )
                )
            );
        }
        return specification;
    }
}
