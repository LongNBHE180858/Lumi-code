import React from 'react';
import { cleanup, render, screen } from '@testing-library/react';
import { afterEach, expect, it, vi } from 'vitest';
import StudentExamResult from './student-exam-result';
import { getStudentExamResult, getStudentExams } from 'app/shared/util/class-exam-api';

const navigateMock = vi.hoisted(() => vi.fn());
vi.mock('react-router', () => ({
  useNavigate: () => navigateMock,
  useParams: () => ({ id: '7', examId: '8', attemptId: '12' }),
}));
vi.mock('app/shared/util/class-exam-api', () => ({
  getStudentExamResult: vi.fn(),
  getStudentExams: vi.fn(),
}));

afterEach(() => {
  cleanup();
  vi.clearAllMocks();
});

it('shows the separate answer page after release', async () => {
  vi.mocked(getStudentExams).mockResolvedValue([
    {
      id: 8,
      materialId: null,
      title: 'Kiểm tra phân số',
      durationMinutes: 15,
      passScore: 50,
      status: 'COMPLETED',
      questionCount: 1,
      assignedStudentId: null,
    },
  ]);
  vi.mocked(getStudentExamResult).mockResolvedValue({
    attemptId: 12,
    status: 'SUBMITTED',
    score: 80,
    passed: true,
    submittedAt: '2026-11-16T10:00:00Z',
    resultAvailableAt: '2026-11-16T10:00:00Z',
    answersAvailableAt: '2026-11-16T10:00:00Z',
    feedback: [{ questionId: 4, question: '1 + 1?', selected: '1', correct: '2', isCorrect: false, explanation: 'Cộng hai số.' }],
  });

  render(<StudentExamResult />);

  expect(await screen.findByText('Kiểm tra phân số')).toBeTruthy();
  expect(screen.getByText('Đáp án đúng')).toBeTruthy();
  expect(screen.getByText('2')).toBeTruthy();
  expect(screen.getByText(/Cộng hai số/)).toBeTruthy();
});

it('keeps answers hidden until release', async () => {
  vi.mocked(getStudentExams).mockResolvedValue([]);
  vi.mocked(getStudentExamResult).mockResolvedValue({
    attemptId: 12,
    status: 'SUBMITTED',
    score: null,
    passed: null,
    submittedAt: '2026-11-16T10:00:00Z',
    resultAvailableAt: '2026-11-20T10:00:00Z',
    answersAvailableAt: null,
    feedback: [],
  });

  render(<StudentExamResult />);

  expect(await screen.findByText('Đáp án chưa được công bố')).toBeTruthy();
  expect(screen.queryByText('Đáp án đúng')).toBeNull();
  expect(screen.queryByText('2')).toBeNull();
});
