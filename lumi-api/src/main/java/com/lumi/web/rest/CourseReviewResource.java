package com.lumi.web.rest;

import com.lumi.repository.CourseReviewRepository;
import com.lumi.service.CourseReviewQueryService;
import com.lumi.service.CourseReviewService;
import com.lumi.service.criteria.CourseReviewCriteria;
import com.lumi.service.dto.CourseReviewDTO;
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
 * REST controller for managing {@link com.lumi.domain.CourseReview}.
 */
@RestController
@RequestMapping("/api/course-reviews")
public class CourseReviewResource {

    private static final Logger LOG = LoggerFactory.getLogger(CourseReviewResource.class);

    private static final String ENTITY_NAME = "courseReview";

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    private final CourseReviewService courseReviewService;

    private final CourseReviewRepository courseReviewRepository;

    private final CourseReviewQueryService courseReviewQueryService;

    public CourseReviewResource(
        CourseReviewService courseReviewService,
        CourseReviewRepository courseReviewRepository,
        CourseReviewQueryService courseReviewQueryService
    ) {
        this.courseReviewService = courseReviewService;
        this.courseReviewRepository = courseReviewRepository;
        this.courseReviewQueryService = courseReviewQueryService;
    }

    /**
     * {@code POST  /course-reviews} : Create a new courseReview.
     *
     * @param courseReviewDTO the courseReviewDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new courseReviewDTO, or with status {@code 400 (Bad Request)} if the courseReview already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CourseReviewDTO> createCourseReview(@Valid @RequestBody CourseReviewDTO courseReviewDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save CourseReview : {}", courseReviewDTO);
        if (courseReviewDTO.getId() != null) {
            throw new BadRequestAlertException("A new courseReview cannot already have an ID", ENTITY_NAME, "idexists");
        }
        courseReviewDTO = courseReviewService.save(courseReviewDTO);
        return ResponseEntity.created(new URI("/api/course-reviews/" + courseReviewDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, courseReviewDTO.getId().toString()))
            .body(courseReviewDTO);
    }

    /**
     * {@code PUT  /course-reviews/:id} : Updates an existing courseReview.
     *
     * @param id the id of the courseReviewDTO to save.
     * @param courseReviewDTO the courseReviewDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated courseReviewDTO,
     * or with status {@code 400 (Bad Request)} if the courseReviewDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the courseReviewDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CourseReviewDTO> updateCourseReview(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CourseReviewDTO courseReviewDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CourseReview : {}, {}", id, courseReviewDTO);
        if (courseReviewDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, courseReviewDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!courseReviewRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        courseReviewDTO = courseReviewService.update(courseReviewDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, courseReviewDTO.getId().toString()))
            .body(courseReviewDTO);
    }

    /**
     * {@code PATCH  /course-reviews/:id} : Partial updates given fields of an existing courseReview, field will ignore if it is null
     *
     * @param id the id of the courseReviewDTO to save.
     * @param courseReviewDTO the courseReviewDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated courseReviewDTO,
     * or with status {@code 400 (Bad Request)} if the courseReviewDTO is not valid,
     * or with status {@code 404 (Not Found)} if the courseReviewDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the courseReviewDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CourseReviewDTO> partialUpdateCourseReview(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CourseReviewDTO courseReviewDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update CourseReview : {}, {}", id, courseReviewDTO);
        if (courseReviewDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, courseReviewDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!courseReviewRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CourseReviewDTO> result = courseReviewService.partialUpdate(courseReviewDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, courseReviewDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /course-reviews} : get all the Course Reviews.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Course Reviews in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CourseReviewDTO>> getAllCourseReviews(
        CourseReviewCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get CourseReviews by criteria: {}", criteria);

        Page<CourseReviewDTO> page = courseReviewQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /course-reviews/count} : count all the courseReviews.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countCourseReviews(CourseReviewCriteria criteria) {
        LOG.debug("REST request to count CourseReviews by criteria: {}", criteria);
        return ResponseEntity.ok().body(courseReviewQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /course-reviews/:id} : get the "id" courseReview.
     *
     * @param id the id of the courseReviewDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the courseReviewDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourseReviewDTO> getCourseReview(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CourseReview : {}", id);
        Optional<CourseReviewDTO> courseReviewDTO = courseReviewService.findOne(id);
        return ResponseUtil.wrapOrNotFound(courseReviewDTO);
    }

    /**
     * {@code DELETE  /course-reviews/:id} : delete the "id" courseReview.
     *
     * @param id the id of the courseReviewDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourseReview(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CourseReview : {}", id);
        courseReviewService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
