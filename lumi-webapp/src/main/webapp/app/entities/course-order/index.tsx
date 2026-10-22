import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import CourseOrder from './course-order';
import CourseOrderDeleteDialog from './course-order-delete-dialog';
import CourseOrderDetail from './course-order-detail';
import CourseOrderUpdate from './course-order-update';

const CourseOrderRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<CourseOrder />} />
    <Route path="new" element={<CourseOrderUpdate />} />
    <Route path=":id">
      <Route index element={<CourseOrderDetail />} />
      <Route path="edit" element={<CourseOrderUpdate />} />
      <Route path="delete" element={<CourseOrderDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CourseOrderRoutes;
