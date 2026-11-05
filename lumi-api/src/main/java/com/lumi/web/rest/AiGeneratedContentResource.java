package com.lumi.web.rest;

import com.lumi.repository.AiGeneratedContentRepository;
import com.lumi.service.AiGeneratedContentService;
import com.lumi.service.criteria.AiGeneratedContentCriteria;
import com.lumi.service.dto.AiGeneratedContentDTO;
import com.lumi.service.query.AiGeneratedContentQueryService;
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
 * REST controller for managing {@link com.lumi.domain.AiGeneratedContent}.
 */
@RestController
@RequestMapping("/api/ai-generated-contents")
public class AiGeneratedContentResource {

    private static final Logger LOG = LoggerFactory.getLogger(AiGeneratedContentResource.class);

    private static final String ENTITY_NAME = "aiGeneratedContent";

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    private final AiGeneratedContentService aiGeneratedContentService;

    private final AiGeneratedContentRepository aiGeneratedContentRepository;

    private final AiGeneratedContentQueryService aiGeneratedContentQueryService;

    public AiGeneratedContentResource(
        AiGeneratedContentService aiGeneratedContentService,
        AiGeneratedContentRepository aiGeneratedContentRepository,
        AiGeneratedContentQueryService aiGeneratedContentQueryService
    ) {
        this.aiGeneratedContentService = aiGeneratedContentService;
        this.aiGeneratedContentRepository = aiGeneratedContentRepository;
        this.aiGeneratedContentQueryService = aiGeneratedContentQueryService;
    }

    /**
     * {@code POST  /ai-generated-contents} : Create a new aiGeneratedContent.
     *
     * @param aiGeneratedContentDTO the aiGeneratedContentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new aiGeneratedContentDTO, or with status {@code 400 (Bad Request)} if the aiGeneratedContent already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AiGeneratedContentDTO> createAiGeneratedContent(@Valid @RequestBody AiGeneratedContentDTO aiGeneratedContentDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save AiGeneratedContent : {}", aiGeneratedContentDTO);
        if (aiGeneratedContentDTO.getId() != null) {
            throw new BadRequestAlertException("A new aiGeneratedContent cannot already have an ID", ENTITY_NAME, "idexists");
        }
        aiGeneratedContentDTO = aiGeneratedContentService.save(aiGeneratedContentDTO);
        return ResponseEntity.created(new URI("/api/ai-generated-contents/" + aiGeneratedContentDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, aiGeneratedContentDTO.getId().toString()))
            .body(aiGeneratedContentDTO);
    }

    /**
     * {@code PUT  /ai-generated-contents/:id} : Updates an existing aiGeneratedContent.
     *
     * @param id the id of the aiGeneratedContentDTO to save.
     * @param aiGeneratedContentDTO the aiGeneratedContentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aiGeneratedContentDTO,
     * or with status {@code 400 (Bad Request)} if the aiGeneratedContentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the aiGeneratedContentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AiGeneratedContentDTO> updateAiGeneratedContent(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AiGeneratedContentDTO aiGeneratedContentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AiGeneratedContent : {}, {}", id, aiGeneratedContentDTO);
        if (aiGeneratedContentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aiGeneratedContentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aiGeneratedContentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        aiGeneratedContentDTO = aiGeneratedContentService.update(aiGeneratedContentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, aiGeneratedContentDTO.getId().toString()))
            .body(aiGeneratedContentDTO);
    }

    /**
     * {@code PATCH  /ai-generated-contents/:id} : Partial updates given fields of an existing aiGeneratedContent, field will ignore if it is null
     *
     * @param id the id of the aiGeneratedContentDTO to save.
     * @param aiGeneratedContentDTO the aiGeneratedContentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aiGeneratedContentDTO,
     * or with status {@code 400 (Bad Request)} if the aiGeneratedContentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the aiGeneratedContentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the aiGeneratedContentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AiGeneratedContentDTO> partialUpdateAiGeneratedContent(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AiGeneratedContentDTO aiGeneratedContentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update AiGeneratedContent : {}, {}", id, aiGeneratedContentDTO);
        if (aiGeneratedContentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aiGeneratedContentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aiGeneratedContentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AiGeneratedContentDTO> result = aiGeneratedContentService.partialUpdate(aiGeneratedContentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, aiGeneratedContentDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /ai-generated-contents} : get all the Ai Generated Contents.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Ai Generated Contents in body.
     */
    @GetMapping("")
    public ResponseEntity<List<AiGeneratedContentDTO>> getAllAiGeneratedContents(
        AiGeneratedContentCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get AiGeneratedContents by criteria: {}", criteria);

        Page<AiGeneratedContentDTO> page = aiGeneratedContentQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /ai-generated-contents/count} : count all the aiGeneratedContents.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countAiGeneratedContents(AiGeneratedContentCriteria criteria) {
        LOG.debug("REST request to count AiGeneratedContents by criteria: {}", criteria);
        return ResponseEntity.ok().body(aiGeneratedContentQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /ai-generated-contents/:id} : get the "id" aiGeneratedContent.
     *
     * @param id the id of the aiGeneratedContentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the aiGeneratedContentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AiGeneratedContentDTO> getAiGeneratedContent(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AiGeneratedContent : {}", id);
        Optional<AiGeneratedContentDTO> aiGeneratedContentDTO = aiGeneratedContentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(aiGeneratedContentDTO);
    }

    /**
     * {@code DELETE  /ai-generated-contents/:id} : delete the "id" aiGeneratedContent.
     *
     * @param id the id of the aiGeneratedContentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAiGeneratedContent(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AiGeneratedContent : {}", id);
        aiGeneratedContentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
