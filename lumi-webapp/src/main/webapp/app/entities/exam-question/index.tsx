import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ExamQuestion from './exam-question';
import ExamQuestionDeleteDialog from './exam-question-delete-dialog';
import ExamQuestionDetail from './exam-question-detail';
import ExamQuestionUpdate from './exam-question-update';

const ExamQuestionRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ExamQuestion />} />
    <Route path="new" element={<ExamQuestionUpdate />} />
    <Route path=":id">
      <Route index element={<ExamQuestionDetail />} />
      <Route path="edit" element={<ExamQuestionUpdate />} />
      <Route path="delete" element={<ExamQuestionDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ExamQuestionRoutes;
