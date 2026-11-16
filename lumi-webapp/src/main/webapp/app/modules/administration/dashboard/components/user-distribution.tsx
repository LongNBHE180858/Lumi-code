import React from 'react';

import type { DashboardCounts } from '../dashboard-api';

interface UserDistributionProps {
  counts: DashboardCounts;
}

const UserDistribution = ({ counts }: UserDistributionProps) => {
  const users = counts.users;
  const distribution = [
    { label: 'Học sinh', count: counts.students, role: 'student' },
    { label: 'Phụ huynh', count: counts.parents, role: 'parent' },
    { label: 'Giảng viên', count: counts.instructors, role: 'instructor' },
  ];
  const studentShare = users ? (counts.students / users) * 100 : 0;
  const parentShare = users ? (counts.parents / users) * 100 : 0;
  const background = users
    ? `conic-gradient(#24a8e8 0 ${studentShare}%, #7894af ${studentShare}% ${studentShare + parentShare}%, #102b59 ${studentShare + parentShare}% 100%)`
    : '#e6f5ff';
  const percentage = (count: number) =>
    users ? `${((count / users) * 100).toLocaleString('vi-VN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}%` : '0,00%';

  return (
    <div className="admin-panel admin-distribution-card">
      <h2>Phân bố người dùng</h2>
      <div
        className="admin-donut-chart"
        style={{ background }}
        role="img"
        aria-label={distribution.map(item => `${item.count} ${item.label.toLocaleLowerCase('vi')}`).join(', ')}
      >
        <div>
          <strong>{users.toLocaleString('vi-VN')}</strong>
          <span>Người dùng</span>
        </div>
      </div>
      <div className="admin-legend">
        {distribution.map(({ label, count, role }) => (
          <div className="admin-legend-row" key={role}>
            <span className={`admin-legend-marker admin-legend-marker-${role}`} aria-hidden="true" />
            <span>{label}</span>
            <strong>{count.toLocaleString('vi-VN')}</strong>
            <small>{percentage(count)}</small>
          </div>
        ))}
      </div>
    </div>
  );
};

export default UserDistribution;
