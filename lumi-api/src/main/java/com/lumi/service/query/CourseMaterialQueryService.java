package com.lumi.service.query;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.CourseMaterial;
import com.lumi.repository.CourseMaterialRepository;
import com.lumi.service.criteria.CourseMaterialCriteria;
import com.lumi.service.dto.CourseMaterialDTO;
import com.lumi.service.mapper.CourseMaterialMapper;
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
 * Service for executing complex queries for {@link CourseMaterial} entities in the database.
 * The main input is a {@link CourseMaterialCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link CourseMaterialDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CourseMaterialQueryService extends QueryService<CourseMaterial> {

    private static final Logger LOG = LoggerFactory.getLogger(CourseMaterialQueryService.class);

    private final CourseMaterialRepository courseMaterialRepository;

    private final CourseMaterialMapper courseMaterialMapper;

    public CourseMaterialQueryService(CourseMaterialRepository courseMaterialRepository, CourseMaterialMapper courseMaterialMapper) {
        this.courseMaterialRepository = courseMaterialRepository;
        this.courseMaterialMapper = courseMaterialMapper;
    }

    /**
     * Return a {@link Page} of {@link CourseMaterialDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CourseMaterialDTO> findByCriteria(CourseMaterialCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<CourseMaterial> specification = createSpecification(criteria);
        return courseMaterialRepository.findAll(specification, page).map(courseMaterialMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CourseMaterialCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<CourseMaterial> specification = createSpecification(criteria);
        return courseMaterialRepository.count(specification);
    }

    /**
     * Function to convert {@link CourseMaterialCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<CourseMaterial> createSpecification(CourseMaterialCriteria criteria) {
        Specification<CourseMaterial> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(CourseMaterial_.lesson, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), CourseMaterial_.id),
                    buildStringSpecification(criteria.getFileName(), CourseMaterial_.fileName),
                    buildStringSpecification(criteria.getDisplayName(), CourseMaterial_.displayName),
                    buildStringSpecification(criteria.getMimeType(), CourseMaterial_.mimeType),
                    buildRangeSpecification(criteria.getOrderIndex(), CourseMaterial_.orderIndex),
                    buildStringSpecification(criteria.getFileType(), CourseMaterial_.fileType),
                    buildStringSpecification(criteria.getFileUrl(), CourseMaterial_.fileUrl),
                    buildRangeSpecification(criteria.getFileSizeBytes(), CourseMaterial_.fileSizeBytes),
                    buildStringSpecification(criteria.getFileObjectKey(), CourseMaterial_.fileObjectKey),
                    buildRangeSpecification(criteria.getUpdatedAt(), CourseMaterial_.updatedAt),
                    buildSpecification(criteria.getLessonId(), root -> root.join(CourseMaterial_.lesson, JoinType.LEFT).get(Lesson_.id)),
                    buildSpecification(criteria.getLearningDocumentId(), root ->
                        root.join(CourseMaterial_.learningDocument, JoinType.LEFT).get(LearningDocument_.id)
                    )
                )
            );
        }
        return specification;
    }
}
