import './admin-components.scss';

import React from 'react';
import { ChevronRight, type LucideIcon } from 'lucide-react';
import { Link } from 'react-router';

export type MonitoringKind = 'activity' | 'request';

export type MonitoringItem = {
  id: string;
  label: string;
  icon: LucideIcon;
  href?: string;
} & ({ time: string; status?: never; warning?: never } | { status: string; warning?: boolean; time?: never });

interface MonitoringCardProps {
  title: string;
  kind: MonitoringKind;
  items: MonitoringItem[];
  search: string;
  onViewAll: () => void;
}

const MonitoringCard = ({ title, kind, items, search, onViewAll }: MonitoringCardProps) => {
  const visibleItems = items.filter(item => item.label.toLocaleLowerCase('vi').includes(search));

  return (
    <section className="admin-panel admin-monitoring-card">
      <div className="admin-panel-heading">
        <h2>{title}</h2>
        <button className="admin-text-action" type="button" onClick={onViewAll}>
          Xem tất cả
        </button>
      </div>
      <div className="admin-monitoring-list">
        {visibleItems.length === 0 && <p className="admin-no-results">{search ? 'Không tìm thấy kết quả phù hợp.' : 'Chưa có dữ liệu.'}</p>}
        {visibleItems.map(item => {
          const Icon = item.icon;
          return (
            <div className="admin-monitoring-row" key={item.id}>
              <span className={`admin-list-icon${kind === 'request' ? ' admin-list-icon-request' : ''}`}>
                <Icon size={16} aria-hidden="true" />
              </span>
              {item.href ? (
                <Link className="admin-row-label" to={item.href}>
                  {item.label}
                </Link>
              ) : (
                <span className="admin-row-label">{item.label}</span>
              )}
              {item.time !== undefined ? (
                <span className="admin-row-time">{item.time}</span>
              ) : (
                <>
                  <span className={`admin-request-badge${item.warning ? ' admin-request-badge-warning' : ''}`}>{item.status}</span>
                  <ChevronRight className="admin-chevron-right" size={14} aria-hidden="true" />
                </>
              )}
            </div>
          );
        })}
      </div>
    </section>
  );
};

export default MonitoringCard;
