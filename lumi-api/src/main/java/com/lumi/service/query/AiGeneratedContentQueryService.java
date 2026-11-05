package com.lumi.service.query;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.AiGeneratedContent;
import com.lumi.repository.AiGeneratedContentRepository;
import com.lumi.service.criteria.AiGeneratedContentCriteria;
import com.lumi.service.dto.AiGeneratedContentDTO;
import com.lumi.service.mapper.AiGeneratedContentMapper;
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
 * Service for executing complex queries for {@link AiGeneratedContent} entities in the database.
 * The main input is a {@link AiGeneratedContentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link AiGeneratedContentDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AiGeneratedContentQueryService extends QueryService<AiGeneratedContent> {

    private static final Logger LOG = LoggerFactory.getLogger(AiGeneratedContentQueryService.class);

    private final AiGeneratedContentRepository aiGeneratedContentRepository;

    private final AiGeneratedContentMapper aiGeneratedContentMapper;

    public AiGeneratedContentQueryService(
        AiGeneratedContentRepository aiGeneratedContentRepository,
        AiGeneratedContentMapper aiGeneratedContentMapper
    ) {
        this.aiGeneratedContentRepository = aiGeneratedContentRepository;
        this.aiGeneratedContentMapper = aiGeneratedContentMapper;
    }

    /**
     * Return a {@link Page} of {@link AiGeneratedContentDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<AiGeneratedContentDTO> findByCriteria(AiGeneratedContentCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<AiGeneratedContent> specification = createSpecification(criteria);
        return aiGeneratedContentRepository.findAll(specification, page).map(aiGeneratedContentMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AiGeneratedContentCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<AiGeneratedContent> specification = createSpecification(criteria);
        return aiGeneratedContentRepository.count(specification);
    }

    /**
     * Function to convert {@link AiGeneratedContentCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<AiGeneratedContent> createSpecification(AiGeneratedContentCriteria criteria) {
        Specification<AiGeneratedContent> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(AiGeneratedContent_.createdBy, JoinType.LEFT);
                root.fetch(AiGeneratedContent_.document, JoinType.LEFT);
                root.fetch(AiGeneratedContent_.lesson, JoinType.LEFT);
                root.fetch(AiGeneratedContent_.reviewedBy, JoinType.LEFT);
                root.fetch(AiGeneratedContent_.course, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), AiGeneratedContent_.id),
                    buildStringSpecification(criteria.getContentType(), AiGeneratedContent_.contentType),
                    buildStringSpecification(criteria.getRequestHash(), AiGeneratedContent_.requestHash),
                    buildStringSpecification(criteria.getSourceVersion(), AiGeneratedContent_.sourceVersion),
                    buildStringSpecification(criteria.getModelName(), AiGeneratedContent_.modelName),
                    buildStringSpecification(criteria.getPromptVersion(), AiGeneratedContent_.promptVersion),
                    buildStringSpecification(criteria.getGenerationStatus(), AiGeneratedContent_.generationStatus),
                    buildStringSpecification(criteria.getVisibility(), AiGeneratedContent_.visibility),
                    buildRangeSpecification(criteria.getVersion(), AiGeneratedContent_.version),
                    buildStringSpecification(criteria.getReviewStatus(), AiGeneratedContent_.reviewStatus),
                    buildRangeSpecification(criteria.getReviewedAt(), AiGeneratedContent_.reviewedAt),
                    buildRangeSpecification(criteria.getCreatedAt(), AiGeneratedContent_.createdAt),
                    buildRangeSpecification(criteria.getUpdatedAt(), AiGeneratedContent_.updatedAt),
                    buildRangeSpecification(criteria.getSourceQuestionId(), AiGeneratedContent_.sourceQuestionId),
                    buildStringSpecification(criteria.getContentHash(), AiGeneratedContent_.contentHash),
                    buildSpecification(criteria.getCreatedById(), root ->
                        root.join(AiGeneratedContent_.createdBy, JoinType.LEFT).get(User_.id)
                    ),
                    buildSpecification(criteria.getDocumentId(), root ->
                        root.join(AiGeneratedContent_.document, JoinType.LEFT).get(LearningDocument_.id)
                    ),
                    buildSpecification(criteria.getLessonId(), root ->
                        root.join(AiGeneratedContent_.lesson, JoinType.LEFT).get(Lesson_.id)
                    ),
                    buildSpecification(criteria.getReviewedById(), root ->
                        root.join(AiGeneratedContent_.reviewedBy, JoinType.LEFT).get(User_.id)
                    ),
                    buildSpecification(criteria.getCourseId(), root -> root.join(AiGeneratedContent_.course, JoinType.LEFT).get(Course_.id))
                )
            );
        }
        return specification;
    }
}
