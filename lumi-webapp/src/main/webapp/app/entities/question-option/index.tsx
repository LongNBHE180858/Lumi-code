import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import QuestionOption from './question-option';
import QuestionOptionDeleteDialog from './question-option-delete-dialog';
import QuestionOptionDetail from './question-option-detail';
import QuestionOptionUpdate from './question-option-update';

const QuestionOptionRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<QuestionOption />} />
    <Route path="new" element={<QuestionOptionUpdate />} />
    <Route path=":id">
      <Route index element={<QuestionOptionDetail />} />
      <Route path="edit" element={<QuestionOptionUpdate />} />
      <Route path="delete" element={<QuestionOptionDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default QuestionOptionRoutes;
