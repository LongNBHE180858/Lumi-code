package com.lumi.web.rest;

import com.lumi.domain.enumeration.FeedbackStatus;
import com.lumi.domain.enumeration.FeedbackType;
import com.lumi.security.AuthoritiesConstants;
import com.lumi.service.FeedbackService;
import com.lumi.service.dto.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing Feedbacks by Admin.
 */
@RestController
@RequestMapping("/api/admin/feedbacks")
@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
public class AdminFeedbackResource {

    private static final Logger LOG = LoggerFactory.getLogger(AdminFeedbackResource.class);

    private static final String ENTITY_NAME = "feedback";

    private static final List<String> ALLOWED_ORDERED_PROPERTIES = List.of("createdAt", "status", "type");

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    private final FeedbackService feedbackService;

    public AdminFeedbackResource(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    /**
     * {@code GET  /admin/feedbacks} : get all feedbacks with pagination and filtering.
     *
     * @param status the optional status filter.
     * @param type the optional type filter.
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of feedbacks in body.
     */
    @GetMapping("")
    public ResponseEntity<List<FeedbackListDTO>> getAllFeedbacks(
        @RequestParam(name = "status", required = false) FeedbackStatus status,
        @RequestParam(name = "type", required = false) FeedbackType type,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Feedbacks page: {}, status: {}, type: {}", pageable, status, type);
        if (!onlyContainsAllowedProperties(pageable)) {
            return ResponseEntity.badRequest().build();
        }

        Page<FeedbackListDTO> page = feedbackService.getAllFeedbacks(status, type, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * {@code GET  /admin/feedbacks/:id} : get the "id" feedback detail.
     *
     * @param id the id of the feedback to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the feedbackDetailDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<FeedbackDetailDTO> getFeedback(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Feedback detail : {}", id);
        Optional<FeedbackDetailDTO> feedbackDTO = feedbackService.getFeedback(id);
        return ResponseUtil.wrapOrNotFound(feedbackDTO);
    }

    /**
     * {@code PATCH  /admin/feedbacks/:id/status} : update status of feedback.
     *
     * @param id the id of the feedback to update.
     * @param statusUpdateDTO the new status.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated feedbackDetailDTO.
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<FeedbackDetailDTO> updateStatus(
        @PathVariable("id") Long id,
        @Valid @RequestBody FeedbackStatusUpdateDTO statusUpdateDTO
    ) {
        LOG.debug("REST request to update Feedback status : {}, {}", id, statusUpdateDTO);
        FeedbackDetailDTO result = feedbackService.updateFeedbackStatus(id, statusUpdateDTO.getStatus());
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /admin/feedbacks/:id/notes} : update admin notes of feedback.
     *
     * @param id the id of the feedback to update.
     * @param notesUpdateDTO the new admin notes.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated feedbackDetailDTO.
     */
    @PatchMapping("/{id}/notes")
    public ResponseEntity<FeedbackDetailDTO> updateNotes(
        @PathVariable("id") Long id,
        @Valid @RequestBody FeedbackNotesUpdateDTO notesUpdateDTO
    ) {
        LOG.debug("REST request to update Feedback adminNotes : {}, {}", id, notesUpdateDTO);
        FeedbackDetailDTO result = feedbackService.updateFeedbackNotes(id, notesUpdateDTO.getAdminNotes());
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .body(result);
    }

    /**
     * {@code DELETE  /admin/feedbacks/:id} : soft delete the "id" feedback.
     *
     * @param id the id of the feedback to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable("id") Long id) {
        LOG.debug("REST request to soft delete Feedback : {}", id);
        feedbackService.deleteFeedback(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    private boolean onlyContainsAllowedProperties(Pageable pageable) {
        return pageable.getSort().stream().map(Sort.Order::getProperty).allMatch(ALLOWED_ORDERED_PROPERTIES::contains);
    }
}
