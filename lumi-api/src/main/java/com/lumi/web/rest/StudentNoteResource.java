package com.lumi.web.rest;

import com.lumi.repository.StudentNoteRepository;
import com.lumi.service.StudentNoteQueryService;
import com.lumi.service.StudentNoteService;
import com.lumi.service.criteria.StudentNoteCriteria;
import com.lumi.service.dto.StudentNoteDTO;
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
 * REST controller for managing {@link com.lumi.domain.StudentNote}.
 */
@RestController
@RequestMapping("/api/student-notes")
public class StudentNoteResource {

    private static final Logger LOG = LoggerFactory.getLogger(StudentNoteResource.class);

    private static final String ENTITY_NAME = "studentNote";

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    private final StudentNoteService studentNoteService;

    private final StudentNoteRepository studentNoteRepository;

    private final StudentNoteQueryService studentNoteQueryService;

    public StudentNoteResource(
        StudentNoteService studentNoteService,
        StudentNoteRepository studentNoteRepository,
        StudentNoteQueryService studentNoteQueryService
    ) {
        this.studentNoteService = studentNoteService;
        this.studentNoteRepository = studentNoteRepository;
        this.studentNoteQueryService = studentNoteQueryService;
    }

    /**
     * {@code POST  /student-notes} : Create a new studentNote.
     *
     * @param studentNoteDTO the studentNoteDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new studentNoteDTO, or with status {@code 400 (Bad Request)} if the studentNote already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<StudentNoteDTO> createStudentNote(@Valid @RequestBody StudentNoteDTO studentNoteDTO) throws URISyntaxException {
        LOG.debug("REST request to save StudentNote : {}", studentNoteDTO);
        if (studentNoteDTO.getId() != null) {
            throw new BadRequestAlertException("A new studentNote cannot already have an ID", ENTITY_NAME, "idexists");
        }
        studentNoteDTO = studentNoteService.save(studentNoteDTO);
        return ResponseEntity.created(new URI("/api/student-notes/" + studentNoteDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, studentNoteDTO.getId().toString()))
            .body(studentNoteDTO);
    }

    /**
     * {@code PUT  /student-notes/:id} : Updates an existing studentNote.
     *
     * @param id the id of the studentNoteDTO to save.
     * @param studentNoteDTO the studentNoteDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated studentNoteDTO,
     * or with status {@code 400 (Bad Request)} if the studentNoteDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the studentNoteDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<StudentNoteDTO> updateStudentNote(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody StudentNoteDTO studentNoteDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update StudentNote : {}, {}", id, studentNoteDTO);
        if (studentNoteDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, studentNoteDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!studentNoteRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        studentNoteDTO = studentNoteService.update(studentNoteDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, studentNoteDTO.getId().toString()))
            .body(studentNoteDTO);
    }

    /**
     * {@code PATCH  /student-notes/:id} : Partial updates given fields of an existing studentNote, field will ignore if it is null
     *
     * @param id the id of the studentNoteDTO to save.
     * @param studentNoteDTO the studentNoteDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated studentNoteDTO,
     * or with status {@code 400 (Bad Request)} if the studentNoteDTO is not valid,
     * or with status {@code 404 (Not Found)} if the studentNoteDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the studentNoteDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StudentNoteDTO> partialUpdateStudentNote(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody StudentNoteDTO studentNoteDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update StudentNote : {}, {}", id, studentNoteDTO);
        if (studentNoteDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, studentNoteDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!studentNoteRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StudentNoteDTO> result = studentNoteService.partialUpdate(studentNoteDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, studentNoteDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /student-notes} : get all the Student Notes.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Student Notes in body.
     */
    @GetMapping("")
    public ResponseEntity<List<StudentNoteDTO>> getAllStudentNotes(
        StudentNoteCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get StudentNotes by criteria: {}", criteria);

        Page<StudentNoteDTO> page = studentNoteQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /student-notes/count} : count all the studentNotes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countStudentNotes(StudentNoteCriteria criteria) {
        LOG.debug("REST request to count StudentNotes by criteria: {}", criteria);
        return ResponseEntity.ok().body(studentNoteQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /student-notes/:id} : get the "id" studentNote.
     *
     * @param id the id of the studentNoteDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the studentNoteDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StudentNoteDTO> getStudentNote(@PathVariable("id") Long id) {
        LOG.debug("REST request to get StudentNote : {}", id);
        Optional<StudentNoteDTO> studentNoteDTO = studentNoteService.findOne(id);
        return ResponseUtil.wrapOrNotFound(studentNoteDTO);
    }

    /**
     * {@code DELETE  /student-notes/:id} : delete the "id" studentNote.
     *
     * @param id the id of the studentNoteDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudentNote(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete StudentNote : {}", id);
        studentNoteService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
