import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import CourseReviewRequest from './course-review-request';
import CourseReviewRequestDeleteDialog from './course-review-request-delete-dialog';
import CourseReviewRequestDetail from './course-review-request-detail';
import CourseReviewRequestUpdate from './course-review-request-update';

const CourseReviewRequestRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<CourseReviewRequest />} />
    <Route path="new" element={<CourseReviewRequestUpdate />} />
    <Route path=":id">
      <Route index element={<CourseReviewRequestDetail />} />
      <Route path="edit" element={<CourseReviewRequestUpdate />} />
      <Route path="delete" element={<CourseReviewRequestDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CourseReviewRequestRoutes;
