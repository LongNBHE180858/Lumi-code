package com.lumi.web.rest;

import com.lumi.repository.ClassMemberRepository;
import com.lumi.service.ClassMemberQueryService;
import com.lumi.service.ClassMemberService;
import com.lumi.service.criteria.ClassMemberCriteria;
import com.lumi.service.dto.ClassMemberDTO;
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
 * REST controller for managing {@link com.lumi.domain.ClassMember}.
 */
@RestController
@RequestMapping("/api/class-members")
public class ClassMemberResource {

    private static final Logger LOG = LoggerFactory.getLogger(ClassMemberResource.class);

    private static final String ENTITY_NAME = "classMember";

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    private final ClassMemberService classMemberService;

    private final ClassMemberRepository classMemberRepository;

    private final ClassMemberQueryService classMemberQueryService;

    public ClassMemberResource(
        ClassMemberService classMemberService,
        ClassMemberRepository classMemberRepository,
        ClassMemberQueryService classMemberQueryService
    ) {
        this.classMemberService = classMemberService;
        this.classMemberRepository = classMemberRepository;
        this.classMemberQueryService = classMemberQueryService;
    }

    /**
     * {@code POST  /class-members} : Create a new classMember.
     *
     * @param classMemberDTO the classMemberDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new classMemberDTO, or with status {@code 400 (Bad Request)} if the classMember already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ClassMemberDTO> createClassMember(@Valid @RequestBody ClassMemberDTO classMemberDTO) throws URISyntaxException {
        LOG.debug("REST request to save ClassMember : {}", classMemberDTO);
        if (classMemberDTO.getId() != null) {
            throw new BadRequestAlertException("A new classMember cannot already have an ID", ENTITY_NAME, "idexists");
        }
        classMemberDTO = classMemberService.save(classMemberDTO);
        return ResponseEntity.created(new URI("/api/class-members/" + classMemberDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, classMemberDTO.getId().toString()))
            .body(classMemberDTO);
    }

    /**
     * {@code PUT  /class-members/:id} : Updates an existing classMember.
     *
     * @param id the id of the classMemberDTO to save.
     * @param classMemberDTO the classMemberDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated classMemberDTO,
     * or with status {@code 400 (Bad Request)} if the classMemberDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the classMemberDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClassMemberDTO> updateClassMember(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ClassMemberDTO classMemberDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ClassMember : {}, {}", id, classMemberDTO);
        if (classMemberDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, classMemberDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!classMemberRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        classMemberDTO = classMemberService.update(classMemberDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, classMemberDTO.getId().toString()))
            .body(classMemberDTO);
    }

    /**
     * {@code PATCH  /class-members/:id} : Partial updates given fields of an existing classMember, field will ignore if it is null
     *
     * @param id the id of the classMemberDTO to save.
     * @param classMemberDTO the classMemberDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated classMemberDTO,
     * or with status {@code 400 (Bad Request)} if the classMemberDTO is not valid,
     * or with status {@code 404 (Not Found)} if the classMemberDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the classMemberDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ClassMemberDTO> partialUpdateClassMember(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ClassMemberDTO classMemberDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update ClassMember : {}, {}", id, classMemberDTO);
        if (classMemberDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, classMemberDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!classMemberRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ClassMemberDTO> result = classMemberService.partialUpdate(classMemberDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, classMemberDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /class-members} : get all the Class Members.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Class Members in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ClassMemberDTO>> getAllClassMembers(
        ClassMemberCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get ClassMembers by criteria: {}", criteria);

        Page<ClassMemberDTO> page = classMemberQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /class-members/count} : count all the classMembers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countClassMembers(ClassMemberCriteria criteria) {
        LOG.debug("REST request to count ClassMembers by criteria: {}", criteria);
        return ResponseEntity.ok().body(classMemberQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /class-members/:id} : get the "id" classMember.
     *
     * @param id the id of the classMemberDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the classMemberDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClassMemberDTO> getClassMember(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ClassMember : {}", id);
        Optional<ClassMemberDTO> classMemberDTO = classMemberService.findOne(id);
        return ResponseUtil.wrapOrNotFound(classMemberDTO);
    }

    /**
     * {@code DELETE  /class-members/:id} : delete the "id" classMember.
     *
     * @param id the id of the classMemberDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClassMember(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ClassMember : {}", id);
        classMemberService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
