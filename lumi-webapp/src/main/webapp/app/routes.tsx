import React, { Suspense } from 'react';
import { Route } from 'react-router';

import EntitiesRoutes from 'app/entities/routes';
import Activate from 'app/modules/account/activate/activate';
import { EmailVerificationPage } from 'app/modules/account/email-verification/email-verification';
import PasswordResetFinish from 'app/modules/account/password-reset/finish/password-reset-finish';
import PasswordResetInit from 'app/modules/account/password-reset/init/password-reset-init';
import Register from 'app/modules/account/register/register';
import Home from 'app/modules/home/home';
import AdminDashboard from 'app/modules/administration/dashboard/admin-dashboard';
import Login from 'app/modules/login/login';
import Logout from 'app/modules/login/logout';
import PrivateRoute from 'app/shared/auth/private-route';
import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';
import { Authority } from 'app/shared/jhipster/constants';
import Profile from 'app/modules/profile/profile';
import ProfileEdit from 'app/modules/profile/profile-edit';
import InstructorProfile from 'app/modules/instructor-profile/instructor-profile';
import Courses from 'app/modules/courses/courses';
import CourseDetail from 'app/modules/course-detail/course-detail';
import FeedbackSubmit from 'app/entities/feedback/feedback-submit';

const loading = <div>loading ...</div>;

const Account = React.lazy(() => import('app/modules/account'));

const Admin = React.lazy(() => import('app/modules/administration'));

const Instructor = React.lazy(() => import('app/modules/instructor'));

const AppRoutes = () => {
  return (
    <div className="view-routes">
      <Suspense fallback={loading}>
        <ErrorBoundaryRoutes>
          <Route index element={<Home />} />
          <Route
            path="admin/dashboard"
            element={
              <PrivateRoute hasAnyAuthorities={[Authority.ADMIN]}>
                <AdminDashboard />
              </PrivateRoute>
            }
          />
          <Route path="login" element={<Login />} />
          <Route path="logout" element={<Logout />} />
          <Route path="verify-email" element={<EmailVerificationPage />} />
          <Route path="register" element={<Register />} />
          <Route path="forgot-password" element={<PasswordResetInit />} />
          <Route path="reset-password" element={<PasswordResetFinish />} />
          <Route path="instructors/:id" element={<InstructorProfile />} />
          <Route path="courses" element={<Courses />} />
          <Route path="courses/:id" element={<CourseDetail />} />
          <Route
            path="profile/edit"
            element={
              <PrivateRoute
                hasAnyAuthorities={[Authority.ADMIN, Authority.USER, Authority.STUDENT, Authority.INSTRUCTOR, Authority.PARENT]}
              >
                <ProfileEdit />
              </PrivateRoute>
            }
          />
          <Route
            path="profile"
            element={
              <PrivateRoute
                hasAnyAuthorities={[Authority.ADMIN, Authority.USER, Authority.STUDENT, Authority.INSTRUCTOR, Authority.PARENT]}
              >
                <Profile />
              </PrivateRoute>
            }
          />
          <Route
            path="feedback"
            element={
              <PrivateRoute hasAnyAuthorities={[Authority.ADMIN, Authority.USER]}>
                <FeedbackSubmit />
              </PrivateRoute>
            }
          />
          <Route path="account">
            <Route path="register" element={<Register />} />
            <Route path="activate" element={<Activate />} />
            <Route path="verify-email" element={<EmailVerificationPage />} />
            <Route path="reset">
              <Route path="request" element={<PasswordResetInit />} />
              <Route path="finish" element={<PasswordResetFinish />} />
            </Route>
            <Route
              path="*"
              element={
                <PrivateRoute hasAnyAuthorities={[Authority.ADMIN, Authority.USER]}>
                  <Account />
                </PrivateRoute>
              }
            />
          </Route>
          <Route
            path="admin/*"
            element={
              <PrivateRoute hasAnyAuthorities={[Authority.ADMIN]}>
                <Admin />
              </PrivateRoute>
            }
          />
          <Route
            path="instructor/*"
            element={
              <PrivateRoute hasAnyAuthorities={[Authority.ADMIN, Authority.USER]}>
                <Instructor />
              </PrivateRoute>
            }
          />
          <Route
            path="*"
            element={
              <PrivateRoute hasAnyAuthorities={[Authority.USER]}>
                <EntitiesRoutes />
              </PrivateRoute>
            }
          />
        </ErrorBoundaryRoutes>
      </Suspense>
    </div>
  );
};

export default AppRoutes;
