package com.lumi.service;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.ClassMaterial;
import com.lumi.repository.ClassMaterialRepository;
import com.lumi.service.criteria.ClassMaterialCriteria;
import com.lumi.service.dto.ClassMaterialDTO;
import com.lumi.service.mapper.ClassMaterialMapper;
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
 * Service for executing complex queries for {@link ClassMaterial} entities in the database.
 * The main input is a {@link ClassMaterialCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ClassMaterialDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ClassMaterialQueryService extends QueryService<ClassMaterial> {

    private static final Logger LOG = LoggerFactory.getLogger(ClassMaterialQueryService.class);

    private final ClassMaterialRepository classMaterialRepository;

    private final ClassMaterialMapper classMaterialMapper;

    public ClassMaterialQueryService(ClassMaterialRepository classMaterialRepository, ClassMaterialMapper classMaterialMapper) {
        this.classMaterialRepository = classMaterialRepository;
        this.classMaterialMapper = classMaterialMapper;
    }

    /**
     * Return a {@link Page} of {@link ClassMaterialDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ClassMaterialDTO> findByCriteria(ClassMaterialCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ClassMaterial> specification = createSpecification(criteria);
        return classMaterialRepository.findAll(specification, page).map(classMaterialMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ClassMaterialCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<ClassMaterial> specification = createSpecification(criteria);
        return classMaterialRepository.count(specification);
    }

    /**
     * Function to convert {@link ClassMaterialCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ClassMaterial> createSpecification(ClassMaterialCriteria criteria) {
        Specification<ClassMaterial> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(ClassMaterial_.studyClass, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), ClassMaterial_.id),
                    buildStringSpecification(criteria.getTitle(), ClassMaterial_.title),
                    buildStringSpecification(criteria.getFileType(), ClassMaterial_.fileType),
                    buildStringSpecification(criteria.getFileUrl(), ClassMaterial_.fileUrl),
                    buildRangeSpecification(criteria.getFileSizeBytes(), ClassMaterial_.fileSizeBytes),
                    buildRangeSpecification(criteria.getUploadedAt(), ClassMaterial_.uploadedAt),
                    buildSpecification(criteria.getStudyClassId(), root ->
                        root.join(ClassMaterial_.studyClass, JoinType.LEFT).get(StudyClass_.id)
                    )
                )
            );
        }
        return specification;
    }
}
