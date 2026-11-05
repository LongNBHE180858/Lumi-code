package com.lumi.web.rest;

import com.lumi.security.AuthoritiesConstants;
import com.lumi.service.KnowledgeGraphService;
import com.lumi.service.dto.KnowledgeGraphDTO;
import com.lumi.service.dto.SubjectDTO;
import jakarta.validation.Valid;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing Knowledge Graphs.
 * Restricted to administrators only.
 */
@RestController
@RequestMapping("/api/admin/knowledge-graph")
@PreAuthorize("hasAuthority('" + AuthoritiesConstants.ADMIN + "')")
public class KnowledgeGraphResource {

    private static final Logger LOG = LoggerFactory.getLogger(KnowledgeGraphResource.class);

    private final KnowledgeGraphService knowledgeGraphService;

    public KnowledgeGraphResource(KnowledgeGraphService knowledgeGraphService) {
        this.knowledgeGraphService = knowledgeGraphService;
    }

    /**
     * {@code GET /api/admin/knowledge-graph/subjects} : Get list of supported subjects for Knowledge Graph.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of subjects in body.
     */
    @GetMapping("/subjects")
    public ResponseEntity<List<SubjectDTO>> getAvailableSubjects() {
        LOG.debug("REST request to get available Knowledge Graph subjects");
        List<SubjectDTO> subjects = knowledgeGraphService.getAvailableSubjects();
        return ResponseEntity.ok(subjects);
    }

    /**
     * {@code GET /api/admin/knowledge-graph/:subjectId} : Get knowledge graph for a subject.
     *
     * @param subjectId the identifier of the subject.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the KnowledgeGraphDTO in body.
     */
    @GetMapping("/{subjectId}")
    public ResponseEntity<KnowledgeGraphDTO> getKnowledgeGraph(@PathVariable("subjectId") Long subjectId) {
        LOG.debug("REST request to get Knowledge Graph for subject: {}", subjectId);
        KnowledgeGraphDTO graphDTO = knowledgeGraphService.loadGraph(subjectId);
        return ResponseEntity.ok(graphDTO);
    }

    /**
     * {@code PUT /api/admin/knowledge-graph/:subjectId} : Validate and save complete knowledge graph for a subject.
     *
     * @param subjectId the identifier of the subject.
     * @param candidateGraph the complete candidate KnowledgeGraphDTO.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the persisted KnowledgeGraphDTO in body.
     */
    @PutMapping("/{subjectId}")
    public ResponseEntity<KnowledgeGraphDTO> saveKnowledgeGraph(
        @PathVariable("subjectId") Long subjectId,
        @Valid @RequestBody KnowledgeGraphDTO candidateGraph
    ) {
        LOG.debug("REST request to save Knowledge Graph for subject: {}", subjectId);
        KnowledgeGraphDTO saved = knowledgeGraphService.saveGraph(subjectId, candidateGraph);
        return ResponseEntity.ok(saved);
    }
}
