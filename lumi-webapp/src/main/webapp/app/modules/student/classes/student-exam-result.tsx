import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router';
import { classroomError } from 'app/shared/util/classroom-api';
import { getStudentExamResult, getStudentExams, type ExamResult } from 'app/shared/util/class-exam-api';
import '../classroom.scss';
import './student-exam-pages.scss';

export const StudentExamResult: React.FC = () => {
  const { id, examId: examIdParam, attemptId: attemptIdParam } = useParams();
  const classId = Number(id);
  const examId = Number(examIdParam);
  const attemptId = Number(attemptIdParam);
  const navigate = useNavigate();
  const [result, setResult] = useState<ExamResult | null>(null);
  const [examTitle, setExamTitle] = useState('Bài kiểm tra');
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (![classId, examId, attemptId].every(value => Number.isSafeInteger(value) && value > 0)) {
      setError('Đường dẫn kết quả không hợp lệ.');
      return;
    }
    let active = true;
    void getStudentExamResult(classId, examId, attemptId)
      .then(data => active && setResult(data))
      .catch(cause => active && setError(classroomError(cause)));
    void getStudentExams(classId)
      .then(exams => {
        if (active) setExamTitle(exams.find(exam => exam.id === examId)?.title ?? 'Bài kiểm tra');
      })
      .catch(() => {});
    return () => {
      active = false;
    };
  }, [classId, examId, attemptId]);

  const correct = result?.feedback.filter(item => item.isCorrect).length ?? 0;
  const incorrect = result?.feedback.filter(item => !item.isCorrect && item.selected !== null).length ?? 0;
  const skipped = result?.feedback.filter(item => item.selected === null).length ?? 0;
  const answersReleased = result !== null && result.feedback.length > 0;

  return (
    <main className="classroom-page exam-space exam-result-page">
      <button type="button" className="classroom-back" onClick={() => navigate(`/student/classes/${classId}/exams`)}>
        ← Danh sách bài kiểm tra
      </button>
      {error && (
        <p className="classroom-error" role="alert">
          {error}
        </p>
      )}
      {!result && !error && <p className="exam-space-loading">Đang tải kết quả...</p>}
      {result && (
        <>
          <header className="exam-result-hero">
            <div>
              <span className="exam-space-kicker">KẾT QUẢ BÀI LÀM</span>
              <h1>{examTitle}</h1>
              <p>Nộp lúc {new Date(result.submittedAt).toLocaleString('vi-VN')}</p>
              {(result.score === null || result.passed) && (
                <span className={`exam-result-state${result.score !== null && result.passed ? ' is-passed' : ''}`}>
                  {result.score === null ? 'Chờ công bố điểm' : 'Đã đạt'}
                </span>
              )}
            </div>
            <div className="exam-score-orb" aria-label={result.score === null ? 'Điểm chưa công bố' : `Điểm ${result.score}%`}>
              <strong>{result.score === null ? '—' : `${result.score}%`}</strong>
              <span>{result.score === null ? 'Chờ công bố' : 'Điểm của bạn'}</span>
            </div>
          </header>

          {result.score === null && (
            <div className="exam-release-note">
              Giáo viên sẽ công bố điểm lúc {new Date(result.resultAvailableAt).toLocaleString('vi-VN')}.
            </div>
          )}

          <section className="exam-answer-section" aria-labelledby="exam-answer-heading">
            <div className="exam-answer-heading">
              <div>
                <span className="exam-space-kicker">NHÌN LẠI BÀI LÀM</span>
                <h2 id="exam-answer-heading">Đáp án & lời giải</h2>
                <p>Mỗi câu cho biết lựa chọn của bạn và lời giải khi giáo viên đã công bố.</p>
              </div>
              {answersReleased && (
                <div className="exam-answer-counts">
                  <span>
                    <b>{correct}</b> đúng
                  </span>
                  <span>
                    <b>{incorrect}</b> sai
                  </span>
                  <span>
                    <b>{skipped}</b> bỏ trống
                  </span>
                </div>
              )}
            </div>

            {!answersReleased && (
              <div className="exam-answer-locked">
                <span aria-hidden="true">◷</span>
                <h3>Đáp án chưa được công bố</h3>
                <p>
                  {result.answersAvailableAt
                    ? `Bạn có thể xem sau ${new Date(result.answersAvailableAt).toLocaleString('vi-VN')}.`
                    : 'Giáo viên chưa mở phần đáp án cho bài kiểm tra này.'}
                </p>
              </div>
            )}

            {result.feedback.map((item, index) => (
              <article className={`exam-answer-card${item.isCorrect ? ' is-correct' : ' is-incorrect'}`} key={item.questionId}>
                <div className="exam-answer-card-top">
                  <span>CÂU {String(index + 1).padStart(2, '0')}</span>
                  <span>{item.isCorrect ? '✓ Chính xác' : item.selected === null ? '○ Bỏ trống' : '× Chưa đúng'}</span>
                </div>
                <h3>{item.question}</h3>
                <div className="exam-answer-comparison">
                  <div>
                    <small>Bạn đã chọn</small>
                    <strong>{item.selected ?? 'Chưa trả lời'}</strong>
                  </div>
                  <div>
                    <small>Đáp án đúng</small>
                    <strong>{item.correct}</strong>
                  </div>
                </div>
                {item.explanation && (
                  <p className="exam-answer-explanation">
                    <b>Giải thích:</b> {item.explanation}
                  </p>
                )}
              </article>
            ))}
          </section>
          <button type="button" className="exam-result-back" onClick={() => navigate(`/student/classes/${classId}/exams`)}>
            Xem các bài kiểm tra khác →
          </button>
        </>
      )}
    </main>
  );
};

export default StudentExamResult;
