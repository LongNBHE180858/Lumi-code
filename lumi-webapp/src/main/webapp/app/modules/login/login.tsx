import './login.scss';

import React, { useState } from 'react';
import { Navigate, useLocation } from 'react-router';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { login, resetTwoFactor } from 'app/shared/reducers/authentication';

import LoginModal from './login-modal';
import { Authority } from 'app/shared/jhipster/constants';
import { HeroSection } from './hero-section';
import { LoginForm } from './login-form';
import { TwoFactorModal } from './two-factor-modal';

export const Login: React.FC = () => {
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(state => state.authentication.isAuthenticated);
  const account = useAppSelector(state => state.authentication.account);
  const loginError = useAppSelector(state => state.authentication.loginError);
  const loading = useAppSelector(state => state.authentication.loading);
  const errorMessage = useAppSelector(state => state.authentication.errorMessage);
  const twoFactorRequired = useAppSelector(state => state.authentication.twoFactorRequired);
  const twoFactorEmail = useAppSelector(state => state.authentication.twoFactorEmail);
  const pageLocation = useLocation();

  const [rememberMeState, setRememberMeState] = useState(false);

  const handleLogin = (username: string, password: string, rememberMe = false) => {
    setRememberMeState(rememberMe);
    dispatch(login(username, password, rememberMe));
  };

  const { from } = pageLocation.state || {
    from: { pathname: '/', search: pageLocation.search },
  };

  if (isAuthenticated) {
    const isAdmin = account.authorities?.includes(Authority.ADMIN);
    return <Navigate to={isAdmin ? '/admin/dashboard' : from} replace />;
  }

  return (
    <div className="lumi-login-page" id="login-page">
      <div className="login-main-grid">
        <HeroSection />

        <LoginForm handleLogin={handleLogin} loginError={loginError} loading={loading} errorMessage={errorMessage} />
      </div>

      <TwoFactorModal
        isOpen={Boolean(twoFactorRequired && twoFactorEmail)}
        email={twoFactorEmail || ''}
        rememberMe={rememberMeState}
        onClose={() => dispatch(resetTwoFactor())}
      />
    </div>
  );
};

export default Login;
