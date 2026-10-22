import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Wishlist from './wishlist';
import WishlistDeleteDialog from './wishlist-delete-dialog';
import WishlistDetail from './wishlist-detail';
import WishlistUpdate from './wishlist-update';

const WishlistRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Wishlist />} />
    <Route path="new" element={<WishlistUpdate />} />
    <Route path=":id">
      <Route index element={<WishlistDetail />} />
      <Route path="edit" element={<WishlistUpdate />} />
      <Route path="delete" element={<WishlistDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default WishlistRoutes;
