import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import AssignmentSubmission from './assignment-submission';
import AssignmentSubmissionDeleteDialog from './assignment-submission-delete-dialog';
import AssignmentSubmissionDetail from './assignment-submission-detail';
import AssignmentSubmissionUpdate from './assignment-submission-update';

const AssignmentSubmissionRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<AssignmentSubmission />} />
    <Route path="new" element={<AssignmentSubmissionUpdate />} />
    <Route path=":id">
      <Route index element={<AssignmentSubmissionDetail />} />
      <Route path="edit" element={<AssignmentSubmissionUpdate />} />
      <Route path="delete" element={<AssignmentSubmissionDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default AssignmentSubmissionRoutes;
