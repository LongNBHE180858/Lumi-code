import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import StudentStreak from './student-streak';
import StudentStreakDeleteDialog from './student-streak-delete-dialog';
import StudentStreakDetail from './student-streak-detail';
import StudentStreakUpdate from './student-streak-update';

const StudentStreakRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<StudentStreak />} />
    <Route path="new" element={<StudentStreakUpdate />} />
    <Route path=":id">
      <Route index element={<StudentStreakDetail />} />
      <Route path="edit" element={<StudentStreakUpdate />} />
      <Route path="delete" element={<StudentStreakDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default StudentStreakRoutes;
