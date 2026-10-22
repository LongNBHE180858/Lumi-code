import React, { useState } from 'react';
import { Link } from 'react-router';

import PasswordRequirements from './password-requirements';

export interface IChangePasswordFormProps {
  onSubmit: (data: { currentPassword: string; newPassword: string }) => void;
  isLoading?: boolean;
}

export const ChangePasswordForm: React.FC<IChangePasswordFormProps> = ({ onSubmit, isLoading = false }) => {
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmNewPassword, setConfirmNewPassword] = useState('');

  const [showCurrent, setShowCurrent] = useState(false);
  const [showNew, setShowNew] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!currentPassword) {
      setErrorMsg('Vui lòng nhập mật khẩu hiện tại');
      return;
    }
    if (!newPassword || newPassword.length < 8) {
      setErrorMsg('Mật khẩu mới cần tối thiểu 8 ký tự');
      return;
    }
    if (newPassword !== confirmNewPassword) {
      setErrorMsg('Mật khẩu mới và xác nhận mật khẩu không trùng khớp');
      return;
    }
    setErrorMsg(null);
    onSubmit({ currentPassword, newPassword });
  };

  return (
    <div className="settings-card">
      <div className="settings-card-head">
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <h2 className="settings-card-title">Đổi mật khẩu</h2>
          <span style={{ fontSize: '12px', fontWeight: 700, color: '#24a8e8' }}>Các trường có dấu * là bắt buộc</span>
        </div>
        <p className="settings-card-subtitle">Nhập mật khẩu hiện tại để xác nhận, sau đó tạo mật khẩu mới cho tài khoản của bạn.</p>
      </div>

      {errorMsg && (
        <div className="login-alert-danger" role="alert">
          <span>⚠️ {errorMsg}</span>
        </div>
      )}

      <div className="lumi-divider" />

      {/* 2-Column: Form Inputs + Requirements */}
      <div className="password-page-grid">
        {/* Left Inputs */}
        <div className="password-input-column">
          {/* Current Password */}
          <div className="form-field-group">
            <label className="form-label-title" htmlFor="currentPassword">
              Mật khẩu hiện tại <span className="required-star">*</span>
            </label>
            <div className="form-input-container">
              <input
                id="currentPassword"
                type={showCurrent ? 'text' : 'password'}
                className="form-input-element"
                placeholder="Nhập mật khẩu đang sử dụng"
                value={currentPassword}
                onChange={e => {
                  setCurrentPassword(e.target.value);
                  if (errorMsg) setErrorMsg(null);
                }}
                required
              />
              <button type="button" className="password-toggle-btn" onClick={() => setShowCurrent(p => !p)}>
                {showCurrent ? 'Ẩn' : 'Hiện'}
              </button>
            </div>
            <span className="form-field-helper">Dùng mật khẩu bạn đang đăng nhập vào LUMI.</span>
          </div>

          {/* New Password */}
          <div className="form-field-group">
            <label className="form-label-title" htmlFor="newPassword">
              Mật khẩu mới <span className="required-star">*</span>
            </label>
            <div className="form-input-container">
              <input
                id="newPassword"
                type={showNew ? 'text' : 'password'}
                className="form-input-element"
                placeholder="Tạo mật khẩu mới"
                value={newPassword}
                onChange={e => {
                  setNewPassword(e.target.value);
                  if (errorMsg) setErrorMsg(null);
                }}
                required
              />
              <button type="button" className="password-toggle-btn" onClick={() => setShowNew(p => !p)}>
                {showNew ? 'Ẩn' : 'Hiện'}
              </button>
            </div>
          </div>

          {/* Confirm New Password */}
          <div className="form-field-group">
            <label className="form-label-title" htmlFor="confirmNewPassword">
              Xác nhận mật khẩu mới <span className="required-star">*</span>
            </label>
            <div className="form-input-container">
              <input
                id="confirmNewPassword"
                type={showConfirm ? 'text' : 'password'}
                className="form-input-element"
                placeholder="Nhập lại mật khẩu mới"
                value={confirmNewPassword}
                onChange={e => {
                  setConfirmNewPassword(e.target.value);
                  if (errorMsg) setErrorMsg(null);
                }}
                required
              />
              <button type="button" className="password-toggle-btn" onClick={() => setShowConfirm(p => !p)}>
                {showConfirm ? 'Ẩn' : 'Hiện'}
              </button>
            </div>
            <span className="form-field-helper">Nhập giống hoàn toàn mật khẩu mới ở trên.</span>
          </div>
        </div>

        {/* Right Requirements */}
        <div>
          <PasswordRequirements password={newPassword} />
        </div>
      </div>

      <div className="lumi-divider" />

      {/* Action Footer */}
      <div className="password-actions-bar">
        <span className="action-note">
          Mật khẩu chỉ thay đổi khi bạn chọn <strong>Lưu mật khẩu</strong>.
        </span>

        <div className="action-buttons">
          <Link to="/account/settings" className="btn-pill-outline">
            Hủy
          </Link>
          <button type="button" className="btn-pill-primary" onClick={handleSubmit} disabled={isLoading}>
            {isLoading ? 'Đang lưu...' : 'Lưu mật khẩu'}
          </button>
        </div>
      </div>
    </div>
  );
};

export default ChangePasswordForm;
