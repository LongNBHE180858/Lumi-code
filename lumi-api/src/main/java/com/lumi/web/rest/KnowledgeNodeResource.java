package com.lumi.web.rest;

import com.lumi.repository.KnowledgeNodeRepository;
import com.lumi.service.KnowledgeNodeQueryService;
import com.lumi.service.KnowledgeNodeService;
import com.lumi.service.criteria.KnowledgeNodeCriteria;
import com.lumi.service.dto.KnowledgeNodeDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.lumi.domain.KnowledgeNode}.
 */
@RestController
@RequestMapping("/api/knowledge-nodes")
public class KnowledgeNodeResource {

    private static final Logger LOG = LoggerFactory.getLogger(KnowledgeNodeResource.class);

    private static final String ENTITY_NAME = "knowledgeNode";

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    private final KnowledgeNodeService knowledgeNodeService;

    private final KnowledgeNodeRepository knowledgeNodeRepository;

    private final KnowledgeNodeQueryService knowledgeNodeQueryService;

    public KnowledgeNodeResource(
        KnowledgeNodeService knowledgeNodeService,
        KnowledgeNodeRepository knowledgeNodeRepository,
        KnowledgeNodeQueryService knowledgeNodeQueryService
    ) {
        this.knowledgeNodeService = knowledgeNodeService;
        this.knowledgeNodeRepository = knowledgeNodeRepository;
        this.knowledgeNodeQueryService = knowledgeNodeQueryService;
    }

    /**
     * {@code POST  /knowledge-nodes} : Create a new knowledgeNode.
     *
     * @param knowledgeNodeDTO the knowledgeNodeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new knowledgeNodeDTO, or with status {@code 400 (Bad Request)} if the knowledgeNode already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<KnowledgeNodeDTO> createKnowledgeNode(@Valid @RequestBody KnowledgeNodeDTO knowledgeNodeDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save KnowledgeNode : {}", knowledgeNodeDTO);
        if (knowledgeNodeDTO.getId() != null) {
            throw new BadRequestAlertException("A new knowledgeNode cannot already have an ID", ENTITY_NAME, "idexists");
        }
        knowledgeNodeDTO = knowledgeNodeService.save(knowledgeNodeDTO);
        return ResponseEntity.created(new URI("/api/knowledge-nodes/" + knowledgeNodeDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, knowledgeNodeDTO.getId().toString()))
            .body(knowledgeNodeDTO);
    }

    /**
     * {@code PUT  /knowledge-nodes/:id} : Updates an existing knowledgeNode.
     *
     * @param id the id of the knowledgeNodeDTO to save.
     * @param knowledgeNodeDTO the knowledgeNodeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated knowledgeNodeDTO,
     * or with status {@code 400 (Bad Request)} if the knowledgeNodeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the knowledgeNodeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<KnowledgeNodeDTO> updateKnowledgeNode(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody KnowledgeNodeDTO knowledgeNodeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update KnowledgeNode : {}, {}", id, knowledgeNodeDTO);
        if (knowledgeNodeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, knowledgeNodeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!knowledgeNodeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        knowledgeNodeDTO = knowledgeNodeService.update(knowledgeNodeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, knowledgeNodeDTO.getId().toString()))
            .body(knowledgeNodeDTO);
    }

    /**
     * {@code PATCH  /knowledge-nodes/:id} : Partial updates given fields of an existing knowledgeNode, field will ignore if it is null
     *
     * @param id the id of the knowledgeNodeDTO to save.
     * @param knowledgeNodeDTO the knowledgeNodeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated knowledgeNodeDTO,
     * or with status {@code 400 (Bad Request)} if the knowledgeNodeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the knowledgeNodeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the knowledgeNodeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<KnowledgeNodeDTO> partialUpdateKnowledgeNode(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody KnowledgeNodeDTO knowledgeNodeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update KnowledgeNode : {}, {}", id, knowledgeNodeDTO);
        if (knowledgeNodeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, knowledgeNodeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!knowledgeNodeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<KnowledgeNodeDTO> result = knowledgeNodeService.partialUpdate(knowledgeNodeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, knowledgeNodeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /knowledge-nodes} : get all the Knowledge Nodes.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Knowledge Nodes in body.
     */
    @GetMapping("")
    public ResponseEntity<List<KnowledgeNodeDTO>> getAllKnowledgeNodes(
        KnowledgeNodeCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get KnowledgeNodes by criteria: {}", criteria);

        Page<KnowledgeNodeDTO> page = knowledgeNodeQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /knowledge-nodes/count} : count all the knowledgeNodes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countKnowledgeNodes(KnowledgeNodeCriteria criteria) {
        LOG.debug("REST request to count KnowledgeNodes by criteria: {}", criteria);
        return ResponseEntity.ok().body(knowledgeNodeQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /knowledge-nodes/:id} : get the "id" knowledgeNode.
     *
     * @param id the id of the knowledgeNodeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the knowledgeNodeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<KnowledgeNodeDTO> getKnowledgeNode(@PathVariable("id") Long id) {
        LOG.debug("REST request to get KnowledgeNode : {}", id);
        Optional<KnowledgeNodeDTO> knowledgeNodeDTO = knowledgeNodeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(knowledgeNodeDTO);
    }

    /**
     * {@code DELETE  /knowledge-nodes/:id} : delete the "id" knowledgeNode.
     *
     * @param id the id of the knowledgeNodeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKnowledgeNode(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete KnowledgeNode : {}", id);
        knowledgeNodeService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
