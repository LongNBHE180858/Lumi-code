package com.lumi.web.rest;

import com.lumi.repository.StudentStreakRepository;
import com.lumi.service.StudentStreakQueryService;
import com.lumi.service.StudentStreakService;
import com.lumi.service.criteria.StudentStreakCriteria;
import com.lumi.service.dto.StudentStreakDTO;
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
 * REST controller for managing {@link com.lumi.domain.StudentStreak}.
 */
@RestController
@RequestMapping("/api/student-streaks")
public class StudentStreakResource {

    private static final Logger LOG = LoggerFactory.getLogger(StudentStreakResource.class);

    private static final String ENTITY_NAME = "studentStreak";

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    private final StudentStreakService studentStreakService;

    private final StudentStreakRepository studentStreakRepository;

    private final StudentStreakQueryService studentStreakQueryService;

    public StudentStreakResource(
        StudentStreakService studentStreakService,
        StudentStreakRepository studentStreakRepository,
        StudentStreakQueryService studentStreakQueryService
    ) {
        this.studentStreakService = studentStreakService;
        this.studentStreakRepository = studentStreakRepository;
        this.studentStreakQueryService = studentStreakQueryService;
    }

    /**
     * {@code POST  /student-streaks} : Create a new studentStreak.
     *
     * @param studentStreakDTO the studentStreakDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new studentStreakDTO, or with status {@code 400 (Bad Request)} if the studentStreak already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<StudentStreakDTO> createStudentStreak(@Valid @RequestBody StudentStreakDTO studentStreakDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save StudentStreak : {}", studentStreakDTO);
        if (studentStreakDTO.getId() != null) {
            throw new BadRequestAlertException("A new studentStreak cannot already have an ID", ENTITY_NAME, "idexists");
        }
        studentStreakDTO = studentStreakService.save(studentStreakDTO);
        return ResponseEntity.created(new URI("/api/student-streaks/" + studentStreakDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, studentStreakDTO.getId().toString()))
            .body(studentStreakDTO);
    }

    /**
     * {@code PUT  /student-streaks/:id} : Updates an existing studentStreak.
     *
     * @param id the id of the studentStreakDTO to save.
     * @param studentStreakDTO the studentStreakDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated studentStreakDTO,
     * or with status {@code 400 (Bad Request)} if the studentStreakDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the studentStreakDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<StudentStreakDTO> updateStudentStreak(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody StudentStreakDTO studentStreakDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update StudentStreak : {}, {}", id, studentStreakDTO);
        if (studentStreakDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, studentStreakDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!studentStreakRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        studentStreakDTO = studentStreakService.update(studentStreakDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, studentStreakDTO.getId().toString()))
            .body(studentStreakDTO);
    }

    /**
     * {@code PATCH  /student-streaks/:id} : Partial updates given fields of an existing studentStreak, field will ignore if it is null
     *
     * @param id the id of the studentStreakDTO to save.
     * @param studentStreakDTO the studentStreakDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated studentStreakDTO,
     * or with status {@code 400 (Bad Request)} if the studentStreakDTO is not valid,
     * or with status {@code 404 (Not Found)} if the studentStreakDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the studentStreakDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StudentStreakDTO> partialUpdateStudentStreak(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody StudentStreakDTO studentStreakDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update StudentStreak : {}, {}", id, studentStreakDTO);
        if (studentStreakDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, studentStreakDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!studentStreakRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StudentStreakDTO> result = studentStreakService.partialUpdate(studentStreakDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, studentStreakDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /student-streaks} : get all the Student Streaks.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Student Streaks in body.
     */
    @GetMapping("")
    public ResponseEntity<List<StudentStreakDTO>> getAllStudentStreaks(StudentStreakCriteria criteria) {
        LOG.debug("REST request to get StudentStreaks by criteria: {}", criteria);

        List<StudentStreakDTO> entityList = studentStreakQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /student-streaks/count} : count all the studentStreaks.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countStudentStreaks(StudentStreakCriteria criteria) {
        LOG.debug("REST request to count StudentStreaks by criteria: {}", criteria);
        return ResponseEntity.ok().body(studentStreakQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /student-streaks/:id} : get the "id" studentStreak.
     *
     * @param id the id of the studentStreakDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the studentStreakDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StudentStreakDTO> getStudentStreak(@PathVariable("id") Long id) {
        LOG.debug("REST request to get StudentStreak : {}", id);
        Optional<StudentStreakDTO> studentStreakDTO = studentStreakService.findOne(id);
        return ResponseUtil.wrapOrNotFound(studentStreakDTO);
    }

    /**
     * {@code DELETE  /student-streaks/:id} : delete the "id" studentStreak.
     *
     * @param id the id of the studentStreakDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudentStreak(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete StudentStreak : {}", id);
        studentStreakService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
