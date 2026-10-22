import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Exam from './exam';
import ExamDeleteDialog from './exam-delete-dialog';
import ExamDetail from './exam-detail';
import ExamUpdate from './exam-update';

const ExamRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Exam />} />
    <Route path="new" element={<ExamUpdate />} />
    <Route path=":id">
      <Route index element={<ExamDetail />} />
      <Route path="edit" element={<ExamUpdate />} />
      <Route path="delete" element={<ExamDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ExamRoutes;
