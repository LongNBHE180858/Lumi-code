import React from 'react';
import { cleanup, fireEvent, render, screen, within } from '@testing-library/react';
import { afterEach, expect, it, vi } from 'vitest';
import ClassProgress from './class-progress';
import { getClassProgress } from 'app/shared/util/class-exam-api';
import { getClassLearningProgress } from 'app/shared/util/classroom-api';

vi.mock('app/shared/util/class-exam-api', () => ({ getClassProgress: vi.fn() }));
vi.mock('app/shared/util/classroom-api', () => ({
  getClassLearningProgress: vi.fn(),
  classroomError: (error: Error) => error.message,
}));
vi.mock('app/config/store', () => ({
  useAppSelector: (selector: (state: { authentication: { account: { id: number } } }) => number) =>
    selector({ authentication: { account: { id: 7 } } }),
}));

afterEach(() => {
  cleanup();
  window.localStorage.clear();
  vi.clearAllMocks();
});

it('paginates students and resets to the first page when searching', async () => {
  vi.mocked(getClassLearningProgress).mockResolvedValue({ students: [] });
  vi.mocked(getClassProgress).mockResolvedValue({
    classAverageScore: 75,
    totalExams: 1,
    students: Array.from({ length: 10 }, (_, index) => ({
      studentId: index + 1,
      name: `Học sinh ${index + 1}`,
      completedExams: 1,
      totalExams: 1,
      averageScore: 75,
      mistakes: 0,
      details: [],
    })),
  });

  render(<ClassProgress classId={3} />);
  expect(await screen.findByText('Học sinh 1')).toBeTruthy();
  expect(screen.queryByText('Học sinh 9')).toBeNull();
  const pages = screen.getByRole('navigation', { name: 'Trang học sinh' });
  fireEvent.click(within(pages).getByRole('button', { name: 'Sau' }));
  expect(screen.getByText('Học sinh 9')).toBeTruthy();
  expect(screen.queryByText('Học sinh 1')).toBeNull();
  fireEvent.change(screen.getByLabelText('Tìm học sinh'), { target: { value: 'Học sinh 1' } });
  expect(screen.getByText('Học sinh 1')).toBeTruthy();
  expect(screen.getByText('Học sinh 10')).toBeTruthy();
});

it('expands long mistake reviews without a second paginator and keeps the remark editor accessible', async () => {
  vi.mocked(getClassLearningProgress).mockResolvedValue({ students: [] });
  vi.mocked(getClassProgress).mockResolvedValue({
    classAverageScore: 40,
    totalExams: 1,
    students: [
      {
        studentId: 11,
        name: 'An',
        completedExams: 1,
        totalExams: 1,
        averageScore: 40,
        mistakes: 9,
        details: Array.from({ length: 9 }, (_, index) => ({
          examId: 4,
          examTitle: 'Phương trình',
          question: `Câu hỏi ${index + 1}?`,
          selected: null,
          correct: `${index + 1}`,
          explanation: `Giải thích câu ${index + 1}.`,
        })),
      },
    ],
  });

  render(<ClassProgress classId={3} />);
  fireEvent.click(await screen.findByText('Xem câu sai và ghi nhận xét (9)'));
  expect(screen.getByText('Câu hỏi 1?')).toBeTruthy();
  expect(screen.queryByText('Câu hỏi 5?')).toBeNull();
  expect(screen.queryByRole('navigation', { name: /Trang câu sai/ })).toBeNull();
  expect(screen.getByLabelText('Nhận xét về kết quả học tập')).toBeTruthy();
  fireEvent.click(screen.getByRole('button', { name: 'Xem thêm 4 câu' }));
  expect(screen.getByText('Câu hỏi 5?')).toBeTruthy();
  expect(screen.getByText('Câu hỏi 1?')).toBeTruthy();
  fireEvent.click(screen.getByRole('button', { name: 'Thu gọn' }));
  expect(screen.queryByText('Câu hỏi 5?')).toBeNull();
});

it('filters mistakes by exam when a student has results from multiple exams', async () => {
  vi.mocked(getClassLearningProgress).mockResolvedValue({ students: [] });
  vi.mocked(getClassProgress).mockResolvedValue({
    classAverageScore: 50,
    totalExams: 2,
    students: [
      {
        studentId: 11,
        name: 'An',
        completedExams: 2,
        totalExams: 2,
        averageScore: 50,
        mistakes: 2,
        details: [
          { examId: 4, examTitle: 'Phân số', question: 'Câu phân số?', selected: null, correct: 'A', explanation: null },
          { examId: 5, examTitle: 'Phương trình', question: 'Câu phương trình?', selected: null, correct: 'B', explanation: null },
        ],
      },
    ],
  });

  render(<ClassProgress classId={3} />);
  fireEvent.click(await screen.findByText('Xem câu sai và ghi nhận xét (2)'));
  fireEvent.change(screen.getByLabelText('Lọc theo bài kiểm tra'), { target: { value: '5' } });
  expect(screen.getByText('Câu phương trình?')).toBeTruthy();
  expect(screen.queryByText('Câu phân số?')).toBeNull();
});

it('shows completion rate, filters students needing support and saves a local remark', async () => {
  vi.mocked(getClassLearningProgress).mockResolvedValue({ students: [] });
  vi.mocked(getClassProgress).mockResolvedValue({
    classAverageScore: 70,
    totalExams: 2,
    students: [
      {
        studentId: 11,
        name: 'An',
        completedExams: 1,
        totalExams: 2,
        averageScore: 40,
        mistakes: 1,
        details: [{ examId: 4, examTitle: 'Phân số', question: '1/2 + 1/2?', selected: '1/2', correct: '1', explanation: 'Cộng tử số.' }],
      },
      { studentId: 12, name: 'Bình', completedExams: 2, totalExams: 2, averageScore: 100, mistakes: 0, details: [] },
    ],
  });

  render(<ClassProgress classId={3} />);
  expect(await screen.findByText('75%')).toBeTruthy();
  fireEvent.click(screen.getByLabelText('Chỉ xem học sinh cần hỗ trợ'));
  expect(screen.getByText('An')).toBeTruthy();
  expect(screen.queryByText('Bình')).toBeNull();
  fireEvent.click(screen.getByText('Xem câu sai và ghi nhận xét (1)'));
  expect(screen.getByText('1/2 + 1/2?')).toBeTruthy();
  fireEvent.change(screen.getByLabelText('Nhận xét về kết quả học tập'), { target: { value: 'Ôn lại phân số.' } });
  fireEvent.click(screen.getByRole('button', { name: 'Lưu nhận xét' }));
  expect(JSON.parse(window.localStorage.getItem('classroom-progress-notes:7:3') ?? '{}')['11']).toBe('Ôn lại phân số.');
});

it('explains why a student with repeated mistakes needs support', async () => {
  vi.mocked(getClassLearningProgress).mockResolvedValue({ students: [] });
  vi.mocked(getClassProgress).mockResolvedValue({
    classAverageScore: 80,
    totalExams: 1,
    students: [{ studentId: 11, name: 'An', completedExams: 1, totalExams: 1, averageScore: 80, mistakes: 3, details: [] }],
  });

  render(<ClassProgress classId={3} />);
  expect(await screen.findByText('An')).toBeTruthy();
  expect(screen.queryByText('Có từ 3 câu sai trong các lượt gần nhất')).toBeNull();
  fireEvent.click(screen.getByLabelText('Chỉ xem học sinh cần hỗ trợ'));
  expect(screen.getByText('An')).toBeTruthy();
});
