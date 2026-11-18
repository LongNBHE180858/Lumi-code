import React from 'react';
import { cleanup, fireEvent, render, screen, waitFor, within } from '@testing-library/react';
import { afterEach, beforeEach, expect, it, vi } from 'vitest';
import QuestionBankView from './question-bank';
import {
  addBankQuestion,
  getBankQuestions,
  getBankTopics,
  getQuestionBanks,
  updateBankQuestion,
  type QuestionItem,
} from 'app/shared/util/question-bank-api';

vi.mock('app/shared/util/question-bank-api', () => ({
  addBankQuestion: vi.fn(),
  createQuestionBank: vi.fn(),
  downloadBankTemplate: vi.fn(),
  getBankQuestions: vi.fn(),
  getBankTopics: vi.fn(),
  getQuestionBanks: vi.fn(),
  importBankExcel: vi.fn(),
  updateBankQuestion: vi.fn(),
}));

const question: QuestionItem = {
  id: 11,
  content: 'Một cộng một?',
  difficulty: 1,
  topic: 'Phân số',
  explanation: 'Cộng hai số.',
  options: [
    { id: 1, content: 'Một', isCorrect: false, orderIndex: 0 },
    { id: 2, content: 'Hai', isCorrect: true, orderIndex: 1 },
    { id: 3, content: 'Ba', isCorrect: false, orderIndex: 2 },
  ],
};

beforeEach(() => {
  vi.mocked(getQuestionBanks).mockResolvedValue([
    { id: 3, name: 'Ngân hàng Toán', classId: 5, className: 'Toán 9', questionCount: 1, updatedAt: '2026-11-17T00:00:00Z' },
  ]);
  vi.mocked(getBankQuestions).mockResolvedValue([question]);
  vi.mocked(getBankTopics).mockResolvedValue([
    { topic: 'Phân số', totalQuestions: 1, difficulty1: 1, difficulty2: 0, difficulty3: 0, difficulty4: 0, difficulty5: 0 },
  ]);
  vi.mocked(addBankQuestion).mockResolvedValue(question);
  vi.mocked(updateBankQuestion).mockResolvedValue({ ...question, id: 12 });
});

afterEach(() => {
  cleanup();
  vi.clearAllMocks();
});

it('creates a question with two choices from an existing topic', async () => {
  render(<QuestionBankView classId={5} />);
  fireEvent.click(await screen.findByRole('button', { name: '+ Thêm câu hỏi thủ công' }));
  const dialog = screen.getByRole('dialog', { name: 'Thêm câu hỏi' });
  fireEvent.change(within(dialog).getByLabelText('Nội dung câu hỏi'), { target: { value: 'Câu mới?' } });
  fireEvent.change(within(dialog).getByLabelText('Chủ đề:'), { target: { value: 'Phân số' } });
  fireEvent.click(within(dialog).getByRole('button', { name: 'Xóa lựa chọn C' }));
  fireEvent.click(within(dialog).getByRole('button', { name: 'Xóa lựa chọn C' }));
  fireEvent.change(within(dialog).getByLabelText('Nội dung lựa chọn A'), { target: { value: 'Sai' } });
  fireEvent.change(within(dialog).getByLabelText('Nội dung lựa chọn B'), { target: { value: 'Đúng' } });
  fireEvent.click(within(dialog).getAllByRole('radio')[1]);
  fireEvent.click(within(dialog).getByRole('button', { name: 'Lưu câu hỏi' }));

  await waitFor(() =>
    expect(addBankQuestion).toHaveBeenCalledWith(
      3,
      expect.objectContaining({ topic: 'Phân số', options: ['Sai', 'Đúng'], correctIndex: 1 }),
    ),
  );
});

it('edits a bank question with a custom topic and renumbers the correct choice after removal', async () => {
  render(<QuestionBankView classId={5} />);
  fireEvent.click(await screen.findByRole('button', { name: 'Sửa câu hỏi' }));
  const dialog = screen.getByRole('dialog', { name: 'Sửa câu hỏi' });
  fireEvent.change(within(dialog).getByLabelText('Chủ đề:'), { target: { value: 'custom' } });
  fireEvent.change(within(dialog).getByLabelText('Chủ đề khác'), { target: { value: 'Hình học' } });
  fireEvent.click(within(dialog).getByRole('button', { name: 'Xóa lựa chọn A' }));
  fireEvent.click(within(dialog).getByRole('button', { name: 'Lưu thay đổi' }));

  await waitFor(() =>
    expect(updateBankQuestion).toHaveBeenCalledWith(
      3,
      11,
      expect.objectContaining({ topic: 'Hình học', options: ['Hai', 'Ba'], correctIndex: 0 }),
    ),
  );
});

it('adds a fifth choice when composing a question', async () => {
  render(<QuestionBankView classId={5} />);
  fireEvent.click(await screen.findByRole('button', { name: '+ Thêm câu hỏi thủ công' }));
  const dialog = screen.getByRole('dialog', { name: 'Thêm câu hỏi' });
  fireEvent.click(within(dialog).getByRole('button', { name: '+ Thêm lựa chọn' }));
  expect(within(dialog).getByLabelText('Nội dung lựa chọn E')).toBeTruthy();
  fireEvent.change(within(dialog).getByLabelText('Nội dung câu hỏi'), { target: { value: 'Câu có năm đáp án?' } });
  for (const [index, value] of ['Một', 'Hai', 'Ba', 'Bốn', 'Năm'].entries()) {
    fireEvent.change(within(dialog).getByLabelText(`Nội dung lựa chọn ${String.fromCharCode(65 + index)}`), { target: { value } });
  }
  fireEvent.click(within(dialog).getByRole('button', { name: 'Lưu câu hỏi' }));
  await waitFor(() =>
    expect(addBankQuestion).toHaveBeenCalledWith(3, expect.objectContaining({ options: ['Một', 'Hai', 'Ba', 'Bốn', 'Năm'] })),
  );
});
