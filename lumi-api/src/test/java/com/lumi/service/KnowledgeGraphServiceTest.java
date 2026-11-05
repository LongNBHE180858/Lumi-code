package com.lumi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.lumi.domain.KnowledgeEdge;
import com.lumi.domain.KnowledgeNode;
import com.lumi.domain.Subject;
import com.lumi.repository.KnowledgeEdgeRepository;
import com.lumi.repository.KnowledgeNodeRepository;
import com.lumi.repository.SubjectRepository;
import com.lumi.service.dto.KnowledgeGraphDTO;
import com.lumi.service.dto.KnowledgeGraphEdgeDTO;
import com.lumi.service.dto.KnowledgeGraphNodeDTO;
import com.lumi.service.dto.SubjectDTO;
import com.lumi.service.impl.KnowledgeGraphServiceImpl;
import com.lumi.service.mapper.SubjectMapper;
import com.lumi.web.rest.errors.BadRequestAlertException;
import com.lumi.web.rest.errors.GraphCycleException;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import com.lumi.service.builder.KnowledgeGraphDTOBuilder;

@ExtendWith(MockitoExtension.class)
class KnowledgeGraphServiceTest {

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private KnowledgeNodeRepository knowledgeNodeRepository;

    @Mock
    private KnowledgeEdgeRepository knowledgeEdgeRepository;

    @Mock
    private SubjectMapper subjectMapper;

    @InjectMocks
    private KnowledgeGraphServiceImpl service;

    private Subject mathSubject;
    private Subject historySubject;
    private Subject literatureSubject;

    @BeforeEach
    void setUp() {
        mathSubject = new Subject();
        mathSubject.setId(1L);
        mathSubject.setName("Mathematics");
        mathSubject.setDescription("Mathematics Knowledge Graph");

        historySubject = new Subject();
        historySubject.setId(2L);
        historySubject.setName("History");
        historySubject.setDescription("History Knowledge Graph");

        literatureSubject = new Subject();
        literatureSubject.setId(3L);
        literatureSubject.setName("Literature");
        literatureSubject.setDescription("Unsupported Subject");
    }

    @Test
    @DisplayName("Valid acyclic graph should pass validation and save successfully")
    void validAcyclicGraph_shouldPassValidationAndSave() {
        when(subjectRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(mathSubject));
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(mathSubject));
        when(knowledgeNodeRepository.findAllWithSubjectBySubjectId(1L)).thenReturn(Collections.emptyList());
        when(knowledgeEdgeRepository.findAllReferencingSubjectId(1L)).thenReturn(Collections.emptyList());

        when(knowledgeNodeRepository.save(any(KnowledgeNode.class))).thenAnswer(invocation -> {
            KnowledgeNode node = invocation.getArgument(0);
            if (node.getId() == null) {
                node.setId(100L + (long) (Math.random() * 1000));
            }
            return node;
        });

        when(knowledgeEdgeRepository.save(any(KnowledgeEdge.class))).thenAnswer(invocation -> {
            KnowledgeEdge edge = invocation.getArgument(0);
            if (edge.getId() == null) {
                edge.setId(200L + (long) (Math.random() * 1000));
            }
            return edge;
        });

        KnowledgeGraphDTO candidate = new KnowledgeGraphDTO(1L, "Mathematics", "Desc");
        candidate.setNodes(List.of(
            new KnowledgeGraphNodeDTO(-1L, "Node A", "CONCEPT", 1L),
            new KnowledgeGraphNodeDTO(-2L, "Node B", "CONCEPT", 1L),
            new KnowledgeGraphNodeDTO(-3L, "Node C", "CONCEPT", 1L)
        ));
        candidate.setEdges(List.of(
            new KnowledgeGraphEdgeDTO(null, -1L, -2L, "PREREQUISITE"),
            new KnowledgeGraphEdgeDTO(null, -2L, -3L, "PREREQUISITE")
        ));

        KnowledgeGraphDTO saved = service.saveGraph(1L, candidate);

