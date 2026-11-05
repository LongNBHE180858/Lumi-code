package com.lumi.service.impl;

import com.lumi.domain.KnowledgeEdge;
import com.lumi.domain.KnowledgeNode;
import com.lumi.domain.Subject;
import com.lumi.repository.KnowledgeEdgeRepository;
import com.lumi.repository.KnowledgeNodeRepository;
import com.lumi.repository.SubjectRepository;
import com.lumi.service.KnowledgeGraphService;
import com.lumi.service.dto.KnowledgeGraphDTO;
import com.lumi.service.dto.KnowledgeGraphEdgeDTO;
import com.lumi.service.dto.KnowledgeGraphNodeDTO;
import com.lumi.service.dto.SubjectDTO;
import com.lumi.service.mapper.SubjectMapper;
import com.lumi.web.rest.errors.BadRequestAlertException;
import com.lumi.web.rest.errors.GraphCycleException;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing Knowledge Graphs.
 */
@Service
@Transactional
public class KnowledgeGraphServiceImpl implements KnowledgeGraphService {

    private static final Logger LOG = LoggerFactory.getLogger(KnowledgeGraphServiceImpl.class);
    private static final String ENTITY_NAME = "knowledgeGraph";

    public static final Set<String> SUPPORTED_SUBJECT_NAMES = Set.of(
        "mathematics",
        "toán",
        "toán học",
        "history",
        "lịch sử"
    );

    private final SubjectRepository subjectRepository;
    private final KnowledgeNodeRepository knowledgeNodeRepository;
    private final KnowledgeEdgeRepository knowledgeEdgeRepository;
    private final SubjectMapper subjectMapper;

