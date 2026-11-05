package com.lumi.web.rest;

import com.lumi.repository.AiConversationRepository;
import com.lumi.service.AiConversationService;
import com.lumi.service.criteria.AiConversationCriteria;
import com.lumi.service.dto.AiConversationDTO;
import com.lumi.service.query.AiConversationQueryService;
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
 * REST controller for managing {@link com.lumi.domain.AiConversation}.
 */
@RestController
@RequestMapping("/api/ai-conversations")
public class AiConversationResource {

    private static final Logger LOG = LoggerFactory.getLogger(AiConversationResource.class);

    private static final String ENTITY_NAME = "aiConversation";

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    private final AiConversationService aiConversationService;

    private final AiConversationRepository aiConversationRepository;

    private final AiConversationQueryService aiConversationQueryService;

    public AiConversationResource(
        AiConversationService aiConversationService,
        AiConversationRepository aiConversationRepository,
        AiConversationQueryService aiConversationQueryService
    ) {
        this.aiConversationService = aiConversationService;
        this.aiConversationRepository = aiConversationRepository;
        this.aiConversationQueryService = aiConversationQueryService;
    }

    /**
     * {@code POST  /ai-conversations} : Create a new aiConversation.
     *
     * @param aiConversationDTO the aiConversationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new aiConversationDTO, or with status {@code 400 (Bad Request)} if the aiConversation already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AiConversationDTO> createAiConversation(@Valid @RequestBody AiConversationDTO aiConversationDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save AiConversation : {}", aiConversationDTO);
        if (aiConversationDTO.getId() != null) {
            throw new BadRequestAlertException("A new aiConversation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        aiConversationDTO = aiConversationService.save(aiConversationDTO);
        return ResponseEntity.created(new URI("/api/ai-conversations/" + aiConversationDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, aiConversationDTO.getId().toString()))
            .body(aiConversationDTO);
    }

    /**
     * {@code PUT  /ai-conversations/:id} : Updates an existing aiConversation.
     *
     * @param id the id of the aiConversationDTO to save.
     * @param aiConversationDTO the aiConversationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aiConversationDTO,
     * or with status {@code 400 (Bad Request)} if the aiConversationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the aiConversationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AiConversationDTO> updateAiConversation(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AiConversationDTO aiConversationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AiConversation : {}, {}", id, aiConversationDTO);
        if (aiConversationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aiConversationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aiConversationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        aiConversationDTO = aiConversationService.update(aiConversationDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, aiConversationDTO.getId().toString()))
            .body(aiConversationDTO);
    }

    /**
     * {@code PATCH  /ai-conversations/:id} : Partial updates given fields of an existing aiConversation, field will ignore if it is null
     *
     * @param id the id of the aiConversationDTO to save.
     * @param aiConversationDTO the aiConversationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aiConversationDTO,
     * or with status {@code 400 (Bad Request)} if the aiConversationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the aiConversationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the aiConversationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AiConversationDTO> partialUpdateAiConversation(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AiConversationDTO aiConversationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update AiConversation : {}, {}", id, aiConversationDTO);
        if (aiConversationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aiConversationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aiConversationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AiConversationDTO> result = aiConversationService.partialUpdate(aiConversationDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, aiConversationDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /ai-conversations} : get all the Ai Conversations.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Ai Conversations in body.
     */
    @GetMapping("")
    public ResponseEntity<List<AiConversationDTO>> getAllAiConversations(
        AiConversationCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get AiConversations by criteria: {}", criteria);

        Page<AiConversationDTO> page = aiConversationQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /ai-conversations/count} : count all the aiConversations.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countAiConversations(AiConversationCriteria criteria) {
        LOG.debug("REST request to count AiConversations by criteria: {}", criteria);
        return ResponseEntity.ok().body(aiConversationQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /ai-conversations/:id} : get the "id" aiConversation.
     *
     * @param id the id of the aiConversationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the aiConversationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AiConversationDTO> getAiConversation(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AiConversation : {}", id);
        Optional<AiConversationDTO> aiConversationDTO = aiConversationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(aiConversationDTO);
    }

    /**
     * {@code DELETE  /ai-conversations/:id} : delete the "id" aiConversation.
     *
     * @param id the id of the aiConversationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAiConversation(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AiConversation : {}", id);
        aiConversationService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
