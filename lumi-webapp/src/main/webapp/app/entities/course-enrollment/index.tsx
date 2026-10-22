import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import CourseEnrollment from './course-enrollment';
import CourseEnrollmentDeleteDialog from './course-enrollment-delete-dialog';
import CourseEnrollmentDetail from './course-enrollment-detail';
import CourseEnrollmentUpdate from './course-enrollment-update';

const CourseEnrollmentRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<CourseEnrollment />} />
    <Route path="new" element={<CourseEnrollmentUpdate />} />
    <Route path=":id">
      <Route index element={<CourseEnrollmentDetail />} />
      <Route path="edit" element={<CourseEnrollmentUpdate />} />
      <Route path="delete" element={<CourseEnrollmentDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CourseEnrollmentRoutes;
