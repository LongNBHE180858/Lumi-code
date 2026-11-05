import { createAsyncThunk, createSlice, type PayloadAction } from '@reduxjs/toolkit';
import axios from 'axios';
import type { IKnowledgeGraph, IKnowledgeGraphNode, IKnowledgeGraphSubject } from 'app/shared/model/knowledge-graph.model';

export interface KnowledgeGraphState {
  subjects: IKnowledgeGraphSubject[];
  selectedSubjectId: number | null;
  graph: IKnowledgeGraph | null;
  loading: boolean;
  saving: boolean;
  saveSuccess: boolean;
  errorMessage: string | null;
  cyclePath: string | null;
  cycleNodeIds: number[];
}

const initialState: KnowledgeGraphState = {
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

export const fetchSubjects = createAsyncThunk('knowledgeGraph/fetchSubjects', async () => {
  const response = await axios.get<IKnowledgeGraphSubject[]>('/api/admin/knowledge-graph/subjects');
  return response.data;
});

export const fetchKnowledgeGraph = createAsyncThunk('knowledgeGraph/fetchGraph', async (subjectId: number) => {
  const response = await axios.get<IKnowledgeGraph>(`/api/admin/knowledge-graph/${subjectId}`);
  return response.data;
});

export const saveKnowledgeGraph = createAsyncThunk(
  'knowledgeGraph/saveGraph',
  async ({ subjectId, candidateGraph }: { subjectId: number; candidateGraph: IKnowledgeGraph }, { rejectWithValue }) => {
    try {
      const response = await axios.put<IKnowledgeGraph>(`/api/admin/knowledge-graph/${subjectId}`, candidateGraph);
      return response.data;
    } catch (err: any) {
      return rejectWithValue(err.response?.data || { message: err.message || 'Lỗi khi lưu cây kiến thức' });
    }
  },
);

export const KnowledgeGraphSlice = createSlice({
  name: 'knowledgeGraph',
  initialState,
  reducers: {
    setSelectedSubjectId(state, action: PayloadAction<number>) {
      state.selectedSubjectId = action.payload;
      state.saveSuccess = false;
      state.errorMessage = null;
      state.cyclePath = null;
      state.cycleNodeIds = [];
    },
    addNode(state, action: PayloadAction<{ title: string; type?: string }>) {
      if (!state.graph) return;
      // Assign temporary negative ID for new node
      const minId = state.graph.nodes.reduce((min, n) => (n.id < min ? n.id : min), 0);
      const nextTempId = minId <= 0 ? minId - 1 : -1;
      const newNode: IKnowledgeGraphNode = {
        id: nextTempId,
        title: action.payload.title.trim(),
        type: action.payload.type || 'CONCEPT',
        subjectId: state.selectedSubjectId || undefined,
      };
      state.graph.nodes.push(newNode);
      state.saveSuccess = false;
      state.errorMessage = null;
      state.cyclePath = null;
      state.cycleNodeIds = [];
    },
    updateNode(state, action: PayloadAction<{ id: number; title: string; type?: string }>) {
      if (!state.graph) return;
      const node = state.graph.nodes.find(n => n.id === action.payload.id);
      if (node) {
        node.title = action.payload.title.trim();
        if (action.payload.type !== undefined) {
          node.type = action.payload.type;
        }
      }
      state.saveSuccess = false;
      state.errorMessage = null;
    },
    deleteNode(state, action: PayloadAction<number>) {
      if (!state.graph) return;
      const targetId = action.payload;
      state.graph.nodes = state.graph.nodes.filter(n => n.id !== targetId);
      // Clean up connected edges
      state.graph.edges = state.graph.edges.filter(e => e.fromNodeId !== targetId && e.toNodeId !== targetId);
      state.saveSuccess = false;
      state.errorMessage = null;
      state.cyclePath = null;
      state.cycleNodeIds = [];
    },
    addEdge(state, action: PayloadAction<{ fromNodeId: number; toNodeId: number }>) {
      if (!state.graph) return;
      const { fromNodeId, toNodeId } = action.payload;
      if (fromNodeId === toNodeId) return;

      const exists = state.graph.edges.some(e => e.fromNodeId === fromNodeId && e.toNodeId === toNodeId);
      if (!exists) {
        state.graph.edges.push({
          id: null,
          fromNodeId,
          toNodeId,
          relationType: 'PREREQUISITE',
        });
      }
      state.saveSuccess = false;
      state.errorMessage = null;
      state.cyclePath = null;
      state.cycleNodeIds = [];
    },
    deleteEdge(state, action: PayloadAction<{ fromNodeId: number; toNodeId: number }>) {
      if (!state.graph) return;
      const { fromNodeId, toNodeId } = action.payload;
      state.graph.edges = state.graph.edges.filter(e => !(e.fromNodeId === fromNodeId && e.toNodeId === toNodeId));
      state.saveSuccess = false;
      state.errorMessage = null;
      state.cyclePath = null;
      state.cycleNodeIds = [];
    },
    clearNotification(state) {
      state.saveSuccess = false;
      state.errorMessage = null;
      state.cyclePath = null;
      state.cycleNodeIds = [];
    },
  },
  extraReducers(builder) {
    builder
      .addCase(fetchSubjects.fulfilled, (state, action) => {
        state.subjects = action.payload;
        if (action.payload.length > 0 && !state.selectedSubjectId) {
          state.selectedSubjectId = action.payload[0].id;
        }
      })
      .addCase(fetchKnowledgeGraph.pending, state => {
        state.loading = true;
        state.errorMessage = null;
        state.cyclePath = null;
        state.cycleNodeIds = [];
      })
      .addCase(fetchKnowledgeGraph.fulfilled, (state, action) => {
        state.loading = false;
        state.graph = action.payload;
      })
      .addCase(fetchKnowledgeGraph.rejected, (state, action) => {
        state.loading = false;
        state.errorMessage = (action.error.message as string) || 'Không thể tải cây kiến thức.';
      })
      .addCase(saveKnowledgeGraph.pending, state => {
        state.saving = true;
        state.saveSuccess = false;
        state.errorMessage = null;
        state.cyclePath = null;
        state.cycleNodeIds = [];
      })
      .addCase(saveKnowledgeGraph.fulfilled, (state, action) => {
        state.saving = false;
        state.saveSuccess = true;
        state.errorMessage = null;
        state.cyclePath = null;
        state.cycleNodeIds = [];
        state.graph = action.payload; // Reconcile graph with server-saved stable IDs
      })
      .addCase(saveKnowledgeGraph.rejected, (state, action: any) => {
        state.saving = false;
        state.saveSuccess = false;
        const payload = action.payload;
        state.errorMessage = payload?.message || 'Lỗi khi lưu cây kiến thức.';
        state.cyclePath = payload?.cyclePath || null;
        state.cycleNodeIds = Array.isArray(payload?.cycleNodeIds) ? payload.cycleNodeIds : [];
        // Note: state.graph is intentionally NOT overwritten, preserving candidate draft for editing
      });
  },
});

export const { setSelectedSubjectId, addNode, updateNode, deleteNode, addEdge, deleteEdge, clearNotification } =
  KnowledgeGraphSlice.actions;

export default KnowledgeGraphSlice.reducer;
