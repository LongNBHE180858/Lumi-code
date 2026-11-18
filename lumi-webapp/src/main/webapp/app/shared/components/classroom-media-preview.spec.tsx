import React from 'react';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { expect, it, vi } from 'vitest';
import ClassroomMediaPreview from './classroom-media-preview';
import { getClassAssignmentFileAccess } from 'app/shared/util/classroom-api';

vi.mock('app/shared/util/classroom-api', () => ({
  getClassAssignmentFileAccess: vi.fn(),
  getClassMaterialAccess: vi.fn(),
  classroomError: (error: Error) => error.message,
}));

it('loads a signed video URL only after the user opens the assignment', async () => {
  vi.mocked(getClassAssignmentFileAccess).mockResolvedValue({ url: 'about:blank', expiresAt: new Date().toISOString() });
  render(<ClassroomMediaPreview classId={3} itemId={9} kind="assignment" fileType="VIDEO" title="Video bài tập" />);

  expect(getClassAssignmentFileAccess).not.toHaveBeenCalled();
  fireEvent.click(screen.getByRole('button', { name: 'Xem video' }));

  await waitFor(() => expect(getClassAssignmentFileAccess).toHaveBeenCalledWith(3, 9));
  expect(screen.getByLabelText('Video bài tập').getAttribute('src')).toBe('about:blank');
});
