package com.lumi.service.query;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.Chapter;
import com.lumi.repository.ChapterRepository;
import com.lumi.service.criteria.ChapterCriteria;
import com.lumi.service.dto.ChapterDTO;
import com.lumi.service.mapper.ChapterMapper;
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
 * Service for executing complex queries for {@link Chapter} entities in the database.
 * The main input is a {@link ChapterCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ChapterDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ChapterQueryService extends QueryService<Chapter> {

    private static final Logger LOG = LoggerFactory.getLogger(ChapterQueryService.class);

    private final ChapterRepository chapterRepository;

    private final ChapterMapper chapterMapper;

    public ChapterQueryService(ChapterRepository chapterRepository, ChapterMapper chapterMapper) {
        this.chapterRepository = chapterRepository;
        this.chapterMapper = chapterMapper;
    }

    /**
     * Return a {@link Page} of {@link ChapterDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ChapterDTO> findByCriteria(ChapterCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Chapter> specification = createSpecification(criteria);
        return chapterRepository.findAll(specification, page).map(chapterMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ChapterCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Chapter> specification = createSpecification(criteria);
        return chapterRepository.count(specification);
    }

    /**
     * Function to convert {@link ChapterCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Chapter> createSpecification(ChapterCriteria criteria) {
        Specification<Chapter> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(Chapter_.course, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), Chapter_.id),
                    buildStringSpecification(criteria.getTitle(), Chapter_.title),
                    buildRangeSpecification(criteria.getOrderIndex(), Chapter_.orderIndex),
                    buildSpecification(criteria.getCourseId(), root -> root.join(Chapter_.course, JoinType.LEFT).get(Course_.id))
                )
            );
        }
        return specification;
    }
}
