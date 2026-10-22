import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import AssignmentStudent from './assignment-student';
import AssignmentStudentDeleteDialog from './assignment-student-delete-dialog';
import AssignmentStudentDetail from './assignment-student-detail';
import AssignmentStudentUpdate from './assignment-student-update';

const AssignmentStudentRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<AssignmentStudent />} />
    <Route path="new" element={<AssignmentStudentUpdate />} />
    <Route path=":id">
      <Route index element={<AssignmentStudentDetail />} />
      <Route path="edit" element={<AssignmentStudentUpdate />} />
      <Route path="delete" element={<AssignmentStudentDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default AssignmentStudentRoutes;
