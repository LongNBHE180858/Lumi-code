import React from 'react';
import { Route } from 'react-router';
import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import StudentClasses from './classes';
import StudentClassroom from './classes/student-classroom';
import StudentExamHub from './classes/student-exam-hub';
import StudentExamResult from './classes/student-exam-result';
import StudentExamTake from './classes/student-exam-take';
import { Navigate } from 'react-router';

const StudentRoutes = () => (
  <div>
    <ErrorBoundaryRoutes>
      <Route index element={<Navigate to="classes" replace />} />
      <Route path="classes" element={<StudentClasses />} />
      <Route path="classes/:id" element={<StudentClassroom />} />
      <Route path="classes/:id/exams" element={<StudentExamHub />} />
      <Route path="classes/:id/exams/:examId/take" element={<StudentExamTake />} />
      <Route path="classes/:id/exams/:examId/attempts/:attemptId/result" element={<StudentExamResult />} />
      <Route path="assignments" element={<Navigate to="/student/classes" replace />} />
    </ErrorBoundaryRoutes>
  </div>
);

export default StudentRoutes;
