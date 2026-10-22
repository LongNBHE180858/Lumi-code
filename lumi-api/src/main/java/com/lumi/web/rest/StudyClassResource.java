package com.lumi.web.rest;

import com.lumi.repository.StudyClassRepository;
import com.lumi.service.StudyClassQueryService;
import com.lumi.service.StudyClassService;
import com.lumi.service.criteria.StudyClassCriteria;
import com.lumi.service.dto.StudyClassDTO;
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
 * REST controller for managing {@link com.lumi.domain.StudyClass}.
 */
@RestController
@RequestMapping("/api/study-classes")
public class StudyClassResource {

    private static final Logger LOG = LoggerFactory.getLogger(StudyClassResource.class);

    private static final String ENTITY_NAME = "studyClass";

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    private final StudyClassService studyClassService;

    private final StudyClassRepository studyClassRepository;

    private final StudyClassQueryService studyClassQueryService;

    public StudyClassResource(
        StudyClassService studyClassService,
        StudyClassRepository studyClassRepository,
        StudyClassQueryService studyClassQueryService
    ) {
        this.studyClassService = studyClassService;
        this.studyClassRepository = studyClassRepository;
        this.studyClassQueryService = studyClassQueryService;
    }

    /**
     * {@code POST  /study-classes} : Create a new studyClass.
     *
     * @param studyClassDTO the studyClassDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new studyClassDTO, or with status {@code 400 (Bad Request)} if the studyClass already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<StudyClassDTO> createStudyClass(@Valid @RequestBody StudyClassDTO studyClassDTO) throws URISyntaxException {
        LOG.debug("REST request to save StudyClass : {}", studyClassDTO);
        if (studyClassDTO.getId() != null) {
            throw new BadRequestAlertException("A new studyClass cannot already have an ID", ENTITY_NAME, "idexists");
        }
        studyClassDTO = studyClassService.save(studyClassDTO);
        return ResponseEntity.created(new URI("/api/study-classes/" + studyClassDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, studyClassDTO.getId().toString()))
            .body(studyClassDTO);
    }

    /**
     * {@code PUT  /study-classes/:id} : Updates an existing studyClass.
     *
     * @param id the id of the studyClassDTO to save.
     * @param studyClassDTO the studyClassDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated studyClassDTO,
     * or with status {@code 400 (Bad Request)} if the studyClassDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the studyClassDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<StudyClassDTO> updateStudyClass(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody StudyClassDTO studyClassDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update StudyClass : {}, {}", id, studyClassDTO);
        if (studyClassDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, studyClassDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!studyClassRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        studyClassDTO = studyClassService.update(studyClassDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, studyClassDTO.getId().toString()))
            .body(studyClassDTO);
    }

    /**
     * {@code PATCH  /study-classes/:id} : Partial updates given fields of an existing studyClass, field will ignore if it is null
     *
     * @param id the id of the studyClassDTO to save.
     * @param studyClassDTO the studyClassDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated studyClassDTO,
     * or with status {@code 400 (Bad Request)} if the studyClassDTO is not valid,
     * or with status {@code 404 (Not Found)} if the studyClassDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the studyClassDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StudyClassDTO> partialUpdateStudyClass(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody StudyClassDTO studyClassDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update StudyClass : {}, {}", id, studyClassDTO);
        if (studyClassDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, studyClassDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!studyClassRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StudyClassDTO> result = studyClassService.partialUpdate(studyClassDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, studyClassDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /study-classes} : get all the Study Classes.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Study Classes in body.
     */
    @GetMapping("")
    public ResponseEntity<List<StudyClassDTO>> getAllStudyClasses(
        StudyClassCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get StudyClasses by criteria: {}", criteria);

        Page<StudyClassDTO> page = studyClassQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /study-classes/count} : count all the studyClasses.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countStudyClasses(StudyClassCriteria criteria) {
        LOG.debug("REST request to count StudyClasses by criteria: {}", criteria);
        return ResponseEntity.ok().body(studyClassQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /study-classes/:id} : get the "id" studyClass.
     *
     * @param id the id of the studyClassDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the studyClassDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StudyClassDTO> getStudyClass(@PathVariable("id") Long id) {
        LOG.debug("REST request to get StudyClass : {}", id);
        Optional<StudyClassDTO> studyClassDTO = studyClassService.findOne(id);
        return ResponseUtil.wrapOrNotFound(studyClassDTO);
    }

    /**
     * {@code DELETE  /study-classes/:id} : delete the "id" studyClass.
     *
     * @param id the id of the studyClassDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudyClass(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete StudyClass : {}", id);
        studyClassService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
