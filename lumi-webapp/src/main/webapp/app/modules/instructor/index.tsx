import React from 'react';
import { Route } from 'react-router';
import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import { CourseList } from './courses/course-list';
import { CourseBuilder } from './course-builder/course-builder';
import { ClassList } from './classes/class-list';
import InstructorClassroom from './classes/instructor-classroom';
import InstructorFinance from 'app/modules/finance/instructor-finance';

const InstructorRoutes = () => (
  <div>
    <ErrorBoundaryRoutes>
      {/* 1. Mặc định truy cập /instructor sẽ hiện danh sách khóa học */}
      <Route index element={<CourseList />} />
      <Route path="courses" element={<CourseList />} />
      <Route path="classes" element={<ClassList />} />
      <Route path="classes/:id" element={<InstructorClassroom />} />
      <Route path="wallet" element={<InstructorFinance />} />

      {/* Dùng chung builder để giữ form khi draft mới nhận được ID. */}
      <Route element={<CourseBuilder />}>
        <Route path="courses/new" element={<React.Fragment />} />
        <Route path="courses/:id/builder" element={<React.Fragment />} />
      </Route>
    </ErrorBoundaryRoutes>
  </div>
);

export default InstructorRoutes;
