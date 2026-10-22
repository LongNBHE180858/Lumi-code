import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ExamAnswer from './exam-answer';
import ExamAnswerDeleteDialog from './exam-answer-delete-dialog';
import ExamAnswerDetail from './exam-answer-detail';
import ExamAnswerUpdate from './exam-answer-update';

const ExamAnswerRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ExamAnswer />} />
    <Route path="new" element={<ExamAnswerUpdate />} />
    <Route path=":id">
      <Route index element={<ExamAnswerDetail />} />
      <Route path="edit" element={<ExamAnswerUpdate />} />
      <Route path="delete" element={<ExamAnswerDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ExamAnswerRoutes;
