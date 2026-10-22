import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import QuestionBank from './question-bank';
import QuestionBankDeleteDialog from './question-bank-delete-dialog';
import QuestionBankDetail from './question-bank-detail';
import QuestionBankUpdate from './question-bank-update';

const QuestionBankRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<QuestionBank />} />
    <Route path="new" element={<QuestionBankUpdate />} />
    <Route path=":id">
      <Route index element={<QuestionBankDetail />} />
      <Route path="edit" element={<QuestionBankUpdate />} />
      <Route path="delete" element={<QuestionBankDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default QuestionBankRoutes;
