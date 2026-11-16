import './password.scss';

import React, { useEffect } from 'react';
import { useNavigate } from 'react-router';
import { toast } from 'react-toastify';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getSession, logout } from 'app/shared/reducers/authentication';

import SettingsLayout from '../settings-layout/settings-layout';
import ChangePasswordForm from './change-password-form';
import { reset, savePassword } from './password.reducer';

export const PasswordPage: React.FC = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  const loading = useAppSelector(state => state.password.loading);
  const errorMessage = useAppSelector(state => state.password.errorMessage);

  useEffect(() => {
    dispatch(reset());
    dispatch(getSession());
    return () => {
      dispatch(reset());
    };
  }, []);

  useEffect(() => {
    if (errorMessage) {
      toast.error('Đổi mật khẩu thất bại, vui lòng kiểm tra lại mật khẩu hiện tại!');
    }
  }, [errorMessage]);

  const handleSubmit = async (data: { currentPassword: string; newPassword: string }) => {
    try {
      await dispatch(savePassword(data)).unwrap();
      dispatch(logout());
      toast.success('Đổi mật khẩu thành công. Vui lòng đăng nhập lại.');
      navigate('/login');
    } catch {
      // The rejected action updates errorMessage for the notification above.
    }
  };

  return (
    <SettingsLayout
      activeTab="change-password"
      pageTitle="Cài đặt tài khoản"
      pageSubtitle="Quản lý thông tin cá nhân và bảo mật tài khoản học tập của bạn trên LUMI."
      currentBreadcrumb="Đổi mật khẩu"
    >
      <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
        <ChangePasswordForm onSubmit={handleSubmit} isLoading={loading} />
      </div>
    </SettingsLayout>
  );
};

export default PasswordPage;
