import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import TeacherQuota from './teacher-quota';
import TeacherQuotaDeleteDialog from './teacher-quota-delete-dialog';
import TeacherQuotaDetail from './teacher-quota-detail';
import TeacherQuotaUpdate from './teacher-quota-update';

const TeacherQuotaRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<TeacherQuota />} />
    <Route path="new" element={<TeacherQuotaUpdate />} />
    <Route path=":id">
      <Route index element={<TeacherQuotaDetail />} />
      <Route path="edit" element={<TeacherQuotaUpdate />} />
      <Route path="delete" element={<TeacherQuotaDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default TeacherQuotaRoutes;
