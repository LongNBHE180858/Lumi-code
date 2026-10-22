package com.lumi.web.rest;

import com.lumi.security.AuthoritiesConstants;
import com.lumi.service.FeedbackService;
import com.lumi.service.dto.FeedbackCreateDTO;
import com.lumi.service.dto.FeedbackDetailDTO;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;

/**
 * REST controller for managing Feedback submissions by users.
 */
@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackResource {

    private static final Logger LOG = LoggerFactory.getLogger(FeedbackResource.class);

    private static final String ENTITY_NAME = "feedback";

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    private final FeedbackService feedbackService;

    public FeedbackResource(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    /**
     * {@code POST  /feedbacks} : Create a new feedback.
     *
     * @param feedbackCreateDTO the feedbackCreateDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new feedbackDetailDTO.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasAnyAuthority(\"" + AuthoritiesConstants.USER + "\", \"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<FeedbackDetailDTO> createFeedback(@Valid @RequestBody FeedbackCreateDTO feedbackCreateDTO)
        throws URISyntaxException {
        LOG.debug("REST request to submit Feedback : {}", feedbackCreateDTO);
        FeedbackDetailDTO result = feedbackService.createFeedback(feedbackCreateDTO);
        return ResponseEntity.created(new URI("/api/feedbacks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }
}
