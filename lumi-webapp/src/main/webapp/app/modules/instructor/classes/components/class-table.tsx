import React, { useState } from 'react';
import { ClassItem } from '../class-management.model';

interface ClassTableProps {
  classes: ClassItem[];
  selectedClassId: string;
  onSelectClass: (cls: ClassItem) => void;
  onManageClass: (cls: ClassItem) => void;
  onEditClass: (cls: ClassItem) => void;
}

export const ClassTable: React.FC<ClassTableProps> = ({ classes, selectedClassId, onSelectClass, onManageClass, onEditClass }) => {
  const [copiedCode, setCopiedCode] = useState<string | null>(null);

  const handleCopyCode = (code: string, e: React.MouseEvent) => {
    e.stopPropagation();
    navigator.clipboard.writeText(code);
    setCopiedCode(code);
    setTimeout(() => setCopiedCode(null), 2000);
  };

  if (classes.length === 0) {
    return (
      <div className="table-empty-state">
        <svg
          width="48"
          height="48"
          viewBox="0 0 24 24"
          fill="none"
          stroke="#91abc2"
          strokeWidth="1.5"
          strokeLinecap="round"
          strokeLinejoin="round"
        >
          <circle cx="12" cy="12" r="10" />
          <line x1="8" y1="12" x2="16" y2="12" />
        </svg>
        <p className="empty-title">Không tìm thấy lớp học nào phù hợp</p>
        <p className="empty-desc">Thử thay đổi từ khóa tìm kiếm hoặc điều chỉnh bộ lọc.</p>
      </div>
    );
  }

  return (
    <div className="class-table-container">
      <table className="class-data-table">
        <thead>
          <tr>
            <th style={{ width: '22%' }}>Tên lớp</th>
            <th style={{ width: '18%' }}>Mã lớp</th>
            <th style={{ width: '20%' }}>Khóa học</th>
            <th style={{ width: '10%' }}>Khối</th>
            <th style={{ width: '12%' }}>Học viên</th>
            <th style={{ width: '10%' }}>Trạng thái</th>
            <th style={{ width: '8%', textAlign: 'right' }}>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          {classes.map(cls => {
            const isSelected = cls.id === selectedClassId;
            return (
              <tr key={cls.id} className={`class-table-row ${isSelected ? 'row-selected' : ''}`} onClick={() => onSelectClass(cls)}>
                {/* Class Name */}
                <td>
                  <div className="cell-class-name-wrap">
                    <span className="cell-class-badge-icon">🏫</span>
                    <div>
                      <span className="cell-class-name">{cls.name}</span>
                      {cls.description && <div className="cell-class-subtext">{cls.description}</div>}
                    </div>
                  </div>
                </td>

                {/* Class Code */}
                <td>
                  <div className="cell-code-wrapper">
                    <code className="cell-code-tag">{cls.code}</code>
                    <button
                      type="button"
                      className="btn-copy-code"
                      title={copiedCode === cls.code ? 'Đã sao chép!' : 'Sao chép mã lớp'}
                      onClick={e => handleCopyCode(cls.code, e)}
                    >
                      {copiedCode === cls.code ? (
                        <span style={{ color: '#16a34a', fontSize: '12px', fontWeight: 'bold' }}>✓</span>
                      ) : (
                        <svg
                          width="13"
                          height="13"
                          viewBox="0 0 24 24"
                          fill="none"
                          stroke="currentColor"
                          strokeWidth="2.2"
                          strokeLinecap="round"
                          strokeLinejoin="round"
                        >
                          <rect x="9" y="9" width="13" height="13" rx="2" ry="2" />
                          <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1" />
                        </svg>
                      )}
                    </button>
                  </div>
                </td>

                {/* Course */}
                <td>
                  <span className="cell-course-name">{cls.course}</span>
                </td>

                {/* Grade */}
                <td>
                  <span className="cell-grade-tag">{cls.grade}</span>
                </td>

                {/* Student Count */}
                <td>
                  <div className="cell-student-count-wrap">
                    <span className="cell-student-number">{cls.studentCount}</span>
                    <span className="cell-student-unit">học viên</span>
                  </div>
                </td>

                {/* Status */}
                <td>
                  {cls.status === 'active' ? (
                    <span className="status-pill pill-active">Đang hoạt động</span>
                  ) : (
                    <span className="status-pill pill-archived">Tạm dừng</span>
                  )}
                </td>

                {/* Actions */}
                <td style={{ textAlign: 'right' }}>
                  <div className="cell-actions-group" onClick={e => e.stopPropagation()}>
                    <button
                      type="button"
                      className="btn-table-action btn-action-edit"
                      title="Chỉnh sửa thông tin lớp"
                      onClick={() => onEditClass(cls)}
                    >
                      <svg
                        width="15"
                        height="15"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        strokeWidth="2"
                        strokeLinecap="round"
                        strokeLinejoin="round"
                      >
                        <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" />
                        <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" />
                      </svg>
                    </button>
                    <button
                      type="button"
                      className="btn-table-action btn-action-manage"
                      title="Xem chi tiết & danh sách học viên"
                      onClick={() => onManageClass(cls)}
                    >
                      <svg
                        width="15"
                        height="15"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        strokeWidth="2"
                        strokeLinecap="round"
                        strokeLinejoin="round"
                      >
                        <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
                        <circle cx="9" cy="7" r="4" />
                        <path d="M23 21v-2a4 4 0 0 0-3-3.87" />
                        <path d="M16 3.13a4 4 0 0 1 0 7.75" />
                      </svg>
                    </button>
                  </div>
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
};

export default ClassTable;
