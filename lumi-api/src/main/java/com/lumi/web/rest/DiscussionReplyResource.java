package com.lumi.web.rest;

import com.lumi.repository.DiscussionReplyRepository;
import com.lumi.service.DiscussionReplyQueryService;
import com.lumi.service.DiscussionReplyService;
import com.lumi.service.criteria.DiscussionReplyCriteria;
import com.lumi.service.dto.DiscussionReplyDTO;
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
 * REST controller for managing {@link com.lumi.domain.DiscussionReply}.
 */
@RestController
@RequestMapping("/api/discussion-replies")
public class DiscussionReplyResource {

    private static final Logger LOG = LoggerFactory.getLogger(DiscussionReplyResource.class);

    private static final String ENTITY_NAME = "discussionReply";

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    private final DiscussionReplyService discussionReplyService;

    private final DiscussionReplyRepository discussionReplyRepository;

    private final DiscussionReplyQueryService discussionReplyQueryService;

    public DiscussionReplyResource(
        DiscussionReplyService discussionReplyService,
        DiscussionReplyRepository discussionReplyRepository,
        DiscussionReplyQueryService discussionReplyQueryService
    ) {
        this.discussionReplyService = discussionReplyService;
        this.discussionReplyRepository = discussionReplyRepository;
        this.discussionReplyQueryService = discussionReplyQueryService;
    }

    /**
     * {@code POST  /discussion-replies} : Create a new discussionReply.
     *
     * @param discussionReplyDTO the discussionReplyDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new discussionReplyDTO, or with status {@code 400 (Bad Request)} if the discussionReply already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<DiscussionReplyDTO> createDiscussionReply(@Valid @RequestBody DiscussionReplyDTO discussionReplyDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save DiscussionReply : {}", discussionReplyDTO);
        if (discussionReplyDTO.getId() != null) {
            throw new BadRequestAlertException("A new discussionReply cannot already have an ID", ENTITY_NAME, "idexists");
        }
        discussionReplyDTO = discussionReplyService.save(discussionReplyDTO);
        return ResponseEntity.created(new URI("/api/discussion-replies/" + discussionReplyDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, discussionReplyDTO.getId().toString()))
            .body(discussionReplyDTO);
    }

    /**
     * {@code PUT  /discussion-replies/:id} : Updates an existing discussionReply.
     *
     * @param id the id of the discussionReplyDTO to save.
     * @param discussionReplyDTO the discussionReplyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated discussionReplyDTO,
     * or with status {@code 400 (Bad Request)} if the discussionReplyDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the discussionReplyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DiscussionReplyDTO> updateDiscussionReply(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DiscussionReplyDTO discussionReplyDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update DiscussionReply : {}, {}", id, discussionReplyDTO);
        if (discussionReplyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, discussionReplyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!discussionReplyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        discussionReplyDTO = discussionReplyService.update(discussionReplyDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, discussionReplyDTO.getId().toString()))
            .body(discussionReplyDTO);
    }

    /**
     * {@code PATCH  /discussion-replies/:id} : Partial updates given fields of an existing discussionReply, field will ignore if it is null
     *
     * @param id the id of the discussionReplyDTO to save.
     * @param discussionReplyDTO the discussionReplyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated discussionReplyDTO,
     * or with status {@code 400 (Bad Request)} if the discussionReplyDTO is not valid,
     * or with status {@code 404 (Not Found)} if the discussionReplyDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the discussionReplyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DiscussionReplyDTO> partialUpdateDiscussionReply(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DiscussionReplyDTO discussionReplyDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update DiscussionReply : {}, {}", id, discussionReplyDTO);
        if (discussionReplyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, discussionReplyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!discussionReplyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DiscussionReplyDTO> result = discussionReplyService.partialUpdate(discussionReplyDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, discussionReplyDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /discussion-replies} : get all the Discussion Replies.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Discussion Replies in body.
     */
    @GetMapping("")
    public ResponseEntity<List<DiscussionReplyDTO>> getAllDiscussionReplies(
        DiscussionReplyCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get DiscussionReplies by criteria: {}", criteria);

        Page<DiscussionReplyDTO> page = discussionReplyQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /discussion-replies/count} : count all the discussionReplies.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countDiscussionReplies(DiscussionReplyCriteria criteria) {
        LOG.debug("REST request to count DiscussionReplies by criteria: {}", criteria);
        return ResponseEntity.ok().body(discussionReplyQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /discussion-replies/:id} : get the "id" discussionReply.
     *
     * @param id the id of the discussionReplyDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the discussionReplyDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DiscussionReplyDTO> getDiscussionReply(@PathVariable("id") Long id) {
        LOG.debug("REST request to get DiscussionReply : {}", id);
        Optional<DiscussionReplyDTO> discussionReplyDTO = discussionReplyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(discussionReplyDTO);
    }

    /**
     * {@code DELETE  /discussion-replies/:id} : delete the "id" discussionReply.
     *
     * @param id the id of the discussionReplyDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiscussionReply(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete DiscussionReply : {}", id);
        discussionReplyService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
