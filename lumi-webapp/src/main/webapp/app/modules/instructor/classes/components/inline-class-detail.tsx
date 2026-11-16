import React, { useState } from 'react';
import { ClassItem, StudentItem, AssignTaskFormValues } from '../class-management.model';
import { AssignTaskTab } from './AssignTaskTab';

interface InlineClassDetailProps {
  cls: ClassItem;
  students: StudentItem[];
  allClasses?: ClassItem[];
  onOpenEditModal: (cls: ClassItem) => void;
  onApproveStudent?: (studentId: string) => void;
  onRemoveStudent?: (studentId: string) => void;
  onUpdateClass?: (cls: ClassItem) => void;
  onAssignTaskSuccess?: (formValues: AssignTaskFormValues) => void;
}

export type DetailTab = 'overview' | 'students' | 'assign' | 'settings';

export const InlineClassDetail: React.FC<InlineClassDetailProps> = ({
  cls,
  students,
  allClasses = [],
  onOpenEditModal,
  onApproveStudent,
  onRemoveStudent,
  onUpdateClass,
  onAssignTaskSuccess,
}) => {
  const [activeTab, setActiveTab] = useState<DetailTab>('overview');

  // Settings form local state
  const [editName, setEditName] = useState(cls.name);
  const [editPassword, setEditPassword] = useState(cls.password || '••••••••');
  const [editStatus, setEditStatus] = useState<string>(cls.status);
  const [saveSuccess, setSaveSuccess] = useState(false);

  // Student filter
  const [studentSearch, setStudentSearch] = useState('');
  const [studentStatusFilter, setStudentStatusFilter] = useState('all');

  const filteredStudents = students.filter(s => {
    const matchName =
      s.name.toLowerCase().includes(studentSearch.toLowerCase()) || s.username.toLowerCase().includes(studentSearch.toLowerCase());
    const matchStatus = studentStatusFilter === 'all' || s.status === studentStatusFilter;
    return matchName && matchStatus;
  });

  const handleSaveSettings = (e: React.FormEvent) => {
    e.preventDefault();
    if (onUpdateClass) {
      onUpdateClass({
        ...cls,
        name: editName,
        password: editPassword,
        status: editStatus as any,
      });
    }
    setSaveSuccess(true);
    setTimeout(() => setSaveSuccess(false), 2500);
  };

  return (
    <div className="inline-class-detail-card fade-in">
      {/* Header */}
      <div className="detail-card-head">
        <div className="detail-title-group">
          <div className="detail-title-icon">📘</div>
          <div>
            <h3 className="detail-title">
              {cls.name} <span className="detail-title-sep">/</span> <span className="detail-title-code">{cls.code}</span>
            </h3>
            <p className="detail-subtitle">{cls.description}</p>
          </div>
        </div>

        <button type="button" className="btn-pill-primary btn-sm-manage" onClick={() => onOpenEditModal(cls)}>
          <svg
            width="14"
            height="14"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2.2"
            strokeLinecap="round"
            strokeLinejoin="round"
          >
            <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" />
            <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" />
          </svg>
          <span>Chỉnh sửa lớp</span>
        </button>
      </div>

      {/* Tabs */}
      <div className="detail-tabs-bar">
        <button
          type="button"
          className={`detail-tab-btn ${activeTab === 'overview' ? 'active' : ''}`}
          onClick={() => setActiveTab('overview')}
        >
          Tổng quan
        </button>
        <button
          type="button"
          className={`detail-tab-btn ${activeTab === 'students' ? 'active' : ''}`}
          onClick={() => setActiveTab('students')}
        >
          Học viên ({students.length})
        </button>
        <button
          type="button"
          className={`detail-tab-btn ${activeTab === 'settings' ? 'active' : ''}`}
          onClick={() => setActiveTab('settings')}
        >
          Cài đặt
        </button>
      </div>

      {/* Tab 1: Overview */}
      {activeTab === 'overview' && (
        <div className="detail-tab-pane fade-in">
          <div className="overview-stats-grid">
            <div className="ov-stat-box">
              <span className="ov-stat-label">Sĩ số học viên</span>
              <span className="ov-stat-value">{cls.studentCount}</span>
              <span className="ov-stat-desc">Đã tham gia</span>
            </div>
            <div className="ov-stat-box">
              <span className="ov-stat-label">Chuyên cần</span>
              <span className="ov-stat-value">94%</span>
              <span className="ov-stat-desc">Tỷ lệ tương tác cao</span>
            </div>
            <div className="ov-stat-box">
              <span className="ov-stat-label">Khóa học liên kết</span>
              <span className="ov-stat-value text-ellipsis" style={{ fontSize: '18px' }}>
                {cls.course}
              </span>
              <span className="ov-stat-desc">
                {cls.grade} · {cls.subject}
              </span>
            </div>
            <div className="ov-stat-box">
              <span className="ov-stat-label">Mã vào lớp</span>
              <span className="ov-stat-value text-code">{cls.code}</span>
              <span className="ov-stat-desc">Cung cấp cho học viên</span>
            </div>
          </div>
        </div>
      )}

      {/* Tab 2: Students List */}
      {activeTab === 'students' && (
        <div className="detail-tab-pane fade-in">
          <div className="student-filter-row">
            <input
              type="text"
              className="student-search-input"
              placeholder="Tìm theo tên học viên hoặc tên đăng nhập..."
              value={studentSearch}
              onChange={e => setStudentSearch(e.target.value)}
            />
            <select className="student-status-select" value={studentStatusFilter} onChange={e => setStudentStatusFilter(e.target.value)}>
              <option value="all">Tất cả trạng thái</option>
              <option value="active">Đang hoạt động</option>
              <option value="pending">Chờ phê duyệt</option>
            </select>
          </div>

          {filteredStudents.length === 0 ? (
            <div className="student-empty-state">
              <p>Chưa có học viên nào phù hợp điều kiện lọc.</p>
            </div>
          ) : (
            <div className="student-table-wrap">
              <table className="student-data-table">
                <thead>
                  <tr>
                    <th>Học viên</th>
                    <th>Email</th>
                    <th>Ngày tham gia</th>
                    <th>Tiến độ</th>
                    <th>Trạng thái</th>
                    <th style={{ textAlign: 'right' }}>Thao tác</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredStudents.map(student => (
                    <tr key={student.id}>
                      <td>
                        <div className="student-name-group">
                          <span className="student-avatar-letter">{student.name.charAt(0)}</span>
                          <div>
                            <div className="student-full-name">{student.name}</div>
                            <div className="student-username">@{student.username}</div>
                          </div>
                        </div>
                      </td>
                      <td>{student.email}</td>
                      <td>{student.joinedDate}</td>
                      <td>
                        <div className="progress-cell-group">
                          <div className="progress-bar-track">
                            <div className="progress-bar-fill" style={{ width: `${student.progress}%` }} />
                          </div>
                          <span className="progress-percent">{student.progress}%</span>
                        </div>
                      </td>
                      <td>
                        {student.status === 'active' ? (
                          <span className="student-badge badge-active">Hoạt động</span>
                        ) : (
                          <span className="student-badge badge-pending">Chờ duyệt</span>
                        )}
                      </td>
                      <td style={{ textAlign: 'right' }}>
                        {student.status === 'pending' && onApproveStudent && (
                          <button
                            type="button"
                            className="btn-approve-student"
                            onClick={() => onApproveStudent(student.id)}
                            title="Duyệt vào lớp"
                          >
                            ✓ Duyệt
                          </button>
                        )}
                        {onRemoveStudent && (
                          <button
                            type="button"
                            className="btn-remove-student"
                            onClick={() => onRemoveStudent(student.id)}
                            title="Xóa học viên khỏi lớp"
                          >
                            ✕
                          </button>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}

      {/* Tab 3: Settings */}
      {activeTab === 'settings' && (
        <form onSubmit={handleSaveSettings} className="detail-tab-pane fade-in">
          {saveSuccess && <div className="toast-inline-success fade-in">✓ Cập nhật thông tin cài đặt thành công!</div>}
          <div className="settings-form-grid">
            <div className="settings-field">
              <label className="settings-label">Tên lớp học</label>
              <input type="text" className="settings-input" value={editName} onChange={e => setEditName(e.target.value)} required />
            </div>
            <div className="settings-field">
              <label className="settings-label">Trạng thái lớp</label>
              <select className="settings-input" value={editStatus} onChange={e => setEditStatus(e.target.value)}>
                <option value="active">Đang hoạt động</option>
                <option value="archived">Tạm dừng</option>
              </select>
            </div>
            <div className="settings-field">
              <label className="settings-label">Mật khẩu vào lớp</label>
              <input type="text" className="settings-input" value={editPassword} onChange={e => setEditPassword(e.target.value)} />
            </div>
          </div>
          <div className="settings-actions">
            <button type="submit" className="btn-pill-primary">
              Lưu thay đổi
            </button>
          </div>
        </form>
      )}
    </div>
  );
};

export default InlineClassDetail;
