import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ParentStudentLink from './parent-student-link';
import ParentStudentLinkDeleteDialog from './parent-student-link-delete-dialog';
import ParentStudentLinkDetail from './parent-student-link-detail';
import ParentStudentLinkUpdate from './parent-student-link-update';

const ParentStudentLinkRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ParentStudentLink />} />
    <Route path="new" element={<ParentStudentLinkUpdate />} />
    <Route path=":id">
      <Route index element={<ParentStudentLinkDetail />} />
      <Route path="edit" element={<ParentStudentLinkUpdate />} />
      <Route path="delete" element={<ParentStudentLinkDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ParentStudentLinkRoutes;
