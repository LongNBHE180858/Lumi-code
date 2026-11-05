import React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { configureStore } from '@reduxjs/toolkit';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import axios from 'axios';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import knowledgeGraphReducer from './knowledge-graph.reducer';
import KnowledgeGraphAdmin from './knowledge-graph';

describe('KnowledgeGraphAdmin Component', () => {
  const mockSubjects = [
    { id: 1, name: 'Mathematics', description: 'Toán học' },
    { id: 2, name: 'History', description: 'Lịch sử' },
  ];

  const mockGraphMath = {
    subjectId: 1,
    subjectName: 'Mathematics',
    nodes: [
      { id: 10, title: 'Đại số', type: 'CONCEPT' },
      { id: 20, title: 'Hình học', type: 'CONCEPT' },
    ],
    edges: [{ id: 100, fromNodeId: 10, toNodeId: 20, relationType: 'PREREQUISITE' }],
  };

  const createTestStore = (customState?: any) =>
    configureStore({
      reducer: {
        knowledgeGraph: knowledgeGraphReducer,
      },
      preloadedState: {
        knowledgeGraph: {
          subjects: mockSubjects,
          selectedSubjectId: 1,
          graph: mockGraphMath,
          loading: false,
          saving: false,
          saveSuccess: false,
          errorMessage: null,
          cyclePath: null,
          cycleNodeIds: [],
          ...customState,
        },
      },
    });

  const renderComponent = (store = createTestStore()) =>
    render(
      <Provider store={store}>
        <MemoryRouter>
          <KnowledgeGraphAdmin />
        </MemoryRouter>
      </Provider>,
    );

  beforeEach(() => {
    vi.restoreAllMocks();
    vi.spyOn(axios, 'get').mockImplementation((url: string) => {
      if (url.includes('/api/admin/knowledge-graph/subjects')) {
        return Promise.resolve({ data: mockSubjects });
      }
      if (url.includes('/api/admin/knowledge-graph/1')) {
        return Promise.resolve({ data: mockGraphMath });
      }
      return Promise.reject(new Error('not found'));
    });
    vi.spyOn(axios, 'put').mockResolvedValue({ data: mockGraphMath });
  });

  it('renders Knowledge Graph administration title and controls', () => {
    renderComponent();

    expect(screen.getByText(/Quản lý Cây tri thức/i)).toBeTruthy();
    expect(screen.getByText(/Lưu cây kiến thức/i)).toBeTruthy();
    expect(screen.getByText(/Thêm nút kiến thức/i)).toBeTruthy();
    expect(screen.getByText(/Tạo quan hệ tiên quyết/i)).toBeTruthy();
    expect(screen.getByText('Mathematics')).toBeTruthy();
    expect(screen.getByText('History')).toBeTruthy();
  });

  it('loads and renders nodes and prerequisite edges for selected subject', () => {
    renderComponent();

    expect(screen.getAllByText('Đại số').length).toBeGreaterThan(0);
    expect(screen.getAllByText('Hình học').length).toBeGreaterThan(0);
  });

  it('allows adding a new concept node to the candidate draft', async () => {
    renderComponent();

    const titleInput = screen.getByLabelText(/Tên khái niệm/i);
    const addButton = screen.getByRole('button', { name: /Thêm vào bản thảo/i });

    fireEvent.change(titleInput, { target: { value: 'Giải tích' } });
    fireEvent.click(addButton);

    await waitFor(() => {
      expect(screen.getAllByText('Giải tích').length).toBeGreaterThan(0);
    });
  });

  it('displays cycle error banner with cycle path and preserves draft on save rejection', async () => {
    vi.spyOn(axios, 'put').mockRejectedValueOnce({
      response: {
        data: {
          message: 'Circular prerequisite dependency detected: Đại số -> Hình học -> Đại số',
          cyclePath: 'Đại số -> Hình học -> Đại số',
          cycleNodeIds: [10, 20, 10],
        },
      },
    });

    renderComponent();

    expect(screen.getAllByText('Đại số').length).toBeGreaterThan(0);

    const saveButton = screen.getByRole('button', { name: /Lưu cây kiến thức/i });
    await waitFor(() => {
      expect(saveButton.hasAttribute('disabled')).toBe(false);
    });
    fireEvent.click(saveButton);

    await waitFor(() => {
      expect(screen.getByText(/Lỗi xác thực cây kiến thức:/i)).toBeTruthy();
      expect(screen.getByText('Đại số -> Hình học -> Đại số')).toBeTruthy();
    });

    // Verify candidate draft nodes are still present on screen
    expect(screen.getAllByText('Đại số').length).toBeGreaterThan(0);
    expect(screen.getAllByText('Hình học').length).toBeGreaterThan(0);
  });

  it('displays success alert only after API confirms successful persistence', async () => {
    vi.spyOn(axios, 'put').mockResolvedValueOnce({
      data: {
        subjectId: 1,
        subjectName: 'Mathematics',
        nodes: [{ id: 10, title: 'Đại số', type: 'CONCEPT' }],
        edges: [],
      },
    });

    renderComponent();

    const saveButton = screen.getByRole('button', { name: /Lưu cây kiến thức/i });
    await waitFor(() => {
      expect(saveButton.hasAttribute('disabled')).toBe(false);
    });
    fireEvent.click(saveButton);

    await waitFor(() => {
      expect(screen.getByText(/Lưu thành công!/i)).toBeTruthy();
    });
  });

  it('switches between Mathematics and History subjects and loads respective graph', async () => {
    const mockGraphHistory = {
      subjectId: 2,
      subjectName: 'History',
      nodes: [{ id: 30, title: 'Lịch sử Cổ đại', type: 'CONCEPT' }],
      edges: [],
    };

    vi.spyOn(axios, 'get').mockImplementation((url: string) => {
      if (url.includes('/api/admin/knowledge-graph/subjects')) {
        return Promise.resolve({ data: mockSubjects });
      }
      if (url.includes('/api/admin/knowledge-graph/1')) {
        return Promise.resolve({ data: mockGraphMath });
      }
      if (url.includes('/api/admin/knowledge-graph/2')) {
        return Promise.resolve({ data: mockGraphHistory });
      }
      return Promise.reject(new Error('not found'));
    });

    renderComponent();

    const historyPill = screen.getByRole('button', { name: /History/i });
    fireEvent.click(historyPill);

    await waitFor(() => {
      expect(screen.getAllByText('Lịch sử Cổ đại').length).toBeGreaterThan(0);
    });
  });

  it('displays empty state message when graph has no nodes', () => {
    const emptyStore = createTestStore({
      graph: {
        subjectId: 1,
        subjectName: 'Mathematics',
        nodes: [],
        edges: [],
      },
    });

    renderComponent(emptyStore);

    expect(screen.getByText(/Chưa có nút kiến thức nào cho môn học này/i)).toBeTruthy();
  });

  it('prevents adding self-referencing prerequisite edge in the form', () => {
    renderComponent();

    const fromSelect = screen.getByLabelText(/Nút tiên quyết \(Học trước\):/i);
    const toSelect = screen.getByLabelText(/Nút phụ thuộc \(Học sau\):/i);
    const addEdgeButton = screen.getByRole('button', { name: /Nối quan hệ tiên quyết/i });

    // When both are empty or unselected, button is disabled
    expect(addEdgeButton.hasAttribute('disabled')).toBe(true);

    // Select node 10 for both from and to
    fireEvent.change(fromSelect, { target: { value: '10' } });
    fireEvent.change(toSelect, { target: { value: '10' } });

    // Button must remain disabled when fromNode === toNode
    expect(addEdgeButton.hasAttribute('disabled')).toBe(true);
  });

  it('clicking reload button fetches graph from server', async () => {
    const getSpy = vi.spyOn(axios, 'get');
    renderComponent();

    const reloadButton = screen.getByRole('button', { name: /Làm mới/i });
    fireEvent.click(reloadButton);

    await waitFor(() => {
      expect(getSpy).toHaveBeenCalledWith(expect.stringContaining('/api/admin/knowledge-graph/1'));
    });
  });
});
