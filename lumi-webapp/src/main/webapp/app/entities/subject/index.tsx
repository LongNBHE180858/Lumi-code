import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Subject from './subject';
import SubjectDeleteDialog from './subject-delete-dialog';
import SubjectDetail from './subject-detail';
import SubjectUpdate from './subject-update';

const SubjectRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Subject />} />
    <Route path="new" element={<SubjectUpdate />} />
    <Route path=":id">
      <Route index element={<SubjectDetail />} />
      <Route path="edit" element={<SubjectUpdate />} />
      <Route path="delete" element={<SubjectDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default SubjectRoutes;
