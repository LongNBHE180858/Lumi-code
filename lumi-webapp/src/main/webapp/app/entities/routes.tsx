import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';
import PageNotFound from 'app/shared/error/page-not-found';

import AiConversation from './ai-conversation';
import AiMessage from './ai-message';
import Assignment from './assignment';
import AssignmentStudent from './assignment-student';
import AssignmentSubmission from './assignment-submission';
import ClassMaterial from './class-material';
import ClassMember from './class-member';
import CourseReview from './course-review';
import StudyClass from './study-class';
import TeacherQuota from './teacher-quota';
import Coupon from './coupon';
import CourseOrder from './course-order';
import Payment from './payment';
import Refund from './refund';
import InstructorWallet from './instructor-wallet';
import WalletTransaction from './wallet-transaction';
import PayoutRequest from './payout-request';
import QuestionBank from './question-bank';
import Question from './question';
import QuestionOption from './question-option';
import Exam from './exam';
import ExamQuestion from './exam-question';
import ExamAttempt from './exam-attempt';
import ExamAnswer from './exam-answer';
import ProctorSession from './proctor-session';
import ProctorEvent from './proctor-event';
import StudentNote from './student-note';
import StudentStreak from './student-streak';
import FlashcardDeck from './flashcard-deck';
import Flashcard from './flashcard';
import FlashcardReview from './flashcard-review';
import KnowledgeNode from './knowledge-node';
import KnowledgeEdge from './knowledge-edge';
import PdfChunk from './pdf-chunk';
import Discussion from './discussion';
import DiscussionReply from './discussion-reply';
import ContentReport from './content-report';
import Notification from './notification';
import AuditLog from './audit-log';
import AuthSession from './auth-session';
import Category from './category';
import Chapter from './chapter';
import Course from './course';
import Grade from './grade';
import Lesson from './lesson';
import ParentStudentLink from './parent-student-link';
import Subject from './subject';
import TeacherVerification from './teacher-verification';
import UserProfile from './user-profile';
import CourseEnrollment from './course-enrollment';
import CourseMaterial from './course-material';
import Wishlist from './wishlist';
import FeedbackSubmit from './feedback/feedback-submit';
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
        <Route path="/category/*" element={<Category />} />
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
        <Route path="/teacher-quota/*" element={<TeacherQuota />} />
        <Route path="/coupon/*" element={<Coupon />} />
        <Route path="/course-order/*" element={<CourseOrder />} />
        <Route path="/payment/*" element={<Payment />} />
        <Route path="/refund/*" element={<Refund />} />
        <Route path="/instructor-wallet/*" element={<InstructorWallet />} />
        <Route path="/wallet-transaction/*" element={<WalletTransaction />} />
        <Route path="/payout-request/*" element={<PayoutRequest />} />
        <Route path="/question-bank/*" element={<QuestionBank />} />
        <Route path="/question/*" element={<Question />} />
        <Route path="/question-option/*" element={<QuestionOption />} />
        <Route path="/exam/*" element={<Exam />} />
        <Route path="/exam-question/*" element={<ExamQuestion />} />
        <Route path="/assignment/*" element={<Assignment />} />
        <Route path="/assignment-submission/*" element={<AssignmentSubmission />} />
        <Route path="/exam-attempt/*" element={<ExamAttempt />} />
        <Route path="/exam-answer/*" element={<ExamAnswer />} />
        <Route path="/proctor-session/*" element={<ProctorSession />} />
        <Route path="/proctor-event/*" element={<ProctorEvent />} />
        <Route path="/student-note/*" element={<StudentNote />} />
        <Route path="/student-streak/*" element={<StudentStreak />} />
        <Route path="/flashcard-deck/*" element={<FlashcardDeck />} />
        <Route path="/flashcard/*" element={<Flashcard />} />
        <Route path="/flashcard-review/*" element={<FlashcardReview />} />
        <Route path="/knowledge-node/*" element={<KnowledgeNode />} />
        <Route path="/knowledge-edge/*" element={<KnowledgeEdge />} />
        <Route path="/pdf-chunk/*" element={<PdfChunk />} />
        <Route path="/ai-conversation/*" element={<AiConversation />} />
        <Route path="/ai-message/*" element={<AiMessage />} />
        <Route path="/discussion/*" element={<Discussion />} />
        <Route path="/discussion-reply/*" element={<DiscussionReply />} />
        <Route path="/content-report/*" element={<ContentReport />} />
        <Route path="/notification/*" element={<Notification />} />
        <Route path="/audit-log/*" element={<AuditLog />} />
        <Route path="/assignment-student/*" element={<AssignmentStudent />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
        <Route path="*" element={<PageNotFound />} />
      </ErrorBoundaryRoutes>
    </div>
  );
};
