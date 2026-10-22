package com.lumi.web.rest;

import com.lumi.repository.AssignmentSubmissionRepository;
import com.lumi.service.AssignmentSubmissionQueryService;
import com.lumi.service.AssignmentSubmissionService;
import com.lumi.service.criteria.AssignmentSubmissionCriteria;
import com.lumi.service.dto.AssignmentSubmissionDTO;
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
 * REST controller for managing {@link com.lumi.domain.AssignmentSubmission}.
 */
@RestController
@RequestMapping("/api/assignment-submissions")
public class AssignmentSubmissionResource {

    private static final Logger LOG = LoggerFactory.getLogger(AssignmentSubmissionResource.class);

    private static final String ENTITY_NAME = "assignmentSubmission";

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    private final AssignmentSubmissionService assignmentSubmissionService;

    private final AssignmentSubmissionRepository assignmentSubmissionRepository;

    private final AssignmentSubmissionQueryService assignmentSubmissionQueryService;

    public AssignmentSubmissionResource(
        AssignmentSubmissionService assignmentSubmissionService,
        AssignmentSubmissionRepository assignmentSubmissionRepository,
        AssignmentSubmissionQueryService assignmentSubmissionQueryService
    ) {
        this.assignmentSubmissionService = assignmentSubmissionService;
        this.assignmentSubmissionRepository = assignmentSubmissionRepository;
        this.assignmentSubmissionQueryService = assignmentSubmissionQueryService;
    }

    /**
     * {@code POST  /assignment-submissions} : Create a new assignmentSubmission.
     *
     * @param assignmentSubmissionDTO the assignmentSubmissionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new assignmentSubmissionDTO, or with status {@code 400 (Bad Request)} if the assignmentSubmission already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AssignmentSubmissionDTO> createAssignmentSubmission(
        @Valid @RequestBody AssignmentSubmissionDTO assignmentSubmissionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save AssignmentSubmission : {}", assignmentSubmissionDTO);
        if (assignmentSubmissionDTO.getId() != null) {
            throw new BadRequestAlertException("A new assignmentSubmission cannot already have an ID", ENTITY_NAME, "idexists");
        }
        assignmentSubmissionDTO = assignmentSubmissionService.save(assignmentSubmissionDTO);
        return ResponseEntity.created(new URI("/api/assignment-submissions/" + assignmentSubmissionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, assignmentSubmissionDTO.getId().toString()))
            .body(assignmentSubmissionDTO);
    }

    /**
     * {@code PUT  /assignment-submissions/:id} : Updates an existing assignmentSubmission.
     *
     * @param id the id of the assignmentSubmissionDTO to save.
     * @param assignmentSubmissionDTO the assignmentSubmissionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated assignmentSubmissionDTO,
     * or with status {@code 400 (Bad Request)} if the assignmentSubmissionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the assignmentSubmissionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AssignmentSubmissionDTO> updateAssignmentSubmission(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AssignmentSubmissionDTO assignmentSubmissionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AssignmentSubmission : {}, {}", id, assignmentSubmissionDTO);
        if (assignmentSubmissionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, assignmentSubmissionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!assignmentSubmissionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        assignmentSubmissionDTO = assignmentSubmissionService.update(assignmentSubmissionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, assignmentSubmissionDTO.getId().toString()))
            .body(assignmentSubmissionDTO);
    }

    /**
     * {@code PATCH  /assignment-submissions/:id} : Partial updates given fields of an existing assignmentSubmission, field will ignore if it is null
     *
     * @param id the id of the assignmentSubmissionDTO to save.
     * @param assignmentSubmissionDTO the assignmentSubmissionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated assignmentSubmissionDTO,
     * or with status {@code 400 (Bad Request)} if the assignmentSubmissionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the assignmentSubmissionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the assignmentSubmissionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AssignmentSubmissionDTO> partialUpdateAssignmentSubmission(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AssignmentSubmissionDTO assignmentSubmissionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update AssignmentSubmission : {}, {}", id, assignmentSubmissionDTO);
        if (assignmentSubmissionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, assignmentSubmissionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!assignmentSubmissionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AssignmentSubmissionDTO> result = assignmentSubmissionService.partialUpdate(assignmentSubmissionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, assignmentSubmissionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /assignment-submissions} : get all the Assignment Submissions.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Assignment Submissions in body.
     */
    @GetMapping("")
    public ResponseEntity<List<AssignmentSubmissionDTO>> getAllAssignmentSubmissions(
        AssignmentSubmissionCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get AssignmentSubmissions by criteria: {}", criteria);

        Page<AssignmentSubmissionDTO> page = assignmentSubmissionQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /assignment-submissions/count} : count all the assignmentSubmissions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countAssignmentSubmissions(AssignmentSubmissionCriteria criteria) {
        LOG.debug("REST request to count AssignmentSubmissions by criteria: {}", criteria);
        return ResponseEntity.ok().body(assignmentSubmissionQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /assignment-submissions/:id} : get the "id" assignmentSubmission.
     *
     * @param id the id of the assignmentSubmissionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the assignmentSubmissionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AssignmentSubmissionDTO> getAssignmentSubmission(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AssignmentSubmission : {}", id);
        Optional<AssignmentSubmissionDTO> assignmentSubmissionDTO = assignmentSubmissionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(assignmentSubmissionDTO);
    }

    /**
     * {@code DELETE  /assignment-submissions/:id} : delete the "id" assignmentSubmission.
     *
     * @param id the id of the assignmentSubmissionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssignmentSubmission(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AssignmentSubmission : {}", id);
        assignmentSubmissionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
