import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import LearningDocument from './learning-document';
import LearningDocumentDeleteDialog from './learning-document-delete-dialog';
import LearningDocumentDetail from './learning-document-detail';
import LearningDocumentUpdate from './learning-document-update';

const LearningDocumentRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<LearningDocument />} />
    <Route path="new" element={<LearningDocumentUpdate />} />
    <Route path=":id">
      <Route index element={<LearningDocumentDetail />} />
      <Route path="edit" element={<LearningDocumentUpdate />} />
      <Route path="delete" element={<LearningDocumentDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default LearningDocumentRoutes;
