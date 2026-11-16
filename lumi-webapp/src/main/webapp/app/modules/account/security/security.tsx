import './security.scss';

import React, { useEffect, useState } from 'react';
import { toast } from 'react-toastify';
import { apiGet2faStatus, apiGetSessions, apiInit2fa, apiRevokeOtherSessions, apiRevokeSession } from 'app/shared/api/auth.api';

import SettingsLayout from '../settings-layout/settings-layout';
import ActiveSessionsList, { IUserSessionItem } from './active-sessions-list';
import PasswordQuickCard from './password-quick-card';
import TwoFactorSection from './two-factor-section';
import SecurityTwoFactorModal from './security-two-factor-modal';

export const ManageSecurityPage: React.FC = () => {
  const [sessions, setSessions] = useState<IUserSessionItem[]>([]);
  const [loadingSessions, setLoadingSessions] = useState(true);
  const [twoFactorEnabled, setTwoFactorEnabled] = useState(false);
  const [twoFactorEmail, setTwoFactorEmail] = useState('');
  const [loadingTwoFactor, setLoadingTwoFactor] = useState(true);
  const [sendingTwoFactorOtp, setSendingTwoFactorOtp] = useState(false);
  const [pendingTwoFactorState, setPendingTwoFactorState] = useState<boolean | null>(null);
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
        const status = res?.data?.data ?? res?.data;
        const enabled = Boolean(status?.is2faEnable);
        setTwoFactorEnabled(enabled);
        setTwoFactorEmail(status?.email ?? '');
      })
      .catch(() => {
        toast.error('Không thể tải trạng thái 2FA. Vui lòng tải lại trang.');
      })
      .finally(() => setLoadingTwoFactor(false));
  }, []);

  const handleLogoutSession = async (sessionId: number | string) => {
    try {
      await apiRevokeSession(sessionId);
      setSessions(prev => prev.filter(s => s.id !== sessionId));
      const msg = 'Đã đăng xuất thiết bị thành công.';
      setNotice(msg);
      toast.success(msg);
    } catch (error: any) {
      toast.error(error?.response?.data?.message || 'Không thể đăng xuất thiết bị. Vui lòng thử lại.');
    }
  };

  const handleLogoutAllOther = async () => {
    try {
      await apiRevokeOtherSessions();
      await fetchSessions();
      const msg = 'Đã đăng xuất tất cả các thiết bị khác thành công.';
      setNotice(msg);
      toast.success(msg);
    } catch (error: any) {
      toast.error(error?.response?.data?.message || 'Không thể đăng xuất các thiết bị khác. Vui lòng thử lại.');
    }
  };

  const handleToggle2FA = async () => {
    if (!twoFactorEmail || sendingTwoFactorOtp || pendingTwoFactorState !== null) return;
    setSendingTwoFactorOtp(true);
    try {
      const next = !twoFactorEnabled;
      await apiInit2fa();
      setPendingTwoFactorState(next);
      toast.success('Mã OTP đã được gửi về email của bạn.');
    } catch (error: any) {
      toast.error(error?.response?.data?.message || 'Không thể gửi mã OTP. Vui lòng thử lại.');
    } finally {
      setSendingTwoFactorOtp(false);
    }
  };

  const handleTwoFactorConfirmed = (enabled: boolean) => {
    setTwoFactorEnabled(enabled);
    setPendingTwoFactorState(null);
    const message = enabled ? 'Đã bật xác thực hai bước.' : 'Đã tắt xác thực hai bước.';
    setNotice(message);
    toast.success(message);
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
            <TwoFactorSection
              isEnabled={twoFactorEnabled}
              onToggle={handleToggle2FA}
              loading={loadingTwoFactor || sendingTwoFactorOtp || !twoFactorEmail}
            />

            <div className="lumi-divider" />

            <PasswordQuickCard />
          </div>
        </div>
      </div>
      {pendingTwoFactorState !== null && (
        <SecurityTwoFactorModal
          email={twoFactorEmail}
          enable={pendingTwoFactorState}
          onClose={() => setPendingTwoFactorState(null)}
          onConfirmed={handleTwoFactorConfirmed}
        />
      )}
    </SettingsLayout>
  );
};

export default ManageSecurityPage;
