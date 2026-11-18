import React from 'react';
import { cleanup, fireEvent, render, screen, waitFor } from '@testing-library/react';
import { afterEach, expect, it, vi } from 'vitest';
import StudentExamTake from './student-exam-take';
import { getStudentExams, startClassExam, submitClassExam } from 'app/shared/util/class-exam-api';

const navigateMock = vi.hoisted(() => vi.fn());
vi.mock('react-router', () => ({ useNavigate: () => navigateMock, useParams: () => ({ id: '7', examId: '8' }) }));
vi.mock('app/shared/util/class-exam-api', () => ({ getStudentExams: vi.fn(), startClassExam: vi.fn(), submitClassExam: vi.fn() }));

afterEach(() => {
  cleanup();
  vi.clearAllMocks();
});

it('resumes the existing attempt, restores saved answers and submits from the separate page', async () => {
  vi.mocked(getStudentExams).mockResolvedValue([
    { id: 8, materialId: null, title: 'Kiểm tra phép cộng', questionCount: 1, durationMinutes: 15, passScore: 50, status: 'AVAILABLE' },
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
    savedAnswers: [{ questionId: 22, optionId: 5 }],
  });
  vi.mocked(submitClassExam).mockResolvedValue({
    attemptId: 12,
    status: 'SUBMITTED',
    score: 100,
    passed: true,
    submittedAt: '2026-11-17T00:00:00Z',
    resultAvailableAt: '2026-11-17T00:00:00Z',
    answersAvailableAt: '2026-11-17T00:00:00Z',
    feedback: [],
  });

  render(<StudentExamTake />);
  expect(await screen.findByText('Kiểm tra phép cộng')).toBeTruthy();
  expect((screen.getByRole('radio', { name: '3' }) as HTMLInputElement).checked).toBe(true);
  fireEvent.click(screen.getByRole('radio', { name: '4' }));
  fireEvent.click(screen.getByRole('button', { name: 'Nộp bài kiểm tra' }));

  await waitFor(() => expect(submitClassExam).toHaveBeenCalledWith(7, 8, 12, [{ questionId: 22, optionId: 6 }]));
  expect(navigateMock).toHaveBeenCalledWith('/student/classes/7/exams/8/attempts/12/result', { replace: true });
});

it('labels a practice attempt as practice in the take screen', async () => {
  vi.mocked(getStudentExams).mockResolvedValue([
    {
      id: 8,
      materialId: null,
      title: 'Ôn tập phân số',
      assessmentType: 'PRACTICE',
      questionCount: 1,
      durationMinutes: 15,
      passScore: 50,
      status: 'AVAILABLE',
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
          { id: 5, content: '4' },
          { id: 6, content: '5' },
        ],
      },
    ],
    savedAnswers: [],
  });

  render(<StudentExamTake />);
  expect(await screen.findByText('PHÒNG LUYỆN TẬP')).toBeTruthy();
  expect(screen.getByText('Ôn tập phân số')).toBeTruthy();
});

it('shows one question at a time and keeps answers when moving between questions', async () => {
  const confirmMock = vi.fn();
  vi.stubGlobal('confirm', confirmMock);
  vi.mocked(getStudentExams).mockResolvedValue([]);
  vi.mocked(startClassExam).mockResolvedValue({
    attemptId: 12,
    examId: 8,
    deadlineAt: '2099-01-01T00:00:00Z',
    questions: [
      {
        id: 21,
        content: 'Câu hỏi thứ nhất?',
        points: 1,
        options: [
          { id: 1, content: 'Đáp án A' },
          { id: 2, content: 'Đáp án B' },
        ],
      },
      {
        id: 22,
        content: 'Câu hỏi thứ hai?',
        points: 1,
        options: [
          { id: 3, content: 'Đáp án C' },
          { id: 4, content: 'Đáp án D' },
        ],
      },
    ],
    savedAnswers: [],
  });

  render(<StudentExamTake />);
  expect(await screen.findByText(/Câu hỏi thứ nhất\?/)).toBeTruthy();
  expect(screen.queryByText(/Câu hỏi thứ hai\?/)).toBeNull();
  expect(screen.getByRole('timer', { name: 'Thời gian còn lại' })).toBeTruthy();
  fireEvent.click(screen.getByRole('radio', { name: 'Đáp án A' }));
  fireEvent.click(screen.getByRole('button', { name: 'Câu tiếp →' }));
  expect(confirmMock).not.toHaveBeenCalled();
  expect(submitClassExam).not.toHaveBeenCalled();
  expect(screen.queryByText(/Câu hỏi thứ nhất\?/)).toBeNull();
  expect(screen.getByText(/Câu hỏi thứ hai\?/)).toBeTruthy();
  expect(screen.getByRole('button', { name: 'Nộp bài kiểm tra' })).toBeTruthy();
  fireEvent.click(screen.getByRole('button', { name: 'Câu trước' }));
  expect((screen.getByRole('radio', { name: 'Đáp án A' }) as HTMLInputElement).checked).toBe(true);
  fireEvent.click(screen.getByRole('button', { name: 'Đến câu 2' }));
  expect(screen.getByText(/Câu hỏi thứ hai\?/)).toBeTruthy();
  vi.unstubAllGlobals();
});
