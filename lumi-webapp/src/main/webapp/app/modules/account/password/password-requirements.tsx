import React from 'react';

export interface IPasswordRequirementsProps {
  password?: string;
}

export const PasswordRequirements: React.FC<IPasswordRequirementsProps> = ({ password = '' }) => {
  const reqs = [
    { label: 'Ít nhất 8 ký tự.', valid: password.length >= 8 },
    {
      label: 'Có chữ hoa và chữ thường (A–Z, a–z).',
      valid: /[A-Z]/.test(password) && /[a-z]/.test(password),
    },
    { label: 'Có ít nhất một chữ số (0–9).', valid: /[0-9]/.test(password) },
    { label: 'Có ký tự đặc biệt, ví dụ: !, @, #, $, %...', valid: /[^A-Za-z0-9]/.test(password) },
    { label: 'Khác với mật khẩu hiện tại.', valid: true },
  ];

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
      <div className="password-requirements-card">
        <h4 className="req-title">Mật khẩu mới cần có</h4>
        <ul className="req-list">
          {reqs.map((r, i) => {
            const isValid = password.length > 0 && r.valid;
            return (
              <li key={i} className={`req-item ${isValid ? 'is-valid' : ''}`}>
                <span className="req-icon">{isValid ? '✓' : '•'}</span>
                <span>{r.label}</span>
              </li>
            );
          })}
        </ul>
      </div>

      <div className="security-tip-card">
        <h4 className="tip-title">Giữ tài khoản an toàn</h4>
        <p className="tip-desc">
          Không dùng tên, ngày sinh hoặc mật khẩu đã dùng ở nơi khác. Tránh chia sẻ mật khẩu của bạn với bất kỳ ai.
        </p>
      </div>
    </div>
  );
};

export default PasswordRequirements;
