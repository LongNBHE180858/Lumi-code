import React from 'react';

import { totalUsers, userDistribution } from '../dashboard-data';

const UserDistribution = () => (
  <div className="admin-panel admin-distribution-card">
    <h2>Phân bố người dùng</h2>
    <div className="admin-donut-chart" role="img" aria-label="11.920 học sinh, 520 giảng viên, 40 quản trị viên">
      <div>
        <strong>{totalUsers}</strong>
        <span>Người dùng</span>
      </div>
    </div>
    <div className="admin-legend">
      {userDistribution.map(({ label, count, percentage, role }) => (
        <div className="admin-legend-row" key={label}>
          <span className={`admin-legend-marker admin-legend-marker-${role}`} aria-hidden="true" />
          <span>{label}</span>
          <strong>{count}</strong>
          <small>{percentage}</small>
        </div>
      ))}
    </div>
  </div>
);

export default UserDistribution;
