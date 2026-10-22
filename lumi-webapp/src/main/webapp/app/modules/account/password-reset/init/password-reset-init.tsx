import '../password-reset.scss';

import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router';
import { toast } from 'react-toastify';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { handlePasswordResetInit, reset } from '../password-reset.reducer';

export const PasswordResetInit: React.FC = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  const loading = useAppSelector(state => state.passwordReset.loading);
  const resetPasswordSuccess = useAppSelector(state => state.passwordReset.resetPasswordSuccess);
  const errorMessage = useAppSelector(state => state.passwordReset.errorMessage);

  const [email, setEmail] = useState('');
  const [clientError, setClientError] = useState<string | null>(null);
  const [isSuccessLocal, setIsSuccessLocal] = useState(false);

  useEffect(() => {
    dispatch(reset());
    return () => {
      dispatch(reset());
    };
  }, [dispatch]);

  useEffect(() => {
    if (resetPasswordSuccess && isSuccessLocal) {
      toast.success('Mã xác minh đã được gửi thành công đến email của bạn!');
      const trimmedEmail = email.trim();
      const timer = setTimeout(() => {
        navigate(`/account/reset/finish?email=${encodeURIComponent(trimmedEmail)}`, {
          state: { email: trimmedEmail },
        });
      }, 1200);
      return () => clearTimeout(timer);
    }
    return undefined;
  }, [resetPasswordSuccess, isSuccessLocal, email, navigate]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setClientError(null);

    const trimmedEmail = email.trim();
    if (!trimmedEmail) {
      setClientError('Vui lòng nhập địa chỉ email đã đăng ký.');
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(trimmedEmail)) {
      setClientError('Địa chỉ email không hợp lệ. Vui lòng kiểm tra lại.');
      return;
    }

    setIsSuccessLocal(true);
    dispatch(handlePasswordResetInit(trimmedEmail));
  };

  const currentError = clientError || errorMessage;

  return (
    <div className="password-reset-page-wrapper">
      <div className="auth-card-container">
        <div className="auth-card forgot-password-card">
          {/* Header Introduction */}
          <div className="auth-card-intro">
            <div className="auth-icon-badge" aria-hidden="true">
              {/* Mail Icon matching Figma Vector */}
              <svg
                width="24"
                height="24"
                viewBox="0 0 24 24"
                fill="none"
                stroke="#24A8E8"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
              >
                <rect x="2" y="4" width="20" height="16" rx="2" />
                <path d="m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7" />
              </svg>
            </div>

            <h1 className="auth-card-title">Quên mật khẩu?</h1>
            <p className="auth-card-desc">
              Nhập email đã đăng ký với tài khoản LUMI. Chúng tôi sẽ gửi mã xác minh đến email của bạn để đặt lại mật khẩu.
            </p>
          </div>

          {/* Error alert */}
          {currentError && (
            <div className="auth-error-alert" role="alert">
              <svg
                width="18"
                height="18"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
              >
                <circle cx="12" cy="12" r="10" />
                <line x1="12" y1="8" x2="12" y2="12" />
                <line x1="12" y1="16" x2="12.01" y2="16" />
              </svg>
              <span>{currentError}</span>
            </div>
          )}

          {/* Success alert */}
          {resetPasswordSuccess && isSuccessLocal && (
            <div className="auth-success-alert" role="status">
              <svg
                width="18"
                height="18"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
              >
                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14" />
                <polyline points="22 4 12 14.01 9 11.01" />
              </svg>
              <span>
                Mã xác minh đã được gửi đến <strong>{email}</strong>. Đang chuyển hướng...
              </span>
            </div>
          )}

          <form onSubmit={handleSubmit} className="auth-form-body" noValidate>
            {/* Email Field */}
            <div className="form-field-group">
              <label htmlFor="forgot-email" className="field-label">
                Email <span className="field-required">*</span>
              </label>
              <div className="field-input-wrapper">
                <input
                  id="forgot-email"
                  type="email"
                  className={`field-input ${currentError ? 'input-error' : ''}`}
                  placeholder="Nhập email đã đăng ký"
                  value={email}
                  onChange={e => {
                    setEmail(e.target.value);
                    if (clientError) setClientError(null);
                  }}
                  autoComplete="email"
                  autoFocus
                  disabled={loading}
                />
              </div>
            </div>

            {/* Note */}
            <div className="auth-info-note">
              <div className="auth-info-note-icon" aria-hidden="true">
                <svg
                  width="20"
                  height="20"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="#24A8E8"
                  strokeWidth="2"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                >
                  <circle cx="12" cy="12" r="10" />
                  <line x1="12" y1="16" x2="12" y2="12" />
                  <line x1="12" y1="8" x2="12.01" y2="8" />
                </svg>
              </div>
              <div className="auth-info-note-text">Mã xác minh sẽ được gửi đến email đã đăng ký với tài khoản.</div>
            </div>

            {/* Submit Button */}
            <button type="submit" className="auth-submit-btn" disabled={loading}>
              {loading ? (
                <span className="btn-loading-state">
                  <span className="btn-spinner" />
                  Đang gửi mã...
                </span>
              ) : (
                'Gửi mã xác minh'
              )}
            </button>

            {/* Back to Login Link */}
            <Link to="/login" className="auth-back-link">
              ← Quay lại đăng nhập
            </Link>
          </form>
        </div>
      </div>
    </div>
  );
};

export default PasswordResetInit;
