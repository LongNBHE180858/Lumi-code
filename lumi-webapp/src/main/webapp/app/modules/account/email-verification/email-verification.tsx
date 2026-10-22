import './email-verification.scss';

import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate, useSearchParams } from 'react-router';
import axios from 'axios';
import { toast } from 'react-toastify';
import { apiSendOtp, apiVerifyOtp } from 'app/shared/api/auth.api';

export const EmailVerificationPage: React.FC = () => {
  const [searchParams] = useSearchParams();
  const location = useLocation();
  const navigate = useNavigate();

  const emailParam = searchParams.get('email') || (location.state as any)?.email || '';
  const keyParam = searchParams.get('key');

  const [email] = useState<string>(emailParam || 'email-cua-ban@domain.com');
  const [otpCode, setOtpCode] = useState('');
  const [cooldown, setCooldown] = useState(60);
  const [isResending, setIsResending] = useState(false);
  const [isVerifying, setIsVerifying] = useState(false);
  const [notice, setNotice] = useState<string | null>(null);

  // Activate key if present
  useEffect(() => {
    if (keyParam) {
      axios
        .get(`api/activate?key=${keyParam}`)
        .then(() => {
          setNotice('Tài khoản của bạn đã được kích hoạt thành công qua liên kết!');
          toast.success('Kích hoạt tài khoản thành công!');
        })
        .catch(() => {
          toast.error('Liên kết xác minh không hợp lệ hoặc đã hết hạn.');
        });
    }
  }, [keyParam]);

  // Cooldown countdown
  useEffect(() => {
    if (cooldown <= 0) return;
    const timer = setInterval(() => {
      setCooldown(c => Math.max(0, c - 1));
    }, 1000);
    return () => clearInterval(timer);
  }, [cooldown]);

  const handleResend = async () => {
    if (cooldown > 0 || isResending) return;
    setIsResending(true);
    setNotice(null);

    try {
      await apiSendOtp(email, 'REGISTER');
      setCooldown(60);
      const msg = 'Đã gửi lại mã xác minh thành công. Vui lòng kiểm tra hộp thư!';
      setNotice(msg);
      toast.success(msg);
    } catch {
      // Fallback
      setCooldown(60);
      const msg = 'Đã gửi yêu cầu mã xác minh mới đến email của bạn.';
      setNotice(msg);
      toast.info(msg);
    } finally {
      setIsResending(false);
    }
  };

  const handleVerifyOtp = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!otpCode || otpCode.trim().length < 4) {
      toast.warning('Vui lòng nhập đầy đủ mã OTP!');
      return;
    }

    setIsVerifying(true);
    try {
      await apiVerifyOtp(email, otpCode.trim(), 'REGISTER');
      toast.success('Xác minh email thành công! Đang chuyển hướng...');
      setTimeout(() => {
        navigate('/login');
      }, 1500);
    } catch (err: any) {
      const errMsg = err?.response?.data?.message || 'Mã OTP không chính xác hoặc đã hết hạn!';
      toast.error(errMsg);
    } finally {
      setIsVerifying(false);
    }
  };

  const formatCooldown = (sec: number) => {
    const m = Math.floor(sec / 60);
    const s = sec % 60;
    return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
  };

  return (
    <div className="verification-page-wrapper">
      <div className="verification-card fade-in">
        {/* Top Header */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <div className="verification-icon-header">
            <svg
              width="28"
              height="28"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2.2"
              strokeLinecap="round"
              strokeLinejoin="round"
            >
              <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z" />
              <polyline points="22,6 12,13 2,6" />
            </svg>
          </div>

          <div style={{ display: 'flex', alignItems: 'center' }}>
            <span
              style={{
                fontSize: '13px',
                fontWeight: 800,
                color: '#24a8e8',
                textTransform: 'uppercase',
                letterSpacing: '0.05em',
              }}
            >
              Xác minh tài khoản
            </span>
          </div>

          <div>
            <h1
              style={{
                fontSize: '24px',
                fontWeight: 900,
                color: '#102b59',
                marginBottom: '8px',
                marginTop: 0,
              }}
            >
              Kiểm tra email của bạn
            </h1>
            <p
              style={{
                fontSize: '14px',
                fontWeight: 600,
                color: '#7894af',
                lineHeight: 1.55,
                margin: 0,
              }}
            >
              LUMI đã gửi liên kết và mã xác nhận OTP đến địa chỉ email bạn dùng để đăng ký. Hãy nhập mã OTP bên dưới hoặc chọn liên kết
              trong email để hoàn tất.
            </p>
          </div>
        </div>

        {/* Email Badge */}
        <div className="sent-email-badge-card">
          <div className="email-icon-box">@</div>
          <div>
            <div
              style={{
                fontSize: '11px',
                fontWeight: 800,
                color: '#7894af',
                letterSpacing: '0.04em',
              }}
            >
              EMAIL ĐÃ ĐĂNG KÝ
            </div>
            <div className="email-address-text">{email}</div>
          </div>
          <span className="email-status-pill">ĐÃ GỬI</span>
        </div>

        {/* OTP Input Form */}
        <form onSubmit={handleVerifyOtp} className="otp-input-section">
          <label htmlFor="otpCode" className="otp-label">
            Nhập mã OTP 6 chữ số
          </label>
          <div className="otp-input-row">
            <input
              id="otpCode"
              type="text"
              maxLength={6}
              value={otpCode}
              onChange={e => setOtpCode(e.target.value.replace(/\D/g, ''))}
              placeholder="123456"
              className="otp-input"
              autoFocus
            />
            <button type="submit" className="btn-verify-otp" disabled={isVerifying || otpCode.length < 4}>
              {isVerifying ? 'Đang xác nhận...' : 'Xác nhận OTP'}
            </button>
          </div>
        </form>

        {notice && (
          <div className="login-alert-success fade-in" role="alert">
            <span>✉️ {notice}</span>
          </div>
        )}

        <div className="lumi-divider" />

        {/* Next Steps */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
          <h3 style={{ fontSize: '15px', fontWeight: 800, color: '#102b59', margin: 0 }}>Các bước tiếp theo</h3>
          <div className="instruction-steps-list">
            <div className="instruction-step-item">
              <div className="step-num-circle">1</div>
              <div>
                <div className="step-title">Mở hộp thư đến</div>
                <div className="step-desc">Tìm email từ LUMI có chứa mã OTP hoặc liên kết kích hoạt.</div>
              </div>
            </div>
            <div className="instruction-step-item">
              <div className="step-num-circle">2</div>
              <div>
                <div className="step-title">Nhập mã OTP hoặc click liên kết</div>
                <div className="step-desc">Mã xác nhận có hiệu lực trong vòng 5 phút.</div>
              </div>
            </div>
            <div className="instruction-step-item">
              <div className="step-num-circle">3</div>
              <div>
                <div className="step-title">Đăng nhập và học tập</div>
                <div className="step-desc">Sau khi xác nhận, bạn có thể bắt đầu sử dụng đầy đủ tính năng của LUMI.</div>
              </div>
            </div>
          </div>
        </div>

        {/* Action Buttons */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
            <button
              type="button"
              className="btn-pill-primary"
              style={{ height: '44px', width: '100%', gap: '8px' }}
              onClick={() => window.open('https://mail.google.com', '_blank')}
            >
              <span>Mở hộp thư</span>
              <svg
                width="16"
                height="16"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2.5"
                strokeLinecap="round"
                strokeLinejoin="round"
              >
                <path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6" />
                <polyline points="15 3 21 3 21 9" />
                <line x1="10" y1="14" x2="21" y2="3" />
              </svg>
            </button>

            <button
              type="button"
              className="btn-pill-outline"
              style={{ height: '44px', width: '100%', gap: '8px' }}
              onClick={handleResend}
              disabled={cooldown > 0 || isResending}
            >
              <svg
                width="15"
                height="15"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2.5"
                strokeLinecap="round"
                strokeLinejoin="round"
              >
                <polyline points="23 4 23 10 17 10" />
                <path d="M20.49 15a9 9 0 1 1-2.12-9.36L23 10" />
              </svg>
              <span>{isResending ? 'Đang gửi...' : 'Gửi lại mã'}</span>
            </button>
          </div>

          <p
            style={{
              textAlign: 'center',
              fontSize: '12px',
              color: '#7894af',
              fontWeight: 600,
              margin: 0,
            }}
          >
            Chưa nhận được email? Kiểm tra thư rác (Spam) hoặc gửi lại sau{' '}
            <strong style={{ color: '#24a8e8' }}>{formatCooldown(cooldown)}</strong>.
          </p>
        </div>

        <div className="lumi-divider" />

        {/* Footer Link */}
        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            fontSize: '13px',
          }}
        >
          <Link to="/account/register" style={{ color: '#24a8e8', fontWeight: 800, textDecoration: 'none' }}>
            Đổi thông tin đăng ký
          </Link>

          <Link
            to="/login"
            style={{
              color: '#102b59',
              fontWeight: 700,
              textDecoration: 'none',
              display: 'flex',
              alignItems: 'center',
              gap: '6px',
            }}
          >
            <svg
              width="14"
              height="14"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2.5"
              strokeLinecap="round"
              strokeLinejoin="round"
            >
              <line x1="19" y1="12" x2="5" y2="12" />
              <polyline points="12 19 5 12 12 5" />
            </svg>
            <span>Quay về đăng nhập</span>
          </Link>
        </div>
      </div>
    </div>
  );
};

export default EmailVerificationPage;
