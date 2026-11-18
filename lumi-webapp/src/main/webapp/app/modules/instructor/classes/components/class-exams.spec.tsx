import React from 'react';
import { cleanup, fireEvent, render, screen, waitFor } from '@testing-library/react';
import { afterEach, expect, it, vi } from 'vitest';
import ClassExams from './class-exams';
import { getInstructorClassMembers } from 'app/shared/util/classroom-api';
import {
  createExamFromMatrix,
  generateExamPreview,
  getInstructorExams,
  updateClassExamStatus,
  type ExamSummary,
} from 'app/shared/util/class-exam-api';
import { getBankTopics, getQuestionBanks } from 'app/shared/util/question-bank-api';

vi.mock('app/shared/util/classroom-api', () => ({
  classroomError: (error: Error) => error.message,
  getInstructorClassMembers: vi.fn(),
}));
vi.mock('app/shared/util/class-exam-api', () => ({
  createExamFromMatrix: vi.fn(),
  generateExamPreview: vi.fn(),
  getInstructorExams: vi.fn(),
  updateClassExamStatus: vi.fn(),
}));
vi.mock('app/shared/util/question-bank-api', () => ({
  getBankTopics: vi.fn(),
  getQuestionBanks: vi.fn(),
}));

afterEach(() => {
  cleanup();
  vi.clearAllMocks();
});

const setup = () => {
  vi.mocked(getInstructorClassMembers).mockResolvedValue({
    items: [],
    page: 0,
    totalPages: 1,
    totalElements: 0,
    activeCount: 0,
    pendingCount: 0,
  });
  vi.mocked(getQuestionBanks).mockResolvedValue([
    {
      id: 5,
      name: 'Ngân hàng Toán',
      questionCount: 10,
      classId: 3,
      className: 'Toán',
      updatedAt: '2026-11-16T00:00:00Z',
    },
  ]);
  vi.mocked(getBankTopics).mockResolvedValue([
    {
      topic: 'Phân số',
      totalQuestions: 10,
      difficulty1: 10,
      difficulty2: 0,
      difficulty3: 0,
      difficulty4: 0,
      difficulty5: 0,
    },
  ]);
};

it('opens a practice already linked to the selected lesson', async () => {
  setup();
  vi.mocked(getInstructorExams).mockResolvedValue([]);

  render(
    <ClassExams
      classId={3}
      initialPracticeMaterialId={5}
      lessons={[{ id: 5, title: 'Bài giảng phân số', fileType: 'PDF', fileUrl: '', uploadedAt: null, orderIndex: 0 }]}
    />,
  );

  await waitFor(() => expect(getBankTopics).toHaveBeenCalled());
  expect(screen.getByText('Bước 1: Thiết lập thông tin bài kiểm tra')).toBeTruthy();
  expect((screen.getByRole('combobox', { name: /Loại bài/ }) as HTMLSelectElement).value).toBe('PRACTICE');
  expect((screen.getByRole('option', { name: 'Bài giảng phân số (PDF)' }) as HTMLOptionElement).selected).toBe(true);
});

