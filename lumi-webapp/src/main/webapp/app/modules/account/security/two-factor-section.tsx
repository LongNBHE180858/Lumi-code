import React from 'react';

export interface ITwoFactorSectionProps {
  isEnabled: boolean;
  onToggle: () => void;
  loading?: boolean;
}

export const TwoFactorSection: React.FC<ITwoFactorSectionProps> = ({ isEnabled, onToggle, loading = false }) => {
  return (
    <div
      style={{
        display: 'flex',
        alignItems: 'flex-start',
        justifyContent: 'space-between',
        gap: '20px',
        flexWrap: 'wrap',
      }}
    >
      <div style={{ display: 'flex', flexDirection: 'column', gap: '6px', maxWidth: '640px' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
          <div
            style={{
              width: '24px',
              height: '24px',
              borderRadius: '6px',
              backgroundColor: '#e6f5ff',
              color: '#24a8e8',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
            }}
          >
            <svg
              width="15"
              height="15"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2.2"
              strokeLinecap="round"
              strokeLinejoin="round"
            >
              <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
              <polyline points="9 12 11 14 15 10" />
            </svg>
          </div>

          <h4 style={{ fontSize: '15px', fontWeight: 800, color: '#102b59', margin: 0 }}>Xác thực hai bước (2FA qua Email OTP)</h4>

          <span
            style={{
              fontSize: '11px',
              fontWeight: 800,
              padding: '2px 8px',
              borderRadius: '999px',
              backgroundColor: isEnabled ? '#ecfdf5' : '#e6f5ff',
              color: isEnabled ? '#059669' : '#24a8e8',
            }}
          >
            {isEnabled ? 'Đã bật' : 'Chưa bật'}
          </span>
        </div>

        <p style={{ fontSize: '13px', fontWeight: 600, color: '#7894af', lineHeight: 1.5, margin: 0 }}>
          Thêm một bước xác minh qua mã OTP gửi về Email khi đăng nhập, giúp bảo vệ tài khoản ngay cả khi mật khẩu bị lộ.
        </p>

        <span style={{ fontSize: '12px', color: '#7894af' }}>
          {isEnabled
            ? 'Tài khoản của bạn đang được bảo vệ an toàn bằng mã OTP gửi về Email.'
            : 'Chọn Bật 2FA để kích hoạt lớp bảo vệ tài khoản qua mã OTP.'}
        </span>
      </div>

      <button type="button" className={isEnabled ? 'btn-pill-outline' : 'btn-pill-primary'} onClick={onToggle} disabled={loading}>
        {loading ? 'Đang cập nhật...' : isEnabled ? 'Tắt 2FA' : 'Bật 2FA'}
      </button>
    </div>
  );
};

export default TwoFactorSection;
