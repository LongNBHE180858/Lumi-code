package com.lumi.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lumi.IntegrationTest;
import com.lumi.domain.KnowledgeEdge;
import com.lumi.domain.KnowledgeNode;
import com.lumi.domain.Subject;
import com.lumi.repository.KnowledgeEdgeRepository;
import com.lumi.repository.KnowledgeNodeRepository;
import com.lumi.repository.SubjectRepository;
import com.lumi.security.AuthoritiesConstants;
import com.lumi.service.dto.KnowledgeGraphDTO;
import com.lumi.service.dto.KnowledgeGraphEdgeDTO;
import com.lumi.service.dto.KnowledgeGraphNodeDTO;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

/**
 * Integration and Security tests for {@link KnowledgeGraphResource}.
 */
@IntegrationTest
@AutoConfigureMockMvc
class KnowledgeGraphResourceIT {

    static {
        System.setProperty("user.timezone", "UTC");
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"));
    }

    private static final String API_URL = "/api/admin/knowledge-graph";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private KnowledgeNodeRepository knowledgeNodeRepository;

    @Autowired
    private KnowledgeEdgeRepository knowledgeEdgeRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restKnowledgeGraphMockMvc;

    private Subject mathSubject;
    private Subject historySubject;

    @BeforeEach
    void setUp() {
        mathSubject = subjectRepository.findByNameIgnoreCase("Mathematics")
            .orElseGet(() -> {
                Subject s = new Subject();
                s.setName("Mathematics");
                s.setDescription("Mathematics Description");
                return subjectRepository.saveAndFlush(s);
            });

        historySubject = subjectRepository.findByNameIgnoreCase("History")
            .orElseGet(() -> {
                Subject s = new Subject();
                s.setName("History");
                s.setDescription("History Description");
                return subjectRepository.saveAndFlush(s);
            });
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
    @DisplayName("Admin can load subjects list")
    void getAvailableSubjects_asAdmin_shouldSucceed() throws Exception {
        restKnowledgeGraphMockMvc.perform(get(API_URL + "/subjects"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[*]").isArray());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthoritiesConstants.USER)
    @DisplayName("Non-Admin user is forbidden from accessing subjects list")
    void getAvailableSubjects_asNonAdmin_shouldBeForbidden() throws Exception {
        restKnowledgeGraphMockMvc.perform(get(API_URL + "/subjects"))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @DisplayName("Unauthenticated user is unauthorized from accessing subjects list")
    void getAvailableSubjects_unauthenticated_shouldBeUnauthorized() throws Exception {
        restKnowledgeGraphMockMvc.perform(get(API_URL + "/subjects"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
    @DisplayName("Admin can load graph for Mathematics")
    void loadGraph_asAdmin_shouldSucceed() throws Exception {
        KnowledgeNode node1 = new KnowledgeNode().title("Numbers").type("CONCEPT").subject(mathSubject);
        knowledgeNodeRepository.saveAndFlush(node1);

        restKnowledgeGraphMockMvc.perform(get(API_URL + "/{subjectId}", mathSubject.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.subjectId").value(mathSubject.getId()))
            .andExpect(jsonPath("$.nodes.[*].title").value(org.hamcrest.Matchers.hasItem("Numbers")));
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
    @DisplayName("Admin can save a valid acyclic graph and receive stable IDs")
    void saveGraph_validAcyclic_shouldPersistAndReturnStableIds() throws Exception {
        KnowledgeGraphDTO candidate = new KnowledgeGraphDTO(mathSubject.getId(), mathSubject.getName(), mathSubject.getDescription());
        candidate.setNodes(List.of(
            new KnowledgeGraphNodeDTO(-1L, "Arithmetic", "CONCEPT", mathSubject.getId()),
            new KnowledgeGraphNodeDTO(-2L, "Pre-Algebra", "CONCEPT", mathSubject.getId())
        ));
        candidate.setEdges(List.of(
            new KnowledgeGraphEdgeDTO(null, -1L, -2L, "PREREQUISITE")
        ));

        restKnowledgeGraphMockMvc.perform(put(API_URL + "/{subjectId}", mathSubject.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsBytes(candidate)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.subjectId").value(mathSubject.getId()))
            .andExpect(jsonPath("$.nodes.length()").value(2))
            .andExpect(jsonPath("$.edges.length()").value(1))
            .andExpect(jsonPath("$.edges[0].relationType").value("PREREQUISITE"));
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
    @DisplayName("Cyclic graph save is rejected with 400 and preserves existing data")
    void saveGraph_cyclic_shouldRejectAndPreservePersistedData() throws Exception {
        // Initial persistent state: one node
        KnowledgeNode initialNode = new KnowledgeNode().title("Initial Node").type("CONCEPT").subject(mathSubject);
        knowledgeNodeRepository.saveAndFlush(initialNode);
        long initialCount = knowledgeNodeRepository.findAllBySubjectId(mathSubject.getId()).size();

        // Cyclic payload: Node A -> Node B -> Node A
        KnowledgeGraphDTO cyclicCandidate = new KnowledgeGraphDTO(mathSubject.getId(), mathSubject.getName(), mathSubject.getDescription());
        cyclicCandidate.setNodes(List.of(
            new KnowledgeGraphNodeDTO(-1L, "Node A", "CONCEPT", mathSubject.getId()),
            new KnowledgeGraphNodeDTO(-2L, "Node B", "CONCEPT", mathSubject.getId())
        ));
        cyclicCandidate.setEdges(List.of(
            new KnowledgeGraphEdgeDTO(null, -1L, -2L, "PREREQUISITE"),
            new KnowledgeGraphEdgeDTO(null, -2L, -1L, "PREREQUISITE")
        ));

        restKnowledgeGraphMockMvc.perform(put(API_URL + "/{subjectId}", mathSubject.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsBytes(cyclicCandidate)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorKey").value("cycleDetected"))
            .andExpect(jsonPath("$.cyclePath").value("Node A -> Node B -> Node A"))
            .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Circular prerequisite dependency detected")));

        // Verify database state is untouched
        long afterCount = knowledgeNodeRepository.findAllBySubjectId(mathSubject.getId()).size();
        assertThat(afterCount).isEqualTo(initialCount);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
    @DisplayName("Saving one subject graph never alters or deletes another subject's graph")
    void saveGraph_subjectIsolation_shouldNotAffectOtherSubjects() throws Exception {
        // Seed a node for History
        KnowledgeNode historyNode = new KnowledgeNode().title("Ancient History").type("CONCEPT").subject(historySubject);
        knowledgeNodeRepository.saveAndFlush(historyNode);

        // Save a complete replacement for Mathematics
        KnowledgeGraphDTO mathCandidate = new KnowledgeGraphDTO(mathSubject.getId(), mathSubject.getName(), mathSubject.getDescription());
        mathCandidate.setNodes(List.of(
            new KnowledgeGraphNodeDTO(-1L, "Calculus I", "CONCEPT", mathSubject.getId())
        ));
        mathCandidate.setEdges(List.of());

        restKnowledgeGraphMockMvc.perform(put(API_URL + "/{subjectId}", mathSubject.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsBytes(mathCandidate)))
            .andExpect(status().isOk());

        // Verify History node is intact
        List<KnowledgeNode> historyNodes = knowledgeNodeRepository.findAllBySubjectId(historySubject.getId());
        assertThat(historyNodes).extracting(KnowledgeNode::getTitle).contains("Ancient History");
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthoritiesConstants.USER)
    @DisplayName("Non-Admin user cannot save graph")
    void saveGraph_asNonAdmin_shouldBeForbidden() throws Exception {
        KnowledgeGraphDTO candidate = new KnowledgeGraphDTO(mathSubject.getId(), mathSubject.getName(), mathSubject.getDescription());
        restKnowledgeGraphMockMvc.perform(put(API_URL + "/{subjectId}", mathSubject.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsBytes(candidate)))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
    @DisplayName("Admin can update an existing edge in selected subject")
    void saveGraph_existingEdgeCanBeUpdated() throws Exception {
        KnowledgeNode n1 = knowledgeNodeRepository.saveAndFlush(new KnowledgeNode().title("N1").type("CONCEPT").subject(mathSubject));
        KnowledgeNode n2 = knowledgeNodeRepository.saveAndFlush(new KnowledgeNode().title("N2").type("CONCEPT").subject(mathSubject));
        KnowledgeNode n3 = knowledgeNodeRepository.saveAndFlush(new KnowledgeNode().title("N3").type("CONCEPT").subject(mathSubject));

        KnowledgeEdge edge = knowledgeEdgeRepository.saveAndFlush(
            new KnowledgeEdge().fromNode(n1).toNode(n2).relationType(KnowledgeGraphEdgeDTO.RELATION_TYPE_PREREQUISITE)
        );

        // Candidate updates edge to connect n1 -> n3
        KnowledgeGraphDTO candidate = new KnowledgeGraphDTO(mathSubject.getId(), mathSubject.getName(), mathSubject.getDescription());
        candidate.setNodes(List.of(
            new KnowledgeGraphNodeDTO(n1.getId(), "N1", "CONCEPT", mathSubject.getId()),
            new KnowledgeGraphNodeDTO(n2.getId(), "N2", "CONCEPT", mathSubject.getId()),
            new KnowledgeGraphNodeDTO(n3.getId(), "N3", "CONCEPT", mathSubject.getId())
        ));
        candidate.setEdges(List.of(
            new KnowledgeGraphEdgeDTO(edge.getId(), n1.getId(), n3.getId(), KnowledgeGraphEdgeDTO.RELATION_TYPE_PREREQUISITE)
        ));

        restKnowledgeGraphMockMvc.perform(put(API_URL + "/{subjectId}", mathSubject.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsBytes(candidate)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.edges[0].id").value(edge.getId()))
            .andExpect(jsonPath("$.edges[0].fromNodeId").value(n1.getId()))
            .andExpect(jsonPath("$.edges[0].toNodeId").value(n3.getId()));

        KnowledgeEdge updated = knowledgeEdgeRepository.findById(edge.getId()).orElseThrow();
        assertThat(updated.getToNode().getId()).isEqualTo(n3.getId());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
    @DisplayName("Candidate edge with ID from another subject must be rejected and leave data untouched")
    void saveGraph_foreignEdgeId_shouldBeRejected() throws Exception {
        KnowledgeNode h1 = knowledgeNodeRepository.saveAndFlush(new KnowledgeNode().title("H1").type("CONCEPT").subject(historySubject));
        KnowledgeNode h2 = knowledgeNodeRepository.saveAndFlush(new KnowledgeNode().title("H2").type("CONCEPT").subject(historySubject));
        KnowledgeEdge historyEdge = knowledgeEdgeRepository.saveAndFlush(
            new KnowledgeEdge().fromNode(h1).toNode(h2).relationType(KnowledgeGraphEdgeDTO.RELATION_TYPE_PREREQUISITE)
        );

        KnowledgeNode m1 = knowledgeNodeRepository.saveAndFlush(new KnowledgeNode().title("M1").type("CONCEPT").subject(mathSubject));
        KnowledgeNode m2 = knowledgeNodeRepository.saveAndFlush(new KnowledgeNode().title("M2").type("CONCEPT").subject(mathSubject));

        // Math candidate illegally references historyEdge.getId()
        KnowledgeGraphDTO candidate = new KnowledgeGraphDTO(mathSubject.getId(), mathSubject.getName(), mathSubject.getDescription());
        candidate.setNodes(List.of(
            new KnowledgeGraphNodeDTO(m1.getId(), "M1", "CONCEPT", mathSubject.getId()),
            new KnowledgeGraphNodeDTO(m2.getId(), "M2", "CONCEPT", mathSubject.getId())
        ));
        candidate.setEdges(List.of(
            new KnowledgeGraphEdgeDTO(historyEdge.getId(), m1.getId(), m2.getId(), KnowledgeGraphEdgeDTO.RELATION_TYPE_PREREQUISITE)
        ));

        restKnowledgeGraphMockMvc.perform(put(API_URL + "/{subjectId}", mathSubject.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsBytes(candidate)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("does not belong to subject " + mathSubject.getId())));

        // Verify history edge was NOT altered
        KnowledgeEdge persistedHistoryEdge = knowledgeEdgeRepository.findById(historyEdge.getId()).orElseThrow();
        assertThat(persistedHistoryEdge.getFromNode().getId()).isEqualTo(h1.getId());
        assertThat(persistedHistoryEdge.getToNode().getId()).isEqualTo(h2.getId());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
    @DisplayName("Candidate edge with unknown positive ID must be rejected")
    void saveGraph_unknownEdgeId_shouldBeRejected() throws Exception {
        KnowledgeNode m1 = knowledgeNodeRepository.saveAndFlush(new KnowledgeNode().title("M1").type("CONCEPT").subject(mathSubject));
        KnowledgeNode m2 = knowledgeNodeRepository.saveAndFlush(new KnowledgeNode().title("M2").type("CONCEPT").subject(mathSubject));

        KnowledgeGraphDTO candidate = new KnowledgeGraphDTO(mathSubject.getId(), mathSubject.getName(), mathSubject.getDescription());
        candidate.setNodes(List.of(
            new KnowledgeGraphNodeDTO(m1.getId(), "M1", "CONCEPT", mathSubject.getId()),
            new KnowledgeGraphNodeDTO(m2.getId(), "M2", "CONCEPT", mathSubject.getId())
        ));
        candidate.setEdges(List.of(
            new KnowledgeGraphEdgeDTO(999999L, m1.getId(), m2.getId(), KnowledgeGraphEdgeDTO.RELATION_TYPE_PREREQUISITE)
        ));

        restKnowledgeGraphMockMvc.perform(put(API_URL + "/{subjectId}", mathSubject.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsBytes(candidate)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Edge not found with ID: 999999")));
    }
}
