import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import AiConversation from './ai-conversation';
import AiConversationDeleteDialog from './ai-conversation-delete-dialog';
import AiConversationDetail from './ai-conversation-detail';
import AiConversationUpdate from './ai-conversation-update';

const AiConversationRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<AiConversation />} />
    <Route path="new" element={<AiConversationUpdate />} />
    <Route path=":id">
      <Route index element={<AiConversationDetail />} />
      <Route path="edit" element={<AiConversationUpdate />} />
      <Route path="delete" element={<AiConversationDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default AiConversationRoutes;