        assertThat(saved).isNotNull();
        assertThat(saved.getNodes()).hasSize(3);
        assertThat(saved.getEdges()).hasSize(2);
        assertThat(saved.getNodes()).allMatch(n -> n.getId() != null && n.getId() > 0);
        assertThat(saved.getEdges()).allMatch(e -> e.getId() != null && e.getId() > 0);
    }

    // TC-KG-052-001: Direct cycle between 2 nodes (A -> B -> A) is detected and rejected with GraphCycleException
    @Test
    @DisplayName("TC-KG-052-001: Direct cycle (A -> B -> A) must be rejected with GraphCycleException")
    void directCycle_shouldThrowGraphCycleExceptionWithCyclePath() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(mathSubject));

        KnowledgeGraphDTO candidate = KnowledgeGraphDTOBuilder.forSubject(1L, "Mathematics")
            .addNode(10L, "Algebra I", "CONCEPT")
            .addNode(20L, "Geometry I", "CONCEPT")
            .addPrerequisiteEdge(1L, 10L, 20L)
            .addPrerequisiteEdge(2L, 20L, 10L)
            .build();

        assertThatThrownBy(() -> service.validateGraph(1L, candidate))
            .isInstanceOf(GraphCycleException.class)
            .satisfies(ex -> {
                GraphCycleException gce = (GraphCycleException) ex;
                assertThat(gce.getErrorKey()).isEqualTo("cycleDetected");
                assertThat(gce.getEntityName()).isEqualTo("knowledgeGraph");
                assertThat(gce.getCyclePath()).contains("Algebra I -> Geometry I -> Algebra I");
            });
    }

    // TC-KG-053-001: Indirect cycle across 3 or more nodes (A -> B -> C -> A) is detected and rejected
    @Test
    @DisplayName("TC-KG-053-001: Longer cycle (A -> B -> C -> A) must be rejected with GraphCycleException")
    void longerCycle_shouldThrowGraphCycleExceptionWithCyclePath() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(mathSubject));

        KnowledgeGraphDTO candidate = KnowledgeGraphDTOBuilder.forSubject(1L, "Mathematics")
            .addNode(1L, "Node 1", "CONCEPT")
            .addNode(2L, "Node 2", "CONCEPT")
            .addNode(3L, "Node 3", "CONCEPT")
            .addPrerequisiteEdge(101L, 1L, 2L)
            .addPrerequisiteEdge(102L, 2L, 3L)
            .addPrerequisiteEdge(103L, 3L, 1L)
            .build();

        assertThatThrownBy(() -> service.validateGraph(1L, candidate))
            .isInstanceOf(GraphCycleException.class)
            .satisfies(ex -> {
                GraphCycleException gce = (GraphCycleException) ex;
                assertThat(gce.getErrorKey()).isEqualTo("cycleDetected");
                assertThat(gce.getEntityName()).isEqualTo("knowledgeGraph");
                assertThat(gce.getCyclePath()).contains("Node 1 -> Node 2 -> Node 3 -> Node 1");
            });
    }

    // TC-KG-054-001: Self-referencing edge (A -> A) is rejected during edge validation
    @Test
    @DisplayName("TC-KG-054-001: Self-edge (A -> A) must be rejected with selfReferencingEdge")
    void selfEdge_shouldBeRejected() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(mathSubject));

        KnowledgeGraphDTO candidate = KnowledgeGraphDTOBuilder.forSubject(1L, "Mathematics")
            .addNode(1L, "Node 1", "CONCEPT")
            .addPrerequisiteEdge(101L, 1L, 1L)
            .build();

        assertThatThrownBy(() -> service.validateGraph(1L, candidate))
            .isInstanceOf(BadRequestAlertException.class)
            .satisfies(ex -> {
                BadRequestAlertException bre = (BadRequestAlertException) ex;
                assertThat(bre.getEntityName()).isEqualTo("knowledgeGraph");
                assertThat(bre.getErrorKey()).isEqualTo("selfReferencingEdge");
            });
    }
    // TC-KG-036-001: Reject duplicate prerequisite relationship between same nodes
    @Test
    @DisplayName("TC-KG-036-001: Reject duplicate prerequisite relationship between same nodes with duplicateEdge")
    void validateGraph_duplicateRelationship_shouldRejectWithDuplicateEdge() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(mathSubject));

        KnowledgeGraphDTO candidate = KnowledgeGraphDTOBuilder.forSubject(1L, "Mathematics")
            .addNode(1L, "Node 1", "CONCEPT")
            .addNode(2L, "Node 2", "CONCEPT")
            .addPrerequisiteEdge(101L, 1L, 2L)
            .addPrerequisiteEdge(102L, 1L, 2L)
            .build();

        assertThatThrownBy(() -> service.validateGraph(1L, candidate))
            .isInstanceOf(BadRequestAlertException.class)
            .hasMessageContaining("Duplicate prerequisite relationship")
            .satisfies(ex -> {
                BadRequestAlertException bre = (BadRequestAlertException) ex;
                assertThat(bre.getEntityName()).isEqualTo("knowledgeGraph");
                assertThat(bre.getErrorKey()).isEqualTo("duplicateEdge");
            });
    }

    // TC-KG-023-001: Reject candidate graph with duplicate node IDs
    @Test
    @DisplayName("TC-KG-023-001: Duplicate node IDs in request must be rejected with duplicateNodeId")
    void duplicateNodeIds_shouldBeRejected() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(mathSubject));

        KnowledgeGraphDTO candidate = KnowledgeGraphDTOBuilder.forSubject(1L, "Mathematics")
            .addNode(1L, "Node 1", "CONCEPT")
            .addNode(1L, "Node 1 Duplicated", "CONCEPT")
            .build();

        assertThatThrownBy(() -> service.validateGraph(1L, candidate))
            .isInstanceOf(BadRequestAlertException.class)
            .hasMessageContaining("Duplicate node ID")
            .satisfies(ex -> {
                BadRequestAlertException bre = (BadRequestAlertException) ex;
                assertThat(bre.getEntityName()).isEqualTo("knowledgeGraph");
                assertThat(bre.getErrorKey()).isEqualTo("duplicateNodeId");
            });
    }
    // TC-KG-032-001: Reject edge when fromNodeId is null
    @Test
    @DisplayName("TC-KG-032-001: Reject edge when fromNodeId is null with edgeEndpointsNull")
    void validateGraph_edgeFromNodeIdNull_shouldRejectWithEdgeEndpointsNull() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(mathSubject));

        KnowledgeGraphDTO candidate = KnowledgeGraphDTOBuilder.forSubject(1L, "Mathematics")
            .addNode(10L, "Node A", "CONCEPT")
            .addNode(20L, "Node B", "CONCEPT")
            .addEdge(101L, null, 20L, "PREREQUISITE")
            .build();

        assertThatThrownBy(() -> service.validateGraph(1L, candidate))
            .isInstanceOf(BadRequestAlertException.class)
            .hasMessageContaining("Edge endpoints (fromNodeId and toNodeId) cannot be null")
            .satisfies(ex -> {
                BadRequestAlertException bre = (BadRequestAlertException) ex;
                assertThat(bre.getEntityName()).isEqualTo("knowledgeGraph");
                assertThat(bre.getErrorKey()).isEqualTo("edgeEndpointsNull");
            });
    }

    // TC-KG-033-001: Reject edge when toNodeId is null
    @Test
    @DisplayName("TC-KG-033-001: Reject edge when toNodeId is null with edgeEndpointsNull")
    void validateGraph_edgeToNodeIdNull_shouldRejectWithEdgeEndpointsNull() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(mathSubject));

        KnowledgeGraphDTO candidate = KnowledgeGraphDTOBuilder.forSubject(1L, "Mathematics")
            .addNode(10L, "Node A", "CONCEPT")
            .addNode(20L, "Node B", "CONCEPT")
            .addEdge(101L, 10L, null, "PREREQUISITE")
            .build();

        assertThatThrownBy(() -> service.validateGraph(1L, candidate))
            .isInstanceOf(BadRequestAlertException.class)
            .hasMessageContaining("Edge endpoints (fromNodeId and toNodeId) cannot be null")
            .satisfies(ex -> {
                BadRequestAlertException bre = (BadRequestAlertException) ex;
                assertThat(bre.getEntityName()).isEqualTo("knowledgeGraph");
                assertThat(bre.getErrorKey()).isEqualTo("edgeEndpointsNull");
            });
    }

    // TC-KG-034-001: Reject edge when endpoints reference non-existent node
    @Test
    @DisplayName("TC-KG-034-001: Reject edge when endpoints reference non-existent node with unknownNodeEndpoint")
    void validateGraph_unknownEndpoint_shouldRejectWithUnknownNodeEndpoint() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(mathSubject));

        KnowledgeGraphDTO candidate = KnowledgeGraphDTOBuilder.forSubject(1L, "Mathematics")
            .addNode(1L, "Node 1", "CONCEPT")
            .addPrerequisiteEdge(101L, 1L, 999L)
            .build();

        assertThatThrownBy(() -> service.validateGraph(1L, candidate))
            .isInstanceOf(BadRequestAlertException.class)
            .hasMessageContaining("Edge references unknown")
            .satisfies(ex -> {
                BadRequestAlertException bre = (BadRequestAlertException) ex;
                assertThat(bre.getEntityName()).isEqualTo("knowledgeGraph");
                assertThat(bre.getErrorKey()).isEqualTo("unknownNodeEndpoint");
            });
    }

    // TC-KG-028-001: Reject update when node belongs to another subject
    @Test
    @DisplayName("TC-KG-028-001: Saving candidate node with positive ID belonging to another subject must be rejected with crossSubjectNode")
    void crossSubjectEndpoint_shouldBeRejected() {
        when(subjectRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(mathSubject));
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(mathSubject));
        // Subject 1 only has node 10L
        KnowledgeNode mathNode = new KnowledgeNode().id(10L).title("Math Node").subject(mathSubject);
        when(knowledgeNodeRepository.findAllWithSubjectBySubjectId(1L)).thenReturn(List.of(mathNode));

        KnowledgeGraphDTO candidate = KnowledgeGraphDTOBuilder.forSubject(1L, "Mathematics")
            .addNode(99L, "History Node", "CONCEPT")
            .build();

        assertThatThrownBy(() -> service.saveGraph(1L, candidate))
            .isInstanceOf(BadRequestAlertException.class)
            .hasMessageContaining("does not belong to subject 1")
            .satisfies(ex -> {
                BadRequestAlertException bre = (BadRequestAlertException) ex;
                assertThat(bre.getEntityName()).isEqualTo("knowledgeGraph");
                assertThat(bre.getErrorKey()).isEqualTo("crossSubjectNode");
            });

        verify(knowledgeNodeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Invalid or unsupported subject must be rejected")
    void invalidSubject_shouldBeRejected() {
        when(subjectRepository.findById(3L)).thenReturn(Optional.of(literatureSubject));

        KnowledgeGraphDTO candidate = new KnowledgeGraphDTO(3L, "Literature", "Desc");

        assertThatThrownBy(() -> service.validateGraph(3L, candidate))
            .isInstanceOf(BadRequestAlertException.class)
            .hasMessageContaining("not supported for Knowledge Graph management");
    }

    // TC-KG-020-001: Reject candidate node when title is blank
    @Test
    @DisplayName("TC-KG-020-001: Missing or blank node title must be rejected with nodeTitleRequired")
    void missingRequiredNodeFields_shouldBeRejected() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(mathSubject));

        KnowledgeGraphDTO candidate = KnowledgeGraphDTOBuilder.forSubject(1L, "Mathematics")
            .addNode(1L, "   ", "CONCEPT")
            .build();

        assertThatThrownBy(() -> service.validateGraph(1L, candidate))
            .isInstanceOf(BadRequestAlertException.class)
            .hasMessageContaining("Node title cannot be blank")
            .satisfies(ex -> {
                BadRequestAlertException bre = (BadRequestAlertException) ex;
                assertThat(bre.getEntityName()).isEqualTo("knowledgeGraph");
                assertThat(bre.getErrorKey()).isEqualTo("nodeTitleRequired");
            });
    }
    // TC-KG-035-001: Reject edge with unsupported relation type
    @Test
    @DisplayName("TC-KG-035-001: Reject edge with unsupported relation type with unsupportedRelationType")
    void validateGraph_unsupportedRelationType_shouldRejectWithUnsupportedRelationType() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(mathSubject));

        KnowledgeGraphDTO candidate = KnowledgeGraphDTOBuilder.forSubject(1L, "Mathematics")
            .addNode(1L, "Node 1", "CONCEPT")
            .addNode(2L, "Node 2", "CONCEPT")
            .addEdge(10L, 1L, 2L, "RELATED_TO")
            .build();

        assertThatThrownBy(() -> service.validateGraph(1L, candidate))
            .isInstanceOf(BadRequestAlertException.class)
            .hasMessageContaining("Unsupported relationship type")
            .satisfies(ex -> {
                BadRequestAlertException bre = (BadRequestAlertException) ex;
                assertThat(bre.getEntityName()).isEqualTo("knowledgeGraph");
                assertThat(bre.getErrorKey()).isEqualTo("unsupportedRelationType");
            });
    }

    @Test
    @DisplayName("Duplicate candidate edge IDs must be rejected")
    void duplicateCandidateEdgeIds_shouldBeRejected() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(mathSubject));

        KnowledgeGraphDTO candidate = new KnowledgeGraphDTO(1L, "Mathematics", "Desc");
        candidate.setNodes(List.of(
            new KnowledgeGraphNodeDTO(1L, "Node 1", "CONCEPT", 1L),
            new KnowledgeGraphNodeDTO(2L, "Node 2", "CONCEPT", 1L),
            new KnowledgeGraphNodeDTO(3L, "Node 3", "CONCEPT", 1L)
        ));
        candidate.setEdges(List.of(
            new KnowledgeGraphEdgeDTO(99L, 1L, 2L, "PREREQUISITE"),
            new KnowledgeGraphEdgeDTO(99L, 2L, 3L, "PREREQUISITE")
        ));

        assertThatThrownBy(() -> service.validateGraph(1L, candidate))
            .isInstanceOf(BadRequestAlertException.class)
            .hasMessageContaining("Duplicate edge ID in request: 99");
    }

    // TC-KG-021-001: Reject candidate node when title exceeds 255 chars
    @Test
    @DisplayName("TC-KG-021-001: Node title exceeding 255 characters must be rejected with nodeTitleTooLong")
    void nodeTitleTooLong_shouldBeRejected() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(mathSubject));

        KnowledgeGraphDTO candidate = KnowledgeGraphDTOBuilder.forSubject(1L, "Mathematics")
            .addNode(1L, "A".repeat(256), "CONCEPT")
            .build();

        assertThatThrownBy(() -> service.validateGraph(1L, candidate))
            .isInstanceOf(BadRequestAlertException.class)
            .hasMessageContaining("Node title cannot exceed 255 characters")
            .satisfies(ex -> {
                BadRequestAlertException bre = (BadRequestAlertException) ex;
                assertThat(bre.getEntityName()).isEqualTo("knowledgeGraph");
                assertThat(bre.getErrorKey()).isEqualTo("nodeTitleTooLong");
            });
    }

    // TC-KG-022-001: Reject candidate node when type exceeds 50 chars
    @Test
    @DisplayName("TC-KG-022-001: Node type exceeding 50 characters must be rejected with nodeTypeTooLong")
    void nodeTypeTooLong_shouldBeRejected() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(mathSubject));

        KnowledgeGraphDTO candidate = KnowledgeGraphDTOBuilder.forSubject(1L, "Mathematics")
            .addNode(1L, "Valid Title", "T".repeat(51))
            .build();

        assertThatThrownBy(() -> service.validateGraph(1L, candidate))
            .isInstanceOf(BadRequestAlertException.class)
            .hasMessageContaining("Node type cannot exceed 50 characters")
            .satisfies(ex -> {
                BadRequestAlertException bre = (BadRequestAlertException) ex;
                assertThat(bre.getEntityName()).isEqualTo("knowledgeGraph");
                assertThat(bre.getErrorKey()).isEqualTo("nodeTypeTooLong");
            });
    }

    // TC-KG-047-001: saveGraph rejects payload when subjectId in URL does not match subjectId in candidateGraph
    @Test
    @DisplayName("TC-KG-047-001: Subject ID mismatch between path and payload must be rejected with subjectIdMismatch")
    void subjectIdMismatch_shouldBeRejected() {
        KnowledgeGraphDTO candidate = KnowledgeGraphDTOBuilder.forSubject(2L, "Mathematics").build();

        assertThatThrownBy(() -> service.validateGraph(1L, candidate))
            .isInstanceOf(BadRequestAlertException.class)
            .satisfies(ex -> {
                BadRequestAlertException bre = (BadRequestAlertException) ex;
                assertThat(bre.getEntityName()).isEqualTo("knowledgeGraph");
                assertThat(bre.getErrorKey()).isEqualTo("subjectIdMismatch");
            });
    }

    // TC-KG-065-001: Corrected graph can be saved successfully after previous cycle rejection on same subject
    @Test
    @DisplayName("TC-KG-065-001: Corrected graph can be saved after a cycle error without leaving partial state")
    void correctedGraphCanBeSavedAfterCycleError() {
        when(subjectRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(mathSubject));
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(mathSubject));
        when(knowledgeNodeRepository.findAllWithSubjectBySubjectId(1L)).thenReturn(Collections.emptyList());
        when(knowledgeEdgeRepository.findAllReferencingSubjectId(1L)).thenReturn(Collections.emptyList());

        // First attempt: Cyclic graph (A -> B -> A)
        KnowledgeGraphDTO cyclicCandidate = KnowledgeGraphDTOBuilder.forSubject(1L, "Mathematics")
            .addNode(-1L, "A", "CONCEPT")
            .addNode(-2L, "B", "CONCEPT")
            .addPrerequisiteEdge(null, -1L, -2L)
            .addPrerequisiteEdge(null, -2L, -1L)
            .build();

        assertThatThrownBy(() -> service.saveGraph(1L, cyclicCandidate))
            .isInstanceOf(GraphCycleException.class);

        // Verify zero writes were performed during failed validation
        verify(knowledgeNodeRepository, never()).save(any());
        verify(knowledgeEdgeRepository, never()).save(any());

        // Second attempt: Corrected graph (A -> B only)
        KnowledgeGraphDTO correctedCandidate = KnowledgeGraphDTOBuilder.forSubject(1L, "Mathematics")
            .addNode(-1L, "A", "CONCEPT")
            .addNode(-2L, "B", "CONCEPT")
            .addPrerequisiteEdge(null, -1L, -2L)
            .build();

        when(knowledgeNodeRepository.save(any(KnowledgeNode.class))).thenAnswer(invocation -> {
            KnowledgeNode node = invocation.getArgument(0);
            node.setId(node.getId() != null && node.getId() > 0 ? node.getId() : 50L);
            return node;
        });

        when(knowledgeEdgeRepository.save(any(KnowledgeEdge.class))).thenAnswer(invocation -> {
            KnowledgeEdge edge = invocation.getArgument(0);
            edge.setId(60L);
            return edge;
        });

        KnowledgeGraphDTO saved = service.saveGraph(1L, correctedCandidate);
        assertThat(saved).isNotNull();
        assertThat(saved.getNodes()).hasSize(2);
        assertThat(saved.getEdges()).hasSize(1);
    }

    // =========================================================================
    // BATCH 1: Subject Selection & Graph Loading (8 test cases)
    // =========================================================================

    // TC-KG-001-001: Return supported subjects when both Math and History exist
    @Test
    @DisplayName("TC-KG-001-001: Return supported subjects when both Math and History exist without persisting")
    void getAvailableSubjects_bothSubjectsExist_shouldReturnSubjectsWithoutPersisting() {
        when(subjectRepository.findAll()).thenReturn(List.of(mathSubject, historySubject));
        when(subjectMapper.toDto(anyList())).thenAnswer(inv -> {
            List<Subject> list = inv.getArgument(0);
            return list.stream().map(s -> {
                SubjectDTO d = new SubjectDTO();
                d.setId(s.getId());
                d.setName(s.getName());
                d.setDescription(s.getDescription());
                return d;
            }).toList();
        });

        List<SubjectDTO> subjects = service.getAvailableSubjects();

        assertThat(subjects).hasSize(2);
        assertThat(subjects).extracting(SubjectDTO::getName).containsExactlyInAnyOrder("Mathematics", "History");
        verify(subjectRepository, times(1)).findAll();
        verify(subjectRepository, never()).save(any());
    }

    // TC-KG-002-001: Throw BadRequest when required History subject is missing
    @Test
    @DisplayName("TC-KG-002-001: Missing History subject should throw BadRequestAlertException and never persist")
    void getAvailableSubjects_missingRequiredSubject_shouldThrowExceptionAndNeverPersist() {
        when(subjectRepository.findAll()).thenReturn(List.of(mathSubject)); // History missing

        assertThatThrownBy(() -> service.getAvailableSubjects())
            .isInstanceOf(BadRequestAlertException.class)
            .hasMessageContaining("Required knowledge graph subject(s) missing from database: History");

        verify(subjectRepository, never()).save(any());
    }

    // TC-KG-003-001: Throw BadRequest when both required subjects are missing
    @Test
    @DisplayName("TC-KG-003-001: Both subjects missing should throw BadRequestAlertException and never persist")
    void getAvailableSubjects_bothMissing_shouldThrowExceptionAndNeverPersist() {
        when(subjectRepository.findAll()).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> service.getAvailableSubjects())
            .isInstanceOf(BadRequestAlertException.class)
            .hasMessageContaining("Required knowledge graph subject(s) missing from database: Mathematics and History");

        verify(subjectRepository, never()).save(any());
    }

    // TC-KG-010-001: Verify LoadKnowledgeGraphNodesForMathematics
    @Test
    @DisplayName("TC-KG-010-001: Load knowledge graph nodes for Mathematics should return all nodes mapped to DTOs")
    void loadGraph_forMathematics_shouldReturnNodesMappedToDTOs() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(mathSubject));
        KnowledgeNode node1 = new KnowledgeNode().id(101L).title("Algebra Fundamentals").type("CONCEPT").subject(mathSubject);
        KnowledgeNode node2 = new KnowledgeNode().id(102L).title("Calculus Basics").type("CONCEPT").subject(mathSubject);
        when(knowledgeNodeRepository.findAllWithSubjectBySubjectId(1L)).thenReturn(List.of(node1, node2));
        when(knowledgeEdgeRepository.findAllBySubjectId(1L)).thenReturn(Collections.emptyList());

        KnowledgeGraphDTO graph = service.loadGraph(1L);

        assertThat(graph).isNotNull();
        assertThat(graph.getSubjectId()).isEqualTo(1L);
        assertThat(graph.getSubjectName()).isEqualTo("Mathematics");
        assertThat(graph.getNodes()).hasSize(2);
        assertThat(graph.getNodes()).extracting(KnowledgeGraphNodeDTO::getTitle)
            .containsExactly("Algebra Fundamentals", "Calculus Basics");
        assertThat(graph.getNodes()).extracting(KnowledgeGraphNodeDTO::getId)
            .containsExactly(101L, 102L);
        assertThat(graph.getNodes()).allMatch(n -> "CONCEPT".equals(n.getType()) && Long.valueOf(1L).equals(n.getSubjectId()));

        verify(subjectRepository, times(1)).findById(1L);
        verify(knowledgeNodeRepository, times(1)).findAllWithSubjectBySubjectId(1L);
        verify(knowledgeEdgeRepository, times(1)).findAllBySubjectId(1L);
    }

    // TC-KG-011-001: Verify FilterAndReturnOnlyPrerequisiteEdgesOnLoad
    @Test
    @DisplayName("TC-KG-011-001: Load graph should filter and return only PREREQUISITE relationships, excluding other types")
    void loadGraph_shouldFilterAndReturnOnlyPrerequisiteEdgesOnLoad() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(mathSubject));
        KnowledgeNode node1 = new KnowledgeNode().id(101L).title("Algebra").type("CONCEPT").subject(mathSubject);
        KnowledgeNode node2 = new KnowledgeNode().id(102L).title("Calculus").type("CONCEPT").subject(mathSubject);
        KnowledgeNode node3 = new KnowledgeNode().id(103L).title("Trigonometry").type("CONCEPT").subject(mathSubject);
        when(knowledgeNodeRepository.findAllWithSubjectBySubjectId(1L)).thenReturn(List.of(node1, node2, node3));

        KnowledgeEdge prereqEdge = new KnowledgeEdge()
            .id(501L)
            .fromNode(node1)
            .toNode(node2)
            .relationType(KnowledgeGraphEdgeDTO.RELATION_TYPE_PREREQUISITE);

        KnowledgeEdge relatedEdge = new KnowledgeEdge()
            .id(502L)
            .fromNode(node2)
            .toNode(node3)
            .relationType("RELATED_TO");

        when(knowledgeEdgeRepository.findAllBySubjectId(1L)).thenReturn(List.of(prereqEdge, relatedEdge));

        KnowledgeGraphDTO graph = service.loadGraph(1L);

        assertThat(graph).isNotNull();
        assertThat(graph.getEdges()).hasSize(1);
        assertThat(graph.getEdges().get(0).getId()).isEqualTo(501L);
        assertThat(graph.getEdges().get(0).getFromNodeId()).isEqualTo(101L);
        assertThat(graph.getEdges().get(0).getToNodeId()).isEqualTo(102L);
        assertThat(graph.getEdges().get(0).getRelationType()).isEqualTo(KnowledgeGraphEdgeDTO.RELATION_TYPE_PREREQUISITE);
    }

    // TC-KG-012-001: Verify RejectLoadGraphWhenSubjectIdIsNull
    @Test
    @DisplayName("TC-KG-012-001: Load graph when subjectId is null should throw BadRequestAlertException")
    void loadGraph_whenSubjectIdIsNull_shouldThrowBadRequestAlertException() {
        assertThatThrownBy(() -> service.loadGraph(null))
            .isInstanceOf(BadRequestAlertException.class)
            .hasMessageContaining("Subject ID cannot be null");

        verify(subjectRepository, never()).findById(any());
        verify(knowledgeNodeRepository, never()).findAllWithSubjectBySubjectId(any());
        verify(knowledgeEdgeRepository, never()).findAllBySubjectId(any());
    }

    // TC-KG-013-001: Verify RejectLoadGraphWhenSubjectDoesNotExist
    @Test
    @DisplayName("TC-KG-013-001: Load graph when subject does not exist should throw BadRequestAlertException")
    void loadGraph_whenSubjectDoesNotExist_shouldThrowBadRequestAlertException() {
        when(subjectRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadGraph(999L))
            .isInstanceOf(BadRequestAlertException.class)
            .hasMessageContaining("Subject not found with ID: 999");

        verify(knowledgeNodeRepository, never()).findAllWithSubjectBySubjectId(any());
        verify(knowledgeEdgeRepository, never()).findAllBySubjectId(any());
    }

    // TC-KG-014-001: Verify RejectLoadGraphWhenSubjectIsNotSupported
    @Test
    @DisplayName("TC-KG-014-001: Load graph when subject is unsupported should throw BadRequestAlertException")
    void loadGraph_whenSubjectIsNotSupported_shouldThrowBadRequestAlertException() {
        when(subjectRepository.findById(3L)).thenReturn(Optional.of(literatureSubject));

        assertThatThrownBy(() -> service.loadGraph(3L))
            .isInstanceOf(BadRequestAlertException.class)
            .hasMessageContaining("Subject 'Literature' is not supported for Knowledge Graph management")
            .satisfies(ex -> {
                BadRequestAlertException bre = (BadRequestAlertException) ex;
                assertThat(bre.getEntityName()).isEqualTo("knowledgeGraph");
                assertThat(bre.getErrorKey()).isEqualTo("unsupportedSubject");
            });

        verify(knowledgeNodeRepository, never()).findAllWithSubjectBySubjectId(any());
        verify(knowledgeEdgeRepository, never()).findAllBySubjectId(any());
    }

    // =========================================================================
    // BATCH 2: Node Management (7 test cases: TC-KG-019..023, 027..028)
    // =========================================================================

    // TC-KG-019-001: Persist new knowledge node with generated ID
    @Test
    @DisplayName("TC-KG-019-001: New candidate node with negative ID should be persisted and assigned positive ID")
    void saveGraph_newNodeWithNegativeId_shouldPersistAndAssignPositiveId() {
        when(subjectRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(mathSubject));
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(mathSubject));
        when(knowledgeNodeRepository.findAllWithSubjectBySubjectId(1L)).thenReturn(Collections.emptyList());
        when(knowledgeEdgeRepository.findAllReferencingSubjectId(1L)).thenReturn(Collections.emptyList());

        when(knowledgeNodeRepository.save(any(KnowledgeNode.class))).thenAnswer(inv -> {
            KnowledgeNode node = inv.getArgument(0);
            node.setId(101L);
            return node;
        });

        KnowledgeGraphDTO candidate = KnowledgeGraphDTOBuilder.forSubject(1L, "Mathematics")
            .addNode(-1L, "New Concept Node", "CONCEPT")
            .build();

        KnowledgeGraphDTO saved = service.saveGraph(1L, candidate);

        assertThat(saved).isNotNull();
        assertThat(saved.getNodes()).hasSize(1);
        assertThat(saved.getNodes().get(0).getId()).isEqualTo(101L);
        assertThat(saved.getNodes().get(0).getTitle()).isEqualTo("New Concept Node");

        ArgumentCaptor<KnowledgeNode> nodeCaptor = ArgumentCaptor.forClass(KnowledgeNode.class);
        verify(knowledgeNodeRepository, times(1)).save(nodeCaptor.capture());

        KnowledgeNode capturedNode = nodeCaptor.getValue();
        assertThat(capturedNode.getTitle()).isEqualTo("New Concept Node");
        assertThat(capturedNode.getType()).isEqualTo("CONCEPT");
        assertThat(capturedNode.getSubject()).isEqualTo(mathSubject);
    }

    // TC-KG-027-001: Update existing node title and type when ID matches
    @Test
    @DisplayName("TC-KG-027-001: Existing node title and type should be updated and saved with ArgumentCaptor verification")
    void saveGraph_existingNodeTitleAndTypeUpdated_shouldSaveSuccessfully() {
        when(subjectRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(mathSubject));
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(mathSubject));

        KnowledgeNode existingNode = new KnowledgeNode()
            .id(10L)
            .title("Original Title")
            .type("OLD_TYPE")
            .subject(mathSubject);
        when(knowledgeNodeRepository.findAllWithSubjectBySubjectId(1L)).thenReturn(List.of(existingNode));
        when(knowledgeEdgeRepository.findAllReferencingSubjectId(1L)).thenReturn(Collections.emptyList());

        when(knowledgeNodeRepository.save(any(KnowledgeNode.class))).thenAnswer(inv -> inv.getArgument(0));

        KnowledgeGraphDTO candidate = KnowledgeGraphDTOBuilder.forSubject(1L, "Mathematics")
            .addNode(10L, "Updated Title", "UPDATED_TYPE")
            .build();

        KnowledgeGraphDTO result = service.saveGraph(1L, candidate);

        assertThat(result).isNotNull();
        assertThat(result.getNodes()).hasSize(1);
        assertThat(result.getNodes().get(0).getId()).isEqualTo(10L);
        assertThat(result.getNodes().get(0).getTitle()).isEqualTo("Updated Title");

        ArgumentCaptor<KnowledgeNode> nodeCaptor = ArgumentCaptor.forClass(KnowledgeNode.class);
        verify(knowledgeNodeRepository, times(1)).save(nodeCaptor.capture());

        KnowledgeNode capturedNode = nodeCaptor.getValue();
        assertThat(capturedNode.getId()).isEqualTo(10L);
        assertThat(capturedNode.getTitle()).isEqualTo("Updated Title");
        assertThat(capturedNode.getType()).isEqualTo("UPDATED_TYPE");
        assertThat(capturedNode.getSubject()).isEqualTo(mathSubject);
    }
    // TC-KG-040-001: Update existing edge endpoints in selected subject
    @Test
    @DisplayName("TC-KG-040-001: Existing edge endpoints in selected subject can be updated successfully")
    void saveGraph_existingEdgeEndpointsUpdated_shouldSaveSuccessfully() {
        when(subjectRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(mathSubject));
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(mathSubject));

        KnowledgeNode nodeA = new KnowledgeNode().id(10L).title("Node A").subject(mathSubject);
        KnowledgeNode nodeB = new KnowledgeNode().id(20L).title("Node B").subject(mathSubject);
        KnowledgeNode nodeC = new KnowledgeNode().id(30L).title("Node C").subject(mathSubject);
        when(knowledgeNodeRepository.findAllWithSubjectBySubjectId(1L)).thenReturn(List.of(nodeA, nodeB, nodeC));

        KnowledgeEdge existingEdge = new KnowledgeEdge().id(500L).fromNode(nodeA).toNode(nodeB).relationType("PREREQUISITE");
        when(knowledgeEdgeRepository.findAllReferencingSubjectId(1L)).thenReturn(List.of(existingEdge));

        when(knowledgeNodeRepository.save(any(KnowledgeNode.class))).thenAnswer(inv -> inv.getArgument(0));
        when(knowledgeEdgeRepository.save(any(KnowledgeEdge.class))).thenAnswer(inv -> inv.getArgument(0));

        // Candidate updates existing edge 500L to connect from 10L to 30L instead of 20L
        KnowledgeGraphDTO candidate = KnowledgeGraphDTOBuilder.forSubject(1L, "Mathematics")
            .addNode(10L, "Node A", "CONCEPT")
            .addNode(20L, "Node B", "CONCEPT")
            .addNode(30L, "Node C", "CONCEPT")
            .addPrerequisiteEdge(500L, 10L, 30L)
            .build();

        KnowledgeGraphDTO saved = service.saveGraph(1L, candidate);

        assertThat(saved).isNotNull();
        assertThat(saved.getEdges()).hasSize(1);
        assertThat(saved.getEdges().get(0).getId()).isEqualTo(500L);
        assertThat(saved.getEdges().get(0).getFromNodeId()).isEqualTo(10L);
        assertThat(saved.getEdges().get(0).getToNodeId()).isEqualTo(30L);

        ArgumentCaptor<KnowledgeEdge> edgeCaptor = ArgumentCaptor.forClass(KnowledgeEdge.class);
        verify(knowledgeEdgeRepository, times(1)).save(edgeCaptor.capture());

        KnowledgeEdge capturedEdge = edgeCaptor.getValue();
        assertThat(capturedEdge.getId()).isEqualTo(500L);
        assertThat(capturedEdge.getFromNode().getId()).isEqualTo(10L);
        assertThat(capturedEdge.getToNode().getId()).isEqualTo(30L);
        assertThat(capturedEdge.getRelationType()).isEqualTo(KnowledgeGraphEdgeDTO.RELATION_TYPE_PREREQUISITE);
    }
    // TC-KG-031-001: Persist new prerequisite edge with generated ID
    @Test
    @DisplayName("TC-KG-031-001: Persist new prerequisite edge with generated positive ID")
    void saveGraph_newEdgeWithNullId_shouldPersistAndAssignPositiveId() {
        when(subjectRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(mathSubject));
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(mathSubject));

        KnowledgeNode nodeA = new KnowledgeNode().id(10L).title("Node A").subject(mathSubject);
        KnowledgeNode nodeB = new KnowledgeNode().id(20L).title("Node B").subject(mathSubject);
        when(knowledgeNodeRepository.findAllWithSubjectBySubjectId(1L)).thenReturn(List.of(nodeA, nodeB));
        when(knowledgeEdgeRepository.findAllReferencingSubjectId(1L)).thenReturn(Collections.emptyList());

        when(knowledgeNodeRepository.save(any(KnowledgeNode.class))).thenAnswer(inv -> inv.getArgument(0));
        when(knowledgeEdgeRepository.save(any(KnowledgeEdge.class))).thenAnswer(inv -> {
            KnowledgeEdge e = inv.getArgument(0);
            e.setId(601L);
            return e;
        });

        KnowledgeGraphDTO candidate = KnowledgeGraphDTOBuilder.forSubject(1L, "Mathematics")
            .addNode(10L, "Node A", "CONCEPT")
            .addNode(20L, "Node B", "CONCEPT")
            .addPrerequisiteEdge(null, 10L, 20L)
            .build();

        KnowledgeGraphDTO saved = service.saveGraph(1L, candidate);

        assertThat(saved).isNotNull();
        assertThat(saved.getEdges()).hasSize(1);
        assertThat(saved.getEdges().get(0).getId()).isEqualTo(601L);
        assertThat(saved.getEdges().get(0).getFromNodeId()).isEqualTo(10L);
        assertThat(saved.getEdges().get(0).getToNodeId()).isEqualTo(20L);

        ArgumentCaptor<KnowledgeEdge> edgeCaptor = ArgumentCaptor.forClass(KnowledgeEdge.class);
        verify(knowledgeEdgeRepository, times(1)).save(edgeCaptor.capture());

        KnowledgeEdge capturedEdge = edgeCaptor.getValue();
        assertThat(capturedEdge.getFromNode().getId()).isEqualTo(10L);
        assertThat(capturedEdge.getToNode().getId()).isEqualTo(20L);
        assertThat(capturedEdge.getRelationType()).isEqualTo(KnowledgeGraphEdgeDTO.RELATION_TYPE_PREREQUISITE);
    }
    // TC-KG-041-001: Reject edge update when edge belongs to another subject
    // NC-04 Governance: CHARACTERIZATION TEST — IMPLEMENTATION BEHAVIOR ONLY (NEED CONFIRMATION / PARTIALLY DEFERRED)
    @Test
    @DisplayName("TC-KG-041-001 [NC-04 Characterization]: Edge ID from another subject is rejected with crossSubjectEdge and leaves data unchanged")
    void saveGraph_edgeIdFromAnotherSubject_shouldBeRejectedAndLeaveDataUnchanged() {
        when(subjectRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(mathSubject));
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(mathSubject));

        KnowledgeNode nodeA = new KnowledgeNode().id(10L).title("Node A").subject(mathSubject);
        KnowledgeNode nodeB = new KnowledgeNode().id(20L).title("Node B").subject(mathSubject);
        when(knowledgeNodeRepository.findAllWithSubjectBySubjectId(1L)).thenReturn(List.of(nodeA, nodeB));
        // Subject 1 has no edges
        when(knowledgeEdgeRepository.findAllReferencingSubjectId(1L)).thenReturn(Collections.emptyList());
        // Edge 999L exists in DB under History subject
        when(knowledgeEdgeRepository.existsById(999L)).thenReturn(true);

        KnowledgeGraphDTO candidate = KnowledgeGraphDTOBuilder.forSubject(1L, "Mathematics")
            .addNode(10L, "Node A", "CONCEPT")
            .addNode(20L, "Node B", "CONCEPT")
            .addPrerequisiteEdge(999L, 10L, 20L)
            .build();

        assertThatThrownBy(() -> service.saveGraph(1L, candidate))
            .isInstanceOf(BadRequestAlertException.class)
            .hasMessageContaining("Edge with ID 999 does not belong to subject 1")
            .satisfies(ex -> {
                BadRequestAlertException bre = (BadRequestAlertException) ex;
                assertThat(bre.getEntityName()).isEqualTo("knowledgeGraph");
                assertThat(bre.getErrorKey()).isEqualTo("crossSubjectEdge");
            });

        // Verify zero writes occurred
        verify(knowledgeEdgeRepository, never()).save(any());
        verify(knowledgeNodeRepository, never()).save(any());
    }
    // TC-KG-042-001: Reject edge update when edge ID does not exist in database
    @Test
    @DisplayName("TC-KG-042-001: Unknown positive edge ID is rejected with edgeNotFound and leaves data unchanged")
    void saveGraph_unknownPositiveEdgeId_shouldRejectWithEdgeNotFound() {
        when(subjectRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(mathSubject));
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(mathSubject));

        KnowledgeNode nodeA = new KnowledgeNode().id(10L).title("Node A").subject(mathSubject);
        KnowledgeNode nodeB = new KnowledgeNode().id(20L).title("Node B").subject(mathSubject);
        when(knowledgeNodeRepository.findAllWithSubjectBySubjectId(1L)).thenReturn(List.of(nodeA, nodeB));
        when(knowledgeEdgeRepository.findAllReferencingSubjectId(1L)).thenReturn(Collections.emptyList());
        // Edge 888L does not exist at all in DB
        when(knowledgeEdgeRepository.existsById(888L)).thenReturn(false);

        KnowledgeGraphDTO candidate = KnowledgeGraphDTOBuilder.forSubject(1L, "Mathematics")
            .addNode(10L, "Node A", "CONCEPT")
            .addNode(20L, "Node B", "CONCEPT")
            .addPrerequisiteEdge(888L, 10L, 20L)
            .build();

        assertThatThrownBy(() -> service.saveGraph(1L, candidate))
            .isInstanceOf(BadRequestAlertException.class)
            .hasMessageContaining("Edge not found with ID: 888")
            .satisfies(ex -> {
                BadRequestAlertException bre = (BadRequestAlertException) ex;
                assertThat(bre.getEntityName()).isEqualTo("knowledgeGraph");
                assertThat(bre.getErrorKey()).isEqualTo("edgeNotFound");
            });

        // Verify zero writes occurred
        verify(knowledgeEdgeRepository, never()).save(any());
        verify(knowledgeNodeRepository, never()).save(any());
    }

    // TC-KG-043-001: Reconcile deletions by removing omitted edges
    @Test
    @DisplayName("TC-KG-043-001: Reconcile deletions by removing omitted edges from repository")
    void saveGraph_omittedEdgeFromCandidate_shouldBeDeletedFromRepository() {
        when(subjectRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(mathSubject));
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(mathSubject));

        KnowledgeNode nodeA = new KnowledgeNode().id(10L).title("Node A").subject(mathSubject);
        KnowledgeNode nodeB = new KnowledgeNode().id(20L).title("Node B").subject(mathSubject);
        KnowledgeNode nodeC = new KnowledgeNode().id(30L).title("Node C").subject(mathSubject);
        when(knowledgeNodeRepository.findAllWithSubjectBySubjectId(1L)).thenReturn(List.of(nodeA, nodeB, nodeC));

        KnowledgeEdge existingEdge501 = new KnowledgeEdge().id(501L).fromNode(nodeA).toNode(nodeB).relationType("PREREQUISITE");
        KnowledgeEdge existingEdge502 = new KnowledgeEdge().id(502L).fromNode(nodeB).toNode(nodeC).relationType("PREREQUISITE");
        when(knowledgeEdgeRepository.findAllReferencingSubjectId(1L)).thenReturn(List.of(existingEdge501, existingEdge502));

        when(knowledgeNodeRepository.save(any(KnowledgeNode.class))).thenAnswer(inv -> inv.getArgument(0));
        when(knowledgeEdgeRepository.save(any(KnowledgeEdge.class))).thenAnswer(inv -> inv.getArgument(0));

        // Candidate retains all 3 nodes (so nodeIdsToDelete is empty)
        // Retains edge 501, omits edge 502
        KnowledgeGraphDTO candidate = KnowledgeGraphDTOBuilder.forSubject(1L, "Mathematics")
            .addNode(10L, "Node A", "CONCEPT")
            .addNode(20L, "Node B", "CONCEPT")
            .addNode(30L, "Node C", "CONCEPT")
            .addPrerequisiteEdge(501L, 10L, 20L)
            .build();

        KnowledgeGraphDTO result = service.saveGraph(1L, candidate);

        assertThat(result).isNotNull();
        assertThat(result.getEdges()).hasSize(1);
        assertThat(result.getEdges().get(0).getId()).isEqualTo(501L);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<KnowledgeEdge>> deletedEdgesCaptor = ArgumentCaptor.forClass(List.class);
        verify(knowledgeEdgeRepository, times(1)).deleteAll(deletedEdgesCaptor.capture());

        List<KnowledgeEdge> deletedEdges = deletedEdgesCaptor.getValue();
        assertThat(deletedEdges).isNotNull();
        assertThat(deletedEdges).hasSize(1);
        assertThat(deletedEdges.get(0).getId()).isEqualTo(502L);
        assertThat(deletedEdges.get(0).getId()).isNotEqualTo(501L);

        verify(knowledgeEdgeRepository, atLeastOnce()).flush();
    }

    // =========================================================================
    // BATCH 4: Cycle Detection, Concurrency & Persistence Pipeline (13 test cases)
    // (In-place standardized: TC-047, TC-052, TC-053, TC-054, TC-065)
    // (New implementations: TC-046, TC-055, TC-056, TC-058, TC-059, TC-064, TC-070, TC-071)
    // =========================================================================

    // TC-KG-046-001: saveGraph executes pessimistic write lock on subject repository to serialize concurrent saves
    @Test
    @DisplayName("TC-KG-046-001: saveGraph executes pessimistic write lock on subject repository to serialize concurrent saves")
    void saveGraph_shouldAcquirePessimisticWriteLockOnSubjectBeforeReadOrWrite() {
        when(subjectRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(mathSubject));
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(mathSubject));
        when(knowledgeNodeRepository.findAllWithSubjectBySubjectId(1L)).thenReturn(Collections.emptyList());
        when(knowledgeEdgeRepository.findAllReferencingSubjectId(1L)).thenReturn(Collections.emptyList());

        KnowledgeGraphDTO candidate = KnowledgeGraphDTOBuilder.forSubject(1L, "Mathematics")
            .addNode(-1L, "Concept A", "CONCEPT")
            .addNode(-2L, "Concept B", "CONCEPT")
            .addPrerequisiteEdge(null, -1L, -2L)
            .build();

        when(knowledgeNodeRepository.save(any(KnowledgeNode.class))).thenAnswer(inv -> {
            KnowledgeNode kn = inv.getArgument(0);
            kn.setId(101L);
            return kn;
        });
        when(knowledgeEdgeRepository.save(any(KnowledgeEdge.class))).thenAnswer(inv -> {
            KnowledgeEdge ke = inv.getArgument(0);
            ke.setId(201L);
            return ke;
        });

        service.saveGraph(1L, candidate);

        InOrder inOrder = inOrder(subjectRepository, knowledgeNodeRepository, knowledgeEdgeRepository);
        inOrder.verify(subjectRepository).findByIdForUpdate(1L);
        inOrder.verify(knowledgeNodeRepository).findAllWithSubjectBySubjectId(1L);
        inOrder.verify(knowledgeEdgeRepository).findAllReferencingSubjectId(1L);
    }

    // TC-KG-055-001: Complex acyclic DAG with diamond pattern passes cycle check without false positive
    @Test
    @DisplayName("TC-KG-055-001: Complex acyclic DAG with diamond pattern passes cycle check without false positive")
    void saveGraph_complexAcyclicDiamondDag_shouldPassCycleCheck() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(mathSubject));

        KnowledgeGraphDTO candidate = KnowledgeGraphDTOBuilder.forSubject(1L, "Mathematics")
            .addNode(10L, "Node A", "CONCEPT")
            .addNode(20L, "Node B", "CONCEPT")
            .addNode(30L, "Node C", "CONCEPT")
            .addNode(40L, "Node D", "CONCEPT")
            .addPrerequisiteEdge(1L, 10L, 20L)
            .addPrerequisiteEdge(2L, 10L, 30L)
            .addPrerequisiteEdge(3L, 20L, 40L)
            .addPrerequisiteEdge(4L, 30L, 40L)
            .build();

        // Validating diamond DAG must pass without throwing any GraphCycleException
        service.validateGraph(1L, candidate);
    }

    // TC-KG-056-001: Cycle in secondary disconnected subgraph component is detected and rejected
    @Test
    @DisplayName("TC-KG-056-001: Cycle in secondary disconnected subgraph component is detected and rejected")
    void saveGraph_cycleInSecondaryDisconnectedComponent_shouldThrowGraphCycleException() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(mathSubject));

        KnowledgeGraphDTO candidate = KnowledgeGraphDTOBuilder.forSubject(1L, "Mathematics")
            .addNode(10L, "Node A", "CONCEPT")
            .addNode(20L, "Node B", "CONCEPT")
            .addNode(30L, "Node C", "CONCEPT")
            .addNode(40L, "Node D", "CONCEPT")
            .addPrerequisiteEdge(1L, 10L, 20L)
            .addPrerequisiteEdge(2L, 30L, 40L)
            .addPrerequisiteEdge(3L, 40L, 30L)
            .build();

        assertThatThrownBy(() -> service.validateGraph(1L, candidate))
            .isInstanceOf(GraphCycleException.class)
            .satisfies(ex -> {
                GraphCycleException gce = (GraphCycleException) ex;
                assertThat(gce.getErrorKey()).isEqualTo("cycleDetected");
                assertThat(gce.getEntityName()).isEqualTo("knowledgeGraph");
                assertThat(gce.getCyclePath()).contains("Node C -> Node D -> Node C");
                assertThat(gce.getCycleNodeIds()).contains(30L, 40L);
            });
    }

    // TC-KG-058-001: GraphCycleException contains formatted cycle path string identifying participating nodes
    @Test
    @DisplayName("TC-KG-058-001: GraphCycleException contains formatted cycle path string identifying participating nodes")
    void saveGraph_cycleDetected_exceptionShouldContainFormattedCyclePath() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(mathSubject));

        KnowledgeGraphDTO candidate = KnowledgeGraphDTOBuilder.forSubject(1L, "Mathematics")
            .addNode(101L, "Node A", "CONCEPT")
            .addNode(102L, "Node B", "CONCEPT")
            .addPrerequisiteEdge(1L, 101L, 102L)
            .addPrerequisiteEdge(2L, 102L, 101L)
            .build();

        assertThatThrownBy(() -> service.validateGraph(1L, candidate))
            .isInstanceOf(GraphCycleException.class)
            .satisfies(ex -> {
                GraphCycleException gce = (GraphCycleException) ex;
                assertThat(gce.getErrorKey()).isEqualTo("cycleDetected");
                assertThat(gce.getEntityName()).isEqualTo("knowledgeGraph");
                assertThat(gce.getCyclePath())
                    .isNotBlank()
                    .contains("Node A -> Node B -> Node A");
            });
    }

    // TC-KG-059-001: GraphCycleException contains cycleNodeIds list enabling UI highlighting
    @Test
    @DisplayName("TC-KG-059-001: GraphCycleException contains cycleNodeIds list enabling UI highlighting")
    void saveGraph_cycleDetected_exceptionShouldContainCycleNodeIds() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(mathSubject));

        KnowledgeGraphDTO candidate = KnowledgeGraphDTOBuilder.forSubject(1L, "Mathematics")
            .addNode(101L, "Node A", "CONCEPT")
            .addNode(102L, "Node B", "CONCEPT")
            .addNode(103L, "Node C", "CONCEPT")
            .addPrerequisiteEdge(1L, 101L, 102L)
            .addPrerequisiteEdge(2L, 102L, 103L)
            .addPrerequisiteEdge(3L, 103L, 101L)
            .build();

        assertThatThrownBy(() -> service.validateGraph(1L, candidate))
            .isInstanceOf(GraphCycleException.class)
            .satisfies(ex -> {
                GraphCycleException gce = (GraphCycleException) ex;
                assertThat(gce.getErrorKey()).isEqualTo("cycleDetected");
                assertThat(gce.getCycleNodeIds())
                    .isNotNull()
                    .contains(101L, 102L, 103L);
            });
    }

    // TC-KG-064-001: When cycle validation fails, zero repository writes or flushes are executed
    @Test
    @DisplayName("TC-KG-064-001: When cycle validation fails, zero repository writes or flushes are executed")
    void saveGraph_whenCycleDetected_shouldExecuteZeroRepositoryWritesOrFlushes() {
        when(subjectRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(mathSubject));
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(mathSubject));

        KnowledgeGraphDTO candidate = KnowledgeGraphDTOBuilder.forSubject(1L, "Mathematics")
            .addNode(101L, "Node A", "CONCEPT")
            .addNode(102L, "Node B", "CONCEPT")
            .addPrerequisiteEdge(1L, 101L, 102L)
            .addPrerequisiteEdge(2L, 102L, 101L)
            .build();

        assertThatThrownBy(() -> service.saveGraph(1L, candidate))
            .isInstanceOf(GraphCycleException.class);

        verify(knowledgeNodeRepository, never()).save(any());
        verify(knowledgeEdgeRepository, never()).save(any());
        verify(knowledgeNodeRepository, never()).deleteAll(any());
        verify(knowledgeEdgeRepository, never()).deleteAll(any());
        verify(knowledgeNodeRepository, never()).flush();
        verify(knowledgeEdgeRepository, never()).flush();
    }

    // TC-KG-070-001: saveGraph persists all verified nodes, edges, and deletions in a single transactional boundary
    // Note: Unit test verifies interaction sequence on mock layer using InOrder. ACID rollback deferred to Integration Test.
    @Test
    @DisplayName("TC-KG-070-001: saveGraph executes complete persistence pipeline in order")
    void saveGraph_allVerifiedNodesAndEdges_shouldExecuteFullPersistencePipelineInOrder() {
        when(subjectRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(mathSubject));
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(mathSubject));

        KnowledgeNode existingNode1 = new KnowledgeNode().id(10L).title("Node 1").subject(mathSubject);
        KnowledgeNode existingNodeToDelete = new KnowledgeNode().id(20L).title("Node 2 to delete").subject(mathSubject);
        when(knowledgeNodeRepository.findAllWithSubjectBySubjectId(1L)).thenReturn(List.of(existingNode1, existingNodeToDelete));

        KnowledgeEdge existingEdgeToDelete = new KnowledgeEdge().id(501L).fromNode(existingNode1).toNode(existingNodeToDelete).relationType("PREREQUISITE");
        when(knowledgeEdgeRepository.findAllReferencingSubjectId(1L)).thenReturn(List.of(existingEdgeToDelete));

        when(knowledgeNodeRepository.save(any(KnowledgeNode.class))).thenAnswer(inv -> inv.getArgument(0));
        when(knowledgeEdgeRepository.save(any(KnowledgeEdge.class))).thenAnswer(inv -> inv.getArgument(0));

        // Candidate retains node 1, adds node 3 (new, temp id -3L), omits node 2. Edge from node 1 to node 3.
        KnowledgeGraphDTO candidate = KnowledgeGraphDTOBuilder.forSubject(1L, "Mathematics")
            .addNode(10L, "Node 1 Updated", "CONCEPT")
            .addNode(-3L, "Node 3 New", "CONCEPT")
            .addPrerequisiteEdge(null, 10L, -3L)
            .build();

        KnowledgeGraphDTO result = service.saveGraph(1L, candidate);
        assertThat(result).isNotNull();

        InOrder inOrder = inOrder(subjectRepository, knowledgeNodeRepository, knowledgeEdgeRepository);
        inOrder.verify(subjectRepository).findByIdForUpdate(1L);
        inOrder.verify(knowledgeNodeRepository).findAllWithSubjectBySubjectId(1L);
        inOrder.verify(knowledgeEdgeRepository).findAllReferencingSubjectId(1L);
        inOrder.verify(knowledgeEdgeRepository).deleteAll(anyList());
        inOrder.verify(knowledgeEdgeRepository).flush();
        inOrder.verify(knowledgeNodeRepository).deleteAll(anyList());
        inOrder.verify(knowledgeNodeRepository).flush();
        inOrder.verify(knowledgeNodeRepository, atLeastOnce()).save(any(KnowledgeNode.class));
        inOrder.verify(knowledgeNodeRepository).flush();
        inOrder.verify(knowledgeEdgeRepository, atLeastOnce()).save(any(KnowledgeEdge.class));
        inOrder.verify(knowledgeEdgeRepository).flush();
    }

    // TC-KG-071-001: Candidate temporary negative node IDs (-1, -2) are correctly mapped to persistent DB IDs in edges
    @Test
    @DisplayName("TC-KG-071-001: Candidate temporary negative node IDs are correctly mapped to persistent DB IDs in edges")
    void saveGraph_temporaryNegativeNodeIds_shouldBeMappedToPersistentPositiveDbIdsInEdges() {
        when(subjectRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(mathSubject));
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(mathSubject));
        when(knowledgeNodeRepository.findAllWithSubjectBySubjectId(1L)).thenReturn(Collections.emptyList());
        when(knowledgeEdgeRepository.findAllReferencingSubjectId(1L)).thenReturn(Collections.emptyList());

        when(knowledgeNodeRepository.save(any(KnowledgeNode.class))).thenAnswer(inv -> {
            KnowledgeNode node = inv.getArgument(0);
            if ("Concept Alpha".equals(node.getTitle())) {
                node.setId(1001L);
            } else if ("Concept Beta".equals(node.getTitle())) {
                node.setId(1002L);
            }
            return node;
        });

        when(knowledgeEdgeRepository.save(any(KnowledgeEdge.class))).thenAnswer(inv -> {
            KnowledgeEdge edge = inv.getArgument(0);
            edge.setId(5001L);
            return edge;
        });

        KnowledgeGraphDTO candidate = KnowledgeGraphDTOBuilder.forSubject(1L, "Mathematics")
            .addNode(-1L, "Concept Alpha", "CONCEPT")
            .addNode(-2L, "Concept Beta", "CONCEPT")
            .addPrerequisiteEdge(null, -1L, -2L)
            .build();

        KnowledgeGraphDTO result = service.saveGraph(1L, candidate);
        assertThat(result).isNotNull();

        // Verify entity level using ArgumentCaptor per instructions
        ArgumentCaptor<KnowledgeEdge> edgeCaptor = ArgumentCaptor.forClass(KnowledgeEdge.class);
        verify(knowledgeEdgeRepository).save(edgeCaptor.capture());
        KnowledgeEdge persistedEdge = edgeCaptor.getValue();
        assertThat(persistedEdge).isNotNull();
        assertThat(persistedEdge.getFromNode()).isNotNull();
        assertThat(persistedEdge.getFromNode().getId()).isEqualTo(1001L);
        assertThat(persistedEdge.getToNode()).isNotNull();
        assertThat(persistedEdge.getToNode().getId()).isEqualTo(1002L);

        // Verify response DTO level
        assertThat(result.getEdges()).hasSize(1);
        assertThat(result.getEdges().get(0).getFromNodeId()).isEqualTo(1001L);
        assertThat(result.getEdges().get(0).getToNodeId()).isEqualTo(1002L);
    }
}
