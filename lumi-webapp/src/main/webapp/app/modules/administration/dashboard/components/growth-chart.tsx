import React from 'react';

import type { DashboardData } from '../dashboard-api';

interface GrowthChartProps {
  period: '6' | '3';
  points: DashboardData['monthlyUsers'];
}

const GrowthChart = ({ period, points }: GrowthChartProps) => {
  const visible = period === '3' ? points.slice(-3) : points;
  const largest = Math.max(1, ...visible.map(point => point.users ?? 0));
  const unit = 10 ** Math.floor(Math.log10(largest / 3));
  const tickStep = Math.ceil(largest / 3 / unit) * unit;
  const chartMax = tickStep * 3;
  const plotted = visible.map((point, index) => ({
    x: visible.length === 1 ? 300 : (index * 600) / (visible.length - 1),
    y: point.users == null ? null : 120 - (point.users / chartMax) * 120,
  }));
  const segments: string[] = [];
  let current = '';
  plotted.forEach(point => {
    if (point.y == null) {
      if (current) segments.push(current);
      current = '';
    } else {
      current += `${current ? ' L ' : 'M '}${point.x} ${point.y}`;
    }
  });
  if (current) segments.push(current);
  const known = visible.filter(point => point.users != null);
  const label =
    known.length > 1
      ? `Tổng người dùng theo tháng, ${known.map(point => `${point.month}: ${point.users}`).join(', ')}`
      : 'Chưa đủ mốc lịch sử để vẽ tăng trưởng người dùng';

  return (
    <div className="admin-growth-chart" role="img" aria-label={label}>
      <div className="admin-chart-plot">
        {[3, 2, 1, 0].map((step, index) => (
          <div className="admin-chart-tick" key={step} style={{ top: `${20 + index * 48}px` }}>
            <span>{(tickStep * step).toLocaleString('vi-VN')}</span>
          </div>
        ))}
        <svg className="admin-chart-series" viewBox="0 0 600 120" preserveAspectRatio="none" aria-hidden="true">
          {segments.map((segment, index) => (
            <path key={index} d={segment} fill="none" stroke="#24a8e8" strokeWidth="3" />
          ))}
          {plotted.map((point, index) =>
            point.y == null ? null : <circle key={visible[index].month} cx={point.x} cy={point.y} r="4" fill="#24a8e8" />,
          )}
        </svg>
        {known.length < 2 && <span className="admin-chart-empty">Chưa đủ mốc lịch sử</span>}
        <strong className="admin-chart-value">{visible.at(-1)?.users?.toLocaleString('vi-VN') ?? '—'}</strong>
      </div>
      <div className="admin-chart-months">
        {visible.map(point => {
          const [year, month] = point.month.split('-');
          return <span key={point.month}>{`Tháng ${Number(month)}/${year}`}</span>;
        })}
      </div>
    </div>
  );
};

export default GrowthChart;
