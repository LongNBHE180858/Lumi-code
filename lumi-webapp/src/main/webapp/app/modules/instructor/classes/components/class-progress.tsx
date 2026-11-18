import React, { useEffect, useState } from 'react';
import { classroomError, getClassLearningProgress, type ClassLearningProgress } from 'app/shared/util/classroom-api';
import { getClassProgress, type ClassProgress as ProgressData } from 'app/shared/util/class-exam-api';
import { useAppSelector } from 'app/config/store';
import InstructorPagination from './instructor-pagination';

const STUDENTS_PER_PAGE = 8;
const MISTAKES_PER_BATCH = 4;
type StudentProgress = ProgressData['students'][number];

const MistakeList: React.FC<{ student: StudentProgress }> = ({ student }) => {
  const [examFilter, setExamFilter] = useState('all');
  const [visibleCount, setVisibleCount] = useState(MISTAKES_PER_BATCH);
  const exams = Array.from(new Map(student.details.map(mistake => [mistake.examId, mistake.examTitle])).entries());
  const filteredDetails = examFilter === 'all' ? student.details : student.details.filter(mistake => String(mistake.examId) === examFilter);
  const shownDetails = filteredDetails.slice(0, visibleCount);
  const remaining = filteredDetails.length - shownDetails.length;
  return (
    <div className="classroom-mistake-review">
      {student.details.length === 0 ? (
        <p>Chưa có câu trả lời sai được ghi nhận.</p>
      ) : (
        <>
          <div className="classroom-mistake-review-heading">
            <strong>Câu cần xem lại</strong>
            <span>
              Đang xem {shownDetails.length}/{filteredDetails.length} câu
            </span>
          </div>
          {exams.length > 1 && (
            <div className="classroom-mistake-filter">
              <label htmlFor={`mistake-exam-${student.studentId}`}>Lọc theo bài kiểm tra</label>
              <select
                id={`mistake-exam-${student.studentId}`}
                value={examFilter}
                onChange={event => {
                  setExamFilter(event.target.value);
                  setVisibleCount(MISTAKES_PER_BATCH);
                }}
              >
                <option value="all">Tất cả bài kiểm tra</option>
                {exams.map(([examId, examTitle]) => (
                  <option value={examId} key={examId}>
                    {examTitle}
                  </option>
                ))}
              </select>
            </div>
          )}
          <div className="classroom-mistake-grid">
            {shownDetails.map((mistake, index) => (
              <article className="classroom-feedback-item" key={`${mistake.examId}-${index}`}>
                <div className="classroom-feedback-item-heading">
                  <span>Câu {index + 1}</span>
                  <strong>{mistake.examTitle}</strong>
                </div>
                <p className="classroom-feedback-question">{mistake.question}</p>
                <div className="classroom-feedback-answers">
                  <div>
                    <small>Học sinh chọn</small>
                    <strong>{mistake.selected ?? 'Chưa trả lời'}</strong>
                  </div>
                  <div>
                    <small>Đáp án đúng</small>
                    <strong>{mistake.correct}</strong>
                  </div>
                </div>
                {mistake.explanation && (
                  <p className="classroom-feedback-explanation">
                    <strong>Giải thích:</strong> {mistake.explanation}
                  </p>
                )}
              </article>
            ))}
          </div>
          {(remaining > 0 || visibleCount > MISTAKES_PER_BATCH) && (
            <div className="classroom-mistake-actions">
              {remaining > 0 && (
                <button type="button" onClick={() => setVisibleCount(count => count + MISTAKES_PER_BATCH)}>
                  Xem thêm {Math.min(MISTAKES_PER_BATCH, remaining)} câu
                </button>
              )}
              {visibleCount > MISTAKES_PER_BATCH && (
                <button type="button" className="is-secondary" onClick={() => setVisibleCount(MISTAKES_PER_BATCH)}>
                  Thu gọn
                </button>
              )}
            </div>
          )}
        </>
      )}
    </div>
  );
};

