import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import StudentKnowledgeProgress from './student-knowledge-progress';
import StudentKnowledgeProgressDeleteDialog from './student-knowledge-progress-delete-dialog';
import StudentKnowledgeProgressDetail from './student-knowledge-progress-detail';
import StudentKnowledgeProgressUpdate from './student-knowledge-progress-update';

const StudentKnowledgeProgressRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<StudentKnowledgeProgress />} />
    <Route path="new" element={<StudentKnowledgeProgressUpdate />} />
    <Route path=":id">
      <Route index element={<StudentKnowledgeProgressDetail />} />
      <Route path="edit" element={<StudentKnowledgeProgressUpdate />} />
      <Route path="delete" element={<StudentKnowledgeProgressDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default StudentKnowledgeProgressRoutes;
