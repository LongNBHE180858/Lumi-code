import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import StudentNote from './student-note';
import StudentNoteDeleteDialog from './student-note-delete-dialog';
import StudentNoteDetail from './student-note-detail';
import StudentNoteUpdate from './student-note-update';

const StudentNoteRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<StudentNote />} />
    <Route path="new" element={<StudentNoteUpdate />} />
    <Route path=":id">
      <Route index element={<StudentNoteDetail />} />
      <Route path="edit" element={<StudentNoteUpdate />} />
      <Route path="delete" element={<StudentNoteDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default StudentNoteRoutes;
