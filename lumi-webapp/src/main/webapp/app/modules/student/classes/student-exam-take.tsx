import React, { useEffect, useRef, useState } from 'react';
import { useNavigate, useParams } from 'react-router';
import { classroomError } from 'app/shared/util/classroom-api';
import { getStudentExams, startClassExam, submitClassExam, type ExamAttempt } from 'app/shared/util/class-exam-api';
import '../classroom.scss';
import './student-exam-pages.scss';

const timeLeft = (deadlineAt: string | null): number | null =>
  deadlineAt ? Math.max(0, Math.ceil((new Date(deadlineAt).getTime() - Date.now()) / 1000)) : null;

const formatTime = (seconds: number | null): string => {
  if (seconds === null) return 'Không giới hạn';
  const hours = Math.floor(seconds / 3600);
  const minutes = Math.floor((seconds % 3600) / 60);
  const remaining = seconds % 60;
  return [hours, minutes, remaining]
    .filter((_, index) => hours > 0 || index > 0)
    .map(part => String(part).padStart(2, '0'))
    .join(':');
};

export const StudentExamTake: React.FC = () => {
  const { id, examId: examIdParam } = useParams();
  const classId = Number(id);
  const examId = Number(examIdParam);
  const navigate = useNavigate();
  const [attempt, setAttempt] = useState<ExamAttempt | null>(null);
  const [title, setTitle] = useState('Bài kiểm tra');
  const [assessmentType, setAssessmentType] = useState<'EXAM' | 'PRACTICE'>('EXAM');
  const [selected, setSelected] = useState<Record<number, number>>({});
  const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
  const [remainingSeconds, setRemainingSeconds] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const submittingRef = useRef(false);

  useEffect(() => {
    if (!Number.isSafeInteger(classId) || classId <= 0 || !Number.isSafeInteger(examId) || examId <= 0) {
      setError('Đường dẫn bài kiểm tra không hợp lệ.');
      setLoading(false);
      return;
    }
    let active = true;
    setLoading(true);
    setError(null);
    void startClassExam(classId, examId)
      .then(data => {
        if (!active) return;
        setAttempt(data);
        setCurrentQuestionIndex(0);
        setSelected(
          (data.savedAnswers ?? []).reduce<Record<number, number>>((choices, answer) => {
            if (answer.optionId != null) choices[answer.questionId] = answer.optionId;
            return choices;
          }, {}),
        );
        setRemainingSeconds(timeLeft(data.deadlineAt));
      })
      .catch(cause => active && setError(classroomError(cause)))
      .finally(() => active && setLoading(false));
    void getStudentExams(classId)
      .then(exams => {
        if (active) {
          const exam = exams.find(item => item.id === examId);
          setTitle(exam?.title ?? 'Bài kiểm tra');
          setAssessmentType(exam?.assessmentType === 'PRACTICE' ? 'PRACTICE' : 'EXAM');
        }
      })
      .catch(() => undefined);
    return () => {
      active = false;
    };
  }, [classId, examId]);

  useEffect(() => {
    if (!attempt?.deadlineAt) return;
    const update = () => setRemainingSeconds(timeLeft(attempt.deadlineAt));
    update();
    const interval = window.setInterval(update, 1000);
    return () => window.clearInterval(interval);
  }, [attempt]);

  const submit = async (automatic: boolean) => {
    if (!attempt || submittingRef.current) return;
    if (!automatic && remainingSeconds !== 0) {
      const unanswered = attempt.questions.length - Object.keys(selected).length;
      if (unanswered > 0 && !window.confirm(`Bạn còn ${unanswered} câu chưa trả lời. Vẫn nộp bài?`)) return;
    }
    submittingRef.current = true;
    setBusy(true);
    setError(null);
    try {
      const answers =
        automatic || remainingSeconds === 0
          ? undefined
          : attempt.questions.map(question => ({ questionId: question.id, optionId: selected[question.id] ?? null }));
      const result = await submitClassExam(classId, examId, attempt.attemptId, answers);
      navigate(`/student/classes/${classId}/exams/${examId}/attempts/${result.attemptId}/result`, { replace: true });
    } catch (cause) {
      setError(classroomError(cause));
      submittingRef.current = false;
      setBusy(false);
    }
  };

  useEffect(() => {
    if (attempt && remainingSeconds === 0 && !submittingRef.current) void submit(true);
  }, [attempt, remainingSeconds]);

  const answeredCount = attempt ? attempt.questions.filter(question => selected[question.id] != null).length : 0;
  const currentQuestion = attempt?.questions[currentQuestionIndex];
  const showQuestion = (index: number) => {
    if (!attempt || index < 0 || index >= attempt.questions.length) return;
    setCurrentQuestionIndex(index);
    window.requestAnimationFrame(() => {
      document.getElementById('exam-current-question')?.scrollIntoView?.({ behavior: 'smooth', block: 'start' });
    });
  };

  return (
    <main className="classroom-page exam-space exam-take-page">
      <button type="button" className="classroom-back" onClick={() => navigate(`/student/classes/${classId}/exams`)}>
        ← Danh sách bài kiểm tra
      </button>
      <header className="exam-take-hero">
        <div>
          <span className="exam-space-kicker">{assessmentType === 'PRACTICE' ? 'PHÒNG LUYỆN TẬP' : 'PHÒNG KIỂM TRA'}</span>
          <h1>{title}</h1>
          <p>{attempt ? `${attempt.questions.length} câu hỏi · ${answeredCount} câu đã trả lời` : 'Đang chuẩn bị bài kiểm tra của bạn'}</p>
        </div>
      </header>
      {attempt && (
        <div className="exam-take-sticky-bar">
          <div>
            <small>ĐANG LÀM BÀI</small>
            <strong>
              Câu {currentQuestionIndex + 1}/{attempt.questions.length}
            </strong>
          </div>
          <div
            className={`classroom-exam-timer${remainingSeconds !== null && remainingSeconds <= 60 ? ' is-urgent' : ''}`}
            role="timer"
            aria-label="Thời gian còn lại"
          >
            <small>THỜI GIAN CÒN LẠI</small>
            <strong>{formatTime(remainingSeconds)}</strong>
          </div>
        </div>
      )}
      {loading && <p className="exam-space-loading">Đang mở bài kiểm tra...</p>}
      {error && (
        <p className="classroom-error" role="alert">
          {error}
        </p>
      )}
      {!loading && !attempt && (
        <button type="button" className="exam-take-back-action" onClick={() => navigate(`/student/classes/${classId}/exams`)}>
          Về danh sách bài kiểm tra
        </button>
      )}
      {attempt && (
        <div className="classroom-exam-form">
          <div className="exam-attempt-layout">
            <aside className="exam-attempt-rail" aria-label="Tiến độ câu hỏi">
              <span className="exam-space-kicker">BẢN ĐỒ CÂU HỎI</span>
              <strong>
                {answeredCount}/{attempt.questions.length} đã trả lời
              </strong>
              <div className="exam-attempt-progress">
                <span style={{ width: `${attempt.questions.length ? (answeredCount / attempt.questions.length) * 100 : 0}%` }} />
              </div>
              <div className="exam-question-jump">
                {attempt.questions.map((question, index) => (
                  <button
                    type="button"
                    className={`${selected[question.id] != null ? 'is-answered' : ''}${index === currentQuestionIndex ? ' is-current' : ''}`}
                    key={question.id}
                    aria-label={`Đến câu ${index + 1}`}
                    aria-current={index === currentQuestionIndex ? 'step' : undefined}
                    onClick={() => showQuestion(index)}
                  >
                    {index + 1}
                  </button>
                ))}
              </div>
              <small>Chọn số câu để chuyển nhanh.</small>
            </aside>
            <div className="exam-attempt-questions">
              {currentQuestion && (
                <fieldset
                  id="exam-current-question"
                  className="classroom-exam-question"
                  key={currentQuestion.id}
                  disabled={busy || remainingSeconds === 0}
                >
                  <legend>
                    <span>Câu {currentQuestionIndex + 1}</span> {currentQuestion.content} <small>{currentQuestion.points} điểm</small>
                  </legend>
                  {currentQuestion.options.map(option => (
                    <label
                      className={`classroom-choice${selected[currentQuestion.id] === option.id ? ' is-selected' : ''}`}
                      key={option.id}
                    >
                      <input
                        type="radio"
                        name={`question-${currentQuestion.id}`}
                        checked={selected[currentQuestion.id] === option.id}
                        onChange={() => setSelected(current => ({ ...current, [currentQuestion.id]: option.id }))}
                      />
                      {option.content}
                    </label>
                  ))}
                </fieldset>
              )}
              {remainingSeconds === 0 && <p className="classroom-notice">Đã hết thời gian. Hệ thống đang chốt bài từ đáp án đã lưu.</p>}
              <div className="exam-take-submit-bar">
                <div>
                  <strong>
                    Đã trả lời {answeredCount}/{attempt.questions.length} câu
                  </strong>
                  <small>Chuyển câu không làm mất lựa chọn hiện tại.</small>
                </div>
                <div className="exam-take-navigation">
                  <button
                    type="button"
                    className="is-secondary"
                    disabled={currentQuestionIndex === 0 || busy}
                    onClick={() => showQuestion(currentQuestionIndex - 1)}
                  >
                    Câu trước
                  </button>
                  {currentQuestionIndex < attempt.questions.length - 1 ? (
                    <button type="button" disabled={busy} onClick={() => showQuestion(currentQuestionIndex + 1)}>
                      Câu tiếp →
                    </button>
                  ) : (
                    <button type="button" disabled={busy} onClick={() => void submit(false)}>
                      {busy ? 'Đang nộp bài...' : 'Nộp bài kiểm tra'}
                    </button>
                  )}
                </div>
              </div>
            </div>
          </div>
        </div>
      )}
    </main>
  );
};

export default StudentExamTake;
