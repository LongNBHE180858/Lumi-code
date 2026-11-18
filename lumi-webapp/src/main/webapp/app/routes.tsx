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
import InstructorAccessRoute from 'app/shared/auth/instructor-access-route';
import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';
import { Authority } from 'app/shared/jhipster/constants';
import Profile from 'app/modules/profile/profile';
import ProfileEdit from 'app/modules/profile/profile-edit';
import InstructorProfile from 'app/modules/instructor-profile/instructor-profile';
import Courses from 'app/modules/courses/courses';
import CourseDetail from 'app/modules/course-detail/course-detail';
import CourseCheckout from 'app/modules/course-purchase/course-checkout';
import CoursePayment from 'app/modules/course-purchase/course-payment';
import FeedbackSubmit from 'app/entities/feedback/feedback-submit';
import TicketList from 'app/modules/support-tickets/ticket-list';
import TicketDetail from 'app/modules/support-tickets/ticket-detail';
import ParentRefunds from 'app/modules/finance/parent-refunds';

const loading = <div>loading ...</div>;

const lazyWithRetry = <T extends React.ComponentType<any>>(componentImport: () => Promise<{ default: T }>) =>
  React.lazy(async () => {
    try {
      return await componentImport();
    } catch (error) {
      console.error(error);
      // Retry once after a brief delay if dev server restarted or network momentarily dropped
      await new Promise(resolve => setTimeout(resolve, 800));
      return await componentImport();
    }
  });

const AiChat = lazyWithRetry(() => import('app/modules/ai-chat/ai-chat'));

const Account = lazyWithRetry(() => import('app/modules/account'));

const Admin = lazyWithRetry(() => import('app/modules/administration'));

const Instructor = lazyWithRetry(() => import('app/modules/instructor'));

const Student = lazyWithRetry(() => import('app/modules/student'));

const AppRoutes = () => {
  return (
    <div className="view-routes">
      <Suspense fallback={loading}>
        <ErrorBoundaryRoutes>
          <Route index element={<Home />} />
          <Route
            path="ai-chat"
            element={
              <PrivateRoute hasAnyAuthorities={[Authority.STUDENT]}>
                <AiChat />
              </PrivateRoute>
            }
          />
          <Route
            path="admin/dashboard"
            element={
              <PrivateRoute hasAnyAuthorities={[Authority.ADMIN, Authority.REVIEWER]}>
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
            path="course-orders/:orderId/payment"
            element={
              <PrivateRoute hasAnyAuthorities={[Authority.PARENT]}>
                <CoursePayment />
              </PrivateRoute>
            }
          />
          <Route
            path="refund-requests"
            element={
              <PrivateRoute hasAnyAuthorities={[Authority.PARENT]}>
                <ParentRefunds />
              </PrivateRoute>
            }
          />
          <Route
            path="courses/:courseId/checkout"
            element={
              <PrivateRoute hasAnyAuthorities={[Authority.PARENT]}>
                <CourseCheckout />
              </PrivateRoute>
            }
          />
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
          <Route
            path="support-tickets"
            element={
              <PrivateRoute
                hasAnyAuthorities={[Authority.ADMIN, Authority.USER, Authority.STUDENT, Authority.INSTRUCTOR, Authority.PARENT]}
              >
                <TicketList />
              </PrivateRoute>
            }
          />
          <Route
            path="support-tickets/:id"
            element={
              <PrivateRoute
                hasAnyAuthorities={[Authority.ADMIN, Authority.USER, Authority.STUDENT, Authority.INSTRUCTOR, Authority.PARENT]}
              >
                <TicketDetail />
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
              <PrivateRoute hasAnyAuthorities={[Authority.ADMIN, Authority.REVIEWER]}>
                <Admin />
              </PrivateRoute>
            }
          />
          <Route
            path="instructor/*"
            element={
              <PrivateRoute hasAnyAuthorities={[Authority.ADMIN, Authority.INSTRUCTOR]}>
                <InstructorAccessRoute>
                  <Instructor />
                </InstructorAccessRoute>
              </PrivateRoute>
            }
          />
          <Route
            path="student/*"
            element={
              <PrivateRoute hasAnyAuthorities={[Authority.STUDENT]}>
                <Student />
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
