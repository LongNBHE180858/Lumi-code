import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import KnowledgeEdge from './knowledge-edge';
import KnowledgeEdgeDeleteDialog from './knowledge-edge-delete-dialog';
import KnowledgeEdgeDetail from './knowledge-edge-detail';
import KnowledgeEdgeUpdate from './knowledge-edge-update';

const KnowledgeEdgeRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<KnowledgeEdge />} />
    <Route path="new" element={<KnowledgeEdgeUpdate />} />
    <Route path=":id">
      <Route index element={<KnowledgeEdgeDetail />} />
      <Route path="edit" element={<KnowledgeEdgeUpdate />} />
      <Route path="delete" element={<KnowledgeEdgeDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default KnowledgeEdgeRoutes;
