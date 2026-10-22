import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import CourseReview from './course-review';
import CourseReviewDeleteDialog from './course-review-delete-dialog';
import CourseReviewDetail from './course-review-detail';
import CourseReviewUpdate from './course-review-update';

const CourseReviewRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<CourseReview />} />
    <Route path="new" element={<CourseReviewUpdate />} />
    <Route path=":id">
      <Route index element={<CourseReviewDetail />} />
      <Route path="edit" element={<CourseReviewUpdate />} />
      <Route path="delete" element={<CourseReviewDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CourseReviewRoutes;
