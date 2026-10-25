import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import UserManagement from './user-management';
import UserManagementDetail from './user-management-detail';
import UserManagementCreate from './user-management-create';
import './user-management.scss';

const UserManagementRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<UserManagement />} />
    <Route path="new" element={<UserManagementCreate />} />
    <Route path=":login">
      <Route index element={<UserManagementDetail />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default UserManagementRoutes;
