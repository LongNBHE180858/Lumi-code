import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ProctorSession from './proctor-session';
import ProctorSessionDeleteDialog from './proctor-session-delete-dialog';
import ProctorSessionDetail from './proctor-session-detail';
import ProctorSessionUpdate from './proctor-session-update';

const ProctorSessionRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ProctorSession />} />
    <Route path="new" element={<ProctorSessionUpdate />} />
    <Route path=":id">
      <Route index element={<ProctorSessionDetail />} />
      <Route path="edit" element={<ProctorSessionUpdate />} />
      <Route path="delete" element={<ProctorSessionDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ProctorSessionRoutes;
