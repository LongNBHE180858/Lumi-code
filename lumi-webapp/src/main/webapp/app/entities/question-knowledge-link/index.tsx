import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import QuestionKnowledgeLink from './question-knowledge-link';
import QuestionKnowledgeLinkDeleteDialog from './question-knowledge-link-delete-dialog';
import QuestionKnowledgeLinkDetail from './question-knowledge-link-detail';
import QuestionKnowledgeLinkUpdate from './question-knowledge-link-update';

const QuestionKnowledgeLinkRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<QuestionKnowledgeLink />} />
    <Route path="new" element={<QuestionKnowledgeLinkUpdate />} />
    <Route path=":id">
      <Route index element={<QuestionKnowledgeLinkDetail />} />
      <Route path="edit" element={<QuestionKnowledgeLinkUpdate />} />
      <Route path="delete" element={<QuestionKnowledgeLinkDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default QuestionKnowledgeLinkRoutes;
