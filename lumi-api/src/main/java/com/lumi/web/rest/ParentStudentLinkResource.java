package com.lumi.web.rest;

import com.lumi.repository.ParentStudentLinkRepository;
import com.lumi.service.ParentStudentLinkQueryService;
import com.lumi.service.ParentStudentLinkService;
import com.lumi.service.criteria.ParentStudentLinkCriteria;
import com.lumi.service.dto.ParentStudentLinkDTO;
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
 * REST controller for managing {@link com.lumi.domain.ParentStudentLink}.
 */
@RestController
@RequestMapping("/api/parent-student-links")
public class ParentStudentLinkResource {

    private static final Logger LOG = LoggerFactory.getLogger(ParentStudentLinkResource.class);

    private static final String ENTITY_NAME = "parentStudentLink";

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    private final ParentStudentLinkService parentStudentLinkService;

    private final ParentStudentLinkRepository parentStudentLinkRepository;

    private final ParentStudentLinkQueryService parentStudentLinkQueryService;

    public ParentStudentLinkResource(
        ParentStudentLinkService parentStudentLinkService,
        ParentStudentLinkRepository parentStudentLinkRepository,
        ParentStudentLinkQueryService parentStudentLinkQueryService
    ) {
        this.parentStudentLinkService = parentStudentLinkService;
        this.parentStudentLinkRepository = parentStudentLinkRepository;
        this.parentStudentLinkQueryService = parentStudentLinkQueryService;
    }

    /**
     * {@code POST  /parent-student-links} : Create a new parentStudentLink.
     *
     * @param parentStudentLinkDTO the parentStudentLinkDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new parentStudentLinkDTO, or with status {@code 400 (Bad Request)} if the parentStudentLink already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ParentStudentLinkDTO> createParentStudentLink(@Valid @RequestBody ParentStudentLinkDTO parentStudentLinkDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ParentStudentLink : {}", parentStudentLinkDTO);
        if (parentStudentLinkDTO.getId() != null) {
            throw new BadRequestAlertException("A new parentStudentLink cannot already have an ID", ENTITY_NAME, "idexists");
        }
        parentStudentLinkDTO = parentStudentLinkService.save(parentStudentLinkDTO);
        return ResponseEntity.created(new URI("/api/parent-student-links/" + parentStudentLinkDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, parentStudentLinkDTO.getId().toString()))
            .body(parentStudentLinkDTO);
    }

    /**
     * {@code PUT  /parent-student-links/:id} : Updates an existing parentStudentLink.
     *
     * @param id the id of the parentStudentLinkDTO to save.
     * @param parentStudentLinkDTO the parentStudentLinkDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated parentStudentLinkDTO,
     * or with status {@code 400 (Bad Request)} if the parentStudentLinkDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the parentStudentLinkDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ParentStudentLinkDTO> updateParentStudentLink(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ParentStudentLinkDTO parentStudentLinkDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ParentStudentLink : {}, {}", id, parentStudentLinkDTO);
        if (parentStudentLinkDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, parentStudentLinkDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!parentStudentLinkRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        parentStudentLinkDTO = parentStudentLinkService.update(parentStudentLinkDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, parentStudentLinkDTO.getId().toString()))
            .body(parentStudentLinkDTO);
    }

    /**
     * {@code PATCH  /parent-student-links/:id} : Partial updates given fields of an existing parentStudentLink, field will ignore if it is null
     *
     * @param id the id of the parentStudentLinkDTO to save.
     * @param parentStudentLinkDTO the parentStudentLinkDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated parentStudentLinkDTO,
     * or with status {@code 400 (Bad Request)} if the parentStudentLinkDTO is not valid,
     * or with status {@code 404 (Not Found)} if the parentStudentLinkDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the parentStudentLinkDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ParentStudentLinkDTO> partialUpdateParentStudentLink(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ParentStudentLinkDTO parentStudentLinkDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update ParentStudentLink : {}, {}", id, parentStudentLinkDTO);
        if (parentStudentLinkDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, parentStudentLinkDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!parentStudentLinkRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ParentStudentLinkDTO> result = parentStudentLinkService.partialUpdate(parentStudentLinkDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, parentStudentLinkDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /parent-student-links} : get all the Parent Student Links.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Parent Student Links in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ParentStudentLinkDTO>> getAllParentStudentLinks(ParentStudentLinkCriteria criteria) {
        LOG.debug("REST request to get ParentStudentLinks by criteria: {}", criteria);

        List<ParentStudentLinkDTO> entityList = parentStudentLinkQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /parent-student-links/count} : count all the parentStudentLinks.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countParentStudentLinks(ParentStudentLinkCriteria criteria) {
        LOG.debug("REST request to count ParentStudentLinks by criteria: {}", criteria);
        return ResponseEntity.ok().body(parentStudentLinkQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /parent-student-links/:id} : get the "id" parentStudentLink.
     *
     * @param id the id of the parentStudentLinkDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the parentStudentLinkDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ParentStudentLinkDTO> getParentStudentLink(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ParentStudentLink : {}", id);
        Optional<ParentStudentLinkDTO> parentStudentLinkDTO = parentStudentLinkService.findOne(id);
        return ResponseUtil.wrapOrNotFound(parentStudentLinkDTO);
    }

    /**
     * {@code DELETE  /parent-student-links/:id} : delete the "id" parentStudentLink.
     *
     * @param id the id of the parentStudentLinkDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParentStudentLink(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ParentStudentLink : {}", id);
        parentStudentLinkService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
