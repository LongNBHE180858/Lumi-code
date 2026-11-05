import { beforeEach, describe, expect, it, vi } from 'vitest';
import reducer, {
  addEdge,
  addNode,
  clearNotification,
  deleteEdge,
  deleteNode,
  fetchKnowledgeGraph,
  fetchSubjects,
  saveKnowledgeGraph,
  setSelectedSubjectId,
  updateNode,
  type KnowledgeGraphState,
} from './knowledge-graph.reducer';
import type { IKnowledgeGraph } from 'app/shared/model/knowledge-graph.model';

vi.mock('axios');

describe('Knowledge Graph Reducer Tests', () => {
  let initialState: KnowledgeGraphState;

  beforeEach(() => {
    vi.clearAllMocks();
    initialState = {
      subjects: [],
      selectedSubjectId: null,
      graph: null,
      loading: false,
      saving: false,
      saveSuccess: false,
      errorMessage: null,
      cyclePath: null,
      cycleNodeIds: [],
    };
  });

  it('should return initial state by default', () => {
    const state = reducer(undefined, { type: '' });
    expect(state.selectedSubjectId).toBeNull();
    expect(state.graph).toBeNull();
    expect(state.loading).toBe(false);
  });

  it('should set selected subject and clear previous notifications', () => {
    const modified = { ...initialState, saveSuccess: true, errorMessage: 'Old error' };
    const state = reducer(modified, setSelectedSubjectId(10));
    expect(state.selectedSubjectId).toBe(10);
    expect(state.saveSuccess).toBe(false);
    expect(state.errorMessage).toBeNull();
  });

  it('should clear notifications and error states on clearNotification', () => {
    const errorState: KnowledgeGraphState = {
      ...initialState,
      saveSuccess: true,
      errorMessage: 'Some error',
      cyclePath: '1 -> 2 -> 1',
      cycleNodeIds: [1, 2, 1],
    };
    const state = reducer(errorState, clearNotification());
    expect(state.saveSuccess).toBe(false);
    expect(state.errorMessage).toBeNull();
    expect(state.cyclePath).toBeNull();
    expect(state.cycleNodeIds).toEqual([]);
  });

  it('should add a new node with negative temp ID', () => {
    const withGraph: KnowledgeGraphState = {
      ...initialState,
      selectedSubjectId: 1,
      graph: {
        subjectId: 1,
        nodes: [{ id: 10, title: 'Existing', type: 'CONCEPT' }],
        edges: [],
      },
    };

    const state = reducer(withGraph, addNode({ title: 'New Concept', type: 'SKILL' }));
    expect(state.graph?.nodes).toHaveLength(2);
    const added = state.graph?.nodes[1];
    expect(added?.title).toBe('New Concept');
    expect(added?.type).toBe('SKILL');
    expect(added?.id).toBeLessThan(0);
  });

  it('should update an existing node title and type', () => {
    const withGraph: KnowledgeGraphState = {
      ...initialState,
      graph: {
        subjectId: 1,
        nodes: [{ id: 10, title: 'Old Title', type: 'CONCEPT' }],
        edges: [],
      },
    };

    const state = reducer(withGraph, updateNode({ id: 10, title: 'Updated Title', type: 'TOPIC' }));
    expect(state.graph?.nodes[0].title).toBe('Updated Title');
    expect(state.graph?.nodes[0].type).toBe('TOPIC');
  });

  it('should delete a node and its associated prerequisite edges', () => {
    const withGraph: KnowledgeGraphState = {
      ...initialState,
      graph: {
        subjectId: 1,
        nodes: [
          { id: 1, title: 'Node 1' },
          { id: 2, title: 'Node 2' },
        ],
        edges: [{ id: 100, fromNodeId: 1, toNodeId: 2, relationType: 'PREREQUISITE' }],
      },
    };

    const state = reducer(withGraph, deleteNode(1));
    expect(state.graph?.nodes).toHaveLength(1);
    expect(state.graph?.nodes[0].id).toBe(2);
    // Associated edge should be automatically removed
    expect(state.graph?.edges).toHaveLength(0);
  });

  it('should add a prerequisite edge between nodes', () => {
    const withGraph: KnowledgeGraphState = {
      ...initialState,
      graph: {
        subjectId: 1,
        nodes: [
          { id: 1, title: 'Node 1' },
          { id: 2, title: 'Node 2' },
        ],
        edges: [],
      },
    };

    const state = reducer(withGraph, addEdge({ fromNodeId: 1, toNodeId: 2 }));
    expect(state.graph?.edges).toHaveLength(1);
    expect(state.graph?.edges[0].fromNodeId).toBe(1);
    expect(state.graph?.edges[0].toNodeId).toBe(2);
    expect(state.graph?.edges[0].relationType).toBe('PREREQUISITE');
  });

  it('should delete an edge between nodes', () => {
    const withGraph: KnowledgeGraphState = {
      ...initialState,
      graph: {
        subjectId: 1,
        nodes: [],
        edges: [{ id: 10, fromNodeId: 1, toNodeId: 2, relationType: 'PREREQUISITE' }],
      },
    };

    const state = reducer(withGraph, deleteEdge({ fromNodeId: 1, toNodeId: 2 }));
    expect(state.graph?.edges).toHaveLength(0);
  });

  describe('Async Thunks & Draft Preservation', () => {
    it('fetchSubjects.fulfilled sets available subjects', () => {
      const mockSubjects = [
        { id: 1, name: 'Mathematics' },
        { id: 2, name: 'History' },
      ];
      const state = reducer(initialState, { type: fetchSubjects.fulfilled.type, payload: mockSubjects });
      expect(state.subjects).toEqual(mockSubjects);
      expect(state.selectedSubjectId).toBe(1);
    });

    it('fetchKnowledgeGraph.fulfilled sets active graph', () => {
      const mockGraph: IKnowledgeGraph = {
        subjectId: 1,
        nodes: [{ id: 1, title: 'Algebra' }],
        edges: [],
      };
      const state = reducer(initialState, { type: fetchKnowledgeGraph.fulfilled.type, payload: mockGraph });
      expect(state.loading).toBe(false);
      expect(state.graph).toEqual(mockGraph);
    });

    it('saveKnowledgeGraph.rejected preserves candidate draft and extracts cycle info', () => {
      const candidateDraft: IKnowledgeGraph = {
        subjectId: 1,
        nodes: [
          { id: -1, title: 'Node A' },
          { id: -2, title: 'Node B' },
        ],
        edges: [
          { fromNodeId: -1, toNodeId: -2, relationType: 'PREREQUISITE' },
          { fromNodeId: -2, toNodeId: -1, relationType: 'PREREQUISITE' },
        ],
      };

      const currentState: KnowledgeGraphState = {
        ...initialState,
        saving: true,
        graph: candidateDraft,
      };

      const errorPayload = {
        message: 'Circular prerequisite dependency detected: Node A -> Node B -> Node A',
        cyclePath: 'Node A -> Node B -> Node A',
        cycleNodeIds: [-1, -2, -1],
      };

      const state = reducer(currentState, {
        type: saveKnowledgeGraph.rejected.type,
        payload: errorPayload,
      });

      expect(state.saving).toBe(false);
      expect(state.saveSuccess).toBe(false);
      expect(state.errorMessage).toBe('Circular prerequisite dependency detected: Node A -> Node B -> Node A');
      expect(state.cyclePath).toBe('Node A -> Node B -> Node A');
      expect(state.cycleNodeIds).toEqual([-1, -2, -1]);

      // CRITICAL: Draft is preserved!
      expect(state.graph).toEqual(candidateDraft);
      expect(state.graph?.nodes).toHaveLength(2);
      expect(state.graph?.edges).toHaveLength(2);
    });

    it('saveKnowledgeGraph.fulfilled reconciles graph with persisted stable IDs', () => {
      const serverResponse: IKnowledgeGraph = {
        subjectId: 1,
        nodes: [
          { id: 101, title: 'Node A' },
          { id: 102, title: 'Node B' },
        ],
        edges: [{ id: 201, fromNodeId: 101, toNodeId: 102, relationType: 'PREREQUISITE' }],
      };

      const currentState: KnowledgeGraphState = {
        ...initialState,
        saving: true,
        errorMessage: 'Previous error',
      };

      const state = reducer(currentState, {
        type: saveKnowledgeGraph.fulfilled.type,
        payload: serverResponse,
      });

      expect(state.saving).toBe(false);
      expect(state.saveSuccess).toBe(true);
      expect(state.errorMessage).toBeNull();
      expect(state.cyclePath).toBeNull();
      expect(state.graph?.nodes[0].id).toBe(101);
      expect(state.graph?.edges[0].id).toBe(201);
    });
  });
});
