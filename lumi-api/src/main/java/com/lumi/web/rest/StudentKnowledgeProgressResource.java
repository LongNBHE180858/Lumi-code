package com.lumi.web.rest;

import com.lumi.repository.StudentKnowledgeProgressRepository;
import com.lumi.service.StudentKnowledgeProgressService;
import com.lumi.service.criteria.StudentKnowledgeProgressCriteria;
import com.lumi.service.dto.StudentKnowledgeProgressDTO;
import com.lumi.service.query.StudentKnowledgeProgressQueryService;
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
 * REST controller for managing {@link com.lumi.domain.StudentKnowledgeProgress}.
 */
@RestController
@RequestMapping("/api/student-knowledge-progresses")
public class StudentKnowledgeProgressResource {

    private static final Logger LOG = LoggerFactory.getLogger(StudentKnowledgeProgressResource.class);

    private static final String ENTITY_NAME = "studentKnowledgeProgress";

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    private final StudentKnowledgeProgressService studentKnowledgeProgressService;

    private final StudentKnowledgeProgressRepository studentKnowledgeProgressRepository;

    private final StudentKnowledgeProgressQueryService studentKnowledgeProgressQueryService;

    public StudentKnowledgeProgressResource(
        StudentKnowledgeProgressService studentKnowledgeProgressService,
        StudentKnowledgeProgressRepository studentKnowledgeProgressRepository,
        StudentKnowledgeProgressQueryService studentKnowledgeProgressQueryService
    ) {
        this.studentKnowledgeProgressService = studentKnowledgeProgressService;
        this.studentKnowledgeProgressRepository = studentKnowledgeProgressRepository;
        this.studentKnowledgeProgressQueryService = studentKnowledgeProgressQueryService;
    }

    /**
     * {@code POST  /student-knowledge-progresses} : Create a new studentKnowledgeProgress.
     *
     * @param studentKnowledgeProgressDTO the studentKnowledgeProgressDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new studentKnowledgeProgressDTO, or with status {@code 400 (Bad Request)} if the studentKnowledgeProgress already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<StudentKnowledgeProgressDTO> createStudentKnowledgeProgress(
        @Valid @RequestBody StudentKnowledgeProgressDTO studentKnowledgeProgressDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save StudentKnowledgeProgress : {}", studentKnowledgeProgressDTO);
        if (studentKnowledgeProgressDTO.getId() != null) {
            throw new BadRequestAlertException("A new studentKnowledgeProgress cannot already have an ID", ENTITY_NAME, "idexists");
        }
        studentKnowledgeProgressDTO = studentKnowledgeProgressService.save(studentKnowledgeProgressDTO);
        return ResponseEntity.created(new URI("/api/student-knowledge-progresses/" + studentKnowledgeProgressDTO.getId()))
            .headers(
                HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, studentKnowledgeProgressDTO.getId().toString())
            )
            .body(studentKnowledgeProgressDTO);
    }

    /**
     * {@code PUT  /student-knowledge-progresses/:id} : Updates an existing studentKnowledgeProgress.
     *
     * @param id the id of the studentKnowledgeProgressDTO to save.
     * @param studentKnowledgeProgressDTO the studentKnowledgeProgressDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated studentKnowledgeProgressDTO,
     * or with status {@code 400 (Bad Request)} if the studentKnowledgeProgressDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the studentKnowledgeProgressDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<StudentKnowledgeProgressDTO> updateStudentKnowledgeProgress(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody StudentKnowledgeProgressDTO studentKnowledgeProgressDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update StudentKnowledgeProgress : {}, {}", id, studentKnowledgeProgressDTO);
        if (studentKnowledgeProgressDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, studentKnowledgeProgressDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!studentKnowledgeProgressRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        studentKnowledgeProgressDTO = studentKnowledgeProgressService.update(studentKnowledgeProgressDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, studentKnowledgeProgressDTO.getId().toString()))
            .body(studentKnowledgeProgressDTO);
    }

    /**
     * {@code PATCH  /student-knowledge-progresses/:id} : Partial updates given fields of an existing studentKnowledgeProgress, field will ignore if it is null
     *
     * @param id the id of the studentKnowledgeProgressDTO to save.
     * @param studentKnowledgeProgressDTO the studentKnowledgeProgressDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated studentKnowledgeProgressDTO,
     * or with status {@code 400 (Bad Request)} if the studentKnowledgeProgressDTO is not valid,
     * or with status {@code 404 (Not Found)} if the studentKnowledgeProgressDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the studentKnowledgeProgressDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StudentKnowledgeProgressDTO> partialUpdateStudentKnowledgeProgress(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody StudentKnowledgeProgressDTO studentKnowledgeProgressDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update StudentKnowledgeProgress : {}, {}", id, studentKnowledgeProgressDTO);
        if (studentKnowledgeProgressDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, studentKnowledgeProgressDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!studentKnowledgeProgressRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StudentKnowledgeProgressDTO> result = studentKnowledgeProgressService.partialUpdate(studentKnowledgeProgressDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, studentKnowledgeProgressDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /student-knowledge-progresses} : get all the Student Knowledge Progresses.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Student Knowledge Progresses in body.
     */
    @GetMapping("")
    public ResponseEntity<List<StudentKnowledgeProgressDTO>> getAllStudentKnowledgeProgresses(
        StudentKnowledgeProgressCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get StudentKnowledgeProgresses by criteria: {}", criteria);

        Page<StudentKnowledgeProgressDTO> page = studentKnowledgeProgressQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /student-knowledge-progresses/count} : count all the studentKnowledgeProgresses.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countStudentKnowledgeProgresses(StudentKnowledgeProgressCriteria criteria) {
        LOG.debug("REST request to count StudentKnowledgeProgresses by criteria: {}", criteria);
        return ResponseEntity.ok().body(studentKnowledgeProgressQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /student-knowledge-progresses/:id} : get the "id" studentKnowledgeProgress.
     *
     * @param id the id of the studentKnowledgeProgressDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the studentKnowledgeProgressDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StudentKnowledgeProgressDTO> getStudentKnowledgeProgress(@PathVariable("id") Long id) {
        LOG.debug("REST request to get StudentKnowledgeProgress : {}", id);
        Optional<StudentKnowledgeProgressDTO> studentKnowledgeProgressDTO = studentKnowledgeProgressService.findOne(id);
        return ResponseUtil.wrapOrNotFound(studentKnowledgeProgressDTO);
    }

    /**
     * {@code DELETE  /student-knowledge-progresses/:id} : delete the "id" studentKnowledgeProgress.
     *
     * @param id the id of the studentKnowledgeProgressDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudentKnowledgeProgress(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete StudentKnowledgeProgress : {}", id);
        studentKnowledgeProgressService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
