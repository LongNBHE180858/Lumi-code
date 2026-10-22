import { useCallback, useEffect, useState } from 'react';

import { useAppSelector } from 'app/config/store';
import { getCourseFilterOptions, getCourses } from './courses.api';
import type { CourseBrowseRequest, CourseBrowseResponse, CourseFilterOptions } from './courses.model';

interface RequestState<T> {
  key: string;
  data: T | null;
  loading: boolean;
  error: string | null;
}

export const useCourseBrowse = (filters: CourseBrowseRequest) => {
  const accountId: number | null = useAppSelector(state =>
    state.authentication.isAuthenticated ? (state.authentication.account.id ?? null) : null,
  );
  const key = JSON.stringify({ accountId, ...filters });
  const [state, setState] = useState<RequestState<CourseBrowseResponse>>({ key: '', data: null, loading: false, error: null });
  const [reloadCount, setReloadCount] = useState(0);
  const reload = useCallback(() => setReloadCount(count => count + 1), []);

  useEffect(() => {
    if (accountId === null) return;
    const controller = new AbortController();
    setState({ key, data: null, loading: true, error: null });

    const load = async () => {
      try {
        const data = await getCourses(filters, controller.signal);
        if (!controller.signal.aborted) setState({ key, data, loading: false, error: null });
      } catch {
        if (!controller.signal.aborted) {
          setState({ key, data: null, loading: false, error: 'Không tải được danh sách khóa học. Vui lòng thử lại.' });
        }
      }
    };
    void load();
    return () => controller.abort();
  }, [key, reloadCount]);

  const current = accountId !== null && state.key === key;
  return {
    data: current ? state.data : null,
    loading: accountId !== null && (!current || state.loading),
    error: current ? state.error : null,
    reload,
  };
};

export const useCourseFilterOptions = () => {
  const accountId: number | null = useAppSelector(state =>
    state.authentication.isAuthenticated ? (state.authentication.account.id ?? null) : null,
  );
  const key = String(accountId);
  const [state, setState] = useState<RequestState<CourseFilterOptions>>({ key: '', data: null, loading: false, error: null });
  const [reloadCount, setReloadCount] = useState(0);
  const reload = useCallback(() => setReloadCount(count => count + 1), []);

  useEffect(() => {
    if (accountId === null) return;
    const controller = new AbortController();
    setState({ key, data: null, loading: true, error: null });

    const load = async () => {
      try {
        const data = await getCourseFilterOptions(controller.signal);
        if (!controller.signal.aborted) setState({ key, data, loading: false, error: null });
      } catch {
        if (!controller.signal.aborted) {
          setState({ key, data: null, loading: false, error: 'Không tải được danh mục môn học và khối lớp.' });
        }
      }
    };
    void load();
    return () => controller.abort();
  }, [accountId, reloadCount]);

  const current = accountId !== null && state.key === key;
  return {
    data: current ? state.data : null,
    loading: accountId !== null && (!current || state.loading),
    error: current ? state.error : null,
    reload,
  };
};
