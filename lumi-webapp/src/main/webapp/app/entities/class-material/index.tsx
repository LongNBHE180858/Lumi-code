import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ClassMaterial from './class-material';
import ClassMaterialDeleteDialog from './class-material-delete-dialog';
import ClassMaterialDetail from './class-material-detail';
import ClassMaterialUpdate from './class-material-update';

const ClassMaterialRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ClassMaterial />} />
    <Route path="new" element={<ClassMaterialUpdate />} />
    <Route path=":id">
      <Route index element={<ClassMaterialDetail />} />
      <Route path="edit" element={<ClassMaterialUpdate />} />
      <Route path="delete" element={<ClassMaterialDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ClassMaterialRoutes;
