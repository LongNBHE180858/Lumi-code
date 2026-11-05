import React from 'react';
import FeedbackSubmit from './feedback/feedback-submit';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';
import PageNotFound from 'app/shared/error/page-not-found';

import AiConversation from './ai-conversation';
import AiGeneratedContent from './ai-generated-content';
import AiMessage from './ai-message';
import AuthSession from './auth-session';
import Chapter from './chapter';
import ClassMaterial from './class-material';
import ClassMember from './class-member';
import ContentReport from './content-report';
import Course from './course';
import CourseEnrollment from './course-enrollment';
import CourseReview from './course-review';
import StudyClass from './study-class';
import Refund from './refund';
import InstructorWallet from './instructor-wallet';
import TeacherVerification from './teacher-verification';
import UserProfile from './user-profile';
import WalletTransaction from './wallet-transaction';
import PayoutRequest from './payout-request';
import PdfChunk from './pdf-chunk';
import Notification from './notification';
import Grade from './grade';
import Lesson from './lesson';
import ParentStudentLink from './parent-student-link';
import Subject from './subject';
import CourseMaterial from './course-material';
import Wishlist from './wishlist';
import CourseReviewRequest from './course-review-request';
import LessonProgress from './lesson-progress';
import StudentKnowledgeProgress from './student-knowledge-progress';
import LearningDocument from './learning-document';
import QuestionKnowledgeLink from './question-knowledge-link';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="/user-profile/*" element={<UserProfile />} />
        <Route path="/teacher-verification/*" element={<TeacherVerification />} />
        <Route path="/parent-student-link/*" element={<ParentStudentLink />} />
        <Route path="/auth-session/*" element={<AuthSession />} />
        <Route path="/subject/*" element={<Subject />} />
        <Route path="/grade/*" element={<Grade />} />
        <Route path="/course/*" element={<Course />} />
        <Route path="/chapter/*" element={<Chapter />} />
        <Route path="/lesson/*" element={<Lesson />} />
        <Route path="/course-material/*" element={<CourseMaterial />} />
        <Route path="/course-enrollment/*" element={<CourseEnrollment />} />
        <Route path="/wishlist/*" element={<Wishlist />} />
        <Route path="/feedback/*" element={<FeedbackSubmit />} />
        <Route path="/course-review/*" element={<CourseReview />} />
        <Route path="/study-class/*" element={<StudyClass />} />
        <Route path="/class-member/*" element={<ClassMember />} />
        <Route path="/class-material/*" element={<ClassMaterial />} />
        <Route path="/refund/*" element={<Refund />} />
        <Route path="/instructor-wallet/*" element={<InstructorWallet />} />
        <Route path="/wallet-transaction/*" element={<WalletTransaction />} />
        <Route path="/payout-request/*" element={<PayoutRequest />} />
        <Route path="/pdf-chunk/*" element={<PdfChunk />} />
        <Route path="/ai-conversation/*" element={<AiConversation />} />
        <Route path="/ai-message/*" element={<AiMessage />} />
        <Route path="/content-report/*" element={<ContentReport />} />
        <Route path="/notification/*" element={<Notification />} />
        <Route path="/course-review-request/*" element={<CourseReviewRequest />} />
        <Route path="/lesson-progress/*" element={<LessonProgress />} />
        <Route path="/student-knowledge-progress/*" element={<StudentKnowledgeProgress />} />
        <Route path="/learning-document/*" element={<LearningDocument />} />
        <Route path="/ai-generated-content/*" element={<AiGeneratedContent />} />
        <Route path="/question-knowledge-link/*" element={<QuestionKnowledgeLink />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
        <Route path="*" element={<PageNotFound />} />
      </ErrorBoundaryRoutes>
    </div>
  );
};
