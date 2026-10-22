import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import TeacherVerification from './teacher-verification';
import TeacherVerificationDeleteDialog from './teacher-verification-delete-dialog';
import TeacherVerificationDetail from './teacher-verification-detail';
import TeacherVerificationUpdate from './teacher-verification-update';

const TeacherVerificationRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<TeacherVerification />} />
    <Route path="new" element={<TeacherVerificationUpdate />} />
    <Route path=":id">
      <Route index element={<TeacherVerificationDetail />} />
      <Route path="edit" element={<TeacherVerificationUpdate />} />
      <Route path="delete" element={<TeacherVerificationDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default TeacherVerificationRoutes;
