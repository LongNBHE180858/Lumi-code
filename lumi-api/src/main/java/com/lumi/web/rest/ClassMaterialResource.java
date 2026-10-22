package com.lumi.web.rest;

import com.lumi.repository.ClassMaterialRepository;
import com.lumi.service.ClassMaterialQueryService;
import com.lumi.service.ClassMaterialService;
import com.lumi.service.criteria.ClassMaterialCriteria;
import com.lumi.service.dto.ClassMaterialDTO;
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
 * REST controller for managing {@link com.lumi.domain.ClassMaterial}.
 */
@RestController
@RequestMapping("/api/class-materials")
public class ClassMaterialResource {

    private static final Logger LOG = LoggerFactory.getLogger(ClassMaterialResource.class);

    private static final String ENTITY_NAME = "classMaterial";

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    private final ClassMaterialService classMaterialService;

    private final ClassMaterialRepository classMaterialRepository;

    private final ClassMaterialQueryService classMaterialQueryService;

    public ClassMaterialResource(
        ClassMaterialService classMaterialService,
        ClassMaterialRepository classMaterialRepository,
        ClassMaterialQueryService classMaterialQueryService
    ) {
        this.classMaterialService = classMaterialService;
        this.classMaterialRepository = classMaterialRepository;
        this.classMaterialQueryService = classMaterialQueryService;
    }

    /**
     * {@code POST  /class-materials} : Create a new classMaterial.
     *
     * @param classMaterialDTO the classMaterialDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new classMaterialDTO, or with status {@code 400 (Bad Request)} if the classMaterial already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ClassMaterialDTO> createClassMaterial(@Valid @RequestBody ClassMaterialDTO classMaterialDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ClassMaterial : {}", classMaterialDTO);
        if (classMaterialDTO.getId() != null) {
            throw new BadRequestAlertException("A new classMaterial cannot already have an ID", ENTITY_NAME, "idexists");
        }
        classMaterialDTO = classMaterialService.save(classMaterialDTO);
        return ResponseEntity.created(new URI("/api/class-materials/" + classMaterialDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, classMaterialDTO.getId().toString()))
            .body(classMaterialDTO);
    }

    /**
     * {@code PUT  /class-materials/:id} : Updates an existing classMaterial.
     *
     * @param id the id of the classMaterialDTO to save.
     * @param classMaterialDTO the classMaterialDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated classMaterialDTO,
     * or with status {@code 400 (Bad Request)} if the classMaterialDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the classMaterialDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClassMaterialDTO> updateClassMaterial(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ClassMaterialDTO classMaterialDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ClassMaterial : {}, {}", id, classMaterialDTO);
        if (classMaterialDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, classMaterialDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!classMaterialRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        classMaterialDTO = classMaterialService.update(classMaterialDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, classMaterialDTO.getId().toString()))
            .body(classMaterialDTO);
    }

    /**
     * {@code PATCH  /class-materials/:id} : Partial updates given fields of an existing classMaterial, field will ignore if it is null
     *
     * @param id the id of the classMaterialDTO to save.
     * @param classMaterialDTO the classMaterialDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated classMaterialDTO,
     * or with status {@code 400 (Bad Request)} if the classMaterialDTO is not valid,
     * or with status {@code 404 (Not Found)} if the classMaterialDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the classMaterialDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ClassMaterialDTO> partialUpdateClassMaterial(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ClassMaterialDTO classMaterialDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update ClassMaterial : {}, {}", id, classMaterialDTO);
        if (classMaterialDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, classMaterialDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!classMaterialRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ClassMaterialDTO> result = classMaterialService.partialUpdate(classMaterialDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, classMaterialDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /class-materials} : get all the Class Materials.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Class Materials in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ClassMaterialDTO>> getAllClassMaterials(
        ClassMaterialCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get ClassMaterials by criteria: {}", criteria);

        Page<ClassMaterialDTO> page = classMaterialQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /class-materials/count} : count all the classMaterials.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countClassMaterials(ClassMaterialCriteria criteria) {
        LOG.debug("REST request to count ClassMaterials by criteria: {}", criteria);
        return ResponseEntity.ok().body(classMaterialQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /class-materials/:id} : get the "id" classMaterial.
     *
     * @param id the id of the classMaterialDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the classMaterialDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClassMaterialDTO> getClassMaterial(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ClassMaterial : {}", id);
        Optional<ClassMaterialDTO> classMaterialDTO = classMaterialService.findOne(id);
        return ResponseUtil.wrapOrNotFound(classMaterialDTO);
    }

    /**
     * {@code DELETE  /class-materials/:id} : delete the "id" classMaterial.
     *
     * @param id the id of the classMaterialDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClassMaterial(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ClassMaterial : {}", id);
        classMaterialService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
