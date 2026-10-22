import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import AiMessage from './ai-message';
import AiMessageDeleteDialog from './ai-message-delete-dialog';
import AiMessageDetail from './ai-message-detail';
import AiMessageUpdate from './ai-message-update';

const AiMessageRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<AiMessage />} />
    <Route path="new" element={<AiMessageUpdate />} />
    <Route path=":id">
      <Route index element={<AiMessageDetail />} />
      <Route path="edit" element={<AiMessageUpdate />} />
      <Route path="delete" element={<AiMessageDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default AiMessageRoutes;
