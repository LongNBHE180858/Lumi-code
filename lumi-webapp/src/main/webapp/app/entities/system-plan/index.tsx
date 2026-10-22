import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import SystemPlan from './system-plan';
import SystemPlanDeleteDialog from './system-plan-delete-dialog';
import SystemPlanDetail from './system-plan-detail';
import SystemPlanUpdate from './system-plan-update';

const SystemPlanRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<SystemPlan />} />
    <Route path="new" element={<SystemPlanUpdate />} />
    <Route path=":id">
      <Route index element={<SystemPlanDetail />} />
      <Route path="edit" element={<SystemPlanUpdate />} />
      <Route path="delete" element={<SystemPlanDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default SystemPlanRoutes;
