import './admin-dashboard.scss';

import React, { useEffect, useMemo, useState } from 'react';
import { BookOpen, ChevronDown, ContactRound, GraduationCap, Users } from 'lucide-react';

import MonitoringCard, { type MonitoringKind } from '../components/monitoring-card';
import StatCard, { type StatCardData } from '../components/stat-card';
import AdminLayout from '../layout/admin-layout';
import GrowthChart from './components/growth-chart';
import MonitoringDialog from './components/monitoring-dialog';
import UserDistribution from './components/user-distribution';
import { getAdminDashboard, type DashboardData } from './dashboard-api';
import { getActivityItems, getRequestItems } from './dashboard-data';

const trend = (current: number, previous?: number) => (previous ? ((current - previous) / previous) * 100 : null);

export const AdminDashboard = () => {
  const [query, setQuery] = useState('');
  const [period, setPeriod] = useState<'6' | '3'>('6');
  const [dialog, setDialog] = useState<MonitoringKind | null>(null);
  const [data, setData] = useState<DashboardData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [reload, setReload] = useState(0);
  const search = useMemo(() => query.trim().toLocaleLowerCase('vi'), [query]);

  useEffect(() => {
    const controller = new AbortController();
    setLoading(true);
    setError(false);
    getAdminDashboard(controller.signal)
      .then(response => setData(response.data))
      .catch(() => {
        if (!controller.signal.aborted) setError(true);
      })
      .finally(() => {
        if (!controller.signal.aborted) setLoading(false);
      });
    return () => controller.abort();
  }, [reload]);

  const stats: StatCardData[] = data
    ? [
        {
          label: 'Tổng người dùng',
          value: data.counts.users.toLocaleString('vi-VN'),
          trend: trend(data.counts.users, data.previousMonth?.users),
          icon: Users,
        },
        {
          label: 'Tổng học sinh',
          value: data.counts.students.toLocaleString('vi-VN'),
          trend: trend(data.counts.students, data.previousMonth?.students),
          icon: GraduationCap,
        },
        {
          label: 'Tổng giảng viên',
          value: data.counts.instructors.toLocaleString('vi-VN'),
          trend: trend(data.counts.instructors, data.previousMonth?.instructors),
          icon: ContactRound,
        },
        {
          label: 'Tổng khóa học',
          value: data.counts.courses.toLocaleString('vi-VN'),
          trend: trend(data.counts.courses, data.previousMonth?.courses),
          icon: BookOpen,
        },
      ]
    : [];
  const activities = data ? getActivityItems(data.activities) : [];
  const requests = data ? getRequestItems(data.requests) : [];
  const notificationMessage =
    data && data.requests.supportTickets > 0 ? `${data.requests.supportTickets} yêu cầu hỗ trợ mới cần xem xét.` : undefined;

  return (
    <AdminLayout
      searchValue={query}
      onSearchChange={setQuery}
      searchLabel="Tìm kiếm hoạt động và yêu cầu"
      notificationMessage={notificationMessage}
    >
      <main className="admin-main">
        {loading && (
          <p className="admin-dashboard-state" role="status">
            Đang tải thống kê...
          </p>
        )}
        {error && !loading && (
          <div className="admin-dashboard-state" role="alert">
            <span>Không tải được thống kê.</span>
            <button type="button" onClick={() => setReload(value => value + 1)}>
              Thử lại
            </button>
          </div>
        )}
        {data && !loading && !error && (
          <>
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
                <GrowthChart period={period} points={data.monthlyUsers} />
              </div>
              <UserDistribution counts={data.counts} />
            </section>

            <section className="admin-monitoring" aria-label="Hoạt động và yêu cầu">
              <MonitoringCard
                title="Hoạt động gần đây"
                kind="activity"
                items={activities.slice(0, 4)}
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
          </>
        )}
      </main>
      {dialog && data && <MonitoringDialog key={`${dialog}-${search}`} kind={dialog} search={search} onClose={() => setDialog(null)} />}
    </AdminLayout>
  );
};

export default AdminDashboard;
