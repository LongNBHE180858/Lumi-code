import axios from 'axios';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { catalogApi } from './catalog-api';

vi.mock('axios', () => ({ default: { get: vi.fn(), post: vi.fn(), put: vi.fn() } }));

describe('catalogApi', () => {
  beforeEach(() => vi.clearAllMocks());

  it('reads separate subject and grade payloads', async () => {
    vi.mocked(axios.get)
      .mockResolvedValueOnce({ data: [{ id: 1, name: 'Toán', description: 'Môn học' }] })
      .mockResolvedValueOnce({ data: [{ id: 2, name: 'Lớp 12', level: 12 }] });

    expect(await catalogApi.list('subjects')).toEqual([{ id: 1, name: 'Toán', description: 'Môn học', level: null }]);
    expect(await catalogApi.list('grades')).toEqual([{ id: 2, name: 'Lớp 12', level: 12, description: null }]);
    expect(axios.get).toHaveBeenNthCalledWith(1, 'api/admin/catalog/subjects');
    expect(axios.get).toHaveBeenNthCalledWith(2, 'api/admin/catalog/grades');
  });

  it('sends only the fields belonging to each type', async () => {
    await catalogApi.create('subjects', { name: 'Toán', description: 'Môn học', level: null });
    await catalogApi.update('grades', 2, { name: 'Lớp 12', description: null, level: 12 });

    expect(axios.post).toHaveBeenCalledWith('api/admin/catalog/subjects', { name: 'Toán', description: 'Môn học' });
    expect(axios.put).toHaveBeenCalledWith('api/admin/catalog/grades/2', { name: 'Lớp 12', level: 12 });
  });
});
