import { useCallback, useEffect, useState } from 'react';

import { useAppSelector } from 'app/config/store';

import { getMyLinkedStudents, getMyProfile, getMyStatistics } from './profile.api';
import type { AccountProfile } from './profile.model';

interface ProfileState<T> {
  accountId: number | null;
  data: T | null;
  loading: boolean;
  error: string | null;
}

// Ba phần của trang dùng chung cách tải, hủy request và xử lý lỗi.
const useProfileResource = <T>(load: (signal: AbortSignal, accountId: number) => Promise<T>, enabled: boolean) => {
  const accountId: number | null = useAppSelector(state =>
    state.authentication.isAuthenticated ? (state.authentication.account.id ?? null) : null,
  );

  const [state, setState] = useState<ProfileState<T>>({
    accountId: null,
    data: null,
    loading: false,
    error: null,
  });
  const [reloadCount, setReloadCount] = useState(0);
  const reload = useCallback(() => setReloadCount(count => count + 1), []);

  useEffect(() => {
    if (!enabled || accountId === null) {
      return;
    }

    const controller = new AbortController();

    const loadProfile = async () => {
      // Bắt đầu tải.
      setState({
        accountId,
        data: null,
        loading: true,
        error: null,
      });

      try {
        const data = await load(controller.signal, accountId);

        // Tải thành công.
        if (!controller.signal.aborted) {
          setState({
            accountId,
            data,
            loading: false,
            error: null,
          });
        }
      } catch {
        // Tải thất bại; không báo lỗi nếu chủ động hủy request.
        if (!controller.signal.aborted) {
          setState({
            accountId,
            data: null,
            loading: false,
            error: 'Không tải được dữ liệu. Vui lòng thử lại.',
          });
        }
      }
    };

    void loadProfile();

    // Rời trang hoặc đổi tài khoản thì hủy request cũ.
    return () => controller.abort();
  }, [accountId, enabled, load, reloadCount]);

  const isCurrentAccount = enabled && accountId !== null && state.accountId === accountId;

  return {
    data: isCurrentAccount ? state.data : null,
    loading: enabled && accountId !== null && (!isCurrentAccount || state.loading),
    error: isCurrentAccount ? state.error : null,
    reload,
  };
};

const loadMyProfile = async (signal: AbortSignal, accountId: number): Promise<AccountProfile> => {
  const profile = await getMyProfile(signal);
  if (profile.id !== accountId) {
    throw new Error('Hồ sơ không khớp tài khoản.');
  }
  return profile;
};

export const useProfile = (enabled = true) => useProfileResource(loadMyProfile, enabled);

export const useProfileStatistics = (enabled: boolean) => useProfileResource(getMyStatistics, enabled);

export const useLinkedStudents = (enabled: boolean) => useProfileResource(getMyLinkedStudents, enabled);
