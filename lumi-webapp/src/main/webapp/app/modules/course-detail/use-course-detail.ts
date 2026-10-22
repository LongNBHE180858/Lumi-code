import { useCallback, useEffect, useState } from 'react';
import axios from 'axios';

import { getCourseOverview, getCourseReviews } from './course-detail.api';
import type { CourseReviewSort } from './course-detail.model';

interface ResourceState<T> {
  key: string | null;
  revision: number;
  data: T | null;
  loading: boolean;
  notFound: boolean;
  error: string | null;
}

const useCourseResource = <T>(key: string | null, load: (signal: AbortSignal) => Promise<T>) => {
  const [revision, setRevision] = useState(0);
  const [state, setState] = useState<ResourceState<T>>({
    key: null,
    revision: 0,
    data: null,
    loading: false,
    notFound: false,
    error: null,
  });
  const reload = useCallback(() => setRevision(value => value + 1), []);

  useEffect(() => {
    if (key === null) return;
    const controller = new AbortController();
    setState({ key, revision, data: null, loading: true, notFound: false, error: null });
    void load(controller.signal).then(
      data => {
        if (!controller.signal.aborted) setState({ key, revision, data, loading: false, notFound: false, error: null });
      },
      (error: unknown) => {
        if (controller.signal.aborted) return;
        const notFound = axios.isAxiosError(error) && error.response?.status === 404;
        setState({
          key,
          revision,
          data: null,
          loading: false,
          notFound,
          error: notFound ? 'Khóa học không tồn tại hoặc không còn được công khai.' : 'Không tải được dữ liệu. Vui lòng thử lại.',
        });
      },
    );
    return () => controller.abort();
  }, [key, load, revision]);

  const current = key !== null && state.key === key && state.revision === revision;
  return {
    data: current ? state.data : null,
    loading: key !== null && (!current || state.loading),
    error: current ? state.error : null,
    notFound: current && state.notFound,
    reload,
  };
};

export const useCourseOverview = (id: number | null) => {
  const load = useCallback((signal: AbortSignal) => getCourseOverview(id!, signal), [id]);
  return useCourseResource(id === null ? null : String(id), load);
};

export const useCourseReviews = (id: number, page: number, sort: CourseReviewSort) => {
  const load = useCallback((signal: AbortSignal) => getCourseReviews(id, page, sort, signal), [id, page, sort]);
  return useCourseResource(`${id}:${page}:${sort}`, load);
};
