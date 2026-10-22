import React, { useState } from 'react';
import { Link } from 'react-router';

export interface ILoginFormProps {
  handleLogin: (username: string, password: string, rememberMe?: boolean) => void;
  loginError?: boolean;
  loading?: boolean;
  errorMessage?: string | null;
}

const EyeIcon = ({ isVisible }: { isVisible: boolean }) => (
  <svg
    width="20"
    height="20"
    viewBox="0 0 20 20"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
    style={{
      opacity: isVisible ? 1 : 0.6,
      filter: isVisible ? 'drop-shadow(0 0 2px rgba(36,168,232,0.6))' : 'none',
      transition: 'all 0.15s ease',
    }}
  >
    <path
      d="M8.83097 3.3837C10.981 3.12745 13.1557 3.58199 15.0232 4.6779C16.8906 5.7738 18.3481 7.45084 19.1731 9.45293C19.25 9.66017 19.25 9.88815 19.1731 10.0954C18.8339 10.9178 18.3856 11.6909 17.8403 12.3939M11.9237 11.7667C11.4014 12.2711 10.702 12.5502 9.97597 12.5439C9.24996 12.5376 8.55547 12.2464 8.04208 11.7329C7.52869 11.2195 7.23748 10.5249 7.23117 9.79884C7.22486 9.07276 7.50396 8.37324 8.00835 7.85096M15.0567 14.8504C13.8323 15.5758 12.4663 16.0292 11.0512 16.18C9.63618 16.3308 8.20526 16.1755 6.85555 15.7245C5.50585 15.2735 4.26893 14.5374 3.22874 13.5662C2.18854 12.595 1.36941 11.4114 0.826923 10.0957C0.75 9.88843 0.75 9.66046 0.826923 9.45321C1.64528 7.46847 3.08518 5.80267 4.93054 4.70583M0.770132 0.54387L19.2299 19.0054"
      strokeWidth="1.6"
      stroke="#7894AF"
      strokeLinecap="round"
      strokeLinejoin="round"
    />
  </svg>
);

export const LoginForm: React.FC<ILoginFormProps> = ({ handleLogin, loginError = false, loading = false, errorMessage = null }) => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [rememberMe, setRememberMe] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [clientError, setClientError] = useState<string | null>(null);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setClientError(null);

    if (!username.trim()) {
      setClientError('Vui lòng nhập tên đăng nhập hoặc email');
      return;
    }
    if (!password) {
      setClientError('Vui lòng nhập mật khẩu');
      return;
    }

    handleLogin(username.trim(), password, rememberMe);
  };

  return (
    <div className="login-card login-fade-in">
      {/* Header */}
      <div className="login-card-header">
        <h2 className="card-title">Đăng nhập</h2>
        <p className="card-subtitle">Chào mừng bạn quay lại LUMI</p>
      </div>

      {/* Alert Notices */}
      {clientError && (
        <div className="login-alert-danger" role="alert">
          <span>{clientError}</span>
        </div>
      )}

      {loginError && !clientError && (
        <div className="login-alert-danger" data-cy="loginError" role="alert">
          <span>{errorMessage || 'Đăng nhập không thành công! Vui lòng kiểm tra lại thông tin.'}</span>
          {(errorMessage?.toLowerCase().includes('kích hoạt') || errorMessage?.toLowerCase().includes('activated')) && (
            <div style={{ marginTop: '8px' }}>
              <Link
                to={`/account/verify-email?email=${encodeURIComponent(username.trim())}`}
                style={{ color: '#24A8E8', fontWeight: 600, textDecoration: 'underline' }}
              >
                Xác thực email ngay tại đây →
              </Link>
            </div>
          )}
        </div>
      )}

      {/* Form */}
      <form className="login-form-inner" onSubmit={handleSubmit} noValidate>
        {/* Username / Email Field */}
        <div className="login-field-group">
          <label htmlFor="username" className="field-label">
            Tên đăng nhập / Email <span className="required-star">*</span>
          </label>
          <div className={`field-input-wrap ${clientError && !username.trim() ? 'input-error' : ''}`}>
            <input
              id="username"
              name="username"
              type="text"
              autoComplete="username email"
              value={username}
              onChange={e => {
                setUsername(e.target.value);
                if (clientError) setClientError(null);
              }}
              placeholder="Nhập tên đăng nhập hoặc email"
              className="field-input"
              data-cy="username"
              required
              autoFocus
            />
          </div>
        </div>

        {/* Password Field */}
        <div className="login-password-section">
          <div className="login-field-group">
            <label htmlFor="password" className="field-label">
              Mật khẩu <span className="required-star">*</span>
            </label>
            <div className={`field-input-wrap ${clientError && !password ? 'input-error' : ''}`}>
              <input
                id="password"
                name="password"
                type={showPassword ? 'text' : 'password'}
                autoComplete="current-password"
                value={password}
                onChange={e => {
                  setPassword(e.target.value);
                  if (clientError) setClientError(null);
                }}
                placeholder="Nhập mật khẩu"
                className="field-input"
                data-cy="password"
                required
              />
              <button
                type="button"
                className="password-eye-btn"
                onClick={() => setShowPassword(prev => !prev)}
                aria-label={showPassword ? 'Ẩn mật khẩu' : 'Hiện mật khẩu'}
                title={showPassword ? 'Ẩn mật khẩu' : 'Hiện mật khẩu'}
              >
                <EyeIcon isVisible={showPassword} />
              </button>
            </div>
          </div>

          {/* Forgot Password Link */}
          <Link to="/account/reset/request" className="forgot-link" data-cy="forgetYourPasswordSelector">
            Quên mật khẩu?
          </Link>
        </div>

        {/* Remember Me Checkbox */}
        <label className="login-remember-row">
          <input type="checkbox" checked={rememberMe} onChange={e => setRememberMe(e.target.checked)} data-cy="rememberMe" />
          <span className="remember-label">Ghi nhớ đăng nhập</span>
        </label>

        {/* Submit Button */}
        <button type="submit" className="btn-submit-login" disabled={loading} data-cy="submit">
          {loading ? 'Đang đăng nhập...' : 'Đăng nhập'}
        </button>
      </form>

      {/* Register Prompt */}
      <p className="login-register-prompt">
        Bạn chưa có tài khoản?
        <Link to="/account/register" className="register-link">
          Đăng ký ngay
        </Link>
      </p>
    </div>
  );
};

export default LoginForm;
