import React from 'react';
import { Route } from 'react-router';
import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';
import { CourseBuilder } from './course-builder';

const CourseBuilderRoutes = () => (
  <ErrorBoundaryRoutes>
    {/* Khi tạo mới: /instructor/course-builder/new */}
    <Route path="new" element={<CourseBuilder />} />
    {/* Khi chỉnh sửa: /instructor/course-builder/:id */}
    <Route path=":id" element={<CourseBuilder />} />
  </ErrorBoundaryRoutes>
);

export default CourseBuilderRoutes;
