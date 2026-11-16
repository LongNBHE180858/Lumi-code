import React from 'react';
import { cleanup, fireEvent, render, screen, waitFor } from '@testing-library/react';
import { afterEach, expect, it, vi } from 'vitest';
import StudentClassExams from './student-class-exams';
import { getStudentExams, getStudentExamResults, startClassExam, submitClassExam } from 'app/shared/util/class-exam-api';

vi.mock('app/shared/util/class-exam-api', () => ({
  getStudentExams: vi.fn(),
  getStudentExamResults: vi.fn(),
  startClassExam: vi.fn(),
  submitClassExam: vi.fn(),
}));

afterEach(() => {
  cleanup();
  vi.clearAllMocks();
});

it('shows a class-wide exam and its submitted attempts in learning history', async () => {
  vi.mocked(getStudentExams).mockResolvedValue([
    {
      id: 19,
      materialId: null,
      title: 'Kiểm tra cuối chương',
      questionCount: 5,
      durationMinutes: 20,
      passScore: 60,
      status: 'ACTIVE',
      assignedStudentId: null,
    },
  ]);
  vi.mocked(getStudentExamResults).mockResolvedValue([
    { attemptId: 44, score: 80, passed: true, submittedAt: '2026-11-12T10:00:00Z', feedback: [] },
    { attemptId: 32, score: 40, passed: false, submittedAt: '2026-11-11T10:00:00Z', feedback: [] },
  ]);

  render(<StudentClassExams classId={7} lessons={[]} />);

  expect(await screen.findByText('Bài kiểm tra của lớp')).toBeTruthy();
  expect(await screen.findByText('2 lần nộp bài')).toBeTruthy();
  expect(screen.getByText('Kiểm tra cuối chương · Lần 2')).toBeTruthy();
  expect(screen.getByText('Kiểm tra cuối chương · Lần 1')).toBeTruthy();
  fireEvent.click(screen.getAllByRole('button', { name: 'Xem bài' })[1]);
  expect(await screen.findByText('Kết quả: 40%')).toBeTruthy();
});

it('submits the selected answer and shows feedback for a mistake', async () => {
  vi.mocked(getStudentExams).mockResolvedValue([
    {
      id: 8,
      materialId: 3,
      title: 'Bài kiểm tra',
      questionCount: 1,
      durationMinutes: 15,
      passScore: 50,
      status: 'ACTIVE',
      assignedStudentId: null,
    },
  ]);
  vi.mocked(startClassExam).mockResolvedValue({
    attemptId: 12,
    examId: 8,
    deadlineAt: '2099-01-01T00:00:00Z',
    questions: [
      {
        id: 22,
        content: '2 + 2 = ?',
        points: 1,
        options: [
          { id: 5, content: '3' },
          { id: 6, content: '4' },
        ],
      },
    ],
  });
  vi.mocked(submitClassExam).mockResolvedValue({
    attemptId: 12,
    score: 0,
    passed: false,
    submittedAt: '2026-11-10T00:00:00Z',
    feedback: [{ questionId: 22, question: '2 + 2 = ?', selected: '3', correct: '4', isCorrect: false, explanation: 'Cộng hai với hai.' }],
  });

  render(
    <StudentClassExams
      classId={7}
      lessons={[{ id: 3, title: 'Phép cộng', fileType: 'PDF', fileUrl: '', uploadedAt: null, orderIndex: 0 }]}
    />,
  );
  fireEvent.click(await screen.findByRole('button', { name: 'Làm bài' }));
  fireEvent.click(await screen.findByLabelText('3'));
  fireEvent.click(screen.getByRole('button', { name: 'Nộp bài' }));

  await waitFor(() => expect(submitClassExam).toHaveBeenCalledWith(7, 8, 12, [{ questionId: 22, optionId: 5 }]));
  expect(await screen.findByText('Giải thích: Cộng hai với hai.')).toBeTruthy();
  expect(screen.getByText('Bạn chọn: 3 · Đáp án đúng: 4')).toBeTruthy();
});
