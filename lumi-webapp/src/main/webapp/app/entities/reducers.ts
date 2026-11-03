import aiConversation from 'app/entities/ai-conversation/ai-conversation.reducer';
import aiMessage from 'app/entities/ai-message/ai-message.reducer';
import assignment from 'app/entities/assignment/assignment.reducer';
import assignmentStudent from 'app/entities/assignment-student/assignment-student.reducer';
import assignmentSubmission from 'app/entities/assignment-submission/assignment-submission.reducer';
import classMaterial from 'app/entities/class-material/class-material.reducer';
import classMember from 'app/entities/class-member/class-member.reducer';
import courseReview from 'app/entities/course-review/course-review.reducer';
import studyClass from 'app/entities/study-class/study-class.reducer';
import teacherQuota from 'app/entities/teacher-quota/teacher-quota.reducer';
import coupon from 'app/entities/coupon/coupon.reducer';
import courseOrder from 'app/entities/course-order/course-order.reducer';
import payment from 'app/entities/payment/payment.reducer';
import refund from 'app/entities/refund/refund.reducer';
import instructorWallet from 'app/entities/instructor-wallet/instructor-wallet.reducer';
import walletTransaction from 'app/entities/wallet-transaction/wallet-transaction.reducer';
import payoutRequest from 'app/entities/payout-request/payout-request.reducer';
import questionBank from 'app/entities/question-bank/question-bank.reducer';
import question from 'app/entities/question/question.reducer';
import questionOption from 'app/entities/question-option/question-option.reducer';
import exam from 'app/entities/exam/exam.reducer';
import examQuestion from 'app/entities/exam-question/exam-question.reducer';
import examAttempt from 'app/entities/exam-attempt/exam-attempt.reducer';
import examAnswer from 'app/entities/exam-answer/exam-answer.reducer';
import proctorSession from 'app/entities/proctor-session/proctor-session.reducer';
import proctorEvent from 'app/entities/proctor-event/proctor-event.reducer';
import studentNote from 'app/entities/student-note/student-note.reducer';
import studentStreak from 'app/entities/student-streak/student-streak.reducer';
import flashcardDeck from 'app/entities/flashcard-deck/flashcard-deck.reducer';
import flashcard from 'app/entities/flashcard/flashcard.reducer';
import flashcardReview from 'app/entities/flashcard-review/flashcard-review.reducer';
import knowledgeNode from 'app/entities/knowledge-node/knowledge-node.reducer';
import knowledgeEdge from 'app/entities/knowledge-edge/knowledge-edge.reducer';
import pdfChunk from 'app/entities/pdf-chunk/pdf-chunk.reducer';
import discussion from 'app/entities/discussion/discussion.reducer';
import discussionReply from 'app/entities/discussion-reply/discussion-reply.reducer';
import contentReport from 'app/entities/content-report/content-report.reducer';
import notification from 'app/entities/notification/notification.reducer';
import auditLog from 'app/entities/audit-log/audit-log.reducer';
import authSession from 'app/entities/auth-session/auth-session.reducer';
import category from 'app/entities/category/category.reducer';
import chapter from 'app/entities/chapter/chapter.reducer';
import course from 'app/entities/course/course.reducer';
import grade from 'app/entities/grade/grade.reducer';
import lesson from 'app/entities/lesson/lesson.reducer';
import parentStudentLink from 'app/entities/parent-student-link/parent-student-link.reducer';
import subject from 'app/entities/subject/subject.reducer';
import teacherVerification from 'app/entities/teacher-verification/teacher-verification.reducer';
import userProfile from 'app/entities/user-profile/user-profile.reducer';
import courseEnrollment from 'app/entities/course-enrollment/course-enrollment.reducer';
import courseMaterial from 'app/entities/course-material/course-material.reducer';
import wishlist from 'app/entities/wishlist/wishlist.reducer';
import feedback from 'app/entities/feedback/feedback.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  userProfile,
  teacherVerification,
  parentStudentLink,
  authSession,
  category,
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
  teacherQuota,
  coupon,
  courseOrder,
  payment,
  refund,
  instructorWallet,
  walletTransaction,
  payoutRequest,
  questionBank,
  question,
  questionOption,
  exam,
  examQuestion,
  assignment,
  assignmentSubmission,
  examAttempt,
  examAnswer,
  proctorSession,
  proctorEvent,
  studentNote,
  studentStreak,
  flashcardDeck,
  flashcard,
  flashcardReview,
  knowledgeNode,
  knowledgeEdge,
  pdfChunk,
  aiConversation,
  aiMessage,
  discussion,
  discussionReply,
  contentReport,
  notification,
  auditLog,
  assignmentStudent,
  // jhipster-needle-add-reducer-combine - JHipster will add reducer here
};

export default entitiesReducers;
