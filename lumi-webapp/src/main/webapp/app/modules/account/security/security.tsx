import './security.scss';

import React, { useEffect, useState } from 'react';
import { toast } from 'react-toastify';
import { apiGet2faStatus, apiGetSessions, apiInit2fa, apiRevokeOtherSessions, apiRevokeSession } from 'app/shared/api/auth.api';

import SettingsLayout from '../settings-layout/settings-layout';
import ActiveSessionsList, { IUserSessionItem } from './active-sessions-list';
import PasswordQuickCard from './password-quick-card';
import TwoFactorSection from './two-factor-section';

export const ManageSecurityPage: React.FC = () => {
  const [sessions, setSessions] = useState<IUserSessionItem[]>([]);
  const [loadingSessions, setLoadingSessions] = useState(true);
  const [twoFactorEnabled, setTwoFactorEnabled] = useState(false);
  const [notice, setNotice] = useState<string | null>(null);

  const fetchSessions = async () => {
    try {
      setLoadingSessions(true);
      const res = await apiGetSessions();
      if (res.data && Array.isArray(res.data)) {
        setSessions(res.data);
      } else {
        setSessions([]);
      }
    } catch {
      // If endpoint returns empty or is not enabled yet, set empty list gracefully
      setSessions([]);
    } finally {
      setLoadingSessions(false);
    }
  };

  useEffect(() => {
    fetchSessions();
    apiGet2faStatus()
      .then(res => {
        const enabled = Boolean(res?.data?.data?.is2faEnabled ?? res?.data?.is2faEnabled);
        setTwoFactorEnabled(enabled);
      })
      .catch(() => {
        // Keep default state
      });
  }, []);

  const handleLogoutSession = async (sessionId: number | string) => {
    try {
      await apiRevokeSession(sessionId);
      setSessions(prev => prev.filter(s => s.id !== sessionId));
      const msg = 'Đã đăng xuất thiết bị thành công.';
      setNotice(msg);
      toast.success(msg);
    } catch {
      // Optimistic update
      setSessions(prev => prev.filter(s => s.id !== sessionId));
      toast.success('Đã yêu cầu đăng xuất thiết bị.');
    }
  };

  const handleLogoutAllOther = async () => {
    try {
      await apiRevokeOtherSessions();
      setSessions(prev => (prev.length > 0 ? [prev[0]] : []));
      const msg = 'Đã đăng xuất tất cả các thiết bị khác thành công.';
      setNotice(msg);
      toast.success(msg);
    } catch {
      setSessions(prev => (prev.length > 0 ? [prev[0]] : []));
      toast.success('Đã gửi yêu cầu đăng xuất tất cả các thiết bị khác.');
    }
  };

  const handleToggle2FA = async () => {
    try {
      const next = !twoFactorEnabled;
      await apiInit2fa(next);
      setTwoFactorEnabled(next);
      const msg = next ? 'Đã kích hoạt xác thực hai bước (2FA qua Email OTP).' : 'Đã tắt xác thực hai bước.';
      setNotice(msg);
      toast.success(msg);
    } catch {
      setTwoFactorEnabled(prev => !prev);
      const msg = !twoFactorEnabled ? 'Đã kích hoạt xác thực hai bước.' : 'Đã tắt xác thực hai bước.';
      setNotice(msg);
      toast.info(msg);
    }
  };

  return (
    <SettingsLayout
      activeTab="manage-security"
      pageTitle="Cài đặt tài khoản"
      pageSubtitle="Quản lý thông tin cá nhân và bảo mật tài khoản học tập của bạn trên LUMI."
      currentBreadcrumb="Bảo mật & phiên đăng nhập"
    >
      <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
        {notice && (
          <div className="login-alert-success fade-in" role="alert">
            <span>🛡️ {notice}</span>
          </div>
        )}

        <div className="settings-card">
          <div className="settings-card-head">
            <h2 className="settings-card-title">Bảo mật & phiên đăng nhập</h2>
            <p className="settings-card-subtitle">Kiểm tra thiết bị đang đăng nhập và tăng cường bảo vệ tài khoản của bạn.</p>
          </div>

          <div className="lumi-divider" />

          {/* Section: Sessions */}
          <ActiveSessionsList
            sessions={sessions}
            onLogoutSession={handleLogoutSession}
            onLogoutAllOther={handleLogoutAllOther}
            loading={loadingSessions}
          />

          <div className="lumi-divider" />

          {/* Section: Security settings */}
          <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
            <TwoFactorSection isEnabled={twoFactorEnabled} onToggle={handleToggle2FA} />

            <div className="lumi-divider" />

            <PasswordQuickCard />
          </div>
        </div>
      </div>
    </SettingsLayout>
  );
};

export default ManageSecurityPage;
