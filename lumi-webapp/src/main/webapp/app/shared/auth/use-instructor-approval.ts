import { useCallback, useEffect, useState } from 'react';

import { getMyProfile } from 'app/modules/profile/profile.api';

type ApprovalState = {
  accountId: number | null;
  status: string | null;
  loading: boolean;
  error: boolean;
};

export const useInstructorApproval = (accountId: number | null, enabled: boolean) => {
  const [state, setState] = useState<ApprovalState>({ accountId: null, status: null, loading: false, error: false });
  const [reloadCount, setReloadCount] = useState(0);
  const reload = useCallback(() => setReloadCount(count => count + 1), []);

  useEffect(() => {
    if (!enabled || accountId === null) return;

    const controller = new AbortController();
    setState({ accountId, status: null, loading: true, error: false });

    getMyProfile(controller.signal)
      .then(profile => {
        if (!controller.signal.aborted) {
          setState({
            accountId,
            status: profile.id === accountId ? profile.verificationStatus : null,
            loading: false,
            error: profile.id !== accountId,
          });
        }
      })
      .catch(() => {
        if (!controller.signal.aborted) setState({ accountId, status: null, loading: false, error: true });
      });

    return () => controller.abort();
  }, [accountId, enabled, reloadCount]);

  const current = enabled && accountId !== null && state.accountId === accountId;
  return {
    approved: current && state.status === 'APPROVED',
    loading: enabled && accountId !== null && (!current || state.loading),
    error: current && state.error,
    status: current ? state.status : null,
    reload,
  };
};
