import React from 'react';
import { ClassStatistics } from '../class-management.model';

interface ClassStatCardsProps {
  stats: ClassStatistics;
  onFilterPending?: () => void;
}

export const ClassStatCards: React.FC<ClassStatCardsProps> = ({ stats, onFilterPending }) => {
  return (
    <div className="class-stats-grid">
      {/* Stat 1: Total Classes */}
      <div className="class-stat-card">
        <div className="stat-card-icon-wrapper stat-icon-primary">
          <svg
            width="24"
            height="24"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
          >
            <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20" />
            <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z" />
            <path d="M12 6v6" />
            <path d="M9 9h6" />
          </svg>
        </div>
        <div className="stat-card-content">
          <span className="stat-card-label">Tổng số lớp</span>
          <div className="stat-card-number-row">
            <span className="stat-card-value">{stats.totalClasses}</span>
            <span className="stat-card-badge stat-badge-blue">Đang vận hành</span>
          </div>
        </div>
      </div>

      {/* Stat 2: Total Students */}
      <div className="class-stat-card">
        <div className="stat-card-icon-wrapper stat-icon-success">
          <svg
            width="24"
            height="24"
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
        </div>
        <div className="stat-card-content">
          <span className="stat-card-label">Tổng học viên</span>
          <div className="stat-card-number-row">
            <span className="stat-card-value">{stats.totalStudents}</span>
            <span className="stat-card-badge stat-badge-green">Toàn bộ các lớp</span>
          </div>
        </div>
      </div>

      {/* Stat 3: Pending Requests */}
      <div
        className="class-stat-card class-stat-card-actionable"
        onClick={onFilterPending}
        title="Nhấp để xem các yêu cầu chờ duyệt"
        role="button"
        tabIndex={0}
      >
        <div className="stat-card-icon-wrapper stat-icon-warning">
          <svg
            width="24"
            height="24"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
          >
            <circle cx="12" cy="12" r="10" />
            <polyline points="12 6 12 12 16 14" />
          </svg>
        </div>
        <div className="stat-card-content">
          <span className="stat-card-label">Yêu cầu tham gia chờ duyệt</span>
          <div className="stat-card-number-row">
            <span className="stat-card-value stat-val-warning">{stats.pendingRequests}</span>
            <span className="stat-card-badge stat-badge-amber">Cần xử lý</span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ClassStatCards;
