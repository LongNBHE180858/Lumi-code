package com.lumi.web.rest;

import com.lumi.repository.AiMessageRepository;
import com.lumi.service.AiMessageService;
import com.lumi.service.criteria.AiMessageCriteria;
import com.lumi.service.dto.AiMessageDTO;
import com.lumi.service.query.AiMessageQueryService;
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
 * REST controller for managing {@link com.lumi.domain.AiMessage}.
 */
@RestController
@RequestMapping("/api/ai-messages")
public class AiMessageResource {

    private static final Logger LOG = LoggerFactory.getLogger(AiMessageResource.class);

    private static final String ENTITY_NAME = "aiMessage";

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    private final AiMessageService aiMessageService;

    private final AiMessageRepository aiMessageRepository;

    private final AiMessageQueryService aiMessageQueryService;

    public AiMessageResource(
        AiMessageService aiMessageService,
        AiMessageRepository aiMessageRepository,
        AiMessageQueryService aiMessageQueryService
    ) {
        this.aiMessageService = aiMessageService;
        this.aiMessageRepository = aiMessageRepository;
        this.aiMessageQueryService = aiMessageQueryService;
    }

    /**
     * {@code POST  /ai-messages} : Create a new aiMessage.
     *
     * @param aiMessageDTO the aiMessageDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new aiMessageDTO, or with status {@code 400 (Bad Request)} if the aiMessage already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AiMessageDTO> createAiMessage(@Valid @RequestBody AiMessageDTO aiMessageDTO) throws URISyntaxException {
        LOG.debug("REST request to save AiMessage : {}", aiMessageDTO);
        if (aiMessageDTO.getId() != null) {
            throw new BadRequestAlertException("A new aiMessage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        aiMessageDTO = aiMessageService.save(aiMessageDTO);
        return ResponseEntity.created(new URI("/api/ai-messages/" + aiMessageDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, aiMessageDTO.getId().toString()))
            .body(aiMessageDTO);
    }

    /**
     * {@code PUT  /ai-messages/:id} : Updates an existing aiMessage.
     *
     * @param id the id of the aiMessageDTO to save.
     * @param aiMessageDTO the aiMessageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aiMessageDTO,
     * or with status {@code 400 (Bad Request)} if the aiMessageDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the aiMessageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AiMessageDTO> updateAiMessage(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AiMessageDTO aiMessageDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AiMessage : {}, {}", id, aiMessageDTO);
        if (aiMessageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aiMessageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aiMessageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        aiMessageDTO = aiMessageService.update(aiMessageDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, aiMessageDTO.getId().toString()))
            .body(aiMessageDTO);
    }

    /**
     * {@code PATCH  /ai-messages/:id} : Partial updates given fields of an existing aiMessage, field will ignore if it is null
     *
     * @param id the id of the aiMessageDTO to save.
     * @param aiMessageDTO the aiMessageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aiMessageDTO,
     * or with status {@code 400 (Bad Request)} if the aiMessageDTO is not valid,
     * or with status {@code 404 (Not Found)} if the aiMessageDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the aiMessageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AiMessageDTO> partialUpdateAiMessage(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AiMessageDTO aiMessageDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update AiMessage : {}, {}", id, aiMessageDTO);
        if (aiMessageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aiMessageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aiMessageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AiMessageDTO> result = aiMessageService.partialUpdate(aiMessageDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, aiMessageDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /ai-messages} : get all the Ai Messages.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Ai Messages in body.
     */
    @GetMapping("")
    public ResponseEntity<List<AiMessageDTO>> getAllAiMessages(
        AiMessageCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get AiMessages by criteria: {}", criteria);

        Page<AiMessageDTO> page = aiMessageQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /ai-messages/count} : count all the aiMessages.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countAiMessages(AiMessageCriteria criteria) {
        LOG.debug("REST request to count AiMessages by criteria: {}", criteria);
        return ResponseEntity.ok().body(aiMessageQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /ai-messages/:id} : get the "id" aiMessage.
     *
     * @param id the id of the aiMessageDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the aiMessageDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AiMessageDTO> getAiMessage(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AiMessage : {}", id);
        Optional<AiMessageDTO> aiMessageDTO = aiMessageService.findOne(id);
        return ResponseUtil.wrapOrNotFound(aiMessageDTO);
    }

    /**
     * {@code DELETE  /ai-messages/:id} : delete the "id" aiMessage.
     *
     * @param id the id of the aiMessageDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAiMessage(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AiMessage : {}", id);
        aiMessageService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
