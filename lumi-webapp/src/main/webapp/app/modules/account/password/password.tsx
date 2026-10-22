import './password.scss';

import React, { useEffect, useState } from 'react';
import { toast } from 'react-toastify';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getSession } from 'app/shared/reducers/authentication';

import SettingsLayout from '../settings-layout/settings-layout';
import ChangePasswordForm from './change-password-form';
import { reset, savePassword } from './password.reducer';

export const PasswordPage: React.FC = () => {
  const dispatch = useAppDispatch();
  const [successNotice, setSuccessNotice] = useState<string | null>(null);

  const loading = useAppSelector(state => state.password.loading);
  const successMessage = useAppSelector(state => state.password.successMessage);
  const errorMessage = useAppSelector(state => state.password.errorMessage);

  useEffect(() => {
    dispatch(reset());
    dispatch(getSession());
    return () => {
      dispatch(reset());
    };
  }, []);

  useEffect(() => {
    if (successMessage) {
      setSuccessNotice('Mật khẩu của bạn đã được thay đổi thành công!');
      toast.success('Đổi mật khẩu thành công!');
    } else if (errorMessage) {
      toast.error('Đổi mật khẩu thất bại, vui lòng kiểm tra lại mật khẩu hiện tại!');
    }
  }, [successMessage, errorMessage]);

  const handleSubmit = (data: { currentPassword: string; newPassword: string }) => {
    setSuccessNotice(null);
    dispatch(savePassword(data));
  };

  return (
    <SettingsLayout
      activeTab="change-password"
      pageTitle="Cài đặt tài khoản"
      pageSubtitle="Quản lý thông tin cá nhân và bảo mật tài khoản học tập của bạn trên LUMI."
      currentBreadcrumb="Đổi mật khẩu"
    >
      <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
        {successNotice && (
          <div className="login-alert-success fade-in" role="alert">
            <span>🎉 {successNotice}</span>
          </div>
        )}
        <ChangePasswordForm onSubmit={handleSubmit} isLoading={loading} />
      </div>
    </SettingsLayout>
  );
};

export default PasswordPage;