export const ClassProgress: React.FC<{ classId: number }> = ({ classId }) => {
  const [progress, setProgress] = useState<ProgressData | null>(null);
  const [learning, setLearning] = useState<ClassLearningProgress | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [notes, setNotes] = useState<Record<number, string>>({});
  const [savedStudentId, setSavedStudentId] = useState<number | null>(null);
  const [search, setSearch] = useState('');
  const [onlyNeedsSupport, setOnlyNeedsSupport] = useState(false);
  const [studentPage, setStudentPage] = useState(1);
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
    void getClassLearningProgress(classId)
      .then(data => active && setLearning(data))
      .catch(cause => active && setError(classroomError(cause)));
    return () => {
      active = false;
    };
  }, [classId]);

  const completionFor = (student: ProgressData['students'][number]): number => {
    const extra = learning?.students.find(item => item.studentId === student.studentId);
    const total = student.totalExams + (extra?.totalAssignments ?? 0);
    const done = student.completedExams + (extra?.submittedAssignments ?? 0);
    return total ? done / total : 0;
  };
  const completionRate = progress?.students.length
    ? Math.round((progress.students.reduce((sum, student) => sum + completionFor(student), 0) / progress.students.length) * 100)
    : 0;
  const supportReasons = (student: ProgressData['students'][number]): string[] => {
    const extra = learning?.students.find(item => item.studentId === student.studentId);
    const assigned = student.totalExams + (extra?.totalAssignments ?? 0);
    const reasons: string[] = [];
    if (student.completedExams > 0 && student.averageScore < 60) reasons.push('Điểm kiểm tra trung bình dưới 60%');
    if (assigned > 0 && completionFor(student) < 0.5) reasons.push('Hoàn thành dưới 50% bài được giao');
    if (student.mistakes >= 3) reasons.push('Có từ 3 câu sai trong các lượt gần nhất');
    return reasons;
  };
  const needsSupport = (progress?.students ?? []).filter(student => supportReasons(student).length > 0);
  const visibleStudents = (progress?.students ?? []).filter(
    student =>
      student.name.toLocaleLowerCase('vi-VN').includes(search.trim().toLocaleLowerCase('vi-VN')) &&
      (!onlyNeedsSupport || needsSupport.some(item => item.studentId === student.studentId)),
  );
  const studentPages = Math.max(1, Math.ceil(visibleStudents.length / STUDENTS_PER_PAGE));
  const currentStudentPage = Math.min(studentPage, studentPages);
  const firstStudentIndex = (currentStudentPage - 1) * STUDENTS_PER_PAGE;
  const pagedStudents = visibleStudents.slice(firstStudentIndex, firstStudentIndex + STUDENTS_PER_PAGE);

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
          <div className="classroom-metric-grid">
            <div className="classroom-metric-card">
              <span>Điểm trung bình</span>
              <strong>{progress.classAverageScore}%</strong>
              <small>Các bài kiểm tra đã nộp</small>
            </div>
            <div className="classroom-metric-card">
              <span>Tỷ lệ hoàn thành</span>
              <strong>{completionRate}%</strong>
              <small>Bài tập và bài kiểm tra</small>
            </div>
            <div className="classroom-metric-card">
              <span>Cần hỗ trợ</span>
              <strong>{needsSupport.length}</strong>
              <small>Học sinh có ít nhất một dấu hiệu cần hỗ trợ</small>
            </div>
          </div>
          <p className="classroom-soft-note">
            Cần hỗ trợ khi điểm kiểm tra trung bình dưới 60% (đã nộp bài), hoàn thành dưới 50% bài tập và bài kiểm tra được giao, hoặc có từ
            3 câu sai trong các lượt gần nhất.
          </p>
          {learning && (
            <p className="classroom-soft-note">
              Bài tập đã nộp: {learning.students.reduce((sum, student) => sum + student.submittedAssignments, 0)}/
              {learning.students.reduce((sum, student) => sum + student.totalAssignments, 0)}
            </p>
          )}
          <div className="classroom-progress-filters">
            <label htmlFor="progress-search">Tìm học sinh</label>
            <input
              id="progress-search"
              value={search}
              onChange={event => {
                setSearch(event.target.value);
                setStudentPage(1);
              }}
              placeholder="Nhập tên học sinh..."
            />
            <label className="classroom-filter-check">
              <input
                type="checkbox"
                checked={onlyNeedsSupport}
                onChange={event => {
                  setOnlyNeedsSupport(event.target.checked);
                  setStudentPage(1);
                }}
              />{' '}
              Chỉ xem học sinh cần hỗ trợ
            </label>
          </div>
          {progress.students.length === 0 && <p>Chưa có học sinh trong lớp.</p>}
          {progress.students.length > 0 && visibleStudents.length === 0 && <p>Không tìm thấy học sinh phù hợp.</p>}
          {visibleStudents.length > 0 && (
            <p className="classroom-progress-count">
              Hiển thị {firstStudentIndex + 1}–{Math.min(firstStudentIndex + STUDENTS_PER_PAGE, visibleStudents.length)} /{' '}
              {visibleStudents.length} học sinh
            </p>
          )}
          {pagedStudents.map(student => (
            <div className="classroom-progress-student" key={student.studentId}>
              <div className="classroom-row">
                <div>
                  <strong>{student.name}</strong>
                  <small>
                    Kiểm tra: {student.completedExams}/{student.totalExams} bài
                  </small>
                  {learning?.students.find(item => item.studentId === student.studentId) && (
                    <small>
                      Bài tập: {learning.students.find(item => item.studentId === student.studentId)?.submittedAssignments}/
                      {learning.students.find(item => item.studentId === student.studentId)?.totalAssignments}
                    </small>
                  )}
                  <div className="classroom-progress-track">
                    <span style={{ width: `${Math.round(completionFor(student) * 100)}%` }} />
                  </div>
                </div>
                <div>
                  <strong>{student.averageScore}%</strong>
                  <small>{student.mistakes} câu sai</small>
                </div>
              </div>
              <details className="classroom-progress-details">
                <summary>Xem câu sai và ghi nhận xét ({student.mistakes})</summary>
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
                <MistakeList student={student} />
              </details>
            </div>
          ))}
          <InstructorPagination
            page={currentStudentPage - 1}
            totalPages={studentPages}
            totalItems={visibleStudents.length}
            itemLabel="học sinh"
            label="Trang học sinh"
            onPageChange={next => setStudentPage(next + 1)}
          />
        </>
      )}
    </section>
  );
};

export default ClassProgress;
