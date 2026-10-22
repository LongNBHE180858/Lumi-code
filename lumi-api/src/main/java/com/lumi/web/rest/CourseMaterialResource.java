package com.lumi.web.rest;

import com.lumi.repository.CourseMaterialRepository;
import com.lumi.service.CourseMaterialQueryService;
import com.lumi.service.CourseMaterialService;
import com.lumi.service.criteria.CourseMaterialCriteria;
import com.lumi.service.dto.CourseMaterialDTO;
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
 * REST controller for managing {@link com.lumi.domain.CourseMaterial}.
 */
@org.springframework.security.access.prepost.PreAuthorize("hasAuthority('ROLE_ADMIN')")
@RestController
@RequestMapping("/api/course-materials")
public class CourseMaterialResource {

    private static final Logger LOG = LoggerFactory.getLogger(CourseMaterialResource.class);

    private static final String ENTITY_NAME = "courseMaterial";

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    private final CourseMaterialService courseMaterialService;

    private final CourseMaterialRepository courseMaterialRepository;

    private final CourseMaterialQueryService courseMaterialQueryService;

    public CourseMaterialResource(
        CourseMaterialService courseMaterialService,
        CourseMaterialRepository courseMaterialRepository,
        CourseMaterialQueryService courseMaterialQueryService
    ) {
        this.courseMaterialService = courseMaterialService;
        this.courseMaterialRepository = courseMaterialRepository;
        this.courseMaterialQueryService = courseMaterialQueryService;
    }

    /**
     * {@code POST  /course-materials} : Create a new courseMaterial.
     *
     * @param courseMaterialDTO the courseMaterialDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new courseMaterialDTO, or with status {@code 400 (Bad Request)} if the courseMaterial already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CourseMaterialDTO> createCourseMaterial(@Valid @RequestBody CourseMaterialDTO courseMaterialDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save CourseMaterial : {}", courseMaterialDTO);
        if (courseMaterialDTO.getId() != null) {
            throw new BadRequestAlertException("A new courseMaterial cannot already have an ID", ENTITY_NAME, "idexists");
        }
        courseMaterialDTO = courseMaterialService.save(courseMaterialDTO);
        return ResponseEntity.created(new URI("/api/course-materials/" + courseMaterialDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, courseMaterialDTO.getId().toString()))
            .body(courseMaterialDTO);
    }

    /**
     * {@code PUT  /course-materials/:id} : Updates an existing courseMaterial.
     *
     * @param id the id of the courseMaterialDTO to save.
     * @param courseMaterialDTO the courseMaterialDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated courseMaterialDTO,
     * or with status {@code 400 (Bad Request)} if the courseMaterialDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the courseMaterialDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CourseMaterialDTO> updateCourseMaterial(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CourseMaterialDTO courseMaterialDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CourseMaterial : {}, {}", id, courseMaterialDTO);
        if (courseMaterialDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, courseMaterialDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!courseMaterialRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        courseMaterialDTO = courseMaterialService.update(courseMaterialDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, courseMaterialDTO.getId().toString()))
            .body(courseMaterialDTO);
    }

    /**
     * {@code PATCH  /course-materials/:id} : Partial updates given fields of an existing courseMaterial, field will ignore if it is null
     *
     * @param id the id of the courseMaterialDTO to save.
     * @param courseMaterialDTO the courseMaterialDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated courseMaterialDTO,
     * or with status {@code 400 (Bad Request)} if the courseMaterialDTO is not valid,
     * or with status {@code 404 (Not Found)} if the courseMaterialDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the courseMaterialDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CourseMaterialDTO> partialUpdateCourseMaterial(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CourseMaterialDTO courseMaterialDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update CourseMaterial : {}, {}", id, courseMaterialDTO);
        if (courseMaterialDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, courseMaterialDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!courseMaterialRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CourseMaterialDTO> result = courseMaterialService.partialUpdate(courseMaterialDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, courseMaterialDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /course-materials} : get all the Course Materials.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Course Materials in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CourseMaterialDTO>> getAllCourseMaterials(
        CourseMaterialCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get CourseMaterials by criteria: {}", criteria);

        Page<CourseMaterialDTO> page = courseMaterialQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /course-materials/count} : count all the courseMaterials.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countCourseMaterials(CourseMaterialCriteria criteria) {
        LOG.debug("REST request to count CourseMaterials by criteria: {}", criteria);
        return ResponseEntity.ok().body(courseMaterialQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /course-materials/:id} : get the "id" courseMaterial.
     *
     * @param id the id of the courseMaterialDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the courseMaterialDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourseMaterialDTO> getCourseMaterial(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CourseMaterial : {}", id);
        Optional<CourseMaterialDTO> courseMaterialDTO = courseMaterialService.findOne(id);
        return ResponseUtil.wrapOrNotFound(courseMaterialDTO);
    }

    /**
     * {@code DELETE  /course-materials/:id} : delete the "id" courseMaterial.
     *
     * @param id the id of the courseMaterialDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourseMaterial(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CourseMaterial : {}", id);
        courseMaterialService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
