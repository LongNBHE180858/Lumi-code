import React from 'react';
import { cleanup, fireEvent, render, screen, waitFor } from '@testing-library/react';
import { afterEach, expect, it, vi } from 'vitest';
import StudentClassExams from './student-class-exams';
import { getStudentExams, getStudentExamResults } from 'app/shared/util/class-exam-api';

const navigateMock = vi.hoisted(() => vi.fn());
vi.mock('react-router', () => ({ useNavigate: () => navigateMock }));

vi.mock('app/shared/util/class-exam-api', () => ({
  getStudentExams: vi.fn(),
  getStudentExamResults: vi.fn(),
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
    {
      attemptId: 44,
      status: 'SUBMITTED',
      score: 80,
      passed: true,
      submittedAt: '2026-11-12T10:00:00Z',
      resultAvailableAt: '2026-11-12T10:00:00Z',
      answersAvailableAt: '2026-11-12T10:00:00Z',
      feedback: [],
    },
    {
      attemptId: 32,
      status: 'SUBMITTED',
      score: 40,
      passed: false,
      submittedAt: '2026-11-11T10:00:00Z',
      resultAvailableAt: '2026-11-11T10:00:00Z',
      answersAvailableAt: '2026-11-11T10:00:00Z',
      feedback: [],
    },
  ]);

  render(<StudentClassExams classId={7} lessons={[]} />);

  expect(await screen.findByText('KIỂM TRA CHUNG')).toBeTruthy();
  fireEvent.click(screen.getByRole('button', { name: 'Lịch sử học tập' }));
  expect(await screen.findByText('2 lần nộp bài')).toBeTruthy();
  expect(screen.getByText('Kiểm tra cuối chương · Lần 2')).toBeTruthy();
  expect(screen.getByText('Kiểm tra cuối chương · Lần 1')).toBeTruthy();
  fireEvent.click(screen.getAllByRole('button', { name: 'Xem bài' })[1]);
  await waitFor(() => expect(navigateMock).toHaveBeenCalledWith('/student/classes/7/exams/19/attempts/32/result'));
});

it('opens an unfinished exam directly on its dedicated take page', async () => {
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
      inProgressAttemptId: 12,
      remainingAttempts: 0,
    },
  ]);

  render(
    <StudentClassExams
      classId={7}
      lessons={[{ id: 3, title: 'Phép cộng', fileType: 'PDF', fileUrl: '', uploadedAt: null, orderIndex: 0 }]}
    />,
  );
  fireEvent.click(await screen.findByRole('button', { name: 'Tiếp tục làm' }));
  expect(navigateMock).toHaveBeenCalledWith('/student/classes/7/exams/8/take');
});

it('shows opening, closing, and attempt limits with urgent and overdue deadlines', async () => {
  vi.mocked(getStudentExams).mockResolvedValue([
    {
      id: 8,
      materialId: null,
      title: 'Kiểm tra sắp đóng',
      questionCount: 3,
      durationMinutes: 15,
      passScore: 50,
      status: 'AVAILABLE',
      maxAttempts: 2,
      remainingAttempts: 1,
      opensAt: new Date(Date.now() - 60 * 60 * 1000).toISOString(),
      closesAt: new Date(Date.now() + 60 * 60 * 1000).toISOString(),
    },
    {
      id: 9,
      materialId: null,
      title: 'Kiểm tra đã đóng',
      questionCount: 3,
      durationMinutes: 15,
      passScore: 50,
      status: 'CLOSED',
      maxAttempts: null,
      remainingAttempts: null,
      opensAt: new Date(Date.now() - 2 * 60 * 60 * 1000).toISOString(),
      closesAt: new Date(Date.now() - 60 * 60 * 1000).toISOString(),
    },
  ]);
  vi.mocked(getStudentExamResults).mockResolvedValue([]);

  render(<StudentClassExams classId={7} lessons={[]} />);
  const urgent = (await screen.findByText('Kiểm tra sắp đóng')).closest('.exam-list-card') as HTMLElement;
  expect(urgent.textContent).toContain('Số lượt: 2');
  expect(urgent.textContent).toContain('Mở:');
  expect(urgent.textContent).toContain('Đóng:');
  expect(urgent.querySelector('.exam-list-schedule .is-urgent')).toBeTruthy();
  const expired = screen.getByText('Kiểm tra đã đóng').closest('.exam-list-card') as HTMLElement;
  expect(expired.textContent).toContain('Số lượt: Không giới hạn');
  expect(expired.querySelector('.exam-list-schedule .is-expired')).toBeTruthy();
  expect((expired.querySelector('.exam-list-actions button') as HTMLButtonElement).disabled).toBe(true);
});

