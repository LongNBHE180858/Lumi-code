import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Password from './password/password';
import Settings from './settings/settings';
import { ManageSecurityPage } from './security/security';

const AccountRoutes = () => (
  <div>
    <ErrorBoundaryRoutes>
      <Route path="settings" element={<Settings />} />
      <Route path="password" element={<Password />} />
      <Route path="security" element={<ManageSecurityPage />} />
    </ErrorBoundaryRoutes>
  </div>
);

export default AccountRoutes;
