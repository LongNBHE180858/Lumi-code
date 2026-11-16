import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router';
import { classroomError, getStudentClassroom, openClassMaterial, type Classroom } from 'app/shared/util/classroom-api';
import '../classroom.scss';
import StudentClassExams from './student-class-exams';
import { useAppSelector } from 'app/config/store';

export const StudentClassroom: React.FC = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  const [room, setRoom] = useState<Classroom | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [completedLessonIds, setCompletedLessonIds] = useState<number[]>([]);
  const accountId = useAppSelector(state => state.authentication.account.id);
  const completionKey = `classroom-lessons:${accountId}:${id}`;

  useEffect(() => {
    try {
      const saved = JSON.parse(window.localStorage.getItem(completionKey) ?? '[]');
      setCompletedLessonIds(Array.isArray(saved) ? saved.filter((value): value is number => Number.isSafeInteger(value)) : []);
    } catch {
      setCompletedLessonIds([]);
    }
  }, [completionKey]);

  const toggleCompleted = (lessonId: number) => {
    const updated = completedLessonIds.includes(lessonId)
      ? completedLessonIds.filter(value => value !== lessonId)
      : [...completedLessonIds, lessonId];
    setCompletedLessonIds(updated);
    window.localStorage.setItem(completionKey, JSON.stringify(updated));
  };

  const openLesson = async (url: string) => {
    try {
      if (url.startsWith('/api/classes/')) await openClassMaterial(url);
      else window.open(url, '_blank', 'noopener,noreferrer');
    } catch (cause) {
      setError(classroomError(cause));
    }
  };

  useEffect(() => {
    const classId = Number(id);
    if (!Number.isSafeInteger(classId) || classId <= 0) {
      setError('Mã lớp không hợp lệ.');
      return;
    }
    void getStudentClassroom(classId)
      .then(setRoom)
      .catch(cause => setError(classroomError(cause)));
  }, [id]);

  return (
    <main className="classroom-page">
      <button type="button" className="classroom-back" onClick={() => navigate('/student/classes')}>
        ← Lớp học của tôi
      </button>
      {error && (
        <p className="classroom-error" role="alert">
          {error}
        </p>
      )}
      {!room && !error && <p>Đang tải lớp học...</p>}
      {room && (
        <>
          <header className="classroom-header">
            <div>
              <h1>{room.name}</h1>
              <p>{room.description || 'Chưa có mô tả.'}</p>
            </div>
          </header>
          <section className="classroom-panel">
            <h2>Bài tập ({room.assignments.length})</h2>
            {room.assignments.length === 0 && <p>Giáo viên chưa giao bài tập.</p>}
            {room.assignments.map(task => (
              <div className="classroom-row" key={task.id}>
                <div>
                  <strong>{task.title}</strong>
                  <small>{task.instructions || 'Không có hướng dẫn thêm.'}</small>
                </div>
                <span>{task.dueAt ? `Hạn: ${new Date(task.dueAt).toLocaleString('vi-VN')}` : 'Không có hạn'}</span>
              </div>
            ))}
          </section>
          <section className="classroom-panel">
            <h2>Tài liệu bài học ({room.lessons.length})</h2>
            {room.lessons.length === 0 && <p>Chưa có tài liệu bài học.</p>}
            {room.lessons.map((lesson, index) => (
              <div className="classroom-row" key={lesson.id}>
                <div>
                  <strong>
                    {index + 1}. {lesson.title}
                  </strong>
                  <small>{lesson.fileType}</small>
                </div>
                <div className="classroom-member-actions">
                  <button type="button" onClick={() => void openLesson(lesson.fileUrl)}>
                    Mở bài học
                  </button>
                  <button type="button" className="classroom-secondary-action" onClick={() => toggleCompleted(lesson.id)}>
                    {completedLessonIds.includes(lesson.id) ? 'Bỏ hoàn thành' : 'Đánh dấu hoàn thành'}
                  </button>
                </div>
              </div>
            ))}
          </section>
          <StudentClassExams classId={room.id} lessons={room.lessons} completedLessonIds={completedLessonIds} />
        </>
      )}
    </main>
  );
};

export default StudentClassroom;