it('stops deadline warnings after an exam has a submitted attempt', async () => {
  vi.mocked(getStudentExams).mockResolvedValue([
    {
      id: 8,
      materialId: null,
      title: 'Bài đã nộp sắp đóng',
      questionCount: 1,
      durationMinutes: 15,
      passScore: 50,
      status: 'AVAILABLE',
      maxAttempts: null,
      opensAt: new Date(Date.now() - 60 * 60 * 1000).toISOString(),
      closesAt: new Date(Date.now() + 60 * 60 * 1000).toISOString(),
    },
    {
      id: 9,
      materialId: null,
      title: 'Bài đã nộp quá hạn',
      questionCount: 1,
      durationMinutes: 15,
      passScore: 50,
      status: 'CLOSED',
      maxAttempts: 1,
      opensAt: new Date(Date.now() - 2 * 60 * 60 * 1000).toISOString(),
      closesAt: new Date(Date.now() - 60 * 60 * 1000).toISOString(),
    },
  ]);
  vi.mocked(getStudentExamResults).mockImplementation((_classId, examId) =>
    Promise.resolve([
      {
        attemptId: examId + 100,
        status: 'SUBMITTED',
        score: 80,
        passed: true,
        submittedAt: new Date().toISOString(),
        resultAvailableAt: new Date().toISOString(),
        answersAvailableAt: null,
        feedback: [],
      },
    ]),
  );

  render(<StudentClassExams classId={7} lessons={[]} />);
  await screen.findByText('Bài đã nộp sắp đóng');
  await waitFor(() => expect(screen.getAllByText('Đã nộp')).toHaveLength(2));
  for (const title of ['Bài đã nộp sắp đóng', 'Bài đã nộp quá hạn']) {
    const card = screen.getByText(title).closest('.exam-list-card') as HTMLElement;
    expect(card.querySelector('.exam-list-schedule .is-urgent, .exam-list-schedule .is-expired')).toBeNull();
    expect(card.textContent).not.toContain('Sắp hết hạn');
    expect(card.textContent).not.toContain('Quá hạn');
  }
});

it('paginates missed questions and quizzes them without changing the original attempt', async () => {
  vi.mocked(getStudentExams).mockResolvedValue([
    {
      id: 8,
      materialId: 3,
      title: 'Phép cộng',
      questionCount: 7,
      durationMinutes: 10,
      passScore: 50,
      status: 'ACTIVE',
      assignedStudentId: null,
    },
  ]);
  vi.mocked(getStudentExamResults).mockResolvedValue([
    {
      attemptId: 12,
      status: 'SUBMITTED',
      score: 0,
      passed: false,
      submittedAt: '2026-11-10T00:00:00Z',
      resultAvailableAt: '2026-11-10T00:00:00Z',
      answersAvailableAt: '2026-11-10T00:00:00Z',
      feedback: Array.from({ length: 7 }, (_, index) => ({
        questionId: 22 + index,
        question: `Câu hỏi ${index + 1}?`,
        selected: index === 0 ? '3' : null,
        correct: '4',
        isCorrect: false,
        explanation: 'Đáp án là 4.',
        options: ['1', '2', '3', '4'],
      })),
    },
  ]);

  render(<StudentClassExams classId={7} lessons={[]} />);
  fireEvent.click(await screen.findByRole('button', { name: /Gợi ý ôn tập 7/ }));
  expect(screen.getByText('Câu hỏi 1?')).toBeTruthy();
  expect(screen.queryByText('Câu hỏi 6?')).toBeNull();
  fireEvent.click(screen.getByRole('button', { name: 'Sau' }));
  expect(screen.getByText('Câu hỏi 6?')).toBeTruthy();
  expect(screen.getByText('Trang 2/2')).toBeTruthy();
  fireEvent.click(screen.getByRole('button', { name: /Làm bài với 7 câu cần ôn/ }));
  expect(screen.getByText('Câu hỏi 1?')).toBeTruthy();
  expect(screen.queryByText('Đáp án đúng: 4')).toBeNull();
  fireEvent.click(screen.getByLabelText('4'));
  for (let index = 0; index < 6; index++) fireEvent.click(screen.getByRole('button', { name: 'Câu tiếp' }));
  fireEvent.click(screen.getByRole('button', { name: 'Nộp lượt luyện' }));
  expect(screen.getByText('Bạn trả lời đúng 1/7 câu')).toBeTruthy();
  expect(screen.getByText('Đáp án đúng: 4')).toBeTruthy();
  fireEvent.click(screen.getByRole('button', { name: 'Luyện lại' }));
  expect(screen.queryByText('Đáp án đúng: 4')).toBeNull();
});

it('does not display a score before the result is released', async () => {
  vi.mocked(getStudentExams).mockResolvedValue([
    {
      id: 8,
      materialId: null,
      title: 'Bài kiểm tra',
      questionCount: 1,
      durationMinutes: 15,
      passScore: 50,
      status: 'ACTIVE',
      assignedStudentId: null,
    },
  ]);
  vi.mocked(getStudentExamResults).mockResolvedValue([
    {
      attemptId: 12,
      status: 'SUBMITTED',
      score: null,
      passed: null,
      submittedAt: '2026-11-10T00:00:00Z',
      resultAvailableAt: '2026-11-20T00:00:00Z',
      answersAvailableAt: null,
      feedback: [],
    },
  ]);

  render(<StudentClassExams classId={7} lessons={[]} />);
  fireEvent.click(await screen.findByRole('button', { name: 'Xem kết quả' }));

  await waitFor(() => expect(navigateMock).toHaveBeenCalledWith('/student/classes/7/exams/8/attempts/12/result'));
  expect(screen.queryByText(/Kết quả: \d+%/)).toBeNull();
  expect(screen.queryByText(/Đáp án đúng:/)).toBeNull();
});
