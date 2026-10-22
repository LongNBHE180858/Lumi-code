import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import AuthSession from './auth-session';
import AuthSessionDeleteDialog from './auth-session-delete-dialog';
import AuthSessionDetail from './auth-session-detail';
import AuthSessionUpdate from './auth-session-update';

const AuthSessionRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<AuthSession />} />
    <Route path="new" element={<AuthSessionUpdate />} />
    <Route path=":id">
      <Route index element={<AuthSessionDetail />} />
      <Route path="edit" element={<AuthSessionUpdate />} />
      <Route path="delete" element={<AuthSessionDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default AuthSessionRoutes;
