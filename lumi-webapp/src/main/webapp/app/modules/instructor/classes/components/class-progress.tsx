import React, { useEffect, useState } from 'react';
import { classroomError } from 'app/shared/util/classroom-api';
import { getClassProgress, type ClassProgress as ProgressData } from 'app/shared/util/class-exam-api';
import { useAppSelector } from 'app/config/store';

export const ClassProgress: React.FC<{ classId: number }> = ({ classId }) => {
  const [progress, setProgress] = useState<ProgressData | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [notes, setNotes] = useState<Record<number, string>>({});
  const [savedStudentId, setSavedStudentId] = useState<number | null>(null);
  const accountId = useAppSelector(state => state.authentication.account.id);
  const notesKey = `classroom-progress-notes:${accountId}:${classId}`;

  useEffect(() => {
    try {
      const saved = JSON.parse(window.localStorage.getItem(notesKey) ?? '{}');
      setNotes(saved && typeof saved === 'object' && !Array.isArray(saved) ? saved : {});
    } catch {
      setNotes({});
    }
  }, [notesKey]);

  useEffect(() => {
    let active = true;
    void getClassProgress(classId)
      .then(data => active && setProgress(data))
      .catch(cause => active && setError(classroomError(cause)));
    return () => {
      active = false;
    };
  }, [classId]);

  return (
    <section className="classroom-panel">
      <div className="classroom-section-heading">
        <div>
          <h2>Kết quả học tập của lớp</h2>
          <p>Theo dõi điểm trung bình, bài đã hoàn thành và các câu trả lời sai của từng học sinh.</p>
        </div>
      </div>
      {error && (
        <p className="classroom-error" role="alert">
          {error}
        </p>
      )}
      {!progress && !error && <p>Đang tải tiến độ...</p>}
      {progress && (
        <>
          <div className="classroom-history-stats">
            <span>{progress.totalExams} bài kiểm tra</span>
            <span>Điểm trung bình lớp: {progress.classAverageScore}%</span>
            <span>{progress.students.length} học sinh</span>
          </div>
          {progress.students.length === 0 && <p>Chưa có học sinh trong lớp.</p>}
          {progress.students.map(student => (
            <div className="classroom-progress-student" key={student.studentId}>
              <div className="classroom-row">
                <div>
                  <strong>{student.name}</strong>
                  <small>
                    Hoàn thành {student.completedExams}/{student.totalExams} bài
                  </small>
                </div>
                <div>
                  <strong>{student.averageScore}%</strong>
                  <small>{student.mistakes} câu sai</small>
                </div>
              </div>
              <details className="classroom-progress-details">
                <summary>Xem câu sai và ghi nhận xét ({student.mistakes})</summary>
                {student.details.length === 0 && <p>Chưa có câu trả lời sai được ghi nhận.</p>}
                {student.details.map((mistake, index) => (
                  <div className="classroom-feedback-item" key={`${mistake.examId}-${index}`}>
                    <strong>{mistake.examTitle}</strong>
                    <p>{mistake.question}</p>
                    <p>
                      Học sinh chọn: {mistake.selected ?? 'Chưa trả lời'} · Đáp án đúng: {mistake.correct}
                    </p>
                    {mistake.explanation && <p>Giải thích: {mistake.explanation}</p>}
                  </div>
                ))}
                <div className="classroom-note-editor">
                  <label htmlFor={`progress-note-${student.studentId}`}>Nhận xét về kết quả học tập</label>
                  <textarea
                    id={`progress-note-${student.studentId}`}
                    value={notes[student.studentId] ?? ''}
                    onChange={event => {
                      setNotes(current => ({ ...current, [student.studentId]: event.target.value }));
                      setSavedStudentId(null);
                    }}
                    placeholder="Ví dụ: Cần ôn lại các câu về phân số..."
                  />
                  <div className="classroom-member-actions">
                    <small>Ghi chú chỉ lưu trên trình duyệt này, chưa gửi cho học sinh.</small>
                    <button
                      type="button"
                      className="classroom-outline-button"
                      onClick={() => {
                        window.localStorage.setItem(notesKey, JSON.stringify(notes));
                        setSavedStudentId(student.studentId);
                      }}
                    >
                      Lưu nhận xét
                    </button>
                  </div>
                  {savedStudentId === student.studentId && <p className="classroom-notice">Đã lưu ghi chú trên trình duyệt này.</p>}
                </div>
              </details>
            </div>
          ))}
        </>
      )}
    </section>
  );
};

export default ClassProgress;
