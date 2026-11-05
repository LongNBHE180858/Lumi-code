import aiConversation from 'app/entities/ai-conversation/ai-conversation.reducer';
import aiGeneratedContent from 'app/entities/ai-generated-content/ai-generated-content.reducer';
import aiMessage from 'app/entities/ai-message/ai-message.reducer';
import authSession from 'app/entities/auth-session/auth-session.reducer';
import chapter from 'app/entities/chapter/chapter.reducer';
import classMaterial from 'app/entities/class-material/class-material.reducer';
import classMember from 'app/entities/class-member/class-member.reducer';
import contentReport from 'app/entities/content-report/content-report.reducer';
import course from 'app/entities/course/course.reducer';
import courseEnrollment from 'app/entities/course-enrollment/course-enrollment.reducer';
import courseReview from 'app/entities/course-review/course-review.reducer';
import studyClass from 'app/entities/study-class/study-class.reducer';
// import systemPlan from 'app/entities/system-plan/system-plan.reducer';
import refund from 'app/entities/refund/refund.reducer';
import instructorWallet from 'app/entities/instructor-wallet/instructor-wallet.reducer';
import teacherVerification from 'app/entities/teacher-verification/teacher-verification.reducer';
import userProfile from 'app/entities/user-profile/user-profile.reducer';
import walletTransaction from 'app/entities/wallet-transaction/wallet-transaction.reducer';
import payoutRequest from 'app/entities/payout-request/payout-request.reducer';
import pdfChunk from 'app/entities/pdf-chunk/pdf-chunk.reducer';
import notification from 'app/entities/notification/notification.reducer';
import grade from 'app/entities/grade/grade.reducer';
import lesson from 'app/entities/lesson/lesson.reducer';
import parentStudentLink from 'app/entities/parent-student-link/parent-student-link.reducer';
import subject from 'app/entities/subject/subject.reducer';
import courseMaterial from 'app/entities/course-material/course-material.reducer';
import wishlist from 'app/entities/wishlist/wishlist.reducer';
import feedback from 'app/entities/feedback/feedback.reducer';
import courseReviewRequest from 'app/entities/course-review-request/course-review-request.reducer';
import lessonProgress from 'app/entities/lesson-progress/lesson-progress.reducer';
import studentKnowledgeProgress from 'app/entities/student-knowledge-progress/student-knowledge-progress.reducer';
import learningDocument from 'app/entities/learning-document/learning-document.reducer';
import questionKnowledgeLink from 'app/entities/question-knowledge-link/question-knowledge-link.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  userProfile,
  teacherVerification,
  parentStudentLink,
  authSession,
  subject,
  grade,
  course,
  chapter,
  lesson,
  courseMaterial,
  courseEnrollment,
  wishlist,
  feedback,
  courseReview,
  studyClass,
  classMember,
  classMaterial,
  // systemPlan,
  refund,
  instructorWallet,
  walletTransaction,
  payoutRequest,
  pdfChunk,
  aiConversation,
  aiMessage,
  contentReport,
  notification,
  courseReviewRequest,
  lessonProgress,
  studentKnowledgeProgress,
  learningDocument,
  aiGeneratedContent,
  questionKnowledgeLink,
  // jhipster-needle-add-reducer-combine - JHipster will add reducer here
};

export default entitiesReducers;
