import React from 'react';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import AdminCatalog from './admin-catalog';
import { catalogApi } from './catalog-api';

vi.mock('../layout/admin-layout', () => ({ default: ({ children }: { children: React.ReactNode }) => <div>{children}</div> }));
vi.mock('./catalog-api', () => ({
  catalogApi: {
    list: vi.fn(),
    create: vi.fn(),
    update: vi.fn(),
  },
}));

describe('AdminCatalog', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    vi.mocked(catalogApi.list).mockImplementation(kind =>
      Promise.resolve({
        data:
          kind === 'subjects'
            ? [{ id: 1, name: 'Toán', description: null, level: null }]
            : [{ id: 2, name: 'Lớp 1', description: null, level: 1 }],
      } as never),
    );
  });

  it('opens subjects first and only shows subject and grade management', async () => {
    render(<AdminCatalog />);

    expect(await screen.findByText('Toán')).toBeTruthy();
    expect(catalogApi.list).toHaveBeenCalledWith('subjects');
    expect(screen.queryByRole('button', { name: 'Danh mục khóa học' })).toBeNull();
    expect(screen.queryByText('Trạng thái')).toBeNull();
    expect(screen.queryByRole('button', { name: /Kích hoạt|Tạm ngưng/ })).toBeNull();

    fireEvent.click(screen.getByRole('button', { name: 'Khối lớp' }));
    await waitFor(() => expect(catalogApi.list).toHaveBeenCalledWith('grades'));
    expect((await screen.findAllByText('Lớp 1')).length).toBe(2);
  });
});
