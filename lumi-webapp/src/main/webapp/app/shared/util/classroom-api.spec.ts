import axios from 'axios';
import { afterEach, expect, it, vi } from 'vitest';
import { submitClassAssignment } from './classroom-api';

vi.mock('axios', () => ({ default: { post: vi.fn() } }));

afterEach(() => vi.clearAllMocks());

it('posts a student assignment file and optional text as multipart data', async () => {
  const submission = { id: 15, assignmentId: 9, attachments: [{ fileName: 'answer.pdf', fileType: 'PDF', sizeBytes: 20 }] };
  vi.mocked(axios.post).mockResolvedValue({ data: { status: 201, code: 1000, message: 'Thành công', data: submission } });
  const file = new File(['%PDF-1.4\n%%EOF'], 'answer.pdf', { type: 'application/pdf' });

  const result = await submitClassAssignment(3, 9, '', file);

  expect(result).toBe(submission);
  const [url, payload] = vi.mocked(axios.post).mock.calls[0];
  expect(url).toBe('api/student/classes/3/assignments/9/submission/with-file');
  expect(payload).toBeInstanceOf(FormData);
  expect((payload as FormData).get('content')).toBe('');
  expect((payload as FormData).get('file')).toBe(file);
});