it('sends all required scheduling and release fields when creating an exam', async () => {
  setup();
  vi.mocked(getInstructorExams).mockResolvedValue([]);
  vi.mocked(generateExamPreview).mockResolvedValue([
    {
      questionId: 8,
      content: '1 + 1?',
      difficulty: 1,
      topic: 'Phân số',
      points: 1,
      options: ['1', '2'],
      correctIndex: 1,
    },
  ]);
  const summary: ExamSummary = {
    id: 10,
    materialId: null,
    title: 'Đề Toán',
    durationMinutes: 15,
    passScore: 50,
    status: 'ACTIVE',
    questionCount: 1,
    assignedStudentId: null,
  };
  vi.mocked(createExamFromMatrix).mockResolvedValue(summary);

  render(<ClassExams classId={3} lessons={[]} />);
  fireEvent.click(screen.getByRole('button', { name: /Tạo bài kiểm tra từ ma trận/ }));
  fireEvent.change(screen.getByPlaceholderText('VD: Kiểm tra 15 phút: Đại số chương 1'), { target: { value: 'Đề Toán' } });
  fireEvent.click(screen.getByRole('button', { name: /Tiếp tục: Chọn ngân hàng/ }));
  await screen.findByText('Ngân hàng Toán');
  fireEvent.click(screen.getByRole('button', { name: /Tiếp tục: Lập ma trận/ }));
  const previewButton = screen.getByRole('button', { name: /Xem trước bộ câu hỏi/ });
  await waitFor(() => expect((previewButton as HTMLButtonElement).disabled).toBe(false));
  fireEvent.click(previewButton);
  await screen.findByText('1 + 1?');
  fireEvent.click(screen.getByRole('button', { name: /Xác nhận tạo bài kiểm tra/ }));

  await waitFor(() =>
    expect(createExamFromMatrix).toHaveBeenCalledWith(
      3,
      expect.objectContaining({
        assessmentType: 'EXAM',
        maxAttempt: 1,
        resultReleaseMode: 'IMMEDIATE',
        answerReleaseMode: 'AFTER_SUBMIT',
        shuffleQuestions: false,
        openAt: expect.any(String),
        closesAt: expect.any(String),
      }),
    ),
  );
});

it('creates a matrix exam with unlimited attempts when selected', async () => {
  setup();
  vi.mocked(getInstructorExams).mockResolvedValue([]);
  vi.mocked(generateExamPreview).mockResolvedValue([
    { questionId: 8, content: '1 + 1?', difficulty: 1, topic: 'Phân số', points: 1, options: ['1', '2'], correctIndex: 1 },
  ]);
  vi.mocked(createExamFromMatrix).mockResolvedValue({
    id: 10,
    materialId: null,
    title: 'Luyện tập',
    durationMinutes: 15,
    passScore: 50,
    status: 'ACTIVE',
    questionCount: 1,
    maxAttempts: null,
  });

  render(<ClassExams classId={3} lessons={[]} />);
  fireEvent.click(screen.getByRole('button', { name: /Tạo bài kiểm tra từ ma trận/ }));
  fireEvent.change(screen.getByPlaceholderText('VD: Kiểm tra 15 phút: Đại số chương 1'), { target: { value: 'Luyện tập' } });
  fireEvent.click(screen.getByRole('checkbox', { name: 'Không giới hạn lượt làm' }));
  fireEvent.click(screen.getByRole('button', { name: /Tiếp tục: Chọn ngân hàng/ }));
  await screen.findByText('Ngân hàng Toán');
  fireEvent.click(screen.getByRole('button', { name: /Tiếp tục: Lập ma trận/ }));
  fireEvent.click(await screen.findByRole('button', { name: /Xem trước bộ câu hỏi/ }));
  await screen.findByText('1 + 1?');
  fireEvent.click(screen.getByRole('button', { name: /Xác nhận tạo bài kiểm tra/ }));
  await waitFor(() => expect(createExamFromMatrix).toHaveBeenCalledWith(3, expect.objectContaining({ maxAttempt: null })));
});

it('changes status without sending a full exam update', async () => {
  setup();
  vi.mocked(getInstructorExams).mockResolvedValue([
    {
      id: 10,
      materialId: null,
      title: 'Đề cũ',
      durationMinutes: 15,
      passScore: 50,
      status: 'ACTIVE',
      questionCount: 1,
      assignedStudentId: null,
    },
  ]);
  vi.mocked(updateClassExamStatus).mockResolvedValue({
    id: 10,
    materialId: null,
    title: 'Đề cũ',
    durationMinutes: 15,
    passScore: 50,
    status: 'ARCHIVED',
    questionCount: 1,
    assignedStudentId: null,
  });

  render(<ClassExams classId={3} lessons={[]} />);
  fireEvent.click(await screen.findByRole('button', { name: 'Lưu trữ' }));

  await waitFor(() => expect(updateClassExamStatus).toHaveBeenCalledWith(3, 10, 'ARCHIVED'));
});
