import React, { useState, useMemo } from 'react';
// App layout already has Header
// App layout already has Footer
import JoinClassModal from '../components/JoinClassModal';
import SubmitTaskModal from '../components/SubmitTaskModal';
import type { AssignmentItem, ClassItem } from '../types';

export interface StudentAssignmentsPageProps {
  assignments: AssignmentItem[];
  allClasses: ClassItem[];
  onNavigateClasses?: () => void;
  onNavigateHome?: () => void;
  onNavigateNav?: (id: string) => void;
  onRequestJoinClass: (classItem: ClassItem, password?: string) => void;
  onSubmitTask: (taskId: string, fileName: string, comment: string) => void;
}

export type AssignmentStatusFilter = 'all' | 'need_action' | 'submitted' | 'graded';

export const StudentAssignmentsPage: React.FC<StudentAssignmentsPageProps> = ({
  assignments,
  allClasses,
  onNavigateClasses,
  onNavigateHome,
  onNavigateNav,
  onRequestJoinClass,
  onSubmitTask,
}) => {
  const [activeFilterTab, setActiveFilterTab] = useState<AssignmentStatusFilter>('all');
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedClassFilter, setSelectedClassFilter] = useState('all');
  const [sortOrder, setSortOrder] = useState('newest');

  // Modals
  const [isJoinModalOpen, setIsJoinModalOpen] = useState(false);
  const [selectedTask, setSelectedTask] = useState<AssignmentItem | null>(null);

  // Student navbar items (Figma 565:227)
  const studentNavItems = [
    { id: 'home', label: 'Trang chủ' },
    { id: 'classes', label: 'Lớp học' },
    { id: 'assignments', label: 'Việc được giao' },
    { id: 'results', label: 'Kết quả' },
    { id: 'communication', label: 'Giao tiếp' },
    { id: 'profile', label: 'Hồ sơ' },
  ];

  // Statistics
  const stats = useMemo(() => {
    const needAction = assignments.filter(a => a.status === 'not_started' || a.status === 'in_progress' || a.status === 'overdue').length;
    const submitted = assignments.filter(a => a.status === 'submitted').length;
    const graded = assignments.filter(a => a.status === 'graded').length;
    return {
      all: assignments.length,
      needAction,
      submitted,
      graded,
    };
  }, [assignments]);

  // Filtered List
  const filteredAssignments = useMemo(() => {
    return assignments.filter(a => {
      // Status filter
      if (activeFilterTab === 'need_action') {
        if (a.status !== 'not_started' && a.status !== 'in_progress' && a.status !== 'overdue') {
          return false;
        }
      } else if (activeFilterTab === 'submitted') {
        if (a.status !== 'submitted') return false;
      } else if (activeFilterTab === 'graded') {
        if (a.status !== 'graded') return false;
      }

      // Search filter
      if (searchTerm.trim() && !a.title.toLowerCase().includes(searchTerm.toLowerCase())) {
        return false;
      }

      // Class filter
      if (selectedClassFilter !== 'all' && a.classId !== selectedClassFilter) {
        return false;
      }

      return true;
    });
  }, [assignments, activeFilterTab, searchTerm, selectedClassFilter]);

  // Distinct classes for dropdown
  const classOptions = useMemo(() => {
    const map = new Map<string, string>();
    assignments.forEach(a => map.set(a.classId, a.className));
    return Array.from(map.entries());
  }, [assignments]);

  return (
    <div className="class-management-page-wrapper">
      {/* Student Navigation Bar */}
      {/* Global Header used */}

      <main className="class-management-main">
        <div className="class-page-container">
          {/* Page Top Heading & Action Button */}
          <div className="page-header-row">
            <div>
              <div className="badge-screen-id">Học sinh · Figma Node 565:227</div>
              <h1 className="class-page-title">Việc được giao</h1>
              <p className="class-page-desc">Theo dõi công việc giáo viên giao cho lớp và hoàn thành đúng hạn.</p>
            </div>

            <button type="button" className="btn-pill-primary btn-join-class-action" onClick={() => setIsJoinModalOpen(true)}>
              <span>+ Tham gia lớp bằng mã</span>
            </button>
          </div>

          {/* 3 Stat Cards (Figma 565:227) */}
          <div className="class-stats-grid">
            {/* Card 1: Cần hoàn thành */}
            <div
              className={`class-stat-card class-stat-card-actionable ${activeFilterTab === 'need_action' ? 'stat-active-selected' : ''}`}
              onClick={() => setActiveFilterTab('need_action')}
            >
              <div className="stat-card-icon-wrapper stat-icon-primary">
                <svg
                  width="24"
                  height="24"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth="2.2"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                >
                  <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
                  <polyline points="14 2 14 8 20 8" />
                  <line x1="16" y1="13" x2="8" y2="13" />
                  <line x1="16" y1="17" x2="8" y2="17" />
                </svg>
              </div>
              <div className="stat-card-content">
                <span className="stat-card-label">Cần hoàn thành</span>
                <div className="stat-card-number-row">
                  <span className="stat-card-value">{stats.needAction}</span>
                  <span className="stat-card-badge stat-badge-blue">3 trong hạn · 1 quá hạn</span>
                </div>
              </div>
            </div>

            {/* Card 2: Đã nộp */}
            <div
              className={`class-stat-card class-stat-card-actionable ${activeFilterTab === 'submitted' ? 'stat-active-selected' : ''}`}
              onClick={() => setActiveFilterTab('submitted')}
            >
              <div className="stat-card-icon-wrapper stat-icon-success">
                <svg
                  width="24"
                  height="24"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth="2.2"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                >
                  <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14" />
                  <polyline points="22 4 12 14.01 9 11.01" />
                </svg>
              </div>
              <div className="stat-card-content">
                <span className="stat-card-label">Đã nộp</span>
                <div className="stat-card-number-row">
                  <span className="stat-card-value">{stats.submitted}</span>
                  <span className="stat-card-badge stat-badge-green">Đang chờ giáo viên phản hồi</span>
                </div>
              </div>
            </div>

            {/* Card 3: Đã chấm */}
            <div
              className={`class-stat-card class-stat-card-actionable ${activeFilterTab === 'graded' ? 'stat-active-selected' : ''}`}
              onClick={() => setActiveFilterTab('graded')}
            >
              <div className="stat-card-icon-wrapper stat-icon-warning">
                <svg
                  width="24"
                  height="24"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth="2.2"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                >
                  <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2" />
                </svg>
              </div>
              <div className="stat-card-content">
                <span className="stat-card-label">Đã chấm</span>
                <div className="stat-card-number-row">
                  <span className="stat-card-value stat-val-warning">{stats.graded}</span>
                  <span className="stat-card-badge stat-badge-amber">Xem điểm và nhận xét</span>
                </div>
              </div>
            </div>
          </div>

          {/* Status Tabs Filter Bar (Figma 565:227) */}
          <div className="assignments-tabs-filter-bar">
            <button
              type="button"
              className={`assignment-tab-btn ${activeFilterTab === 'all' ? 'active' : ''}`}
              onClick={() => setActiveFilterTab('all')}
            >
              Tất cả ({stats.all})
            </button>
            <button
              type="button"
              className={`assignment-tab-btn ${activeFilterTab === 'need_action' ? 'active' : ''}`}
              onClick={() => setActiveFilterTab('need_action')}
            >
              Cần hoàn thành ({stats.needAction})
            </button>
            <button
              type="button"
              className={`assignment-tab-btn ${activeFilterTab === 'submitted' ? 'active' : ''}`}
              onClick={() => setActiveFilterTab('submitted')}
            >
              Đã nộp ({stats.submitted})
            </button>
            <button
              type="button"
              className={`assignment-tab-btn ${activeFilterTab === 'graded' ? 'active' : ''}`}
              onClick={() => setActiveFilterTab('graded')}
            >
              Đã chấm ({stats.graded})
            </button>
          </div>

          {/* Search & Class Dropdown Filter Bar */}
          <div className="class-filter-bar">
            <div className="filter-search-box">
              <svg
                width="18"
                height="18"
                viewBox="0 0 24 24"
                fill="none"
                stroke="#7894af"
                strokeWidth="2.2"
                strokeLinecap="round"
                strokeLinejoin="round"
              >
                <circle cx="11" cy="11" r="8" />
                <line x1="21" y1="21" x2="16.65" y2="16.65" />
              </svg>
              <input
                type="text"
                className="filter-search-input"
                placeholder="Tìm theo tên công việc..."
                value={searchTerm}
                onChange={e => setSearchTerm(e.target.value)}
              />
              {searchTerm && (
                <button type="button" className="filter-clear-btn" onClick={() => setSearchTerm('')}>
                  ✕
                </button>
              )}
            </div>

            <div className="filter-select-wrapper">
              <select className="filter-select-input" value={selectedClassFilter} onChange={e => setSelectedClassFilter(e.target.value)}>
                <option value="all">Lớp học: Tất cả</option>
                {classOptions.map(([id, name]) => (
                  <option key={id} value={id}>
                    {name}
                  </option>
                ))}
              </select>
              <span className="filter-select-arrow">▼</span>
            </div>

            <div className="filter-select-wrapper">
              <select className="filter-select-input" value={sortOrder} onChange={e => setSortOrder(e.target.value)}>
                <option value="newest">Sắp xếp: Mới giao gần nhất</option>
                <option value="due_soon">Sắp xếp: Sắp hết hạn</option>
              </select>
              <span className="filter-select-arrow">▼</span>
            </div>
          </div>

          {/* List Information & Table */}
          <div className="class-table-card">
            <div className="table-card-head">
              <h2 className="table-card-title">
                {filteredAssignments.length} công việc ·{' '}
                {selectedClassFilter === 'all'
                  ? 'Toàn bộ các lớp'
                  : classOptions.find(([id]) => id === selectedClassFilter)?.[1] || 'Toán 6A'}
              </h2>
              <span className="table-card-hint">Hạn nộp theo giờ Việt Nam (GMT+7)</span>
            </div>

            <div className="class-table-container">
              <table className="class-data-table">
                <thead>
                  <tr>
                    <th style={{ width: '38%' }}>Công việc / Lớp</th>
                    <th style={{ width: '15%' }}>Giáo viên</th>
                    <th style={{ width: '18%' }}>Hạn nộp</th>
                    <th style={{ width: '15%' }}>Trạng thái</th>
                    <th style={{ width: '14%', textAlign: 'right' }}>Thao tác</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredAssignments.length === 0 ? (
                    <tr>
                      <td colSpan={5} style={{ textAlign: 'center', padding: '48px 16px' }}>
                        <p style={{ color: 'var(--color-text-secondary)', fontSize: '15px' }}>
                          Không có bài tập nào phù hợp với bộ lọc hiện tại.
                        </p>
                      </td>
                    </tr>
                  ) : (
                    filteredAssignments.map(task => (
                      <tr key={task.id} className="class-table-row" onClick={() => setSelectedTask(task)}>
                        {/* Task Title & Meta */}
                        <td>
                          <div className="cell-task-info">
                            <span className="cell-task-title">{task.title}</span>
                            <span className="cell-task-meta">
                              {task.className} · {task.category}
                              {task.attachments.length > 0 && ` · ${task.attachments.length} tài liệu`}
                            </span>
                          </div>
                        </td>

                        {/* Teacher */}
                        <td>
                          <span className="cell-teacher-name">{task.teacherName}</span>
                        </td>

                        {/* Due Date */}
                        <td>
                          <span className="cell-due-date">{task.dueDate}</span>
                        </td>

                        {/* Status */}
                        <td>
                          {task.status === 'not_started' && <span className="status-pill status-not-started">Chưa làm</span>}
                          {task.status === 'in_progress' && <span className="status-pill status-in-progress">Đang làm</span>}
                          {task.status === 'overdue' && <span className="status-pill status-overdue">Quá hạn · Chưa nộp</span>}
                          {task.status === 'submitted' && <span className="status-pill status-active">Đã nộp</span>}
                          {task.status === 'graded' && <span className="status-pill status-graded">Đã chấm · {task.grade}</span>}
                        </td>

                        {/* Action */}
                        <td style={{ textAlign: 'right' }}>
                          <button
                            type="button"
                            className="btn-table-action btn-table-task-action"
                            onClick={e => {
                              e.stopPropagation();
                              setSelectedTask(task);
                            }}
                          >
                            {task.status === 'not_started' && 'Làm bài'}
                            {task.status === 'in_progress' && 'Tiếp tục'}
                            {task.status === 'overdue' && 'Xem chi tiết'}
                            {task.status === 'submitted' && 'Xem bài nộp'}
                            {task.status === 'graded' && 'Xem kết quả'}
                          </button>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </div>

          {/* Footer Callout Note (Figma 565:227) */}
          <div className="student-callout-note">
            <span className="note-icon">💡</span>
            <span>Bạn chỉ thấy công việc được giao cho lớp đã tham gia. Mở công việc để xem hướng dẫn, tài liệu và yêu cầu nộp bài.</span>
          </div>
        </div>
      </main>

      {/* Global Footer used */}

      {/* Join Class Popup Modal */}
      <JoinClassModal
        isOpen={isJoinModalOpen}
        allClasses={allClasses}
        onClose={() => setIsJoinModalOpen(false)}
        onRequestJoin={onRequestJoinClass}
      />

      {/* Submit / View Task Modal */}
      <SubmitTaskModal
        isOpen={Boolean(selectedTask)}
        assignment={selectedTask}
        onClose={() => setSelectedTask(null)}
        onSubmitWork={onSubmitTask}
      />
    </div>
  );
};

export default StudentAssignmentsPage;
