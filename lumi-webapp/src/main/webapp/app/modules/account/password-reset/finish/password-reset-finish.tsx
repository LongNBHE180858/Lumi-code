import '../password-reset.scss';

import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate, useSearchParams } from 'react-router';
import { toast } from 'react-toastify';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { apiSendOtp } from 'app/shared/api/auth.api';
import { handlePasswordResetFinish, reset } from '../password-reset.reducer';

export const PasswordResetFinishPage: React.FC = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const location = useLocation();
  const [searchParams] = useSearchParams();

  const loading = useAppSelector(state => state.passwordReset.loading);
  const resetPasswordSuccess = useAppSelector(state => state.passwordReset.resetPasswordSuccess);
  const errorMessage = useAppSelector(state => state.passwordReset.errorMessage);

  const initialEmail = searchParams.get('email') || (location.state as any)?.email || '';
  const keyParam = searchParams.get('key');

  const [email, setEmail] = useState(initialEmail);
  const [verificationCode, setVerificationCode] = useState(keyParam || '');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [showNewPassword, setShowNewPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  const [cooldown, setCooldown] = useState(45);
  const [isResending, setIsResending] = useState(false);
  const [clientError, setClientError] = useState<string | null>(null);
  const [isSubmittedLocal, setIsSubmittedLocal] = useState(false);

  useEffect(() => {
    dispatch(reset());
    return () => {
      dispatch(reset());
    };
  }, [dispatch]);

  // Cooldown countdown timer
  useEffect(() => {
    if (cooldown <= 0) return undefined;
    const timer = setInterval(() => {
      setCooldown(prev => Math.max(0, prev - 1));
    }, 1000);
    return () => clearInterval(timer);
  }, [cooldown]);

  // Redirect on success
  useEffect(() => {
    if (resetPasswordSuccess && isSubmittedLocal) {
      toast.success('Đặt lại mật khẩu thành công! Vui lòng đăng nhập lại.');
      const timer = setTimeout(() => {
        navigate('/login');
      }, 1500);
      return () => clearTimeout(timer);
    }
    return undefined;
  }, [resetPasswordSuccess, isSubmittedLocal, navigate]);

  const maskEmail = (raw: string) => {
    if (!raw || !raw.includes('@')) return raw || 'email-cua-ban@domain.com';
    const [name, domain] = raw.split('@');
    if (name.length <= 2) {
      return `${name}****@${domain}`;
    }
    const visiblePrefix = name.substring(0, 2);
    const maskedLength = Math.max(6, name.length - 2);
    return `${visiblePrefix}${'*'.repeat(maskedLength)}@${domain}`;
  };

  const formatTimer = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  const handleResendCode = async () => {
    if (cooldown > 0 || isResending) return;
    const targetEmail = email.trim();
    if (!targetEmail) {
      setClientError('Không tìm thấy thông tin email. Vui lòng quay lại bước trước.');
      return;
    }

    setIsResending(true);
    setClientError(null);

    try {
      await apiSendOtp(targetEmail, 'FORGOT_PASSWORD');
      setCooldown(45);
      toast.success('Mã xác minh mới đã được gửi đến email của bạn!');
    } catch (err: any) {
      const msg = err?.response?.data?.message || 'Không thể gửi lại mã OTP lúc này. Vui lòng thử lại sau.';
      toast.error(msg);
      setClientError(msg);
    } finally {
      setIsResending(false);
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setClientError(null);

    const targetEmail = email.trim();
    if (!targetEmail) {
      setClientError('Vui lòng nhập địa chỉ email của bạn.');
      return;
    }

    const code = verificationCode.trim();
    if (!code) {
      setClientError('Vui lòng nhập mã xác minh đã được gửi đến email của bạn.');
      return;
    }

    if (!newPassword) {
      setClientError('Vui lòng nhập mật khẩu mới.');
      return;
    }

    if (newPassword.length < 6) {
      setClientError('Mật khẩu mới phải chứa ít nhất 6 ký tự.');
      return;
    }

    if (!confirmPassword) {
      setClientError('Vui lòng nhập lại mật khẩu mới để xác nhận.');
      return;
    }

    if (newPassword !== confirmPassword) {
      setClientError('Mật khẩu nhập lại không trùng khớp với mật khẩu mới.');
      return;
    }

    setIsSubmittedLocal(true);
    dispatch(
      handlePasswordResetFinish({
        email: targetEmail,
        otpCode: code,
        key: code,
        newPassword,
      }),
    );
  };

  const currentError = clientError || errorMessage;

  return (
    <div className="password-reset-page-wrapper">
      <div className="auth-card-container">
        <div className="auth-card reset-password-card">
          {/* Header Introduction */}
          <div className="auth-card-intro">
            <div className="auth-icon-badge" aria-hidden="true">
              {/* Lock Icon matching Figma Vector */}
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
                <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
                <path d="M7 11V7a5 5 0 0 1 10 0v4" />
              </svg>
            </div>

            <h1 className="auth-card-title">Đặt lại mật khẩu</h1>
            <p className="auth-card-desc">Nhập mã xác minh đã được gửi đến email và thiết lập mật khẩu mới.</p>
          </div>

          {/* Registered Email Card */}
          {email ? (
            <div className="auth-registered-email-card">
              <span className="registered-email-label">Mã xác minh đã được gửi đến:</span>
              <span className="registered-email-value">{maskEmail(email)}</span>
            </div>
          ) : (
            <div className="form-field-group">
              <label htmlFor="reset-email" className="field-label">
                Email tài khoản <span className="field-required">*</span>
              </label>
              <div className="field-input-wrapper">
                <input
                  id="reset-email"
                  type="email"
                  className="field-input"
                  placeholder="Nhập email tài khoản cần đặt lại mật khẩu"
                  value={email}
                  onChange={e => {
                    setEmail(e.target.value);
                    if (clientError) setClientError(null);
                  }}
                  autoComplete="email"
                />
              </div>
            </div>
          )}

          {/* Error Alert */}
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

          {/* Success Alert */}
          {resetPasswordSuccess && isSubmittedLocal && (
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
              <span>Đặt lại mật khẩu thành công! Đang chuyển hướng về trang đăng nhập...</span>
            </div>
          )}

          <form onSubmit={handleSubmit} className="auth-form-body" noValidate>
            {/* OTP Verification Code Field */}
            <div className="verification-field-container">
              <div className="form-field-group">
                <label htmlFor="reset-verification-code" className="field-label">
                  Mã xác minh <span className="field-required">*</span>
                </label>
                <div className="field-input-wrapper">
                  <input
                    id="reset-verification-code"
                    type="text"
                    className="field-input"
                    placeholder="Nhập mã xác minh 6 số"
                    value={verificationCode}
                    onChange={e => {
                      setVerificationCode(e.target.value);
                      if (clientError) setClientError(null);
                    }}
                    autoComplete="one-time-code"
                    maxLength={10}
                    disabled={loading}
                    autoFocus
                  />
                </div>
              </div>

              {/* Sub row: Resend info & Countdown */}
              <div className="resend-info-row">
                <div className="resend-action-text">
                  Không nhận được mã?
                  <button
                    type="button"
                    className={`btn-link-resend ${cooldown > 0 || isResending ? 'disabled' : ''}`}
                    onClick={handleResendCode}
                    disabled={cooldown > 0 || isResending || loading}
                  >
                    {isResending ? 'Đang gửi...' : 'Gửi lại mã'}
                  </button>
                </div>
                {cooldown > 0 && <div className="resend-countdown-text">Có thể gửi lại sau {formatTimer(cooldown)}</div>}
              </div>
            </div>

            {/* New Password Field */}
            <div className="form-field-group">
              <label htmlFor="reset-new-password" className="field-label">
                Mật khẩu mới <span className="field-required">*</span>
              </label>
              <div className="field-input-wrapper">
                <input
                  id="reset-new-password"
                  type={showNewPassword ? 'text' : 'password'}
                  className="field-input input-with-icon-right"
                  placeholder="Nhập mật khẩu mới"
                  value={newPassword}
                  onChange={e => {
                    setNewPassword(e.target.value);
                    if (clientError) setClientError(null);
                  }}
                  autoComplete="new-password"
                  disabled={loading}
                />
                <button
                  type="button"
                  className="btn-password-toggle"
                  onClick={() => setShowNewPassword(prev => !prev)}
                  aria-label={showNewPassword ? 'Ẩn mật khẩu' : 'Hiện mật khẩu'}
                  tabIndex={-1}
                >
                  {showNewPassword ? (
                    <svg
                      width="18"
                      height="18"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="#7894AF"
                      strokeWidth="1.8"
                      strokeLinecap="round"
                      strokeLinejoin="round"
                    >
                      <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" />
                      <line x1="1" y1="1" x2="23" y2="23" />
                    </svg>
                  ) : (
                    <svg
                      width="18"
                      height="18"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="#7894AF"
                      strokeWidth="1.8"
                      strokeLinecap="round"
                      strokeLinejoin="round"
                    >
                      <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
                      <circle cx="12" cy="12" r="3" />
                    </svg>
                  )}
                </button>
              </div>
            </div>

            {/* Confirm Password Field */}
            <div className="form-field-group">
              <label htmlFor="reset-confirm-password" className="field-label">
                Nhập lại mật khẩu mới <span className="field-required">*</span>
              </label>
              <div className="field-input-wrapper">
                <input
                  id="reset-confirm-password"
                  type={showConfirmPassword ? 'text' : 'password'}
                  className="field-input input-with-icon-right"
                  placeholder="Nhập lại mật khẩu mới"
                  value={confirmPassword}
                  onChange={e => {
                    setConfirmPassword(e.target.value);
                    if (clientError) setClientError(null);
                  }}
                  autoComplete="new-password"
                  disabled={loading}
                />
                <button
                  type="button"
                  className="btn-password-toggle"
                  onClick={() => setShowConfirmPassword(prev => !prev)}
                  aria-label={showConfirmPassword ? 'Ẩn mật khẩu' : 'Hiện mật khẩu'}
                  tabIndex={-1}
                >
                  {showConfirmPassword ? (
                    <svg
                      width="18"
                      height="18"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="#7894AF"
                      strokeWidth="1.8"
                      strokeLinecap="round"
                      strokeLinejoin="round"
                    >
                      <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" />
                      <line x1="1" y1="1" x2="23" y2="23" />
                    </svg>
                  ) : (
                    <svg
                      width="18"
                      height="18"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="#7894AF"
                      strokeWidth="1.8"
                      strokeLinecap="round"
                      strokeLinejoin="round"
                    >
                      <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
                      <circle cx="12" cy="12" r="3" />
                    </svg>
                  )}
                </button>
              </div>
            </div>

            {/* Submit Action */}
            <button type="submit" className="auth-submit-btn" disabled={loading}>
              {loading ? (
                <span className="btn-loading-state">
                  <span className="btn-spinner" />
                  Đang đặt lại mật khẩu...
                </span>
              ) : (
                'Đặt lại mật khẩu'
              )}
            </button>

            {/* Back to Forgot Password */}
            <Link to="/account/reset/request" className="auth-back-link">
              ← Quay lại
            </Link>
          </form>
        </div>
      </div>
    </div>
  );
};

export default PasswordResetFinishPage;
