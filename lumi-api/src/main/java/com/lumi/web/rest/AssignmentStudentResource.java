package com.lumi.web.rest;

import com.lumi.repository.AssignmentStudentRepository;
import com.lumi.service.AssignmentStudentQueryService;
import com.lumi.service.AssignmentStudentService;
import com.lumi.service.criteria.AssignmentStudentCriteria;
import com.lumi.service.dto.AssignmentStudentDTO;
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
 * REST controller for managing {@link com.lumi.domain.AssignmentStudent}.
 */
@RestController
@RequestMapping("/api/assignment-students")
public class AssignmentStudentResource {

    private static final Logger LOG = LoggerFactory.getLogger(AssignmentStudentResource.class);

    private static final String ENTITY_NAME = "assignmentStudent";

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    private final AssignmentStudentService assignmentStudentService;

    private final AssignmentStudentRepository assignmentStudentRepository;

    private final AssignmentStudentQueryService assignmentStudentQueryService;

    public AssignmentStudentResource(
        AssignmentStudentService assignmentStudentService,
        AssignmentStudentRepository assignmentStudentRepository,
        AssignmentStudentQueryService assignmentStudentQueryService
    ) {
        this.assignmentStudentService = assignmentStudentService;
        this.assignmentStudentRepository = assignmentStudentRepository;
        this.assignmentStudentQueryService = assignmentStudentQueryService;
    }

    /**
     * {@code POST  /assignment-students} : Create a new assignmentStudent.
     *
     * @param assignmentStudentDTO the assignmentStudentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new assignmentStudentDTO, or with status {@code 400 (Bad Request)} if the assignmentStudent already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AssignmentStudentDTO> createAssignmentStudent(@Valid @RequestBody AssignmentStudentDTO assignmentStudentDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save AssignmentStudent : {}", assignmentStudentDTO);
        if (assignmentStudentDTO.getId() != null) {
            throw new BadRequestAlertException("A new assignmentStudent cannot already have an ID", ENTITY_NAME, "idexists");
        }
        assignmentStudentDTO = assignmentStudentService.save(assignmentStudentDTO);
        return ResponseEntity.created(new URI("/api/assignment-students/" + assignmentStudentDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, assignmentStudentDTO.getId().toString()))
            .body(assignmentStudentDTO);
    }

    /**
     * {@code PUT  /assignment-students/:id} : Updates an existing assignmentStudent.
     *
     * @param id the id of the assignmentStudentDTO to save.
     * @param assignmentStudentDTO the assignmentStudentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated assignmentStudentDTO,
     * or with status {@code 400 (Bad Request)} if the assignmentStudentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the assignmentStudentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AssignmentStudentDTO> updateAssignmentStudent(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AssignmentStudentDTO assignmentStudentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AssignmentStudent : {}, {}", id, assignmentStudentDTO);
        if (assignmentStudentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, assignmentStudentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!assignmentStudentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        assignmentStudentDTO = assignmentStudentService.update(assignmentStudentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, assignmentStudentDTO.getId().toString()))
            .body(assignmentStudentDTO);
    }

    /**
     * {@code PATCH  /assignment-students/:id} : Partial updates given fields of an existing assignmentStudent, field will ignore if it is null
     *
     * @param id the id of the assignmentStudentDTO to save.
     * @param assignmentStudentDTO the assignmentStudentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated assignmentStudentDTO,
     * or with status {@code 400 (Bad Request)} if the assignmentStudentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the assignmentStudentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the assignmentStudentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AssignmentStudentDTO> partialUpdateAssignmentStudent(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AssignmentStudentDTO assignmentStudentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update AssignmentStudent : {}, {}", id, assignmentStudentDTO);
        if (assignmentStudentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, assignmentStudentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!assignmentStudentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AssignmentStudentDTO> result = assignmentStudentService.partialUpdate(assignmentStudentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, assignmentStudentDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /assignment-students} : get all the Assignment Students.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Assignment Students in body.
     */
    @GetMapping("")
    public ResponseEntity<List<AssignmentStudentDTO>> getAllAssignmentStudents(
        AssignmentStudentCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get AssignmentStudents by criteria: {}", criteria);

        Page<AssignmentStudentDTO> page = assignmentStudentQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /assignment-students/count} : count all the assignmentStudents.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countAssignmentStudents(AssignmentStudentCriteria criteria) {
        LOG.debug("REST request to count AssignmentStudents by criteria: {}", criteria);
        return ResponseEntity.ok().body(assignmentStudentQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /assignment-students/:id} : get the "id" assignmentStudent.
     *
     * @param id the id of the assignmentStudentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the assignmentStudentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AssignmentStudentDTO> getAssignmentStudent(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AssignmentStudent : {}", id);
        Optional<AssignmentStudentDTO> assignmentStudentDTO = assignmentStudentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(assignmentStudentDTO);
    }

    /**
     * {@code DELETE  /assignment-students/:id} : delete the "id" assignmentStudent.
     *
     * @param id the id of the assignmentStudentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssignmentStudent(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AssignmentStudent : {}", id);
        assignmentStudentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
