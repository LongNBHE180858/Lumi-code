import './admin-components.scss';

import React from 'react';
import { TrendingUp, type LucideIcon } from 'lucide-react';

export interface StatCardData {
  label: string;
  value: string;
  trend: string;
  icon: LucideIcon;
}

const StatCard = ({ label, value, trend, icon: Icon }: StatCardData) => (
  <article className="admin-panel admin-stat-card">
    <div className="admin-stat-summary">
      <div>
        <p>{label}</p>
        <strong>{value}</strong>
      </div>
      <span className="admin-stat-icon">
        <Icon size={20} aria-hidden="true" />
      </span>
    </div>
    <p className="admin-stat-trend">
      <TrendingUp size={14} aria-hidden="true" />
      <strong>{trend}</strong>
      <span>so với tháng trước</span>
    </p>
  </article>
);

export default StatCard;
