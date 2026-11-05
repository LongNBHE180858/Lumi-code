import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import AiGeneratedContent from './ai-generated-content';
import AiGeneratedContentDeleteDialog from './ai-generated-content-delete-dialog';
import AiGeneratedContentDetail from './ai-generated-content-detail';
import AiGeneratedContentUpdate from './ai-generated-content-update';

const AiGeneratedContentRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<AiGeneratedContent />} />
    <Route path="new" element={<AiGeneratedContentUpdate />} />
    <Route path=":id">
      <Route index element={<AiGeneratedContentDetail />} />
      <Route path="edit" element={<AiGeneratedContentUpdate />} />
      <Route path="delete" element={<AiGeneratedContentDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default AiGeneratedContentRoutes;
