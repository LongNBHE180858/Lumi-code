import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import KnowledgeNode from './knowledge-node';
import KnowledgeNodeDeleteDialog from './knowledge-node-delete-dialog';
import KnowledgeNodeDetail from './knowledge-node-detail';
import KnowledgeNodeUpdate from './knowledge-node-update';

const KnowledgeNodeRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<KnowledgeNode />} />
    <Route path="new" element={<KnowledgeNodeUpdate />} />
    <Route path=":id">
      <Route index element={<KnowledgeNodeDetail />} />
      <Route path="edit" element={<KnowledgeNodeUpdate />} />
      <Route path="delete" element={<KnowledgeNodeDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default KnowledgeNodeRoutes;
