import './admin-dashboard.scss';

import React, { useMemo, useState } from 'react';
import { ChevronDown } from 'lucide-react';

import MonitoringCard, { type MonitoringKind } from '../components/monitoring-card';
import StatCard from '../components/stat-card';
import AdminLayout from '../layout/admin-layout';
import GrowthChart from './components/growth-chart';
import MonitoringDialog from './components/monitoring-dialog';
import UserDistribution from './components/user-distribution';
import { activities, notificationMessage, requests, stats } from './dashboard-data';

export const AdminDashboard = () => {
  const [query, setQuery] = useState('');
  const [period, setPeriod] = useState<'6' | '3'>('6');
  const [dialog, setDialog] = useState<MonitoringKind | null>(null);
  const search = useMemo(() => query.trim().toLocaleLowerCase('vi'), [query]);

  return (
    <AdminLayout
      searchValue={query}
      onSearchChange={setQuery}
      searchLabel="Tìm kiếm hoạt động và yêu cầu"
      notificationMessage={notificationMessage}
    >
      <main className="admin-main">
        <section className="admin-statistics" aria-label="Thống kê tổng quan">
          {stats.map(stat => (
            <StatCard key={stat.label} {...stat} />
          ))}
        </section>

        <section className="admin-analytics" aria-label="Biểu đồ người dùng">
          <div className="admin-panel admin-growth-card">
            <div className="admin-panel-heading">
              <h2>Thống kê người dùng</h2>
              <label className="admin-period-select">
                <span className="visually-hidden">Khoảng thời gian</span>
                <select value={period} onChange={event => setPeriod(event.target.value as '6' | '3')}>
                  <option value="6">6 tháng gần nhất</option>
                  <option value="3">3 tháng gần nhất</option>
                </select>
                <ChevronDown size={14} aria-hidden="true" />
              </label>
            </div>
            <GrowthChart period={period} />
          </div>

          <UserDistribution />
        </section>

        <section className="admin-monitoring" aria-label="Hoạt động và yêu cầu">
          <MonitoringCard
            title="Hoạt động gần đây"
            kind="activity"
            items={activities}
            search={search}
            onViewAll={() => setDialog('activity')}
          />
          <MonitoringCard
            title="Yêu cầu cần xử lý"
            kind="request"
            items={requests}
            search={search}
            onViewAll={() => setDialog('request')}
          />
        </section>
        <p className="admin-sample-note">Dữ liệu minh họa</p>
      </main>
      {dialog && (
        <MonitoringDialog
          kind={dialog}
          items={dialog === 'activity' ? activities : requests}
          search={search}
          onClose={() => setDialog(null)}
        />
      )}
    </AdminLayout>
  );
};

export default AdminDashboard;
