import React from 'react';

import { chartLabels, totalUsers } from '../dashboard-data';

interface GrowthChartProps {
  period: '6' | '3';
}

const GrowthChart = ({ period }: GrowthChartProps) => {
  const shortPeriod = period === '3';
  return (
    <div
      className="admin-growth-chart"
      role="img"
      aria-label={`Biểu đồ tăng trưởng người dùng trong ${period} tháng gần nhất, đạt ${totalUsers} người dùng`}
    >
      <div className="admin-chart-plot">
        {[15000, 10000, 5000, 0].map((tick, index) => (
          <div className="admin-chart-tick" key={tick} style={{ top: `${20 + index * 48}px` }}>
            <span>{tick.toLocaleString('vi-VN')}</span>
          </div>
        ))}
        {shortPeriod ? (
          <svg className="admin-chart-series" viewBox="0 0 600 144" preserveAspectRatio="none" aria-hidden="true">
            <path d="M0 75 C150 61 260 44 360 29 S530 15 600 4 L600 144 L0 144Z" fill="#24a8e8" fillOpacity=".11" />
            <path d="M0 75 C150 61 260 44 360 29 S530 15 600 4" fill="none" stroke="#24a8e8" strokeWidth="3" />
            <circle cx="600" cy="4" r="4" fill="#24a8e8" />
          </svg>
        ) : (
          <svg className="admin-chart-series" viewBox="0 0 600 120" preserveAspectRatio="none" aria-hidden="true">
            <path
              d="M0 57.41 C40 57.41 80 52.81 120 48.77 C160 44.73 200 45.81 240 41.09 C280 36.37 320 31.81 360 27.65 C400 23.49 440 18.81 480 13.59 C520 8.37 560 5.81 600 0 V120 H0Z"
              fill="#24a8e8"
              fillOpacity=".1"
            />
            <path
              d="M0 57.41 C40 57.41 80 52.81 120 48.77 C160 44.73 200 45.81 240 41.09 C280 36.37 320 31.81 360 27.65 C400 23.49 440 18.81 480 13.59 C520 8.37 560 5.81 600 0"
              fill="none"
              stroke="#24a8e8"
              strokeWidth="3"
              strokeLinecap="round"
            />
            <circle cx="600" cy="0" r="4" fill="#24a8e8" stroke="#fff" strokeWidth="2" />
          </svg>
        )}
        <strong className="admin-chart-value">{totalUsers}</strong>
      </div>
      <div className="admin-chart-months">
        {(shortPeriod ? chartLabels.slice(3) : chartLabels).map(label => (
          <span key={label}>{label}</span>
        ))}
      </div>
    </div>
  );
};

export default GrowthChart;
