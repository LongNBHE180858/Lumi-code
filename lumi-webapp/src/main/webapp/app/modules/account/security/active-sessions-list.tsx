import React from 'react';

export interface IUserSessionItem {
  id: number | string;
  deviceInfo?: string;
  createdAt?: string;
  expiresAt?: string;
  isRevoked?: boolean;
  isActive?: boolean;
  isCurrent?: boolean;
}

export interface IActiveSessionsListProps {
  sessions: IUserSessionItem[];
  onLogoutSession: (sessionId: number | string) => void;
  onLogoutAllOther: () => void;
  loading?: boolean;
}

const parseDeviceType = (ua = ''): 'desktop' | 'mobile' | 'laptop' => {
  const lower = ua.toLowerCase();
  if (lower.includes('iphone') || lower.includes('android') || lower.includes('mobile')) {
    return 'mobile';
  }
  if (lower.includes('macintosh') || lower.includes('mac os')) {
    return 'laptop';
  }
  return 'desktop';
};

const parseBrowserAndOs = (ua = ''): { name: string; details: string } => {
  if (!ua) return { name: 'Thiết bị không xác định', details: 'Trình duyệt web' };

  let os = 'Windows';
  if (ua.includes('iPhone')) os = 'iPhone';
  else if (ua.includes('iPad')) os = 'iPad';
  else if (ua.includes('Android')) os = 'Android';
  else if (ua.includes('Mac OS') || ua.includes('Macintosh')) os = 'macOS';
  else if (ua.includes('Linux')) os = 'Linux';

  let browser = 'Web Browser';
  if (ua.includes('Edg/')) browser = 'Microsoft Edge';
  else if (ua.includes('Chrome/')) browser = 'Chrome';
  else if (ua.includes('Safari/') && !ua.includes('Chrome/')) browser = 'Safari';
  else if (ua.includes('Firefox/')) browser = 'Firefox';

  return {
    name: `${browser} trên ${os}`,
    details: `${browser} • ${os}`,
  };
};

const renderDeviceIcon = (type: 'desktop' | 'mobile' | 'laptop') => {
  switch (type) {
    case 'mobile':
      return (
        <svg
          width="20"
          height="20"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          strokeWidth="2"
          strokeLinecap="round"
          strokeLinejoin="round"
        >
          <rect x="5" y="2" width="14" height="20" rx="2" ry="2" />
          <line x1="12" y1="18" x2="12.01" y2="18" />
        </svg>
      );
    case 'laptop':
      return (
        <svg
          width="20"
          height="20"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          strokeWidth="2"
          strokeLinecap="round"
          strokeLinejoin="round"
        >
          <rect x="2" y="3" width="20" height="14" rx="2" ry="2" />
          <line x1="2" y1="20" x2="22" y2="20" />
        </svg>
      );
    case 'desktop':
    default:
      return (
        <svg
          width="20"
          height="20"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          strokeWidth="2"
          strokeLinecap="round"
          strokeLinejoin="round"
        >
          <rect x="2" y="3" width="20" height="14" rx="2" ry="2" />
          <line x1="8" y1="21" x2="16" y2="21" />
          <line x1="12" y1="17" x2="12" y2="21" />
        </svg>
      );
  }
};

export const ActiveSessionsList: React.FC<IActiveSessionsListProps> = ({
  sessions,
  onLogoutSession,
  onLogoutAllOther,
  loading = false,
}) => {
  const activeSessions = (sessions || []).filter(s => !s.isRevoked);

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
      {/* Header */}
      <div>
        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            marginBottom: '4px',
          }}
        >
          <h3 style={{ fontSize: '16px', fontWeight: 800, color: '#102b59', margin: 0 }}>Thiết bị đang đăng nhập</h3>
          <span style={{ fontSize: '13px', fontWeight: 700, color: '#7894af' }}>{activeSessions.length} phiên hoạt động</span>
        </div>
        <p style={{ fontSize: '13px', fontWeight: 600, color: '#7894af', margin: 0 }}>
          Đăng xuất những thiết bị bạn không nhận ra hoặc không còn sử dụng.
        </p>
      </div>

      {/* Table Header */}
      <div className="session-table-header">
        <span>Thiết bị / Trình duyệt</span>
        <span>Vị trí</span>
        <span>Đăng nhập lúc</span>
        <span style={{ textAlign: 'right' }}>Hành động</span>
      </div>

      {/* Session Rows */}
      <div className="session-list-wrap">
        {loading && <div style={{ padding: '16px', color: '#7894af' }}>Đang tải danh sách thiết bị...</div>}

        {!loading && activeSessions.length === 0 && (
          <div style={{ padding: '20px', color: '#7894af', textAlign: 'center' }}>Chưa có thông tin phiên đăng nhập nào được lưu.</div>
        )}

        {!loading &&
          activeSessions.map((s, index) => {
            const devType = parseDeviceType(s.deviceInfo);
            const { name, details } = parseBrowserAndOs(s.deviceInfo);
            const isCurrent = s.isCurrent ?? index === 0;

            const formattedTime = s.createdAt
              ? new Date(s.createdAt).toLocaleString('vi-VN', {
                  day: '2-digit',
                  month: '2-digit',
                  year: 'numeric',
                  hour: '2-digit',
                  minute: '2-digit',
                })
              : 'Đang hoạt động';

            return (
              <div key={s.id} className={`session-row ${isCurrent ? 'current' : ''}`}>
                {/* Device Info */}
                <div className="session-device-wrap">
                  <div className="session-icon-box">{renderDeviceIcon(devType)}</div>
                  <div>
                    <div className="session-device-name">
                      <span>{name}</span>
                      {isCurrent && <span className="badge-current-session">Phiên hiện tại</span>}
                    </div>
                    <div className="session-client-info">{details}</div>
                  </div>
                </div>

                {/* Location */}
                <div className="session-meta-text">Việt Nam</div>

                {/* Last Active */}
                <div>
                  <div className="session-meta-text" style={{ color: isCurrent ? '#24a8e8' : '#102b59' }}>
                    {isCurrent ? 'Đang hoạt động' : 'Đã đăng nhập'}
                  </div>
                  <div className="session-meta-sub">{formattedTime}</div>
                </div>

                {/* Action */}
                <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
                  {isCurrent ? (
                    <span style={{ fontSize: '13px', fontWeight: 700, color: '#7894af' }}>Thiết bị này</span>
                  ) : (
                    <button
                      type="button"
                      className="btn-pill-outline"
                      style={{ height: '32px', padding: '0 14px', fontSize: '13px' }}
                      onClick={() => onLogoutSession(s.id)}
                    >
                      Đăng xuất
                    </button>
                  )}
                </div>
              </div>
            );
          })}
      </div>

      {/* Bulk Logout Action */}
      {sessions.length > 1 && (
        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            flexWrap: 'wrap',
            gap: '14px',
            paddingTop: '8px',
          }}
        >
          <span style={{ fontSize: '13px', fontWeight: 600, color: '#7894af' }}>
            Phiên hiện tại được giữ lại. Các thiết bị khác sẽ cần đăng nhập lại để tiếp tục.
          </span>

          <button type="button" className="btn-pill-outline" onClick={onLogoutAllOther}>
            Đăng xuất tất cả thiết bị khác
          </button>
        </div>
      )}
    </div>
  );
};

export default ActiveSessionsList;