    public KnowledgeGraphServiceImpl(
        SubjectRepository subjectRepository,
        KnowledgeNodeRepository knowledgeNodeRepository,
        KnowledgeEdgeRepository knowledgeEdgeRepository,
        SubjectMapper subjectMapper
    ) {
        this.subjectRepository = subjectRepository;
        this.knowledgeNodeRepository = knowledgeNodeRepository;
        this.knowledgeEdgeRepository = knowledgeEdgeRepository;
        this.subjectMapper = subjectMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectDTO> getAvailableSubjects() {
        LOG.debug("Fetching available subjects for Knowledge Graph");
        List<Subject> allSubjects = subjectRepository.findAll();
        List<Subject> supported = allSubjects.stream()
            .filter(s -> s.getName() != null && isSupportedSubjectName(s.getName()))
            .sorted(Comparator.comparing(Subject::getName))
            .collect(Collectors.toList());

        // Ensure both Mathematics and History exist if not found
        boolean hasMath = supported.stream().anyMatch(s -> isMathSubject(s.getName()));
        boolean hasHistory = supported.stream().anyMatch(s -> isHistorySubject(s.getName()));

        if (!hasMath || !hasHistory) {
            String missing = !hasMath && !hasHistory ? "Mathematics and History" : (!hasMath ? "Mathematics" : "History");
            throw new BadRequestAlertException(
                "Required knowledge graph subject(s) missing from database: " + missing + ". Please ensure seed migrations have been applied.",
                ENTITY_NAME,
                "requiredSubjectMissing"
            );
        }

        return subjectMapper.toDto(supported);
    }

    @Override
    @Transactional(readOnly = true)
    public KnowledgeGraphDTO loadGraph(Long subjectId) {
        LOG.debug("Loading knowledge graph for subjectId: {}", subjectId);
        if (subjectId == null) {
            throw new BadRequestAlertException("Subject ID cannot be null", ENTITY_NAME, "subjectIdRequired");
        }

        Subject subject = subjectRepository.findById(subjectId)
            .orElseThrow(() -> new BadRequestAlertException("Subject not found with ID: " + subjectId, ENTITY_NAME, "subjectNotFound"));

        validateSubjectSupported(subject);

        List<KnowledgeNode> nodes = knowledgeNodeRepository.findAllWithSubjectBySubjectId(subjectId);
        List<KnowledgeEdge> edges = knowledgeEdgeRepository.findAllBySubjectId(subjectId);

        KnowledgeGraphDTO graphDTO = new KnowledgeGraphDTO(subject.getId(), subject.getName(), subject.getDescription());

        List<KnowledgeGraphNodeDTO> nodeDTOs = nodes.stream()
            .map(n -> new KnowledgeGraphNodeDTO(n.getId(), n.getTitle(), n.getType(), subjectId))
            .collect(Collectors.toList());

        // Only return PREREQUISITE relationships
        List<KnowledgeGraphEdgeDTO> edgeDTOs = edges.stream()
            .filter(e -> e.getRelationType() == null || e.getRelationType().equalsIgnoreCase(KnowledgeGraphEdgeDTO.RELATION_TYPE_PREREQUISITE))
            .map(e -> new KnowledgeGraphEdgeDTO(
                e.getId(),
                e.getFromNode().getId(),
                e.getToNode().getId(),
                KnowledgeGraphEdgeDTO.RELATION_TYPE_PREREQUISITE
            ))
            .collect(Collectors.toList());

        graphDTO.setNodes(nodeDTOs);
        graphDTO.setEdges(edgeDTOs);
        return graphDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public void validateGraph(Long subjectId, KnowledgeGraphDTO candidateGraph) {
        LOG.debug("Validating candidate graph for subjectId: {}", subjectId);
        if (subjectId == null) {
            throw new BadRequestAlertException("Subject ID cannot be null", ENTITY_NAME, "subjectIdRequired");
        }
        if (candidateGraph == null) {
            throw new BadRequestAlertException("Candidate graph cannot be null", ENTITY_NAME, "graphRequired");
        }
        if (candidateGraph.getSubjectId() != null && !subjectId.equals(candidateGraph.getSubjectId())) {
            throw new BadRequestAlertException("Subject ID in payload does not match URL path", ENTITY_NAME, "subjectIdMismatch");
        }

        Subject subject = subjectRepository.findById(subjectId)
            .orElseThrow(() -> new BadRequestAlertException("Subject not found with ID: " + subjectId, ENTITY_NAME, "subjectNotFound"));

        validateSubjectSupported(subject);

        List<KnowledgeGraphNodeDTO> nodes = candidateGraph.getNodes() != null ? candidateGraph.getNodes() : Collections.emptyList();
        List<KnowledgeGraphEdgeDTO> edges = candidateGraph.getEdges() != null ? candidateGraph.getEdges() : Collections.emptyList();

        // 1. Check duplicate node IDs in request
        Set<Long> seenNodeIds = new HashSet<>();
        for (KnowledgeGraphNodeDTO node : nodes) {
            if (node.getId() != null) {
                if (!seenNodeIds.add(node.getId())) {
                    throw new BadRequestAlertException("Duplicate node ID in request: " + node.getId(), ENTITY_NAME, "duplicateNodeId");
                }
            }
        }

        // 2. Validate node fields
        for (KnowledgeGraphNodeDTO node : nodes) {
            if (node.getTitle() == null || node.getTitle().trim().isEmpty()) {
                throw new BadRequestAlertException("Node title cannot be blank", ENTITY_NAME, "nodeTitleRequired");
            }
            if (node.getTitle().length() > 255) {
                throw new BadRequestAlertException("Node title cannot exceed 255 characters", ENTITY_NAME, "nodeTitleTooLong");
            }
            if (node.getType() != null && node.getType().length() > 50) {
                throw new BadRequestAlertException("Node type cannot exceed 50 characters", ENTITY_NAME, "nodeTypeTooLong");
            }
        }

        // 3. Validate edges and endpoints
        Map<Long, KnowledgeGraphNodeDTO> nodeMap = nodes.stream()
            .filter(n -> n.getId() != null)
            .collect(Collectors.toMap(KnowledgeGraphNodeDTO::getId, n -> n, (a, b) -> a));

        Set<Long> seenEdgeIds = new HashSet<>();
        Set<String> seenEdges = new HashSet<>();
        for (KnowledgeGraphEdgeDTO edge : edges) {
            if (edge.getId() != null && edge.getId() > 0) {
                if (!seenEdgeIds.add(edge.getId())) {
                    throw new BadRequestAlertException("Duplicate edge ID in request: " + edge.getId(), ENTITY_NAME, "duplicateEdgeId");
                }
            }
            if (edge.getFromNodeId() == null || edge.getToNodeId() == null) {
                throw new BadRequestAlertException("Edge endpoints (fromNodeId and toNodeId) cannot be null", ENTITY_NAME, "edgeEndpointsNull");
            }

            if (!nodeMap.containsKey(edge.getFromNodeId())) {
                throw new BadRequestAlertException("Edge references unknown fromNode ID: " + edge.getFromNodeId(), ENTITY_NAME, "unknownNodeEndpoint");
            }

            if (!nodeMap.containsKey(edge.getToNodeId())) {
                throw new BadRequestAlertException("Edge references unknown toNode ID: " + edge.getToNodeId(), ENTITY_NAME, "unknownNodeEndpoint");
            }

            // Self-referencing check
            if (edge.getFromNodeId().equals(edge.getToNodeId())) {
                throw new BadRequestAlertException("Self-referencing prerequisite edge detected on node: " + edge.getFromNodeId(), ENTITY_NAME, "selfReferencingEdge");
            }

            // Duplicate relationship check
            String edgeKey = edge.getFromNodeId() + "->" + edge.getToNodeId();
            if (!seenEdges.add(edgeKey)) {
                throw new BadRequestAlertException("Duplicate prerequisite relationship between node " + edge.getFromNodeId() + " and " + edge.getToNodeId(), ENTITY_NAME, "duplicateEdge");
            }

            // Validate relationship type
            String relType = edge.getRelationType();
            if (relType != null && !relType.trim().isEmpty() && !relType.equalsIgnoreCase(KnowledgeGraphEdgeDTO.RELATION_TYPE_PREREQUISITE)) {
                throw new BadRequestAlertException("Unsupported relationship type: " + relType + ". Only PREREQUISITE is allowed.", ENTITY_NAME, "unsupportedRelationType");
            }
        }

        // 4. Cycle Detection in directed prerequisite graph: fromNode -> toNode
        detectCycles(nodes, edges);
    }

    @Override
    @Transactional
    public KnowledgeGraphDTO saveGraph(Long subjectId, KnowledgeGraphDTO candidateGraph) {
        LOG.debug("Saving complete knowledge graph for subjectId: {}", subjectId);

        // Step 1: Pessimistic locking on Subject to serialize concurrent saves for the same subject
        Subject subject = subjectRepository.findByIdForUpdate(subjectId)
            .orElseThrow(() -> new BadRequestAlertException("Subject not found with ID: " + subjectId, ENTITY_NAME, "subjectNotFound"));

        validateSubjectSupported(subject);

        // Step 2: Validate complete candidate graph before any modification
        validateGraph(subjectId, candidateGraph);

        // Step 3: Verify subject isolation: all candidate existing nodes and edges must belong to this subject
        List<KnowledgeNode> existingNodes = knowledgeNodeRepository.findAllWithSubjectBySubjectId(subjectId);
        Set<Long> existingNodeIds = existingNodes.stream().map(KnowledgeNode::getId).collect(Collectors.toSet());

        List<KnowledgeEdge> existingEdges = knowledgeEdgeRepository.findAllReferencingSubjectId(subjectId);
        Set<Long> existingEdgeIds = existingEdges.stream().map(KnowledgeEdge::getId).collect(Collectors.toSet());

        List<KnowledgeGraphNodeDTO> candidateNodes = candidateGraph.getNodes() != null ? candidateGraph.getNodes() : Collections.emptyList();
        List<KnowledgeGraphEdgeDTO> candidateEdges = candidateGraph.getEdges() != null ? candidateGraph.getEdges() : Collections.emptyList();

        for (KnowledgeGraphNodeDTO nodeDto : candidateNodes) {
            if (nodeDto.getId() != null && nodeDto.getId() > 0) {
                if (!existingNodeIds.contains(nodeDto.getId())) {
                    throw new BadRequestAlertException(
                        "Node with ID " + nodeDto.getId() + " does not belong to subject " + subjectId,
                        ENTITY_NAME,
                        "crossSubjectNode"
                    );
                }
            }
        }

        for (KnowledgeGraphEdgeDTO edgeDto : candidateEdges) {
            if (edgeDto.getId() != null && edgeDto.getId() > 0) {
                if (!existingEdgeIds.contains(edgeDto.getId())) {
                    if (knowledgeEdgeRepository.existsById(edgeDto.getId())) {
                        throw new BadRequestAlertException(
                            "Edge with ID " + edgeDto.getId() + " does not belong to subject " + subjectId,
                            ENTITY_NAME,
                            "crossSubjectEdge"
                        );
                    } else {
                        throw new BadRequestAlertException(
                            "Edge not found with ID: " + edgeDto.getId(),
                            ENTITY_NAME,
                            "edgeNotFound"
                        );
                    }
                }
            }
        }

        // Step 4: Reconcile deletions
        Set<Long> candidateExistingNodeIds = candidateNodes.stream()
            .map(KnowledgeGraphNodeDTO::getId)
            .filter(Objects::nonNull)
            .filter(id -> id > 0)
            .collect(Collectors.toSet());

        List<KnowledgeNode> nodesToDelete = existingNodes.stream()
            .filter(n -> !candidateExistingNodeIds.contains(n.getId()))
            .collect(Collectors.toList());
        Set<Long> nodeIdsToDelete = nodesToDelete.stream().map(KnowledgeNode::getId).collect(Collectors.toSet());

        Set<Long> candidateExistingEdgeIds = candidateEdges.stream()
            .map(KnowledgeGraphEdgeDTO::getId)
            .filter(Objects::nonNull)
            .filter(id -> id > 0)
            .collect(Collectors.toSet());

        // Delete edges that are omitted or reference nodes to be deleted
        List<KnowledgeEdge> edgesToDelete = existingEdges.stream()
            .filter(e -> !candidateExistingEdgeIds.contains(e.getId())
                || nodeIdsToDelete.contains(e.getFromNode().getId())
                || nodeIdsToDelete.contains(e.getToNode().getId()))
            .collect(Collectors.toList());

        if (!edgesToDelete.isEmpty()) {
            LOG.debug("Deleting {} obsolete edges for subjectId: {}", edgesToDelete.size(), subjectId);
            knowledgeEdgeRepository.deleteAll(edgesToDelete);
            knowledgeEdgeRepository.flush();
        }

        if (!nodesToDelete.isEmpty()) {
            LOG.debug("Deleting {} obsolete nodes for subjectId: {}", nodesToDelete.size(), subjectId);
            knowledgeNodeRepository.deleteAll(nodesToDelete);
            knowledgeNodeRepository.flush();
        }

        // Step 5: Save/Update nodes
        Map<Long, KnowledgeNode> clientToEntityMap = new HashMap<>();
        Map<Long, KnowledgeNode> existingNodeMap = existingNodes.stream()
            .collect(Collectors.toMap(KnowledgeNode::getId, n -> n));

        for (KnowledgeGraphNodeDTO nodeDto : candidateNodes) {
            KnowledgeNode node;
            if (nodeDto.getId() != null && nodeDto.getId() > 0) {
                node = existingNodeMap.get(nodeDto.getId());
                node.setTitle(nodeDto.getTitle().trim());
                node.setType(nodeDto.getType() != null ? nodeDto.getType().trim() : null);
            } else {
                node = new KnowledgeNode();
                node.setTitle(nodeDto.getTitle().trim());
                node.setType(nodeDto.getType() != null ? nodeDto.getType().trim() : null);
                node.setSubject(subject);
            }
            KnowledgeNode savedNode = knowledgeNodeRepository.save(node);
            clientToEntityMap.put(nodeDto.getId(), savedNode);
        }
        knowledgeNodeRepository.flush();

        // Step 6: Save/Update edges
        Map<Long, KnowledgeEdge> existingEdgeMap = existingEdges.stream()
            .filter(e -> !edgesToDelete.contains(e))
            .collect(Collectors.toMap(KnowledgeEdge::getId, e -> e, (a, b) -> a));

        List<KnowledgeEdge> savedEdges = new ArrayList<>();
        for (KnowledgeGraphEdgeDTO edgeDto : candidateEdges) {
            KnowledgeNode fromNode = clientToEntityMap.get(edgeDto.getFromNodeId());
            KnowledgeNode toNode = clientToEntityMap.get(edgeDto.getToNodeId());

            if (fromNode == null || toNode == null) {
                throw new BadRequestAlertException("Resolved edge endpoints cannot be null", ENTITY_NAME, "edgeEndpointsNull");
            }

            KnowledgeEdge edge;
            if (edgeDto.getId() != null && edgeDto.getId() > 0) {
                edge = existingEdgeMap.get(edgeDto.getId());
                if (edge == null) {
                    throw new BadRequestAlertException(
                        "Edge with ID " + edgeDto.getId() + " does not belong to subject " + subjectId,
                        ENTITY_NAME,
                        "crossSubjectEdge"
                    );
                }
                edge.setFromNode(fromNode);
                edge.setToNode(toNode);
                edge.setRelationType(KnowledgeGraphEdgeDTO.RELATION_TYPE_PREREQUISITE);
            } else {
                edge = new KnowledgeEdge();
                edge.setFromNode(fromNode);
                edge.setToNode(toNode);
                edge.setRelationType(KnowledgeGraphEdgeDTO.RELATION_TYPE_PREREQUISITE);
            }
            savedEdges.add(knowledgeEdgeRepository.save(edge));
        }
        knowledgeEdgeRepository.flush();

        // Step 7: Construct saved response DTO with stable persistent IDs
        KnowledgeGraphDTO savedGraph = new KnowledgeGraphDTO(subject.getId(), subject.getName(), subject.getDescription());
        List<KnowledgeGraphNodeDTO> savedNodeDTOs = candidateNodes.stream()
            .map(dto -> {
                KnowledgeNode persisted = clientToEntityMap.get(dto.getId());
                return new KnowledgeGraphNodeDTO(
                    persisted.getId(),
                    persisted.getTitle(),
                    persisted.getType(),
                    subject.getId()
                );
            })
            .collect(Collectors.toList());

        List<KnowledgeGraphEdgeDTO> savedEdgeDTOs = savedEdges.stream()
            .map(e -> new KnowledgeGraphEdgeDTO(
                e.getId(),
                e.getFromNode().getId(),
                e.getToNode().getId(),
                e.getRelationType()
            ))
            .collect(Collectors.toList());

        savedGraph.setNodes(savedNodeDTOs);
        savedGraph.setEdges(savedEdgeDTOs);

        LOG.info("Successfully persisted knowledge graph for subjectId: {}. Nodes: {}, Edges: {}",
            subjectId, savedNodeDTOs.size(), savedEdgeDTOs.size());

        return savedGraph;
    }

    /**
     * O(V + E) DFS Cycle Detection algorithm with visiting/visited states and cycle path reconstruction.
     */
    private void detectCycles(List<KnowledgeGraphNodeDTO> nodes, List<KnowledgeGraphEdgeDTO> edges) {
        Map<Long, String> nodeTitleMap = nodes.stream()
            .filter(n -> n.getId() != null)
            .collect(Collectors.toMap(KnowledgeGraphNodeDTO::getId, n -> n.getTitle() != null ? n.getTitle() : "Node #" + n.getId(), (a, b) -> a));

        Map<Long, List<Long>> adj = new LinkedHashMap<>();
        for (KnowledgeGraphNodeDTO node : nodes) {
            if (node.getId() != null) {
                adj.put(node.getId(), new ArrayList<>());
            }
        }
        for (KnowledgeGraphEdgeDTO edge : edges) {
            List<Long> neighbors = adj.get(edge.getFromNodeId());
            if (neighbors != null) {
                neighbors.add(edge.getToNodeId());
            }
        }

        Map<Long, Integer> state = new HashMap<>(); // 0: UNVISITED, 1: VISITING, 2: VISITED
        Map<Long, Long> parent = new HashMap<>();

        for (KnowledgeGraphNodeDTO node : nodes) {
            Long startId = node.getId();
            if (startId != null && state.getOrDefault(startId, 0) == 0) {
                List<Long> cycle = dfsCycle(startId, adj, state, parent);
                if (cycle != null && !cycle.isEmpty()) {
                    String cyclePath = cycle.stream()
                        .map(id -> nodeTitleMap.getOrDefault(id, "Node #" + id))
                        .collect(Collectors.joining(" -> "));

                    LOG.warn("Cycle detected in prerequisite graph: {}", cyclePath);
                    throw new GraphCycleException(
                        "Circular prerequisite dependency detected: " + cyclePath,
                        cyclePath,
                        cycle
                    );
                }
            }
        }
    }

    private List<Long> dfsCycle(
        Long u,
        Map<Long, List<Long>> adj,
        Map<Long, Integer> state,
        Map<Long, Long> parent
    ) {
        state.put(u, 1); // VISITING

        for (Long v : adj.getOrDefault(u, Collections.emptyList())) {
            Integer vState = state.getOrDefault(v, 0);
            if (vState == 1) {
                // Back-edge detected: cycle from v to u, plus u -> v
                List<Long> cycle = new ArrayList<>();
                cycle.add(v);
                Long curr = u;
                while (curr != null && !curr.equals(v)) {
                    cycle.add(curr);
                    curr = parent.get(curr);
                }
                cycle.add(v);
                Collections.reverse(cycle);
                return cycle;
            } else if (vState == 0) {
                parent.put(v, u);
                List<Long> cycle = dfsCycle(v, adj, state, parent);
                if (cycle != null) {
                    return cycle;
                }
            }
        }

        state.put(u, 2); // VISITED
        return null;
    }

    private void validateSubjectSupported(Subject subject) {
        if (subject.getName() == null || !isSupportedSubjectName(subject.getName())) {
            throw new BadRequestAlertException(
                "Subject '" + subject.getName() + "' is not supported for Knowledge Graph management. Only Mathematics and History are supported.",
                ENTITY_NAME,
                "unsupportedSubject"
            );
        }
    }

    private boolean isSupportedSubjectName(String name) {
        if (name == null) return false;
        String lower = name.trim().toLowerCase();
        return SUPPORTED_SUBJECT_NAMES.contains(lower) || lower.contains("toán") || lower.contains("lịch sử") || lower.contains("math") || lower.contains("history");
    }

    private boolean isMathSubject(String name) {
        if (name == null) return false;
        String lower = name.trim().toLowerCase();
        return lower.contains("toán") || lower.contains("math");
    }

    private boolean isHistorySubject(String name) {
        if (name == null) return false;
        String lower = name.trim().toLowerCase();
        return lower.contains("lịch sử") || lower.contains("history");
    }
}
