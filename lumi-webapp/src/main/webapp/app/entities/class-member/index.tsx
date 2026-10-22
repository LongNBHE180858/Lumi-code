import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ClassMember from './class-member';
import ClassMemberDeleteDialog from './class-member-delete-dialog';
import ClassMemberDetail from './class-member-detail';
import ClassMemberUpdate from './class-member-update';

const ClassMemberRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ClassMember />} />
    <Route path="new" element={<ClassMemberUpdate />} />
    <Route path=":id">
      <Route index element={<ClassMemberDetail />} />
      <Route path="edit" element={<ClassMemberUpdate />} />
      <Route path="delete" element={<ClassMemberDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ClassMemberRoutes;
