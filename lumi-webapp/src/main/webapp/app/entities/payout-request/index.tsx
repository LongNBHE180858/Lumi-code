import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import PayoutRequest from './payout-request';
import PayoutRequestDeleteDialog from './payout-request-delete-dialog';
import PayoutRequestDetail from './payout-request-detail';
import PayoutRequestUpdate from './payout-request-update';

const PayoutRequestRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<PayoutRequest />} />
    <Route path="new" element={<PayoutRequestUpdate />} />
    <Route path=":id">
      <Route index element={<PayoutRequestDetail />} />
      <Route path="edit" element={<PayoutRequestUpdate />} />
      <Route path="delete" element={<PayoutRequestDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default PayoutRequestRoutes;
