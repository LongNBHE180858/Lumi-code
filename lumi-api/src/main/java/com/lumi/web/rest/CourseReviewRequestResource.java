package com.lumi.web.rest;

import com.lumi.repository.CourseReviewRequestRepository;
import com.lumi.service.CourseReviewRequestService;
import com.lumi.service.criteria.CourseReviewRequestCriteria;
import com.lumi.service.dto.CourseReviewRequestDTO;
import com.lumi.service.query.CourseReviewRequestQueryService;
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
 * REST controller for managing {@link com.lumi.domain.CourseReviewRequest}.
 */
@RestController
@RequestMapping("/api/course-review-requests")
public class CourseReviewRequestResource {

    private static final Logger LOG = LoggerFactory.getLogger(CourseReviewRequestResource.class);

    private static final String ENTITY_NAME = "courseReviewRequest";

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    private final CourseReviewRequestService courseReviewRequestService;

    private final CourseReviewRequestRepository courseReviewRequestRepository;

    private final CourseReviewRequestQueryService courseReviewRequestQueryService;

    public CourseReviewRequestResource(
        CourseReviewRequestService courseReviewRequestService,
        CourseReviewRequestRepository courseReviewRequestRepository,
        CourseReviewRequestQueryService courseReviewRequestQueryService
    ) {
        this.courseReviewRequestService = courseReviewRequestService;
        this.courseReviewRequestRepository = courseReviewRequestRepository;
        this.courseReviewRequestQueryService = courseReviewRequestQueryService;
    }

    /**
     * {@code POST  /course-review-requests} : Create a new courseReviewRequest.
     *
     * @param courseReviewRequestDTO the courseReviewRequestDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new courseReviewRequestDTO, or with status {@code 400 (Bad Request)} if the courseReviewRequest already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CourseReviewRequestDTO> createCourseReviewRequest(
        @Valid @RequestBody CourseReviewRequestDTO courseReviewRequestDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save CourseReviewRequest : {}", courseReviewRequestDTO);
        if (courseReviewRequestDTO.getId() != null) {
            throw new BadRequestAlertException("A new courseReviewRequest cannot already have an ID", ENTITY_NAME, "idexists");
        }
        courseReviewRequestDTO = courseReviewRequestService.save(courseReviewRequestDTO);
        return ResponseEntity.created(new URI("/api/course-review-requests/" + courseReviewRequestDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, courseReviewRequestDTO.getId().toString()))
            .body(courseReviewRequestDTO);
    }

    /**
     * {@code PUT  /course-review-requests/:id} : Updates an existing courseReviewRequest.
     *
     * @param id the id of the courseReviewRequestDTO to save.
     * @param courseReviewRequestDTO the courseReviewRequestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated courseReviewRequestDTO,
     * or with status {@code 400 (Bad Request)} if the courseReviewRequestDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the courseReviewRequestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CourseReviewRequestDTO> updateCourseReviewRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CourseReviewRequestDTO courseReviewRequestDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CourseReviewRequest : {}, {}", id, courseReviewRequestDTO);
        if (courseReviewRequestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, courseReviewRequestDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!courseReviewRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        courseReviewRequestDTO = courseReviewRequestService.update(courseReviewRequestDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, courseReviewRequestDTO.getId().toString()))
            .body(courseReviewRequestDTO);
    }

    /**
     * {@code PATCH  /course-review-requests/:id} : Partial updates given fields of an existing courseReviewRequest, field will ignore if it is null
     *
     * @param id the id of the courseReviewRequestDTO to save.
     * @param courseReviewRequestDTO the courseReviewRequestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated courseReviewRequestDTO,
     * or with status {@code 400 (Bad Request)} if the courseReviewRequestDTO is not valid,
     * or with status {@code 404 (Not Found)} if the courseReviewRequestDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the courseReviewRequestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CourseReviewRequestDTO> partialUpdateCourseReviewRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CourseReviewRequestDTO courseReviewRequestDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update CourseReviewRequest : {}, {}", id, courseReviewRequestDTO);
        if (courseReviewRequestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, courseReviewRequestDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!courseReviewRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CourseReviewRequestDTO> result = courseReviewRequestService.partialUpdate(courseReviewRequestDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, courseReviewRequestDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /course-review-requests} : get all the Course Review Requests.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Course Review Requests in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CourseReviewRequestDTO>> getAllCourseReviewRequests(
        CourseReviewRequestCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get CourseReviewRequests by criteria: {}", criteria);

        Page<CourseReviewRequestDTO> page = courseReviewRequestQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /course-review-requests/count} : count all the courseReviewRequests.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countCourseReviewRequests(CourseReviewRequestCriteria criteria) {
        LOG.debug("REST request to count CourseReviewRequests by criteria: {}", criteria);
        return ResponseEntity.ok().body(courseReviewRequestQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /course-review-requests/:id} : get the "id" courseReviewRequest.
     *
     * @param id the id of the courseReviewRequestDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the courseReviewRequestDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourseReviewRequestDTO> getCourseReviewRequest(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CourseReviewRequest : {}", id);
        Optional<CourseReviewRequestDTO> courseReviewRequestDTO = courseReviewRequestService.findOne(id);
        return ResponseUtil.wrapOrNotFound(courseReviewRequestDTO);
    }

    /**
     * {@code DELETE  /course-review-requests/:id} : delete the "id" courseReviewRequest.
     *
     * @param id the id of the courseReviewRequestDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourseReviewRequest(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CourseReviewRequest : {}", id);
        courseReviewRequestService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
