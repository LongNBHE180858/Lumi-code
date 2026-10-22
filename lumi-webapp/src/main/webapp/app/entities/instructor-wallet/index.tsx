import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import InstructorWallet from './instructor-wallet';
import InstructorWalletDeleteDialog from './instructor-wallet-delete-dialog';
import InstructorWalletDetail from './instructor-wallet-detail';
import InstructorWalletUpdate from './instructor-wallet-update';

const InstructorWalletRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<InstructorWallet />} />
    <Route path="new" element={<InstructorWalletUpdate />} />
    <Route path=":id">
      <Route index element={<InstructorWalletDetail />} />
      <Route path="edit" element={<InstructorWalletUpdate />} />
      <Route path="delete" element={<InstructorWalletDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default InstructorWalletRoutes;
