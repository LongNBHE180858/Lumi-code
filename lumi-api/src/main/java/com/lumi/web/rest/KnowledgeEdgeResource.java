package com.lumi.web.rest;

import com.lumi.repository.KnowledgeEdgeRepository;
import com.lumi.service.KnowledgeEdgeQueryService;
import com.lumi.service.KnowledgeEdgeService;
import com.lumi.service.criteria.KnowledgeEdgeCriteria;
import com.lumi.service.dto.KnowledgeEdgeDTO;
import com.lumi.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.lumi.domain.KnowledgeEdge}.
 */
@RestController
@RequestMapping("/api/knowledge-edges")
public class KnowledgeEdgeResource {

    private static final Logger LOG = LoggerFactory.getLogger(KnowledgeEdgeResource.class);

    private static final String ENTITY_NAME = "knowledgeEdge";

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    private final KnowledgeEdgeService knowledgeEdgeService;

    private final KnowledgeEdgeRepository knowledgeEdgeRepository;

    private final KnowledgeEdgeQueryService knowledgeEdgeQueryService;

    public KnowledgeEdgeResource(
        KnowledgeEdgeService knowledgeEdgeService,
        KnowledgeEdgeRepository knowledgeEdgeRepository,
        KnowledgeEdgeQueryService knowledgeEdgeQueryService
    ) {
        this.knowledgeEdgeService = knowledgeEdgeService;
        this.knowledgeEdgeRepository = knowledgeEdgeRepository;
        this.knowledgeEdgeQueryService = knowledgeEdgeQueryService;
    }

    /**
     * {@code POST  /knowledge-edges} : Create a new knowledgeEdge.
     *
     * @param knowledgeEdgeDTO the knowledgeEdgeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new knowledgeEdgeDTO, or with status {@code 400 (Bad Request)} if the knowledgeEdge already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<KnowledgeEdgeDTO> createKnowledgeEdge(@Valid @RequestBody KnowledgeEdgeDTO knowledgeEdgeDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save KnowledgeEdge : {}", knowledgeEdgeDTO);
        if (knowledgeEdgeDTO.getId() != null) {
            throw new BadRequestAlertException("A new knowledgeEdge cannot already have an ID", ENTITY_NAME, "idexists");
        }
        knowledgeEdgeDTO = knowledgeEdgeService.save(knowledgeEdgeDTO);
        return ResponseEntity.created(new URI("/api/knowledge-edges/" + knowledgeEdgeDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, knowledgeEdgeDTO.getId().toString()))
            .body(knowledgeEdgeDTO);
    }

    /**
     * {@code PUT  /knowledge-edges/:id} : Updates an existing knowledgeEdge.
     *
     * @param id the id of the knowledgeEdgeDTO to save.
     * @param knowledgeEdgeDTO the knowledgeEdgeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated knowledgeEdgeDTO,
     * or with status {@code 400 (Bad Request)} if the knowledgeEdgeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the knowledgeEdgeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<KnowledgeEdgeDTO> updateKnowledgeEdge(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody KnowledgeEdgeDTO knowledgeEdgeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update KnowledgeEdge : {}, {}", id, knowledgeEdgeDTO);
        if (knowledgeEdgeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, knowledgeEdgeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!knowledgeEdgeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        knowledgeEdgeDTO = knowledgeEdgeService.update(knowledgeEdgeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, knowledgeEdgeDTO.getId().toString()))
            .body(knowledgeEdgeDTO);
    }

    /**
     * {@code PATCH  /knowledge-edges/:id} : Partial updates given fields of an existing knowledgeEdge, field will ignore if it is null
     *
     * @param id the id of the knowledgeEdgeDTO to save.
     * @param knowledgeEdgeDTO the knowledgeEdgeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated knowledgeEdgeDTO,
     * or with status {@code 400 (Bad Request)} if the knowledgeEdgeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the knowledgeEdgeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the knowledgeEdgeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<KnowledgeEdgeDTO> partialUpdateKnowledgeEdge(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody KnowledgeEdgeDTO knowledgeEdgeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update KnowledgeEdge : {}, {}", id, knowledgeEdgeDTO);
        if (knowledgeEdgeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, knowledgeEdgeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!knowledgeEdgeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<KnowledgeEdgeDTO> result = knowledgeEdgeService.partialUpdate(knowledgeEdgeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, knowledgeEdgeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /knowledge-edges} : get all the Knowledge Edges.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Knowledge Edges in body.
     */
    @GetMapping("")
    public ResponseEntity<List<KnowledgeEdgeDTO>> getAllKnowledgeEdges(KnowledgeEdgeCriteria criteria) {
        LOG.debug("REST request to get KnowledgeEdges by criteria: {}", criteria);

        List<KnowledgeEdgeDTO> entityList = knowledgeEdgeQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /knowledge-edges/count} : count all the knowledgeEdges.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countKnowledgeEdges(KnowledgeEdgeCriteria criteria) {
        LOG.debug("REST request to count KnowledgeEdges by criteria: {}", criteria);
        return ResponseEntity.ok().body(knowledgeEdgeQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /knowledge-edges/:id} : get the "id" knowledgeEdge.
     *
     * @param id the id of the knowledgeEdgeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the knowledgeEdgeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<KnowledgeEdgeDTO> getKnowledgeEdge(@PathVariable("id") Long id) {
        LOG.debug("REST request to get KnowledgeEdge : {}", id);
        Optional<KnowledgeEdgeDTO> knowledgeEdgeDTO = knowledgeEdgeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(knowledgeEdgeDTO);
    }

    /**
     * {@code DELETE  /knowledge-edges/:id} : delete the "id" knowledgeEdge.
     *
     * @param id the id of the knowledgeEdgeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKnowledgeEdge(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete KnowledgeEdge : {}", id);
        knowledgeEdgeService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
