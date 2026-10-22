import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Coupon from './coupon';
import CouponDeleteDialog from './coupon-delete-dialog';
import CouponDetail from './coupon-detail';
import CouponUpdate from './coupon-update';

const CouponRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Coupon />} />
    <Route path="new" element={<CouponUpdate />} />
    <Route path=":id">
      <Route index element={<CouponDetail />} />
      <Route path="edit" element={<CouponUpdate />} />
      <Route path="delete" element={<CouponDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CouponRoutes;
