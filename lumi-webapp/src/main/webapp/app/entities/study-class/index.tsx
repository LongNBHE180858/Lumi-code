import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import StudyClass from './study-class';
import StudyClassDeleteDialog from './study-class-delete-dialog';
import StudyClassDetail from './study-class-detail';
import StudyClassUpdate from './study-class-update';

const StudyClassRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<StudyClass />} />
    <Route path="new" element={<StudyClassUpdate />} />
    <Route path=":id">
      <Route index element={<StudyClassDetail />} />
      <Route path="edit" element={<StudyClassUpdate />} />
      <Route path="delete" element={<StudyClassDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default StudyClassRoutes;
