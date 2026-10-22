import React from 'react';
import { Link } from 'react-router';

export const PasswordQuickCard: React.FC = () => {
  return (
    <div
      style={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        gap: '20px',
        flexWrap: 'wrap',
      }}
    >
      <div style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
        <h4 style={{ fontSize: '15px', fontWeight: 800, color: '#102b59', margin: 0 }}>Mật khẩu</h4>
        <p style={{ fontSize: '13px', fontWeight: 600, color: '#7894af', margin: 0 }}>
          Nếu nghi ngờ tài khoản bị truy cập trái phép, hãy đổi mật khẩu ngay để bảo vệ tài khoản.
        </p>
      </div>

      <Link to="/account/password" className="btn-pill-outline">
        Đổi mật khẩu
      </Link>
    </div>
  );
};

export default PasswordQuickCard;
