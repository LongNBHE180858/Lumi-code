import React from 'react';
import { cleanup, fireEvent, render, screen, waitFor, within } from '@testing-library/react';
import { afterEach, expect, it, vi } from 'vitest';
import StudentClassroom from './student-classroom';
import {
  getStudentClassroom,
  getMyClassAssignmentSubmission,
  getClassMaterialAccess,
  getClassAssignmentSubmissionFileAccess,
  submitClassAssignment,
} from 'app/shared/util/classroom-api';
import { getStudentExams, getStudentExamResults } from 'app/shared/util/class-exam-api';

const navigateMock = vi.hoisted(() => vi.fn());
vi.mock('react-router', () => ({ useNavigate: () => navigateMock, useParams: () => ({ id: '3' }) }));
vi.mock('app/config/store', () => ({
  useAppSelector: (selector: (state: { authentication: { account: { id: number } } }) => number) =>
    selector({ authentication: { account: { id: 7 } } }),
}));
vi.mock('app/shared/util/classroom-api', () => ({
  getStudentClassroom: vi.fn(),
  getMyClassAssignmentSubmission: vi.fn(),
  submitClassAssignment: vi.fn(),
  classroomError: (error: Error) => error.message,
  getClassMaterialAccess: vi.fn(),
  getClassAssignmentFileAccess: vi.fn(),
  getClassAssignmentSubmissionFileAccess: vi.fn(),
}));
vi.mock('app/shared/util/class-exam-api', () => ({
  getStudentExams: vi.fn(),
  getStudentExamResults: vi.fn(),
  startClassExam: vi.fn(),
  submitClassExam: vi.fn(),
}));

afterEach(() => {
  cleanup();
  window.localStorage.clear();
  vi.clearAllMocks();
});

it('saves and submits a task while hiding the class notification tab', async () => {
  vi.mocked(getStudentClassroom).mockResolvedValue({
    id: 3,
    name: 'Toán 6A',
    description: 'Ôn tập phân số',
    joinCode: null,
    students: [],
    lessons: [],
    assignments: [{ id: 9, title: 'Bài tập phân số', instructions: 'Giải câu 1.', dueAt: null, status: 'ACTIVE' }],
  });
  vi.mocked(getStudentExams).mockResolvedValue([]);
  vi.mocked(getMyClassAssignmentSubmission).mockResolvedValue(null);
  vi.mocked(submitClassAssignment).mockResolvedValue({
    id: 1,
    assignmentId: 9,
    studentId: 7,
    studentName: 'Học sinh',
    content: 'Đáp án của em',
    status: 'SUBMITTED',
    score: null,
    feedback: null,
    submittedAt: new Date().toISOString(),
    gradedAt: null,
    version: 0,
  });

  render(<StudentClassroom />);
  expect(await screen.findByText('Toán 6A')).toBeTruthy();
  const tabs = screen.getByRole('navigation', { name: 'Các mục trong lớp' });
  expect(within(tabs).queryByRole('button', { name: /Thông báo/ })).toBeNull();
  fireEvent.click(within(tabs).getByRole('button', { name: /Kiểm tra & ôn tập/ }));
  expect(navigateMock).toHaveBeenCalledWith('/student/classes/3/exams');
  fireEvent.click(within(tabs).getByRole('button', { name: /Bài được giao/ }));
  fireEvent.click(screen.getByRole('button', { name: 'Soạn bài' }));
  fireEvent.change(screen.getByLabelText('Câu trả lời hoặc ghi chú của bạn'), { target: { value: 'Đáp án của em' } });
  fireEvent.click(screen.getByRole('button', { name: 'Lưu bản nháp' }));
  expect(JSON.parse(window.localStorage.getItem('classroom-task-drafts:7:3') ?? '{}')['9']).toBe('Đáp án của em');
  fireEvent.click(screen.getByRole('button', { name: 'Nộp cho giáo viên' }));
  await waitFor(() => expect(submitClassAssignment).toHaveBeenCalledWith(3, 9, 'Đáp án của em'));
  await waitFor(() => expect((screen.getByRole('button', { name: 'Nộp cho giáo viên' }) as HTMLButtonElement).disabled).toBe(true));
});

