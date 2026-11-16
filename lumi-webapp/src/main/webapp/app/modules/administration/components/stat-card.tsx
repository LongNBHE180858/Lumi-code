import './admin-components.scss';

import React from 'react';
import { Minus, TrendingDown, TrendingUp, type LucideIcon } from 'lucide-react';

export interface StatCardData {
  label: string;
  value: string;
  trend: number | null;
  icon: LucideIcon;
}

const StatCard = ({ label, value, trend, icon: Icon }: StatCardData) => {
  const direction = trend == null || trend === 0 ? 'flat' : trend > 0 ? 'up' : 'down';
  const TrendIcon = direction === 'up' ? TrendingUp : direction === 'down' ? TrendingDown : Minus;
  const trendText =
    trend == null
      ? '--%'
      : `${trend > 0 ? '+' : ''}${new Intl.NumberFormat('vi-VN', { maximumFractionDigits: 1, minimumFractionDigits: 1 }).format(trend)}%`;

  return (
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
      <p className={`admin-stat-trend admin-stat-trend-${direction}`}>
        <TrendIcon size={14} aria-hidden="true" />
        <strong>{trendText}</strong>
        <span>so với tháng trước</span>
      </p>
    </article>
  );
};

export default StatCard;
