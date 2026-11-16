import React from 'react';
import { Route } from 'react-router';
import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';
import CourseReviewList from './course-reviews/course-review-list';
import CourseReviewDetail from './course-reviews/course-review-detail';

const ReviewerRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<CourseReviewList />} />
    <Route path=":id" element={<CourseReviewDetail />} />
  </ErrorBoundaryRoutes>
);

export { CourseReviewList, CourseReviewDetail };
export default ReviewerRoutes;
