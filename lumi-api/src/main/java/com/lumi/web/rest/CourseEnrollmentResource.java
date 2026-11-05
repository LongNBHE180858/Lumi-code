package com.lumi.web.rest;

import com.lumi.repository.CourseEnrollmentRepository;
import com.lumi.service.CourseEnrollmentService;
import com.lumi.service.criteria.CourseEnrollmentCriteria;
import com.lumi.service.dto.CourseEnrollmentDTO;
import com.lumi.service.query.CourseEnrollmentQueryService;
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
 * REST controller for managing {@link com.lumi.domain.CourseEnrollment}.
 */
@RestController
@RequestMapping("/api/course-enrollments")
public class CourseEnrollmentResource {

    private static final Logger LOG = LoggerFactory.getLogger(CourseEnrollmentResource.class);

    private static final String ENTITY_NAME = "courseEnrollment";

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    private final CourseEnrollmentService courseEnrollmentService;

    private final CourseEnrollmentRepository courseEnrollmentRepository;

    private final CourseEnrollmentQueryService courseEnrollmentQueryService;

    public CourseEnrollmentResource(
        CourseEnrollmentService courseEnrollmentService,
        CourseEnrollmentRepository courseEnrollmentRepository,
        CourseEnrollmentQueryService courseEnrollmentQueryService
    ) {
        this.courseEnrollmentService = courseEnrollmentService;
        this.courseEnrollmentRepository = courseEnrollmentRepository;
        this.courseEnrollmentQueryService = courseEnrollmentQueryService;
    }

    /**
     * {@code POST  /course-enrollments} : Create a new courseEnrollment.
     *
     * @param courseEnrollmentDTO the courseEnrollmentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new courseEnrollmentDTO, or with status {@code 400 (Bad Request)} if the courseEnrollment already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CourseEnrollmentDTO> createCourseEnrollment(@Valid @RequestBody CourseEnrollmentDTO courseEnrollmentDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save CourseEnrollment : {}", courseEnrollmentDTO);
        if (courseEnrollmentDTO.getId() != null) {
            throw new BadRequestAlertException("A new courseEnrollment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        courseEnrollmentDTO = courseEnrollmentService.save(courseEnrollmentDTO);
        return ResponseEntity.created(new URI("/api/course-enrollments/" + courseEnrollmentDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, courseEnrollmentDTO.getId().toString()))
            .body(courseEnrollmentDTO);
    }

    /**
     * {@code PUT  /course-enrollments/:id} : Updates an existing courseEnrollment.
     *
     * @param id the id of the courseEnrollmentDTO to save.
     * @param courseEnrollmentDTO the courseEnrollmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated courseEnrollmentDTO,
     * or with status {@code 400 (Bad Request)} if the courseEnrollmentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the courseEnrollmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CourseEnrollmentDTO> updateCourseEnrollment(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CourseEnrollmentDTO courseEnrollmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CourseEnrollment : {}, {}", id, courseEnrollmentDTO);
        if (courseEnrollmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, courseEnrollmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!courseEnrollmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        courseEnrollmentDTO = courseEnrollmentService.update(courseEnrollmentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, courseEnrollmentDTO.getId().toString()))
            .body(courseEnrollmentDTO);
    }

    /**
     * {@code PATCH  /course-enrollments/:id} : Partial updates given fields of an existing courseEnrollment, field will ignore if it is null
     *
     * @param id the id of the courseEnrollmentDTO to save.
     * @param courseEnrollmentDTO the courseEnrollmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated courseEnrollmentDTO,
     * or with status {@code 400 (Bad Request)} if the courseEnrollmentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the courseEnrollmentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the courseEnrollmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CourseEnrollmentDTO> partialUpdateCourseEnrollment(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CourseEnrollmentDTO courseEnrollmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update CourseEnrollment : {}, {}", id, courseEnrollmentDTO);
        if (courseEnrollmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, courseEnrollmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!courseEnrollmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CourseEnrollmentDTO> result = courseEnrollmentService.partialUpdate(courseEnrollmentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, courseEnrollmentDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /course-enrollments} : get all the Course Enrollments.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Course Enrollments in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CourseEnrollmentDTO>> getAllCourseEnrollments(
        CourseEnrollmentCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get CourseEnrollments by criteria: {}", criteria);

        Page<CourseEnrollmentDTO> page = courseEnrollmentQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /course-enrollments/count} : count all the courseEnrollments.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countCourseEnrollments(CourseEnrollmentCriteria criteria) {
        LOG.debug("REST request to count CourseEnrollments by criteria: {}", criteria);
        return ResponseEntity.ok().body(courseEnrollmentQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /course-enrollments/:id} : get the "id" courseEnrollment.
     *
     * @param id the id of the courseEnrollmentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the courseEnrollmentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourseEnrollmentDTO> getCourseEnrollment(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CourseEnrollment : {}", id);
        Optional<CourseEnrollmentDTO> courseEnrollmentDTO = courseEnrollmentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(courseEnrollmentDTO);
    }

    /**
     * {@code DELETE  /course-enrollments/:id} : delete the "id" courseEnrollment.
     *
     * @param id the id of the courseEnrollmentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourseEnrollment(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CourseEnrollment : {}", id);
        courseEnrollmentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
