import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router';
import { classroomError, getStudentClassroom, type Classroom } from 'app/shared/util/classroom-api';
import StudentClassExams from './student-class-exams';
import '../classroom.scss';
import './student-exam-pages.scss';

export const StudentExamHub: React.FC = () => {
  const { id } = useParams();
  const classId = Number(id);
  const navigate = useNavigate();
  const [classroom, setClassroom] = useState<Classroom | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!Number.isSafeInteger(classId) || classId <= 0) {
      setError('Mã lớp không hợp lệ.');
      return;
    }
    let active = true;
    void getStudentClassroom(classId)
      .then(room => active && setClassroom(room))
      .catch(cause => active && setError(classroomError(cause)));
    return () => {
      active = false;
    };
  }, [classId]);

  return (
    <main className="classroom-page exam-space">
      <button type="button" className="classroom-back" onClick={() => navigate(`/student/classes/${classId}`)}>
        ← Trở về lớp học
      </button>
      <header className="exam-space-hero">
        <span className="exam-space-kicker">KHÔNG GIAN KIỂM TRA</span>
        <h1>Học, làm bài và nhìn lại kết quả.</h1>
        <p>{classroom ? classroom.name : 'Các bài kiểm tra trong lớp của bạn'}</p>
        <div className="exam-space-hero-art" aria-hidden="true">
          <span>01</span>
          <span>02</span>
          <span>03</span>
        </div>
      </header>
      {error && (
        <p className="classroom-error" role="alert">
          {error}
        </p>
      )}
      {!classroom && !error && <p className="exam-space-loading">Đang tải không gian kiểm tra...</p>}
      {classroom && <StudentClassExams classId={classId} lessons={classroom.lessons} />}
    </main>
  );
};

export default StudentExamHub;
