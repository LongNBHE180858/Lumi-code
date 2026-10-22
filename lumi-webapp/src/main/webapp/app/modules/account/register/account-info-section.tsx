import React, { useState } from 'react';

export interface AccountInfoFields {
  username: string;
  email: string;
  phoneNumber: string;
  firstName: string;
  lastName: string;
  password: string;
  confirmPassword: string;
}

interface AccountInfoSectionProps {
  fields: AccountInfoFields;
  onChange: (field: keyof AccountInfoFields, value: string) => void;
}

export const AccountInfoSection: React.FC<AccountInfoSectionProps> = ({ fields, onChange }) => {
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  return (
    <div className="register-column-section">
      <div>
        <div className="section-head-title">
          <span className="section-badge-num">02</span>
          <span>Thông tin tài khoản & định danh</span>
        </div>
        <p className="section-head-hint">Thông tin dùng để đăng nhập, liên hệ bảo mật và nhận thông báo từ hệ thống LUMI.</p>
      </div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: '16px', marginTop: '16px' }}>
        {/* Họ và Tên */}
        <div className="form-row-2col">
          <div className="form-field-group">
            <label className="form-label-title">
              Họ <span style={{ color: 'var(--color-primary, #24a8e8)' }}>*</span>
            </label>
            <div className="form-input-container">
              <input
                type="text"
                className="form-input-element"
                value={fields.lastName}
                onChange={e => onChange('lastName', e.target.value)}
                placeholder="Ví dụ: Nguyễn"
                required
              />
            </div>
          </div>
          <div className="form-field-group">
            <label className="form-label-title">
              Tên <span style={{ color: 'var(--color-primary, #24a8e8)' }}>*</span>
            </label>
            <div className="form-input-container">
              <input
                type="text"
                className="form-input-element"
                value={fields.firstName}
                onChange={e => onChange('firstName', e.target.value)}
                placeholder="Ví dụ: Minh Anh"
                required
              />
            </div>
          </div>
        </div>

        {/* Email & Phone */}
        <div className="form-row-2col">
          <div className="form-field-group">
            <label className="form-label-title">
              Email <span style={{ color: 'var(--color-primary, #24a8e8)' }}>*</span>
            </label>
            <div className="form-input-container">
              <input
                type="email"
                className="form-input-element"
                value={fields.email}
                onChange={e => onChange('email', e.target.value)}
                placeholder="name@example.com"
                required
              />
            </div>
          </div>
          <div className="form-field-group">
            <label className="form-label-title">
              Số điện thoại <span style={{ color: 'var(--color-primary, #24a8e8)' }}>*</span>
            </label>
            <div className="form-input-container">
              <input
                type="tel"
                className="form-input-element"
                value={fields.phoneNumber}
                onChange={e => onChange('phoneNumber', e.target.value)}
                placeholder="09xx xxx xxx"
                required
              />
            </div>
          </div>
        </div>

        {/* Username */}
        <div className="form-field-group">
          <label className="form-label-title">
            Tên đăng nhập <span style={{ color: 'var(--color-primary, #24a8e8)' }}>*</span>
          </label>
          <div className="form-input-container">
            <input
              type="text"
              className="form-input-element"
              value={fields.username}
              onChange={e => onChange('username', e.target.value)}
              placeholder="Tối thiểu 3 ký tự, chỉ gồm chữ, số và ký tự -_."
              required
            />
          </div>
          <span className="form-field-helper">Tên đăng nhập là duy nhất và dùng để đăng nhập vào hệ thống.</span>
        </div>

        {/* Password & Confirm Password */}
        <div className="form-row-2col">
          <div className="form-field-group">
            <label className="form-label-title">
              Mật khẩu <span style={{ color: 'var(--color-primary, #24a8e8)' }}>*</span>
            </label>
            <div className="form-input-container">
              <input
                type={showPassword ? 'text' : 'password'}
                className="form-input-element"
                value={fields.password}
                onChange={e => onChange('password', e.target.value)}
                placeholder="Tối thiểu 8 ký tự"
                required
              />
              <button
                type="button"
                className="password-toggle-btn"
                onClick={() => setShowPassword(p => !p)}
                style={{ padding: 0 }}
                tabIndex={-1}
              >
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
                  <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
                  <circle cx="12" cy="12" r="3" />
                </svg>
              </button>
            </div>
          </div>

          <div className="form-field-group">
            <label className="form-label-title">
              Xác nhận mật khẩu <span style={{ color: 'var(--color-primary, #24a8e8)' }}>*</span>
            </label>
            <div className="form-input-container">
              <input
                type={showConfirmPassword ? 'text' : 'password'}
                className="form-input-element"
                value={fields.confirmPassword}
                onChange={e => onChange('confirmPassword', e.target.value)}
                placeholder="Nhập lại mật khẩu"
                required
              />
              <button
                type="button"
                className="password-toggle-btn"
                onClick={() => setShowConfirmPassword(p => !p)}
                style={{ padding: 0 }}
                tabIndex={-1}
              >
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
                  <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
                  <circle cx="12" cy="12" r="3" />
                </svg>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AccountInfoSection;
