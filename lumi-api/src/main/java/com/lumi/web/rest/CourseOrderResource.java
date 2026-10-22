package com.lumi.web.rest;

import com.lumi.repository.CourseOrderRepository;
import com.lumi.service.CourseOrderQueryService;
import com.lumi.service.CourseOrderService;
import com.lumi.service.criteria.CourseOrderCriteria;
import com.lumi.service.dto.CourseOrderDTO;
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
 * REST controller for managing {@link com.lumi.domain.CourseOrder}.
 */
@RestController
@RequestMapping("/api/course-orders")
public class CourseOrderResource {

    private static final Logger LOG = LoggerFactory.getLogger(CourseOrderResource.class);

    private static final String ENTITY_NAME = "courseOrder";

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    private final CourseOrderService courseOrderService;

    private final CourseOrderRepository courseOrderRepository;

    private final CourseOrderQueryService courseOrderQueryService;

    public CourseOrderResource(
        CourseOrderService courseOrderService,
        CourseOrderRepository courseOrderRepository,
        CourseOrderQueryService courseOrderQueryService
    ) {
        this.courseOrderService = courseOrderService;
        this.courseOrderRepository = courseOrderRepository;
        this.courseOrderQueryService = courseOrderQueryService;
    }

    /**
     * {@code POST  /course-orders} : Create a new courseOrder.
     *
     * @param courseOrderDTO the courseOrderDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new courseOrderDTO, or with status {@code 400 (Bad Request)} if the courseOrder already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CourseOrderDTO> createCourseOrder(@Valid @RequestBody CourseOrderDTO courseOrderDTO) throws URISyntaxException {
        LOG.debug("REST request to save CourseOrder : {}", courseOrderDTO);
        if (courseOrderDTO.getId() != null) {
            throw new BadRequestAlertException("A new courseOrder cannot already have an ID", ENTITY_NAME, "idexists");
        }
        courseOrderDTO = courseOrderService.save(courseOrderDTO);
        return ResponseEntity.created(new URI("/api/course-orders/" + courseOrderDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, courseOrderDTO.getId().toString()))
            .body(courseOrderDTO);
    }

    /**
     * {@code PUT  /course-orders/:id} : Updates an existing courseOrder.
     *
     * @param id the id of the courseOrderDTO to save.
     * @param courseOrderDTO the courseOrderDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated courseOrderDTO,
     * or with status {@code 400 (Bad Request)} if the courseOrderDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the courseOrderDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CourseOrderDTO> updateCourseOrder(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CourseOrderDTO courseOrderDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CourseOrder : {}, {}", id, courseOrderDTO);
        if (courseOrderDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, courseOrderDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!courseOrderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        courseOrderDTO = courseOrderService.update(courseOrderDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, courseOrderDTO.getId().toString()))
            .body(courseOrderDTO);
    }

    /**
     * {@code PATCH  /course-orders/:id} : Partial updates given fields of an existing courseOrder, field will ignore if it is null
     *
     * @param id the id of the courseOrderDTO to save.
     * @param courseOrderDTO the courseOrderDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated courseOrderDTO,
     * or with status {@code 400 (Bad Request)} if the courseOrderDTO is not valid,
     * or with status {@code 404 (Not Found)} if the courseOrderDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the courseOrderDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CourseOrderDTO> partialUpdateCourseOrder(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CourseOrderDTO courseOrderDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update CourseOrder : {}, {}", id, courseOrderDTO);
        if (courseOrderDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, courseOrderDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!courseOrderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CourseOrderDTO> result = courseOrderService.partialUpdate(courseOrderDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, courseOrderDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /course-orders} : get all the Course Orders.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Course Orders in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CourseOrderDTO>> getAllCourseOrders(
        CourseOrderCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get CourseOrders by criteria: {}", criteria);

        Page<CourseOrderDTO> page = courseOrderQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /course-orders/count} : count all the courseOrders.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countCourseOrders(CourseOrderCriteria criteria) {
        LOG.debug("REST request to count CourseOrders by criteria: {}", criteria);
        return ResponseEntity.ok().body(courseOrderQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /course-orders/:id} : get the "id" courseOrder.
     *
     * @param id the id of the courseOrderDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the courseOrderDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourseOrderDTO> getCourseOrder(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CourseOrder : {}", id);
        Optional<CourseOrderDTO> courseOrderDTO = courseOrderService.findOne(id);
        return ResponseUtil.wrapOrNotFound(courseOrderDTO);
    }

    /**
     * {@code DELETE  /course-orders/:id} : delete the "id" courseOrder.
     *
     * @param id the id of the courseOrderDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourseOrder(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CourseOrder : {}", id);
        courseOrderService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
