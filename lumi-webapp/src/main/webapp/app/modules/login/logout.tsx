import React, { useLayoutEffect } from 'react';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { logoutWithApi } from 'app/shared/reducers/authentication';

export const Logout = () => {
  const authentication = useAppSelector(state => state.authentication);
  const dispatch = useAppDispatch();

  useLayoutEffect(() => {
    dispatch(logoutWithApi());
  }, [dispatch]);

  useLayoutEffect(() => {
    if (authentication.logoutUrl) {
      globalThis.location.href = authentication.logoutUrl;
    } else if (!authentication.isAuthenticated) {
      globalThis.location.href = '/';
    }
  }, [authentication]);

  return (
    <div
      style={{ minHeight: '60vh', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', gap: '16px' }}
    >
      <div className="spinner-border text-primary" role="status">
        <span className="visually-hidden">Loading...</span>
      </div>
      <p style={{ color: '#6c757d', fontWeight: 500 }}>Đang đăng xuất khỏi hệ thống...</p>
    </div>
  );
};

export default Logout;
