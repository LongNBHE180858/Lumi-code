import '../../login/two-factor-modal.scss';
import './security.scss';

import React, { useEffect, useRef, useState } from 'react';
import { apiConfirm2fa, apiInit2fa } from 'app/shared/api/auth.api';

interface ISecurityTwoFactorModalProps {
  email: string;
  enable: boolean;
  onClose: () => void;
  onConfirmed: (enabled: boolean) => void;
}

const getErrorMessage = (error: any, fallback: string) => error?.response?.data?.message || fallback;

const maskEmail = (email: string) => {
  const [name, domain] = email.split('@');
  if (!name || !domain) return email;
  return `${name.slice(0, 1)}***${name.length > 2 ? name.slice(-1) : ''}@${domain}`;
};

export const SecurityTwoFactorModal: React.FC<ISecurityTwoFactorModalProps> = ({ email, enable, onClose, onConfirmed }) => {
  const [otpCode, setOtpCode] = useState('');
  const [cooldown, setCooldown] = useState(60);
  const [busy, setBusy] = useState(false);
  const [resending, setResending] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const inputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    inputRef.current?.focus();
    const onEscape = (event: KeyboardEvent) => {
      if (event.key === 'Escape' && !busy && !resending) onClose();
    };
    window.addEventListener('keydown', onEscape);
    return () => window.removeEventListener('keydown', onEscape);
  }, [busy, resending, onClose]);

  useEffect(() => {
    if (cooldown === 0) return;
    const timer = window.setTimeout(() => setCooldown(value => value - 1), 1000);
    return () => window.clearTimeout(timer);
  }, [cooldown]);

  const handleConfirm = async (event: React.FormEvent) => {
    event.preventDefault();
    if (!/^\d{6}$/.test(otpCode) || busy) return;
    setBusy(true);
    setError(null);
    try {
      const response = await apiConfirm2fa(otpCode, enable, email);
      const confirmedState = response?.data?.data?.is2faEnable;
      if (confirmedState !== enable) throw new Error('Trạng thái 2FA trả về không đúng. Vui lòng tải lại trang.');
      onConfirmed(confirmedState);
    } catch (caught: any) {
      setError(getErrorMessage(caught, caught?.message || 'Mã OTP không đúng hoặc đã hết hạn. Vui lòng thử lại.'));
    } finally {
      setBusy(false);
    }
  };

  const handleResend = async () => {
    if (cooldown > 0 || resending || busy) return;
    setResending(true);
    setError(null);
    try {
      await apiInit2fa();
      setCooldown(60);
      setOtpCode('');
      inputRef.current?.focus();
    } catch (caught: any) {
      setError(getErrorMessage(caught, 'Không thể gửi lại mã OTP lúc này.'));
    } finally {
      setResending(false);
    }
  };

  return (
    <div
      className="two-factor-modal-backdrop"
      onMouseDown={event => {
        if (event.target === event.currentTarget && !busy && !resending) onClose();
      }}
    >
      <div className="two-factor-modal-dialog" role="dialog" aria-modal="true" aria-labelledby="security-2fa-title">
        <button type="button" className="modal-close-icon-btn" onClick={onClose} disabled={busy || resending} aria-label="Đóng popup">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round">
            <line x1="18" y1="6" x2="6" y2="18" />
            <line x1="6" y1="6" x2="18" y2="18" />
          </svg>
        </button>

        <div className="two-factor-header">
          <div className="shield-icon-badge" aria-hidden="true">
            <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round">
              <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
              <path d="M9 12l2 2 4-4" />
            </svg>
          </div>
          <h2 id="security-2fa-title" className="modal-title">
            {enable ? 'Xác nhận bật 2FA' : 'Xác nhận tắt 2FA'}
          </h2>
          <p className="modal-subtitle">Nhập mã OTP 6 chữ số đã gửi đến email của bạn để {enable ? 'bật' : 'tắt'} xác thực hai bước.</p>
        </div>

        <div className="two-factor-email-pill">
          <span className="email-icon" aria-hidden="true">
            ✉
          </span>
          <span className="email-text">{maskEmail(email)}</span>
        </div>

        {error && (
          <div className="two-factor-alert" role="alert">
            {error}
          </div>
        )}

        <form onSubmit={handleConfirm} className="two-factor-otp-container">
          <label htmlFor="security-2fa-otp" className="security-otp-label">
            Mã xác thực
          </label>
          <input
            ref={inputRef}
            id="security-2fa-otp"
            className="security-otp-input"
            type="text"
            inputMode="numeric"
            autoComplete="one-time-code"
            pattern="[0-9]{6}"
            maxLength={6}
            value={otpCode}
            onChange={event => {
              setOtpCode(event.target.value.replace(/\D/g, '').slice(0, 6));
              setError(null);
            }}
            disabled={busy}
            aria-invalid={Boolean(error)}
          />

          <div className="two-factor-cooldown-row">
            <span>
              {cooldown > 0 ? (
                <>
                  Gửi lại mã sau: <span className="cooldown-time">{cooldown}s</span>
                </>
              ) : (
                'Chưa nhận được mã?'
              )}
            </span>
            <button type="button" className="btn-resend-link" onClick={handleResend} disabled={cooldown > 0 || resending || busy}>
              {resending ? 'Đang gửi...' : 'Gửi lại mã OTP'}
            </button>
          </div>

          <div className="two-factor-actions" style={{ width: '100%' }}>
            <button type="submit" className="btn-verify-2fa" disabled={otpCode.length !== 6 || busy || resending}>
              {busy ? 'Đang xác thực...' : enable ? 'Xác nhận bật 2FA' : 'Xác nhận tắt 2FA'}
            </button>
            <button type="button" className="btn-cancel-2fa" onClick={onClose} disabled={busy || resending}>
              Hủy
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default SecurityTwoFactorModal;