it('highlights assignment deadlines that are near or overdue', async () => {
  vi.mocked(getStudentClassroom).mockResolvedValue({
    id: 3,
    name: 'Toán 6A',
    description: '',
    joinCode: null,
    students: [],
    lessons: [],
    assignments: [
      { id: 1, title: 'Bài sắp hết hạn', instructions: null, dueAt: new Date(Date.now() + 60 * 60 * 1000).toISOString(), status: 'ACTIVE' },
      { id: 2, title: 'Bài quá hạn', instructions: null, dueAt: new Date(Date.now() - 60 * 60 * 1000).toISOString(), status: 'ACTIVE' },
      { id: 3, title: 'Bài đã nộp', instructions: null, dueAt: new Date(Date.now() - 60 * 60 * 1000).toISOString(), status: 'ACTIVE' },
    ],
  });
  vi.mocked(getStudentExams).mockResolvedValue([]);
  vi.mocked(getMyClassAssignmentSubmission).mockImplementation((_classId, taskId) =>
    Promise.resolve(
      taskId === 3
        ? {
            id: 30,
            assignmentId: 3,
            studentId: 7,
            studentName: 'Học sinh',
            content: 'Đã làm',
            status: 'SUBMITTED',
            score: null,
            feedback: null,
            submittedAt: new Date().toISOString(),
            gradedAt: null,
            version: 0,
          }
        : null,
    ),
  );

  render(<StudentClassroom />);
  const tabs = await screen.findByRole('navigation', { name: 'Các mục trong lớp' });
  fireEvent.click(within(tabs).getByRole('button', { name: /Bài được giao/ }));
  expect(screen.getByText(/Sắp hết hạn:/).classList.contains('is-due-soon')).toBe(true);
  expect(screen.getByText(/Quá hạn:/).classList.contains('is-overdue')).toBe(true);
  await waitFor(() => expect(screen.getByText('Bài đã nộp').closest('.classroom-row')?.classList.contains('is-overdue')).toBe(false));
});

it('continues an unfinished exam directly from the class overview', async () => {
  vi.mocked(getStudentClassroom).mockResolvedValue({
    id: 3,
    name: 'Toán 6A',
    description: '',
    joinCode: null,
    students: [],
    lessons: [],
    assignments: [],
  });
  vi.mocked(getStudentExams).mockResolvedValue([
    {
      id: 8,
      materialId: null,
      title: 'Kiểm tra phân số',
      questionCount: 5,
      durationMinutes: 15,
      passScore: 50,
      status: 'AVAILABLE',
      inProgressAttemptId: 12,
    },
  ]);
  vi.mocked(getStudentExamResults).mockResolvedValue([]);

  render(<StudentClassroom />);
  fireEvent.click(await screen.findByRole('button', { name: 'Tiếp tục kiểm tra' }));
  expect(navigateMock).toHaveBeenCalledWith('/student/classes/3/exams/8/take');
});

it('labels an unfinished practice separately from an exam', async () => {
  vi.mocked(getStudentClassroom).mockResolvedValue({
    id: 3,
    name: 'Toán 6A',
    description: '',
    joinCode: null,
    students: [],
    lessons: [],
    assignments: [],
  });
  vi.mocked(getStudentExams).mockResolvedValue([
    {
      id: 8,
      materialId: null,
      title: 'Luyện tập phân số',
      assessmentType: 'PRACTICE',
      questionCount: 4,
      durationMinutes: 15,
      passScore: 50,
      status: 'AVAILABLE',
      inProgressAttemptId: 12,
    },
  ]);
  vi.mocked(getStudentExamResults).mockResolvedValue([]);

  render(<StudentClassroom />);
  expect(await screen.findByText('Luyện tập · 4 câu hỏi')).toBeTruthy();
  fireEvent.click(screen.getByRole('button', { name: 'Tiếp tục luyện tập' }));
  expect(navigateMock).toHaveBeenCalledWith('/student/classes/3/exams/8/take');
});

it('submits a file without requiring text and displays the uploaded attachment', async () => {
  vi.mocked(getStudentClassroom).mockResolvedValue({
    id: 3,
    name: 'Toán 6A',
    description: '',
    joinCode: null,
    students: [],
    lessons: [],
    assignments: [{ id: 9, title: 'Bài tập phân số', instructions: '', dueAt: null, status: 'ACTIVE' }],
  });
  vi.mocked(getStudentExams).mockResolvedValue([]);
  vi.mocked(getMyClassAssignmentSubmission).mockResolvedValue(null);
  vi.mocked(submitClassAssignment).mockResolvedValue({
    id: 15,
    assignmentId: 9,
    studentId: 7,
    studentName: 'An',
    content: '',
    status: 'SUBMITTED',
    score: null,
    feedback: null,
    submittedAt: new Date().toISOString(),
    gradedAt: null,
    version: 0,
    attachments: [{ fileName: 'solution.pdf', fileType: 'PDF', sizeBytes: 20 }],
  });
  vi.mocked(getClassAssignmentSubmissionFileAccess).mockResolvedValue({ url: 'about:blank', expiresAt: new Date().toISOString() });

  render(<StudentClassroom />);
  const tabs = await screen.findByRole('navigation', { name: 'Các mục trong lớp' });
  fireEvent.click(within(tabs).getByRole('button', { name: /Bài được giao/ }));
  fireEvent.click(screen.getByRole('button', { name: 'Soạn bài' }));
  const file = new File(['%PDF-1.4\n%%EOF'], 'solution.pdf', { type: 'application/pdf' });
  fireEvent.change(screen.getByLabelText('Tệp bài làm (tùy chọn)'), { target: { files: [file] } });
  fireEvent.click(screen.getByRole('button', { name: 'Nộp cho giáo viên' }));
  await waitFor(() => expect(submitClassAssignment).toHaveBeenCalledWith(3, 9, '', file));
  expect(await screen.findByText('Tệp đã nộp: solution.pdf')).toBeTruthy();
});

