import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import CourseMaterial from './course-material';
import CourseMaterialDeleteDialog from './course-material-delete-dialog';
import CourseMaterialDetail from './course-material-detail';
import CourseMaterialUpdate from './course-material-update';

const CourseMaterialRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<CourseMaterial />} />
    <Route path="new" element={<CourseMaterialUpdate />} />
    <Route path=":id">
      <Route index element={<CourseMaterialDetail />} />
      <Route path="edit" element={<CourseMaterialUpdate />} />
      <Route path="delete" element={<CourseMaterialDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CourseMaterialRoutes;
