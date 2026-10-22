import React from 'react';

import LoginForm from './login-form';

export interface ILoginModalProps {
  showModal?: boolean;
  loginError: boolean;
  handleLogin: (username: string, password: string, rememberMe?: boolean) => void;
  handleClose?: () => void;
  loading?: boolean;
}

export const LoginModal: React.FC<ILoginModalProps> = ({ handleLogin, loginError, loading = false }) => {
  return <LoginForm handleLogin={handleLogin} loginError={loginError} loading={loading} />;
};

export default LoginModal;