it('keeps lesson files accessible without the removed completion control', async () => {
  vi.mocked(getStudentClassroom).mockResolvedValue({
    id: 3,
    name: 'Toán 6A',
    description: '',
    joinCode: null,
    students: [],
    assignments: [],
    lessons: [
      { id: 5, title: 'Bài giảng phân số', fileType: 'PDF', fileUrl: '/api/classes/3/materials/5/open', uploadedAt: null, orderIndex: 0 },
    ],
  });
  vi.mocked(getStudentExams).mockResolvedValue([]);
  vi.mocked(getClassMaterialAccess).mockResolvedValue({ url: 'about:blank', expiresAt: new Date().toISOString() });

  render(<StudentClassroom />);
  const tabs = await screen.findByRole('navigation', { name: 'Các mục trong lớp' });
  fireEvent.click(within(tabs).getByRole('button', { name: /Bài học/ }));
  expect(screen.getByText('Bài giảng phân số')).toBeTruthy();
  expect(screen.queryByRole('button', { name: 'Đánh dấu hoàn thành' })).toBeNull();
  fireEvent.click(screen.getByRole('button', { name: 'Xem PDF' }));
  await waitFor(() => expect(getClassMaterialAccess).toHaveBeenCalledWith(3, 5));
  expect(await screen.findByTitle('Bài giảng phân số')).toBeTruthy();
});

it('shows a linked practice below its lesson and opens the existing attempt and result pages', async () => {
  vi.mocked(getStudentClassroom).mockResolvedValue({
    id: 3,
    name: 'Toán 6A',
    description: '',
    joinCode: null,
    students: [],
    assignments: [],
    lessons: [
      { id: 5, title: 'Bài giảng phân số', fileType: 'PDF', fileUrl: '', uploadedAt: null, orderIndex: 0 },
      { id: 6, title: 'Bài giảng hình học', fileType: 'VIDEO', fileUrl: '', uploadedAt: null, orderIndex: 1 },
    ],
  });
  vi.mocked(getStudentExams).mockResolvedValue([
    {
      id: 8,
      materialId: 5,
      assessmentType: 'PRACTICE',
      title: 'Luyện tập phân số',
      questionCount: 3,
      durationMinutes: 10,
      passScore: 50,
      status: 'AVAILABLE',
      inProgressAttemptId: 12,
    },
    {
      id: 9,
      materialId: 6,
      assessmentType: 'EXAM',
      title: 'Kiểm tra hình học',
      questionCount: 5,
      durationMinutes: 20,
      passScore: 50,
      status: 'AVAILABLE',
    },
  ]);
  vi.mocked(getStudentExamResults).mockImplementation((_classId, examId) =>
    Promise.resolve(
      examId === 8
        ? [
            {
              attemptId: 11,
              status: 'SUBMITTED',
              score: 80,
              passed: true,
              submittedAt: '2026-11-16T10:00:00Z',
              resultAvailableAt: '2026-11-16T10:00:00Z',
              answersAvailableAt: '2026-11-16T10:00:00Z',
              feedback: [],
            },
          ]
        : [],
    ),
  );

  render(<StudentClassroom />);
  const tabs = await screen.findByRole('navigation', { name: 'Các mục trong lớp' });
  fireEvent.click(within(tabs).getByRole('button', { name: /Bài học/ }));
  const lesson = screen.getByText('Bài giảng phân số').closest('.classroom-lesson-row') as HTMLElement;
  await within(lesson).findByText('Luyện tập phân số');
  expect(within(lesson).queryByText('Kiểm tra hình học')).toBeNull();
  fireEvent.click(within(lesson).getByRole('button', { name: 'Tiếp tục làm' }));
  expect(navigateMock).toHaveBeenCalledWith('/student/classes/3/exams/8/take');
  fireEvent.click(await within(lesson).findByRole('button', { name: 'Xem kết quả' }));
  expect(navigateMock).toHaveBeenCalledWith('/student/classes/3/exams/8/attempts/11/result');
});
