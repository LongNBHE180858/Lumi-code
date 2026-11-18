import React from 'react';
import { cleanup, fireEvent, render, screen, waitFor } from '@testing-library/react';
import { afterEach, expect, it, vi } from 'vitest';
import ClassAssignmentReview from './class-assignment-review';
import { getClassAssignmentSubmissions, getClassAssignmentSubmissionFileAccess, gradeClassAssignment } from 'app/shared/util/classroom-api';

vi.mock('app/shared/util/classroom-api', () => ({
  getClassAssignmentSubmissions: vi.fn(),
  gradeClassAssignment: vi.fn(),
  getClassAssignmentSubmissionFileAccess: vi.fn(),
  classroomError: (error: Error) => error.message,
}));

afterEach(() => {
  cleanup();
  vi.clearAllMocks();
});

it('lets the instructor open an uploaded student submission', async () => {
  vi.mocked(getClassAssignmentSubmissions).mockResolvedValue({
    assignedCount: 1,
    items: [
      {
        id: 15,
        assignmentId: 9,
        studentId: 7,
        studentName: 'An',
        content: '',
        status: 'SUBMITTED',
        score: null,
        feedback: null,
        submittedAt: '2026-11-16T10:00:00Z',
        gradedAt: null,
        version: 0,
        attachments: [{ fileName: 'solution.pdf', fileType: 'PDF', sizeBytes: 20 }],
      },
    ],
  });
  vi.mocked(getClassAssignmentSubmissionFileAccess).mockResolvedValue({ url: 'about:blank', expiresAt: '2099-01-01T00:00:00Z' });

  render(<ClassAssignmentReview classId={3} assignmentId={9} />);
  fireEvent.click(await screen.findByRole('button', { name: /An/ }));
  expect(screen.getByText('Tệp bài làm: solution.pdf')).toBeTruthy();
  fireEvent.click(screen.getByRole('button', { name: 'Xem PDF' }));
  await waitFor(() => expect(getClassAssignmentSubmissionFileAccess).toHaveBeenCalledWith(3, 9, 15));
  expect(await screen.findByRole('dialog', { name: 'Xem tài liệu: solution.pdf' })).toBeTruthy();
});

it('loads a submission and sends its current version when grading', async () => {
  const submission = {
    id: 15,
    assignmentId: 9,
    studentId: 7,
    studentName: 'An',
    content: 'Bài làm',
    status: 'SUBMITTED' as const,
    score: null,
    feedback: null,
    submittedAt: '2026-11-16T10:00:00Z',
    gradedAt: null,
    version: 2,
  };
  vi.mocked(getClassAssignmentSubmissions).mockResolvedValue({ items: [submission], assignedCount: 3 });
  vi.mocked(gradeClassAssignment).mockResolvedValue({ ...submission, status: 'GRADED', score: 80, feedback: 'Cần ôn thêm' });

  render(<ClassAssignmentReview classId={3} assignmentId={9} />);
  fireEvent.click(await screen.findByRole('button', { name: /An/ }));
  expect(screen.getByText('Bài làm')).toBeTruthy();
  fireEvent.change(screen.getByLabelText('Điểm'), { target: { value: '80' } });
  fireEvent.change(screen.getByLabelText('Nhận xét'), { target: { value: 'Cần ôn thêm' } });
  fireEvent.click(screen.getByRole('button', { name: 'Lưu kết quả' }));

  await waitFor(() => expect(gradeClassAssignment).toHaveBeenCalledWith(3, 9, 15, 80, 'Cần ôn thêm', 2));
  expect(await screen.findByText('Đã lưu điểm và nhận xét.')).toBeTruthy();
});
