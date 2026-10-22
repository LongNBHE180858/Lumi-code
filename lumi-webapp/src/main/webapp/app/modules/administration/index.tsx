import React, { useState } from 'react';
import { Route, useLocation } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Configuration from './configuration/configuration';
import Docs from './docs/docs';
import Health from './health/health';
import Logs from './logs/logs';
import Metrics from './metrics/metrics';
import UserManagement from './user-management';
import AdminLayout from './layout/admin-layout';
import InstructorApplications from './instructor-applications/instructor-applications';
import AdminCatalog from './catalog/admin-catalog';

const AdminUserManagement = () => {
  const [searchValue, setSearchValue] = useState('');
  const { pathname } = useLocation();
  const isUserList = pathname.replace(/\/$/, '').endsWith('/user-management');

  return (
    <AdminLayout
      searchValue={searchValue}
      onSearchChange={isUserList ? setSearchValue : undefined}
      searchLabel="Tìm người dùng trong trang hiện tại"
    >
      <main className="admin-page-content">
        <UserManagement searchValue={searchValue} />
      </main>
    </AdminLayout>
  );
};
import FeedbackAdminList from './feedback/feedback-admin-list';
import FeedbackAdminDetail from './feedback/feedback-admin-detail';

const AdministrationRoutes = () => (
  <div>
    <ErrorBoundaryRoutes>
      <Route path="user-management/*" element={<AdminUserManagement />} />
      <Route path="instructor-applications" element={<InstructorApplications />} />
      <Route path="catalog" element={<AdminCatalog />} />
      <Route path="feedback" element={<FeedbackAdminList />} />
      <Route path="feedback/:id" element={<FeedbackAdminDetail />} />
      <Route path="health" element={<Health />} />
      <Route path="metrics" element={<Metrics />} />
      <Route path="configuration" element={<Configuration />} />
      <Route path="logs" element={<Logs />} />
      <Route path="docs" element={<Docs />} />
    </ErrorBoundaryRoutes>
  </div>
);

export default AdministrationRoutes;
