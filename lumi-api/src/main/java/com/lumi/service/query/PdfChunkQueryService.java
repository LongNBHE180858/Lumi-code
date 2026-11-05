package com.lumi.service.query;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.PdfChunk;
import com.lumi.repository.PdfChunkRepository;
import com.lumi.service.criteria.PdfChunkCriteria;
import com.lumi.service.dto.PdfChunkDTO;
import com.lumi.service.mapper.PdfChunkMapper;
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
 * Service for executing complex queries for {@link PdfChunk} entities in the database.
 * The main input is a {@link PdfChunkCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link PdfChunkDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PdfChunkQueryService extends QueryService<PdfChunk> {

    private static final Logger LOG = LoggerFactory.getLogger(PdfChunkQueryService.class);

    private final PdfChunkRepository pdfChunkRepository;

    private final PdfChunkMapper pdfChunkMapper;

    public PdfChunkQueryService(PdfChunkRepository pdfChunkRepository, PdfChunkMapper pdfChunkMapper) {
        this.pdfChunkRepository = pdfChunkRepository;
        this.pdfChunkMapper = pdfChunkMapper;
    }

    /**
     * Return a {@link Page} of {@link PdfChunkDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PdfChunkDTO> findByCriteria(PdfChunkCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<PdfChunk> specification = createSpecification(criteria);
        return pdfChunkRepository.findAll(specification, page).map(pdfChunkMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PdfChunkCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<PdfChunk> specification = createSpecification(criteria);
        return pdfChunkRepository.count(specification);
    }

    /**
     * Function to convert {@link PdfChunkCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<PdfChunk> createSpecification(PdfChunkCriteria criteria) {
        Specification<PdfChunk> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(PdfChunk_.material, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), PdfChunk_.id),
                    buildRangeSpecification(criteria.getPageNumber(), PdfChunk_.pageNumber),
                    buildRangeSpecification(criteria.getChunkIndex(), PdfChunk_.chunkIndex),
                    buildRangeSpecification(criteria.getIndexVersion(), PdfChunk_.indexVersion),
                    buildSpecification(criteria.getMaterialId(), root ->
                        root.join(PdfChunk_.material, JoinType.LEFT).get(LearningDocument_.id)
                    )
                )
            );
        }
        return specification;
    }
}
