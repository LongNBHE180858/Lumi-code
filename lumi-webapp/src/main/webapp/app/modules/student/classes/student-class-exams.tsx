import React, { useEffect, useState } from 'react';
import { classroomError, type ClassroomMaterial } from 'app/shared/util/classroom-api';
import {
  getStudentExams,
  getStudentExamResults,
  startClassExam,
  submitClassExam,
  type ExamAttempt,
  type ExamResult,
  type ExamSummary,
} from 'app/shared/util/class-exam-api';

export const StudentClassExams: React.FC<{ classId: number; lessons: ClassroomMaterial[]; completedLessonIds?: number[] }> = ({
  classId,
  lessons,
  completedLessonIds = [],
}) => {
  const [exams, setExams] = useState<ExamSummary[]>([]);
  const [history, setHistory] = useState<Record<number, ExamResult[]>>({});
  const [attempt, setAttempt] = useState<ExamAttempt | null>(null);
  const [selected, setSelected] = useState<Record<number, number>>({});
  const [result, setResult] = useState<ExamResult | null>(null);
  const [remainingSeconds, setRemainingSeconds] = useState(0);
  const [busy, setBusy] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let active = true;
    setLoading(true);
    setError(null);
    setExams([]);
    setHistory({});
    setAttempt(null);
    setResult(null);
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
      })
      .catch(cause => active && setError(classroomError(cause)))
      .finally(() => active && setLoading(false));
    return () => {
      active = false;
    };
  }, [classId]);

  useEffect(() => {
    if (!attempt) return;
    const updateClock = () => setRemainingSeconds(Math.max(0, Math.ceil((Date.parse(attempt.deadlineAt) - Date.now()) / 1000)));
    updateClock();
    const timer = window.setInterval(updateClock, 1000);
    return () => window.clearInterval(timer);
  }, [attempt]);

  const start = async (examId: number) => {
    setBusy(true);
    setError(null);
    setResult(null);
    try {
      setAttempt(await startClassExam(classId, examId));
      setSelected({});
    } catch (cause) {
      setError(classroomError(cause));
    } finally {
      setBusy(false);
    }
  };

  const submit = async (event: React.FormEvent) => {
    event.preventDefault();
    if (!attempt || remainingSeconds <= 0) return;
    const unanswered = attempt.questions.length - Object.keys(selected).length;
    if (unanswered > 0 && !window.confirm(`Bạn còn ${unanswered} câu chưa trả lời. Vẫn nộp bài?`)) return;
    setBusy(true);
    setError(null);
    try {
      const submitted = await submitClassExam(
        classId,
        attempt.examId,
        attempt.attemptId,
        attempt.questions.map(question => ({ questionId: question.id, optionId: selected[question.id] ?? null })),
      );
      setResult(submitted);
      setHistory(current => ({ ...current, [attempt.examId]: [submitted, ...(current[attempt.examId] ?? [])] }));
      setAttempt(null);
    } catch (cause) {
      setError(classroomError(cause));
    } finally {
      setBusy(false);
    }
  };

  const showResult = async (examId: number, selectedAttemptId?: number) => {
    setBusy(true);
    setError(null);
    try {
      const results = history[examId] ?? (await getStudentExamResults(classId, examId));
      if (results.length === 0) throw new Error('Bạn chưa nộp bài kiểm tra này.');
      setHistory(current => ({ ...current, [examId]: results }));
      setAttempt(null);
      setResult(results.find(item => item.attemptId === selectedAttemptId) ?? results[0]);
    } catch (cause) {
      setError(classroomError(cause));
    } finally {
      setBusy(false);
    }
  };

  const completedExams = exams.filter(exam => (history[exam.id] ?? []).length > 0).length;
  const totalAttempts = Object.values(history).reduce((sum, items) => sum + items.length, 0);
  const timerText = `${String(Math.floor(remainingSeconds / 3600)).padStart(2, '0')}:${String(Math.floor((remainingSeconds % 3600) / 60)).padStart(2, '0')}:${String(remainingSeconds % 60).padStart(2, '0')}`;

  return (
    <>
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
        {exams.map(exam => {
          const lesson = lessons.find(item => item.id === exam.materialId);
          const attempts = history[exam.id] ?? [];
          return (
            <div className="classroom-row" key={exam.id}>
              <div>
                <strong>{exam.title}</strong>
                <small>{lesson ? `Bài học: ${lesson.title}` : 'Bài kiểm tra của lớp'}</small>
                <small>
                  {exam.questionCount} câu · {exam.durationMinutes} phút · Điểm đạt {exam.passScore}% · {attempts.length} lần đã nộp
                </small>
              </div>
              <div className="classroom-member-actions">
                <button type="button" disabled={busy || !!attempt || exam.status !== 'ACTIVE'} onClick={() => void start(exam.id)}>
                  Làm bài
                </button>
                {attempts.length > 0 && (
                  <button type="button" className="classroom-secondary-action" disabled={busy} onClick={() => void showResult(exam.id)}>
                    Xem kết quả
                  </button>
                )}
              </div>
            </div>
          );
        })}
      </section>

      {attempt && (
        <form className="classroom-panel classroom-exam-form" onSubmit={event => void submit(event)}>
          <div className="classroom-exam-toolbar">
            <div>
              <h2>Đang làm bài</h2>
              <p>
                Đã trả lời {Object.keys(selected).length}/{attempt.questions.length} câu · Hạn nộp{' '}
                {new Date(attempt.deadlineAt).toLocaleString('vi-VN')}
              </p>
            </div>
            <div
              className={`classroom-exam-timer${remainingSeconds <= 60 ? ' is-urgent' : ''}`}
              role="timer"
              aria-label="Thời gian còn lại"
            >
              <small>Còn lại</small>
              <strong>{timerText}</strong>
            </div>
          </div>
          {attempt.questions.map((question, index) => (
            <fieldset className="classroom-exam-question" key={question.id} disabled={busy || remainingSeconds <= 0}>
              <legend>
                Câu {index + 1}: {question.content} ({question.points} điểm)
              </legend>
              {question.options.map(option => (
                <label className={`classroom-choice${selected[question.id] === option.id ? ' is-selected' : ''}`} key={option.id}>
                  <input
                    type="radio"
                    name={`question-${question.id}`}
                    checked={selected[question.id] === option.id}
                    onChange={() => setSelected(current => ({ ...current, [question.id]: option.id }))}
                  />
                  {option.content}
                </label>
              ))}
            </fieldset>
          ))}
          {remainingSeconds <= 0 && <p className="classroom-error">Đã hết thời gian. Bài chưa nộp cần được giáo viên xử lý.</p>}
          <button type="submit" disabled={busy || remainingSeconds <= 0}>
            {busy ? 'Đang nộp...' : 'Nộp bài'}
          </button>
        </form>
      )}

      {result && (
        <section className="classroom-panel classroom-exam-result" role="status">
          <div className="classroom-section-heading">
            <div>
              <h2>Kết quả: {result.score}%</h2>
              <p>
                {result.passed ? 'Đạt' : 'Chưa đạt'} · Nộp lúc {new Date(result.submittedAt).toLocaleString('vi-VN')}
              </p>
            </div>
            <button type="button" className="classroom-outline-button" onClick={() => setResult(null)}>
              Đóng
            </button>
          </div>
          {result.feedback.map((item, index) => (
            <div key={item.questionId} className="classroom-feedback-item">
              <strong>
                Câu {index + 1}: {item.isCorrect ? 'Đúng' : 'Sai'}
              </strong>
              <p>{item.question}</p>
              <p>
                Bạn chọn: {item.selected ?? 'Chưa trả lời'} · Đáp án đúng: {item.correct}
              </p>
              {item.explanation && <p>Giải thích: {item.explanation}</p>}
            </div>
          ))}
        </section>
      )}

      <section className="classroom-panel">
        <div className="classroom-section-heading">
          <div>
            <h2>Lịch sử học tập</h2>
            <p>Bài học đánh dấu hoàn thành được lưu trên trình duyệt này; điểm và lần nộp lấy từ hệ thống.</p>
          </div>
        </div>
        <div className="classroom-history-stats">
          <span>
            {completedLessonIds.length}/{lessons.length} bài học đã hoàn thành
          </span>
          <span>
            {completedExams}/{exams.length} bài kiểm tra đã làm
          </span>
          <span>{totalAttempts} lần nộp bài</span>
        </div>
        {lessons.map(lesson => (
          <div className="classroom-row" key={lesson.id}>
            <div>
              <strong>{lesson.title}</strong>
              <small>{lesson.fileType}</small>
            </div>
            <span className={`classroom-status${completedLessonIds.includes(lesson.id) ? ' is-complete' : ''}`}>
              {completedLessonIds.includes(lesson.id) ? 'Đã hoàn thành' : 'Chưa hoàn thành'}
            </span>
          </div>
        ))}
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
                <span className={`classroom-status${item.passed ? ' is-complete' : ''}`}>{item.score}%</span>
                <button type="button" className="classroom-secondary-action" onClick={() => void showResult(exam.id, item.attemptId)}>
                  Xem bài
                </button>
              </div>
            </div>
          )),
        )}
      </section>
    </>
  );
};

export default StudentClassExams;
