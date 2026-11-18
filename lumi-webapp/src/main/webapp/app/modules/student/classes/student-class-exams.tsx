import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';
import { classroomError, type ClassroomMaterial } from 'app/shared/util/classroom-api';
import { getStudentExams, getStudentExamResults, type ExamResult, type ExamSummary } from 'app/shared/util/class-exam-api';
import StudentReviewQuiz from './student-review-quiz';

const REVIEW_PAGE_SIZE = 5;

export const StudentClassExams: React.FC<{ classId: number; lessons: ClassroomMaterial[] }> = ({ classId, lessons }) => {
  const navigate = useNavigate();
  const [exams, setExams] = useState<ExamSummary[]>([]);
  const [history, setHistory] = useState<Record<number, ExamResult[]>>({});
  const [loadedHistory, setLoadedHistory] = useState<Record<number, boolean>>({});
  const [busy, setBusy] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [view, setView] = useState<'exams' | 'review' | 'history'>('exams');
  const [now, setNow] = useState(() => Date.now());
  const [reviewPage, setReviewPage] = useState(0);
  const [reviewQuizOpen, setReviewQuizOpen] = useState(false);

  useEffect(() => {
    const timer = window.setInterval(() => setNow(Date.now()), 60_000);
    return () => window.clearInterval(timer);
  }, []);

  useEffect(() => {
    let active = true;
    setLoading(true);
    setError(null);
    setExams([]);
    setHistory({});
    setLoadedHistory({});
    void getStudentExams(classId)
      .then(async data => {
        if (!active) return;
        setExams(data);
        setLoading(false);
        const results = await Promise.allSettled(data.map(exam => getStudentExamResults(classId, exam.id)));
        if (!active) return;
        setHistory(
          Object.fromEntries(
            results.map((entry, index) => [data[index].id, entry.status === 'fulfilled' && Array.isArray(entry.value) ? entry.value : []]),
          ),
        );
        setLoadedHistory(
          Object.fromEntries(results.map((entry, index) => [data[index].id, entry.status === 'fulfilled' && Array.isArray(entry.value)])),
        );
      })
      .catch(cause => active && setError(classroomError(cause)))
      .finally(() => active && setLoading(false));
    return () => {
      active = false;
    };
  }, [classId]);

  const showResult = async (examId: number, selectedAttemptId?: number) => {
    setBusy(true);
    setError(null);
    try {
      const results = await getStudentExamResults(classId, examId);
      if (results.length === 0) throw new Error('Bạn chưa nộp bài kiểm tra này.');
      setHistory(current => ({ ...current, [examId]: results }));
      setLoadedHistory(current => ({ ...current, [examId]: true }));
      const selectedResult = results.find(item => item.attemptId === selectedAttemptId) ?? results[0];
      navigate(`/student/classes/${classId}/exams/${examId}/attempts/${selectedResult.attemptId}/result`);
    } catch (cause) {
      setError(classroomError(cause));
    } finally {
      setBusy(false);
    }
  };

  const completedExams = exams.filter(exam => (history[exam.id] ?? []).length > 0).length;
  const totalAttempts = Object.values(history).reduce((sum, items) => sum + items.length, 0);
  const reviewQuestions = Array.from(
    new Map(
      exams.flatMap(exam =>
        (history[exam.id] ?? []).flatMap(item =>
          item.feedback
            .filter(feedback => !feedback.isCorrect)
            .map(feedback => [feedback.questionId, { ...feedback, examTitle: exam.title }] as const),
        ),
      ),
    ).values(),
  );
  const reviewPageCount = Math.ceil(reviewQuestions.length / REVIEW_PAGE_SIZE);
  const currentReviewPage = Math.min(reviewPage, Math.max(0, reviewPageCount - 1));
  const visibleReviewQuestions = reviewQuestions.slice(currentReviewPage * REVIEW_PAGE_SIZE, (currentReviewPage + 1) * REVIEW_PAGE_SIZE);
  const recentResults = exams
    .flatMap(exam => (history[exam.id] ?? []).map(item => ({ ...item, examTitle: exam.title })))
    .sort((left, right) => Date.parse(right.submittedAt) - Date.parse(left.submittedAt))
    .slice(0, 2);

  return (
    <>
      <div className="exam-space-stats" aria-label="Tổng quan bài kiểm tra">
        <div>
          <span>Đề trong lớp</span>
          <strong>{exams.length}</strong>
          <small>Được giáo viên giao</small>
        </div>
        <div>
          <span>Đã hoàn thành</span>
          <strong>{completedExams}</strong>
          <small>Bài đã có lượt nộp</small>
        </div>
        <div>
          <span>Tổng lượt làm</span>
          <strong>{totalAttempts}</strong>
          <small>Có thể xem lại kết quả</small>
        </div>
      </div>
      <nav className="classroom-tabs classroom-sub-tabs exam-space-tabs" aria-label="Bài kiểm tra và ôn tập">
        <button type="button" className={view === 'exams' ? 'active' : ''} onClick={() => setView('exams')}>
          Bài kiểm tra <span>{exams.length}</span>
        </button>
        <button type="button" className={view === 'review' ? 'active' : ''} onClick={() => setView('review')}>
          Gợi ý ôn tập <span>{reviewQuestions.length}</span>
        </button>
        <button type="button" className={view === 'history' ? 'active' : ''} onClick={() => setView('history')}>
          Lịch sử học tập
        </button>
      </nav>
      {view === 'exams' && (
        <section className="classroom-panel">
          <div className="classroom-section-heading">
            <div>
              <h2>Bài kiểm tra</h2>
              <p>Chọn bài kiểm tra để bắt đầu. Kết quả được lưu sau khi nộp bài.</p>
            </div>
            <span className="classroom-count-badge">{exams.length} bài</span>
          </div>
          {error && (
            <p className="classroom-error" role="alert">
              {error}
            </p>
          )}
          {loading && <p>Đang tải bài kiểm tra...</p>}
          {!loading && exams.length === 0 && <p>Giáo viên chưa mở bài kiểm tra.</p>}
          {recentResults.length > 0 && (
            <div className="classroom-results-feed">
              <strong>Kết quả gần đây</strong>
              {recentResults.map(item => (
                <span key={item.attemptId}>
                  {item.examTitle}: <b>{item.score === null ? 'Chưa công bố' : `${item.score}%`}</b> ·{' '}
                  {new Date(item.submittedAt).toLocaleDateString('vi-VN')}
                </span>
              ))}
            </div>
          )}
          <div className="exam-list-grid">
            {exams.map(exam => {
              const lesson = lessons.find(item => item.id === exam.materialId);
              const attempts = history[exam.id] ?? [];
              const hasSubmitted = attempts.length > 0;
              const showDeadlineWarning = loadedHistory[exam.id] === true && !hasSubmitted;
              const opensAtTime = exam.opensAt ? Date.parse(exam.opensAt) : NaN;
              const closesAtTime = exam.closesAt ? Date.parse(exam.closesAt) : NaN;
              const expired = Number.isFinite(closesAtTime) && closesAtTime <= now;
              const closingSoon =
                !expired &&
                Number.isFinite(closesAtTime) &&
                closesAtTime - now <= 24 * 60 * 60 * 1000 &&
                exam.status !== 'CLOSED' &&
                exam.status !== 'ARCHIVED';
              const warnExpired = expired && showDeadlineWarning;
              const warnClosingSoon = closingSoon && showDeadlineWarning;
              const canStart =
                (exam.status === 'ACTIVE' || exam.status === 'AVAILABLE') &&
                !expired &&
                (!Number.isFinite(opensAtTime) || opensAtTime <= now);
              const isResuming = exam.inProgressAttemptId != null;
              const statusLabel: Record<string, string> = {
                ACTIVE: 'Có thể làm',
                AVAILABLE: 'Có thể làm',
                UPCOMING: 'Sắp mở',
                COMPLETED: 'Đã hết lượt',
                CLOSED: 'Đã đóng',
                ARCHIVED: 'Đã lưu trữ',
              };
              return (
                <article className="exam-list-card" key={exam.id}>
                  <div className="exam-list-card-head">
                    <span className="exam-list-symbol" aria-hidden="true">
                      <svg
                        width="20"
                        height="20"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        strokeWidth="2.2"
                        strokeLinecap="round"
                        strokeLinejoin="round"
                      >
                        <path d="M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2" />
                        <rect x="9" y="3" width="6" height="4" rx="1" />
                        <path d="m9 14 2 2 4-4" />
                      </svg>
                    </span>
                    <span
                      className={`exam-list-status${warnExpired ? ' is-expired' : warnClosingSoon ? ' is-urgent' : hasSubmitted ? ' is-submitted' : canStart ? ' is-open' : ''}`}
                    >
                      {isResuming
                        ? 'Đang làm'
                        : hasSubmitted
                          ? 'Đã nộp'
                          : warnExpired
                            ? 'Đã hết hạn'
                            : warnClosingSoon
                              ? 'Sắp hết hạn'
                              : (statusLabel[exam.status] ?? exam.status)}
                    </span>
                  </div>
                  <div className="exam-list-card-body">
                    <small>{lesson ? `BÀI HỌC · ${lesson.title}` : 'KIỂM TRA CHUNG'}</small>
                    <h3>{exam.title}</h3>
                    <div className="exam-list-metadata">
                      <span>{exam.questionCount} câu hỏi</span>
                      <span>{exam.durationMinutes} phút</span>
                      <span>Đạt từ {exam.passScore}%</span>
                      <span>Số lượt: {exam.maxAttempts == null ? 'Không giới hạn' : exam.maxAttempts}</span>
                    </div>
                    <div className="exam-list-schedule">
                      <span>Mở: {exam.opensAt ? new Date(exam.opensAt).toLocaleString('vi-VN') : 'Chưa quy định'}</span>
                      <span className={warnExpired ? 'is-expired' : warnClosingSoon ? 'is-urgent' : ''}>
                        Đóng: {exam.closesAt ? new Date(exam.closesAt).toLocaleString('vi-VN') : 'Chưa quy định'}
                        {warnExpired ? ' · Quá hạn' : warnClosingSoon ? ' · Sắp hết hạn' : ''}
                      </span>
                    </div>
                    <p>
                      {isResuming ? 'Bạn có một lượt đang làm · ' : ''}
                      {attempts.length} lượt đã nộp
                      {exam.remainingAttempts != null ? ` · còn ${exam.remainingAttempts} lượt` : ' · không giới hạn lượt còn lại'}
                    </p>
                  </div>
                  <div className="exam-list-actions">
                    <button
                      type="button"
                      disabled={busy || !canStart}
                      onClick={() => navigate(`/student/classes/${classId}/exams/${exam.id}/take`)}
                    >
                      {isResuming ? 'Tiếp tục làm' : 'Làm bài'}
                    </button>
                    {attempts.length > 0 && (
                      <button type="button" className="classroom-secondary-action" disabled={busy} onClick={() => void showResult(exam.id)}>
                        Xem kết quả
                      </button>
                    )}
                  </div>
                </article>
              );
            })}
          </div>
        </section>
      )}

      {view === 'review' && (
        <section className="classroom-panel">
          {reviewQuizOpen && reviewQuestions.length > 0 ? (
            <StudentReviewQuiz questions={reviewQuestions} onExit={() => setReviewQuizOpen(false)} />
          ) : (
            <>
              <div className="classroom-section-heading">
                <div>
                  <h2>Ôn lại phần cần cải thiện</h2>
                  <p>Câu hỏi được gợi ý từ các đáp án sai trong bài kiểm tra đã nộp.</p>
                </div>
                <span className="classroom-count-badge">{reviewQuestions.length} câu</span>
              </div>
              {reviewQuestions.length === 0 && <p>Chưa có câu sai để ôn. Hãy làm bài kiểm tra để nhận gợi ý phù hợp.</p>}
              {reviewQuestions.length > 0 && (
                <div className="review-list-intro">
                  <button type="button" onClick={() => setReviewQuizOpen(true)}>
                    Làm bài với {reviewQuestions.length} câu cần ôn →
                  </button>
                </div>
              )}
              {visibleReviewQuestions.map((question, index) => (
                <div className="classroom-practice-card" key={question.questionId}>
                  <div className="classroom-practice-top">
                    <span>
                      Câu {currentReviewPage * REVIEW_PAGE_SIZE + index + 1} · {question.examTitle}
                    </span>
                    <span>Cần ôn</span>
                  </div>
                  <strong>{question.question}</strong>
                  <p>{question.selected ? `Bạn đã chọn: ${question.selected}` : 'Bạn đã bỏ trống câu này trong bài kiểm tra.'}</p>
                </div>
              ))}
              {reviewPageCount > 1 && (
                <nav className="review-pagination" aria-label="Phân trang câu cần ôn">
                  <button type="button" disabled={currentReviewPage === 0} onClick={() => setReviewPage(currentReviewPage - 1)}>
                    Trước
                  </button>
                  <span>
                    Trang {currentReviewPage + 1}/{reviewPageCount}
                  </span>
                  <button
                    type="button"
                    disabled={currentReviewPage + 1 >= reviewPageCount}
                    onClick={() => setReviewPage(currentReviewPage + 1)}
                  >
                    Sau
                  </button>
                </nav>
              )}
            </>
          )}
        </section>
      )}
      {view === 'history' && (
        <section className="classroom-panel">
          <div className="classroom-section-heading">
            <div>
              <h2>Lịch sử học tập</h2>
              <p>Điểm và các lượt nộp bài kiểm tra trong lớp.</p>
            </div>
          </div>
          <div className="classroom-history-stats">
            <span>
              {completedExams}/{exams.length} bài kiểm tra đã làm
            </span>
            <span>{totalAttempts} lần nộp bài</span>
          </div>
          {exams.flatMap(exam =>
            (history[exam.id] ?? []).map((item, index) => (
              <div className="classroom-row" key={`${exam.id}-${item.attemptId}`}>
                <div>
                  <strong>
                    {exam.title} · Lần {(history[exam.id] ?? []).length - index}
                  </strong>
                  <small>Nộp lúc {new Date(item.submittedAt).toLocaleString('vi-VN')}</small>
                </div>
                <div className="classroom-member-actions">
                  <span className={`classroom-status${item.passed ? ' is-complete' : ''}`}>
                    {item.score === null ? 'Chưa công bố' : `${item.score}%`}
                  </span>
                  <button type="button" className="classroom-secondary-action" onClick={() => void showResult(exam.id, item.attemptId)}>
                    Xem bài
                  </button>
                </div>
              </div>
            )),
          )}
        </section>
      )}
    </>
  );
};

export default StudentClassExams;
