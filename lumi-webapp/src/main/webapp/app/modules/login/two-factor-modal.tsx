import './two-factor-modal.scss';

import React, { useEffect, useRef, useState } from 'react';
import { toast } from 'react-toastify';
import { useAppDispatch } from 'app/config/store';
import { apiSendOtp } from 'app/shared/api/auth.api';
import { login2fa } from 'app/shared/reducers/authentication';

export interface ITwoFactorModalProps {
  isOpen: boolean;
  email: string;
  rememberMe?: boolean;
  onClose: () => void;
  onSuccess?: () => void;
}

export const TwoFactorModal: React.FC<ITwoFactorModalProps> = ({ isOpen, email, rememberMe = false, onClose, onSuccess }) => {
  const dispatch = useAppDispatch();
  const [otp, setOtp] = useState<string[]>(Array(6).fill(''));
  const [cooldown, setCooldown] = useState(60);
  const [isResending, setIsResending] = useState(false);
  const [loading, setLoading] = useState(false);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);

  const inputsRef = useRef<(HTMLInputElement | null)[]>([]);

  // Reset state when opening modal
  useEffect(() => {
    if (isOpen) {
      setOtp(Array(6).fill(''));
      setCooldown(60);
      setErrorMsg(null);
      const timer = setTimeout(() => {
        inputsRef.current[0]?.focus();
      }, 100);
      return () => clearTimeout(timer);
    }
  }, [isOpen]);

  // Handle ESC key to close
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape' && isOpen) {
        onClose();
      }
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [isOpen, onClose]);

  // Cooldown countdown timer
  useEffect(() => {
    if (!isOpen || cooldown <= 0) return;
    const timer = setInterval(() => {
      setCooldown(prev => Math.max(0, prev - 1));
    }, 1000);
    return () => clearInterval(timer);
  }, [isOpen, cooldown]);

  if (!isOpen) return null;

  // Mask email for display: e.g. "nguyen@gmail.com" -> "n***n@gmail.com"
  const maskEmail = (raw: string) => {
    if (!raw || !raw.includes('@')) return raw;
    const [user, domain] = raw.split('@');
    if (user.length <= 2) {
      return `${user[0]}***@${domain}`;
    }
    return `${user[0]}***${user[user.length - 1]}@${domain}`;
  };

  const handleDigitChange = (index: number, val: string) => {
    setErrorMsg(null);
    const clean = val.replace(/\D/g, '');
    const newOtp = [...otp];

    if (!clean) {
      newOtp[index] = '';
      setOtp(newOtp);
      return;
    }

    newOtp[index] = clean.slice(-1);
    setOtp(newOtp);

    // Auto move to next input
    if (index < 5) {
      inputsRef.current[index + 1]?.focus();
    }
  };

  const handleKeyDown = (index: number, e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Backspace' && !otp[index] && index > 0) {
      inputsRef.current[index - 1]?.focus();
    }
  };

  const handlePaste = (e: React.ClipboardEvent<HTMLInputElement>) => {
    e.preventDefault();
    setErrorMsg(null);
    const paste = e.clipboardData.getData('text').replace(/\D/g, '').slice(0, 6);
    if (paste) {
      const newOtp = [...otp];
      paste.split('').forEach((char, idx) => {
        if (idx < 6) newOtp[idx] = char;
      });
      setOtp(newOtp);
      const nextFocus = Math.min(paste.length, 5);
      inputsRef.current[nextFocus]?.focus();
    }
  };

  const handleResendOtp = async () => {
    if (cooldown > 0 || isResending) return;
    setIsResending(true);
    setErrorMsg(null);

    try {
      await apiSendOtp(email, 'LOGIN');
      setCooldown(60);
      toast.success('Mã OTP mới đã được gửi về email của bạn!');
    } catch (err: any) {
      const msg = err?.response?.data?.message || 'Không thể gửi lại mã OTP lúc này. Vui lòng thử lại sau.';
      toast.error(msg);
      setErrorMsg(msg);
    } finally {
      setIsResending(false);
    }
  };

  const handleVerify = async (e: React.FormEvent) => {
    e.preventDefault();
    const otpCode = otp.join('');
    if (otpCode.length < 6) {
      setErrorMsg('Vui lòng nhập đủ 6 chữ số mã OTP.');
      return;
    }

    setLoading(true);
    setErrorMsg(null);

    try {
      await dispatch(login2fa(email, otpCode, rememberMe));
      toast.success('Xác thực 2FA thành công! Đang chuyển hướng...');
      if (onSuccess) onSuccess();
      onClose();
    } catch (err: any) {
      const msg = err?.response?.data?.message || 'Mã OTP không chính xác hoặc đã hết hạn. Vui lòng thử lại.';
      setErrorMsg(msg);
    } finally {
      setLoading(false);
    }
  };

  const otpCode = otp.join('');

  return (
    <div
      className="two-factor-modal-backdrop"
      onClick={e => {
        if (e.target === e.currentTarget) onClose();
      }}
    >
      <div className="two-factor-modal-dialog" role="dialog" aria-modal="true">
        {/* Close button */}
        <button type="button" className="modal-close-icon-btn" onClick={onClose} aria-label="Đóng popup">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round">
            <line x1="18" y1="6" x2="6" y2="18" />
            <line x1="6" y1="6" x2="18" y2="18" />
          </svg>
        </button>

        {/* Header with Shield Icon */}
        <div className="two-factor-header">
          <div className="shield-icon-badge">
            <svg
              width="32"
              height="32"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
            >
              <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
              <path d="M9 12l2 2 4-4" />
            </svg>
          </div>
          <h2 className="modal-title">Xác thực hai yếu tố (2FA)</h2>
          <p className="modal-subtitle">Tài khoản của bạn đã bật bảo vệ 2 lớp. Vui lòng nhập mã OTP 6 chữ số vừa được gửi đến email:</p>
        </div>

        {/* Email Pill Badge */}
        <div className="two-factor-email-pill">
          <span className="email-icon">
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
              <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z" />
              <polyline points="22,6 12,13 2,6" />
            </svg>
          </span>
          <span className="email-text">{maskEmail(email)}</span>
        </div>

        {/* Error Alert */}
        {errorMsg && (
          <div className="two-factor-alert" role="alert">
            {errorMsg}
          </div>
        )}

        {/* OTP Input Form */}
        <form onSubmit={handleVerify} className="two-factor-otp-container">
          <div className="otp-inputs-row">
            {otp.map((digit, idx) => (
              <input
                key={idx}
                ref={el => {
                  inputsRef.current[idx] = el;
                }}
                type="text"
                inputMode="numeric"
                pattern="[0-9]*"
                maxLength={1}
                className={`otp-single-box ${digit ? 'filled' : ''}`}
                value={digit}
                onChange={e => handleDigitChange(idx, e.target.value)}
                onKeyDown={e => handleKeyDown(idx, e)}
                onPaste={handlePaste}
                disabled={loading}
                autoFocus={idx === 0}
              />
            ))}
          </div>

          {/* Cooldown & Resend Row */}
          <div className="two-factor-cooldown-row">
            {cooldown > 0 ? (
              <span>
                Gửi lại mã sau: <span className="cooldown-time">{cooldown}s</span>
              </span>
            ) : (
              <span>Chưa nhận được mã?</span>
            )}

            <button type="button" className="btn-resend-link" onClick={handleResendOtp} disabled={cooldown > 0 || isResending || loading}>
              {isResending ? 'Đang gửi lại...' : 'Gửi lại mã OTP'}
            </button>
          </div>

          {/* Actions */}
          <div className="two-factor-actions" style={{ width: '100%' }}>
            <button type="submit" className="btn-verify-2fa" disabled={otpCode.length < 6 || loading}>
              {loading ? 'Đang xác thực...' : 'Xác nhận & Đăng nhập'}
            </button>

            <button type="button" className="btn-cancel-2fa" onClick={onClose} disabled={loading}>
              Quay lại đăng nhập
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default TwoFactorModal;
