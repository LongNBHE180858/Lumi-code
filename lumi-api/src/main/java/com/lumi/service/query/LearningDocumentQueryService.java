package com.lumi.service.query;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.LearningDocument;
import com.lumi.repository.LearningDocumentRepository;
import com.lumi.service.criteria.LearningDocumentCriteria;
import com.lumi.service.dto.LearningDocumentDTO;
import com.lumi.service.mapper.LearningDocumentMapper;
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
 * Service for executing complex queries for {@link LearningDocument} entities in the database.
 * The main input is a {@link LearningDocumentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link LearningDocumentDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class LearningDocumentQueryService extends QueryService<LearningDocument> {

    private static final Logger LOG = LoggerFactory.getLogger(LearningDocumentQueryService.class);

    private final LearningDocumentRepository learningDocumentRepository;

    private final LearningDocumentMapper learningDocumentMapper;

    public LearningDocumentQueryService(
        LearningDocumentRepository learningDocumentRepository,
        LearningDocumentMapper learningDocumentMapper
    ) {
        this.learningDocumentRepository = learningDocumentRepository;
        this.learningDocumentMapper = learningDocumentMapper;
    }

    /**
     * Return a {@link Page} of {@link LearningDocumentDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<LearningDocumentDTO> findByCriteria(LearningDocumentCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<LearningDocument> specification = createSpecification(criteria);
        return learningDocumentRepository.findAll(specification, page).map(learningDocumentMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(LearningDocumentCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<LearningDocument> specification = createSpecification(criteria);
        return learningDocumentRepository.count(specification);
    }

    /**
     * Function to convert {@link LearningDocumentCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<LearningDocument> createSpecification(LearningDocumentCriteria criteria) {
        Specification<LearningDocument> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(LearningDocument_.courseMaterial, JoinType.LEFT);
                root.fetch(LearningDocument_.classMaterial, JoinType.LEFT);
                root.fetch(LearningDocument_.uploadedBy, JoinType.LEFT);
                root.fetch(LearningDocument_.subject, JoinType.LEFT);
                root.fetch(LearningDocument_.grade, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), LearningDocument_.id),
                    buildStringSpecification(criteria.getSourceType(), LearningDocument_.sourceType),
                    buildStringSpecification(criteria.getTitle(), LearningDocument_.title),
                    buildStringSpecification(criteria.getFileObjectKey(), LearningDocument_.fileObjectKey),
                    buildStringSpecification(criteria.getContentHash(), LearningDocument_.contentHash),
                    buildRangeSpecification(criteria.getSourceVersion(), LearningDocument_.sourceVersion),
                    buildStringSpecification(criteria.getProcessingStatus(), LearningDocument_.processingStatus),
                    buildStringSpecification(criteria.getIndexStatus(), LearningDocument_.indexStatus),
                    buildStringSpecification(criteria.getExtractedTextObjectKey(), LearningDocument_.extractedTextObjectKey),
                    buildStringSpecification(criteria.getEmbeddingModel(), LearningDocument_.embeddingModel),
                    buildRangeSpecification(criteria.getCreatedAt(), LearningDocument_.createdAt),
                    buildRangeSpecification(criteria.getUpdatedAt(), LearningDocument_.updatedAt),
                    buildStringSpecification(criteria.getBookSeries(), LearningDocument_.bookSeries),
                    buildSpecification(criteria.getCourseMaterialId(), root ->
                        root.join(LearningDocument_.courseMaterial, JoinType.LEFT).get(CourseMaterial_.id)
                    ),
                    buildSpecification(criteria.getClassMaterialId(), root ->
                        root.join(LearningDocument_.classMaterial, JoinType.LEFT).get(ClassMaterial_.id)
                    ),
                    buildSpecification(criteria.getUploadedById(), root ->
                        root.join(LearningDocument_.uploadedBy, JoinType.LEFT).get(User_.id)
                    ),
                    buildSpecification(criteria.getSubjectId(), root ->
                        root.join(LearningDocument_.subject, JoinType.LEFT).get(Subject_.id)
                    ),
                    buildSpecification(criteria.getGradeId(), root -> root.join(LearningDocument_.grade, JoinType.LEFT).get(Grade_.id))
                )
            );
        }
        return specification;
    }
}
