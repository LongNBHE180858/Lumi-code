import React from 'react';
import { Route } from 'react-router';

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
import AdminPremiumPackages from './premium-packages/admin-premium-packages';
import TicketList from 'app/modules/support-tickets/ticket-list';
import TicketDetail from 'app/modules/support-tickets/ticket-detail';

const AdminUserManagement = () => {
  return (
    <AdminLayout>
      <main className="admin-page-content">
        <UserManagement />
      </main>
    </AdminLayout>
  );
};
import FeedbackAdminList from './feedback/feedback-admin-list';
import FeedbackAdminDetail from './feedback/feedback-admin-detail';
import KnowledgeGraphAdmin from './knowledge-graph/knowledge-graph';

const AdministrationRoutes = () => (
  <div>
    <ErrorBoundaryRoutes>
      <Route path="user-management/*" element={<AdminUserManagement />} />
      <Route path="instructor-applications" element={<InstructorApplications />} />
      <Route path="catalog" element={<AdminCatalog />} />
      <Route path="premium-packages" element={<AdminPremiumPackages />} />
      <Route path="support-tickets" element={<TicketList admin />} />
      <Route path="support-tickets/:id" element={<TicketDetail admin />} />
      <Route path="feedback" element={<FeedbackAdminList />} />
      <Route path="feedback/:id" element={<FeedbackAdminDetail />} />
      <Route path="knowledge-graph" element={<KnowledgeGraphAdmin />} />
      <Route path="health" element={<Health />} />
      <Route path="metrics" element={<Metrics />} />
      <Route path="configuration" element={<Configuration />} />
      <Route path="logs" element={<Logs />} />
      <Route path="docs" element={<Docs />} />
    </ErrorBoundaryRoutes>
  </div>
);

export default AdministrationRoutes;
