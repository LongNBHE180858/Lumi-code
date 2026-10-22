import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ExamAttempt from './exam-attempt';
import ExamAttemptDeleteDialog from './exam-attempt-delete-dialog';
import ExamAttemptDetail from './exam-attempt-detail';
import ExamAttemptUpdate from './exam-attempt-update';

const ExamAttemptRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ExamAttempt />} />
    <Route path="new" element={<ExamAttemptUpdate />} />
    <Route path=":id">
      <Route index element={<ExamAttemptDetail />} />
      <Route path="edit" element={<ExamAttemptUpdate />} />
      <Route path="delete" element={<ExamAttemptDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ExamAttemptRoutes;
