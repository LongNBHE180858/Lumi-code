import React, { useEffect, useMemo, useState } from 'react';
import { AlertTriangle, ArrowRight, CheckCircle2, GitFork, Link as LinkIcon, Plus, RefreshCw, Save, Trash2, X } from 'lucide-react';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import AdminLayout from '../layout/admin-layout';
import {
  addEdge,
  addNode,
  clearNotification,
  deleteEdge,
  deleteNode,
  fetchKnowledgeGraph,
  fetchSubjects,
  saveKnowledgeGraph,
  setSelectedSubjectId,
} from './knowledge-graph.reducer';
import type { IKnowledgeGraphNode } from 'app/shared/model/knowledge-graph.model';
import './knowledge-graph.scss';

export const KnowledgeGraphAdmin = () => {
  const dispatch = useAppDispatch();

  const { subjects, selectedSubjectId, graph, loading, saving, saveSuccess, errorMessage, cyclePath, cycleNodeIds } = useAppSelector(
    state => state.knowledgeGraph,
  );

  // Local form states
  const [newNodeTitle, setNewNodeTitle] = useState('');
  const [newNodeType, setNewNodeType] = useState('CONCEPT');
  const [fromNodeId, setFromNodeId] = useState<number | ''>('');
  const [toNodeId, setToNodeId] = useState<number | ''>('');
  const [selectedNodeId, setSelectedNodeId] = useState<number | null>(null);

  // Initial fetch of supported subjects
  useEffect(() => {
    dispatch(fetchSubjects());
  }, [dispatch]);

  // When selected subject changes, load graph
  useEffect(() => {
    if (selectedSubjectId) {
      dispatch(fetchKnowledgeGraph(selectedSubjectId));
    }
  }, [dispatch, selectedSubjectId]);

  const handleSelectSubject = (id: number) => {
    if (id !== selectedSubjectId) {
      dispatch(setSelectedSubjectId(id));
      setSelectedNodeId(null);
    }
  };

  const handleAddNode = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newNodeTitle.trim()) return;
    dispatch(addNode({ title: newNodeTitle, type: newNodeType }));
    setNewNodeTitle('');
  };

  const handleAddEdge = (e: React.FormEvent) => {
    e.preventDefault();
    if (fromNodeId === '' || toNodeId === '' || fromNodeId === toNodeId) return;
    dispatch(addEdge({ fromNodeId: Number(fromNodeId), toNodeId: Number(toNodeId) }));
    setFromNodeId('');
    setToNodeId('');
  };

  const handleSave = () => {
    if (!selectedSubjectId || !graph) return;
    dispatch(saveKnowledgeGraph({ subjectId: selectedSubjectId, candidateGraph: graph }));
  };

  const handleReload = () => {
    if (selectedSubjectId) {
      dispatch(fetchKnowledgeGraph(selectedSubjectId));
    }
  };

  // Node position calculation for visual DAG layout
  const nodesWithPositions = useMemo(() => {
    if (!graph || !graph.nodes || graph.nodes.length === 0) return [];

    // Calculate in-degrees
    const inDegree: Record<number, number> = {};
    graph.nodes.forEach(n => {
      inDegree[n.id] = 0;
    });
    graph.edges.forEach(e => {
      if (inDegree[e.toNodeId] !== undefined) {
        inDegree[e.toNodeId] += 1;
      }
    });

    // Topological levels
    const levels: Record<number, number> = {};
    const assignLevels = (nodeId: number, level: number, visited: Set<number>) => {
      if (visited.has(nodeId)) return;
      visited.add(nodeId);
      levels[nodeId] = Math.max(levels[nodeId] || 0, level);
      graph.edges.filter(e => e.fromNodeId === nodeId).forEach(e => assignLevels(e.toNodeId, level + 1, new Set(visited)));
    };

    graph.nodes.forEach(n => {
      if ((inDegree[n.id] || 0) === 0) {
        assignLevels(n.id, 0, new Set());
      }
    });

    // Fallback for remaining nodes
    graph.nodes.forEach(n => {
      if (levels[n.id] === undefined) {
        levels[n.id] = 0;
      }
    });

    // Group nodes by level
    const levelGroups: Record<number, IKnowledgeGraphNode[]> = {};
    graph.nodes.forEach(n => {
      const lvl = levels[n.id] || 0;
      if (!levelGroups[lvl]) levelGroups[lvl] = [];
      levelGroups[lvl].push(n);
    });

    // Calculate (x, y) coordinates
    const positioned: (IKnowledgeGraphNode & { x: number; y: number })[] = [];
    Object.keys(levelGroups).forEach(lvlStr => {
      const lvl = Number(lvlStr);
      const group = levelGroups[lvl];
      const colX = 40 + lvl * 250;
      group.forEach((node, idx) => {
        const rowY = 40 + idx * 110;
        positioned.push({ ...node, x: colX, y: rowY });
      });
    });

    return positioned;
  }, [graph]);

  const nodePosMap = useMemo(() => {
    const map = new Map<number, { x: number; y: number; title: string }>();
    nodesWithPositions.forEach(n => {
      map.set(n.id, { x: n.x, y: n.y, title: n.title });
    });
    return map;
  }, [nodesWithPositions]);

  // Identify edges in cycle
  const isEdgeInCycle = (fromId: number, toId: number) => {
    if (!cycleNodeIds || cycleNodeIds.length === 0) return false;
    for (let i = 0; i < cycleNodeIds.length - 1; i++) {
      if (cycleNodeIds[i] === fromId && cycleNodeIds[i + 1] === toId) {
        return true;
      }
    }
    return false;
  };

  return (
    <AdminLayout>
      <main className="admin-page-content">
        <div className="kg-admin-container">
          {/* Header */}
          <div className="kg-header">
            <div className="kg-title-area">
              <h1>
                <GitFork size={24} color="#24a8e8" />
                Quản lý Cây tri thức (Knowledge Graph)
              </h1>
              <p>Xây dựng cây khái niệm và quan hệ tiên quyết (Prerequisite: Nút A → Nút B) cho từng môn học</p>
            </div>
            <div className="kg-actions-bar">
              <button
                className="kg-header-btn kg-btn-secondary"
                type="button"
                onClick={handleReload}
                disabled={loading || saving}
                title="Tải lại từ server"
                id="btn-reload-graph"
              >
                <RefreshCw size={16} className={`btn-icon ${loading ? 'spin' : ''}`} aria-hidden="true" />
                <span>Làm mới</span>
              </button>
              <button
                className="kg-header-btn kg-btn-primary"
                type="button"
                onClick={handleSave}
                disabled={loading || saving || !graph}
                id="btn-save-graph"
                title="Lưu toàn bộ cây kiến thức"
              >
                {saving ? (
                  <>
                    <RefreshCw size={16} className="btn-icon spin" aria-hidden="true" />
                    <span>Đang lưu...</span>
                  </>
                ) : (
                  <>
                    <Save size={16} className="btn-icon" aria-hidden="true" />
                    <span>Lưu cây kiến thức</span>
                  </>
                )}
              </button>
            </div>
          </div>

          {/* Error Alert with Cycle Path */}
          {errorMessage && (
            <div className="kg-alert kg-alert-error" role="alert" id="kg-error-alert">
              <AlertTriangle size={20} className="alert-icon" />
              <div className="alert-content">
                <strong>Lỗi xác thực cây kiến thức:</strong>
                <div>{errorMessage}</div>
                {cyclePath && (
                  <div className="cycle-badge">
                    Vòng lặp phát hiện: <span>{cyclePath}</span>
                  </div>
                )}
                <div style={{ marginTop: '6px', fontSize: '12px' }}>
                  <em>Bản thảo của bạn đã được giữ nguyên. Vui lòng xóa hoặc sửa quan hệ tạo vòng lặp để tiếp tục lưu.</em>
                </div>
              </div>
              <button className="close-btn" type="button" onClick={() => dispatch(clearNotification())}>
                <X size={18} />
              </button>
            </div>
          )}

          {/* Success Alert */}
          {saveSuccess && (
            <div className="kg-alert kg-alert-success" role="status" id="kg-success-alert">
              <CheckCircle2 size={20} className="alert-icon" />
              <div className="alert-content">
                <strong>Lưu thành công!</strong>
                <div>Toàn bộ cây kiến thức môn học đã được xác thực và cập nhật an toàn vào hệ thống.</div>
              </div>
              <button className="close-btn" type="button" onClick={() => dispatch(clearNotification())}>
                <X size={18} />
              </button>
            </div>
          )}

          {/* Subject Picker */}
          <div className="kg-subject-selector-card">
            <span className="selector-label">Môn học đang quản lý:</span>
            <div className="subject-pill-group">
              {subjects.map(sub => (
                <button
                  key={sub.id}
                  type="button"
                  className={`subject-pill ${selectedSubjectId === sub.id ? 'active' : ''}`}
                  onClick={() => handleSelectSubject(sub.id)}
                  id={`subject-pill-${sub.id}`}
                >
                  <span>{sub.name}</span>
                </button>
              ))}
            </div>
          </div>

          {/* Main Workspace Layout */}
          <div className="kg-main-layout">
            {/* Visual Canvas Card */}
            <div className="kg-canvas-card">
              <div className="canvas-toolbar">
                <span className="canvas-title">Sơ đồ quan hệ tiên quyết (Prerequisite Graph)</span>
                <div className="canvas-meta">
                  <span className="meta-pill">Nút: {graph?.nodes.length || 0}</span>
                  <span className="meta-pill">Cạnh tiên quyết: {graph?.edges.length || 0}</span>
                </div>
              </div>

              <div className="canvas-viewport" id="kg-canvas-viewport">
                {/* SVG Layer for Directed Arrows */}
                <svg className="kg-svg-layer">
                  <defs>
                    <marker id="arrowhead" markerWidth="10" markerHeight="8" refX="9" refY="4" orient="auto">
                      <polygon points="0 0, 10 4, 0 8" fill="#94a3b8" />
                    </marker>
                    <marker id="arrowhead-cycle" markerWidth="10" markerHeight="8" refX="9" refY="4" orient="auto">
                      <polygon points="0 0, 10 4, 0 8" fill="#ef4444" />
                    </marker>
                  </defs>

                  {graph?.edges.map((edge, index) => {
                    const fromPos = nodePosMap.get(edge.fromNodeId);
                    const toPos = nodePosMap.get(edge.toNodeId);
                    if (!fromPos || !toPos) return null;

                    const inCycle = isEdgeInCycle(edge.fromNodeId, edge.toNodeId);
                    const startX = fromPos.x + 190;
                    const startY = fromPos.y + 35;
                    const endX = toPos.x;
                    const endY = toPos.y + 35;

                    return (
                      <line
                        key={`edge-${edge.fromNodeId}-${edge.toNodeId}-${index}`}
                        x1={startX}
                        y1={startY}
                        x2={endX}
                        y2={endY}
                        className={`edge-line ${inCycle ? 'edge-cycle' : ''}`}
                      />
                    );
                  })}
                </svg>

                {/* Concept Nodes */}
                {nodesWithPositions.length === 0 ? (
                  <div className="canvas-empty">
                    <GitFork size={36} opacity={0.4} />
                    <p>Chưa có nút kiến thức nào cho môn học này. Hãy thêm nút ở bảng bên phải để bắt đầu thiết kế cây kiến thức.</p>
                  </div>
                ) : (
                  nodesWithPositions.map(node => {
                    const inCycle = cycleNodeIds?.includes(node.id);
                    const isSelected = selectedNodeId === node.id;

                    return (
                      <div
                        key={node.id}
                        className={`node-card ${isSelected ? 'selected' : ''} ${inCycle ? 'cycle-highlight' : ''}`}
                        style={{ left: `${node.x}px`, top: `${node.y}px` }}
                        onClick={() => setSelectedNodeId(node.id)}
                        id={`node-card-${node.id}`}
                      >
                        <span className="node-type-badge">{node.type || 'CONCEPT'}</span>
                        <h4 className="node-title" title={node.title}>
                          {node.title}
                        </h4>
                        <div className="node-actions">
                          <button
                            type="button"
                            onClick={e => {
                              e.stopPropagation();
                              dispatch(deleteNode(node.id));
                            }}
                            title="Xóa nút này"
                          >
                            <Trash2 size={12} />
                          </button>
                        </div>
                      </div>
                    );
                  })
                )}
              </div>
            </div>

            {/* Sidebar Controls Panel */}
            <div className="kg-sidebar-panel">
              {/* Add Node Form */}
              <div className="panel-card">
                <h3>
                  <Plus size={16} />
                  Thêm nút kiến thức
                </h3>
                <form onSubmit={handleAddNode}>
                  <div className="form-group">
                    <label htmlFor="node-title-input">Tên khái niệm / bài học</label>
                    <input
                      id="node-title-input"
                      type="text"
                      placeholder="Ví dụ: Phân số, Định lý Pytago..."
                      value={newNodeTitle}
                      onChange={e => setNewNodeTitle(e.target.value)}
                      maxLength={255}
                    />
                  </div>
                  <div className="form-group">
                    <label htmlFor="node-type-select">Loại nút</label>
                    <select id="node-type-select" value={newNodeType} onChange={e => setNewNodeType(e.target.value)}>
                      <option value="CONCEPT">Khái niệm (Concept)</option>
                      <option value="TOPIC">Chủ đề (Topic)</option>
                      <option value="SKILL">Kỹ năng (Skill)</option>
                    </select>
                  </div>
                  <button type="submit" className="panel-btn btn-primary" id="btn-add-node">
                    <Plus size={14} />
                    Thêm vào bản thảo
                  </button>
                </form>
              </div>

              {/* Add Prerequisite Edge Form */}
              <div className="panel-card">
                <h3>
                  <LinkIcon size={16} />
                  Tạo quan hệ tiên quyết
                </h3>
                <form onSubmit={handleAddEdge}>
                  <div className="form-group">
                    <label htmlFor="from-node-select">Nút tiên quyết (Học trước):</label>
                    <select
                      id="from-node-select"
                      value={fromNodeId}
                      onChange={e => setFromNodeId(e.target.value === '' ? '' : Number(e.target.value))}
                    >
                      <option value="">-- Chọn nút học trước --</option>
                      {graph?.nodes.map(n => (
                        <option key={n.id} value={n.id}>
                          {n.title}
                        </option>
                      ))}
                    </select>
                  </div>
                  <div className="form-group">
                    <label htmlFor="to-node-select">Nút phụ thuộc (Học sau):</label>
                    <select
                      id="to-node-select"
                      value={toNodeId}
                      onChange={e => setToNodeId(e.target.value === '' ? '' : Number(e.target.value))}
                    >
                      <option value="">-- Chọn nút học sau --</option>
                      {graph?.nodes.map(n => (
                        <option key={n.id} value={n.id} disabled={n.id === fromNodeId}>
                          {n.title}
                        </option>
                      ))}
                    </select>
                  </div>
                  <button
                    type="submit"
                    className="panel-btn btn-primary"
                    disabled={fromNodeId === '' || toNodeId === '' || fromNodeId === toNodeId}
                    id="btn-add-edge"
                  >
                    <ArrowRight size={14} />
                    Nối quan hệ tiên quyết
                  </button>
                </form>
              </div>

              {/* Prerequisite Edges List */}
              <div className="panel-card">
                <h3>Danh sách quan hệ ({graph?.edges.length || 0})</h3>
                <div className="edge-list" id="kg-edge-list">
                  {graph?.edges.length === 0 ? (
                    <div style={{ color: '#7894af', fontSize: '12px', textAlign: 'center', padding: '10px 0' }}>
                      Chưa có quan hệ tiên quyết nào.
                    </div>
                  ) : (
                    graph?.edges.map((edge, idx) => {
                      const fromNode = nodePosMap.get(edge.fromNodeId);
                      const toNode = nodePosMap.get(edge.toNodeId);
                      const inCycle = isEdgeInCycle(edge.fromNodeId, edge.toNodeId);

                      return (
                        <div
                          key={`edge-item-${edge.fromNodeId}-${edge.toNodeId}-${idx}`}
                          className={`edge-item ${inCycle ? 'cycle-edge' : ''}`}
                        >
                          <div className="edge-nodes">
                            <span>{fromNode?.title || `Node #${edge.fromNodeId}`}</span>
                            <ArrowRight size={12} />
                            <span>{toNode?.title || `Node #${edge.toNodeId}`}</span>
                          </div>
                          <button
                            type="button"
                            className="delete-edge-btn"
                            title="Xóa quan hệ này"
                            onClick={() => dispatch(deleteEdge({ fromNodeId: edge.fromNodeId, toNodeId: edge.toNodeId }))}
                          >
                            <Trash2 size={13} />
                          </button>
                        </div>
                      );
                    })
                  )}
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </AdminLayout>
  );
};

export default KnowledgeGraphAdmin;
