import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Discussion from './discussion';
import DiscussionDeleteDialog from './discussion-delete-dialog';
import DiscussionDetail from './discussion-detail';
import DiscussionUpdate from './discussion-update';

const DiscussionRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Discussion />} />
    <Route path="new" element={<DiscussionUpdate />} />
    <Route path=":id">
      <Route index element={<DiscussionDetail />} />
      <Route path="edit" element={<DiscussionUpdate />} />
      <Route path="delete" element={<DiscussionDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default DiscussionRoutes;
