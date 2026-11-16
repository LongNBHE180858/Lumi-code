import React from 'react';
import { Route } from 'react-router';
import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import StudentClasses from './classes';
import StudentClassroom from './classes/student-classroom';
import { Navigate } from 'react-router';

const StudentRoutes = () => (
  <div>
    <ErrorBoundaryRoutes>
      <Route index element={<Navigate to="classes" replace />} />
      <Route path="classes" element={<StudentClasses />} />
      <Route path="classes/:id" element={<StudentClassroom />} />
      <Route path="assignments" element={<Navigate to="/student/classes" replace />} />
    </ErrorBoundaryRoutes>
  </div>
);

export default StudentRoutes;
