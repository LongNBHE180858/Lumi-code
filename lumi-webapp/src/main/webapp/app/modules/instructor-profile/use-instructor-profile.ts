import { useCallback, useEffect, useState } from 'react';
import axios from 'axios';

import { getInstructorCourses, getInstructorProfile } from './instructor-profile.api';

interface ResourceState<T> {
  id: number | null;
  revision: number;
  data: T | null;
  loading: boolean;
  notFound: boolean;
  error: string | null;
}

// Không phụ thuộc phiên đăng nhập; hủy request và ẩn dữ liệu cũ khi đổi giảng viên.
const useInstructorResource = <T>(id: number | null, load: (id: number, signal: AbortSignal) => Promise<T>) => {
  const [revision, setRevision] = useState(0);
  const [state, setState] = useState<ResourceState<T>>({
    id: null,
    revision: 0,
    data: null,
    loading: false,
    notFound: false,
    error: null,
  });
  const reload = useCallback(() => setRevision(value => value + 1), []);

  useEffect(() => {
    if (id === null) return;
    const controller = new AbortController();
    setState({ id, revision, data: null, loading: true, notFound: false, error: null });

    void load(id, controller.signal).then(
      data => {
        if (!controller.signal.aborted) {
          setState({ id, revision, data, loading: false, notFound: false, error: null });
        }
      },
      (error: unknown) => {
        if (!controller.signal.aborted) {
          const notFound = axios.isAxiosError(error) && error.response?.status === 404;
          setState({
            id,
            revision,
            data: null,
            loading: false,
            notFound,
            error: notFound ? 'Không tìm thấy hồ sơ giảng viên.' : 'Không tải được dữ liệu. Vui lòng thử lại.',
          });
        }
      },
    );
    return () => controller.abort();
  }, [id, load, revision]);

  const current = id !== null && state.id === id && state.revision === revision;
  return {
    data: current ? state.data : null,
    loading: id !== null && (!current || state.loading),
    error: current ? state.error : null,
    notFound: current && state.notFound,
    reload,
  };
};

export const useInstructorProfile = (id: number | null) => useInstructorResource(id, getInstructorProfile);
export const useInstructorCourses = (id: number | null) => useInstructorResource(id, getInstructorCourses);
