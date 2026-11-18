import React from 'react';
import { cleanup, fireEvent, render, screen } from '@testing-library/react';
import { afterEach, expect, it, vi } from 'vitest';
import InstructorPagination from './instructor-pagination';

afterEach(cleanup);

it('shows the same page controls for instructor lists', () => {
  const onPageChange = vi.fn();
  render(
    <InstructorPagination
      page={1}
      totalPages={4}
      totalItems={32}
      itemLabel="học sinh"
      label="Phân trang học sinh"
      onPageChange={onPageChange}
    />,
  );
  expect(screen.getByText('Trang 2/4 · 32 học sinh')).toBeTruthy();
  expect(screen.getByRole('button', { name: 'Trang 2' }).getAttribute('aria-current')).toBe('page');
  fireEvent.click(screen.getByRole('button', { name: 'Sau' }));
  expect(onPageChange).toHaveBeenCalledWith(2);
});
