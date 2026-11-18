import React, { useState } from 'react';
import { useNavigate } from 'react-router';
import { StudentAssignmentsPage } from './StudentAssignmentsPage';
import { INITIAL_ASSIGNMENTS, INITIAL_CLASSES, AssignmentItem, ClassItem } from '../types';
import '../student-classroom.scss';

export const StudentAssignments: React.FC = () => {
  const navigate = useNavigate();
  const [assignments, setAssignments] = useState<AssignmentItem[]>(INITIAL_ASSIGNMENTS);
  const [allClasses] = useState<ClassItem[]>(INITIAL_CLASSES);

  const handleRequestJoinClass = () => {
    alert('Đã gửi yêu cầu tham gia lớp.');
  };

  const handleSubmitTask = (taskId: string, fileName: string, comment: string) => {
    setAssignments(prev =>
      prev.map(task => {
        if (task.id === taskId) {
          return {
            ...task,
            status: 'submitted',
            studentSubmission: {
              fileName,
              submittedDate: new Date().toLocaleDateString('vi-VN'),
              comment,
            },
          };
        }
        return task;
      }),
    );
    alert('Đã gửi yêu cầu tham gia lớp.');
  };

  return (
    <StudentAssignmentsPage
      assignments={assignments}
      allClasses={allClasses}
      onRequestJoinClass={handleRequestJoinClass}
      onSubmitTask={handleSubmitTask}
      onNavigateClasses={() => navigate('/student/classes')}
      onNavigateHome={() => navigate('/')}
    />
  );
};

export default StudentAssignments;
