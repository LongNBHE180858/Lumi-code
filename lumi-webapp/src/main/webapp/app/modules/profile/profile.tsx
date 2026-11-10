import React from 'react';
import { Button } from 'react-bootstrap';
import { Link } from 'react-router';

import avatar from '/content/images/profile-avatar.svg';
import { useAppSelector } from 'app/config/store';
import { Authority } from 'app/shared/jhipster/constants';
import SettingsLayout from 'app/modules/account/settings-layout/settings-layout';
import { useLinkedStudents, useProfile, useProfileStatistics } from './use-profile';

import './profile.scss';

type ProfileRole = 'student' | 'instructor' | 'parent';

const roleBadges: Record<ProfileRole, string> = {
  student: 'HỌC VIÊN LUMI',
  instructor: 'GIÁO VIÊN LUMI',
  parent: 'PHỤ HUYNH LUMI',
};

const verificationLabels: Record<string, string> = {
  APPROVED: 'Đã duyệt hồ sơ',
  PENDING: 'Chờ duyệt hồ sơ',
  REJECTED: 'Hồ sơ bị từ chối',
  BLOCKED: 'Đã bị khóa',
  UNVERIFIED: 'Chưa xác minh',
};

const RequestFeedback = ({ loading, error, retry }: { loading: boolean; error: string | null; retry: () => void }) => {
  if (loading)
    return (
      <p className="profile-status-message" role="status">
        Đang tải dữ liệu hồ sơ...
      </p>
    );
  if (!error) return null;

  return (
    <div className="profile-request-error" role="alert">
      <span>{error}</span>
      <Button variant="outline-primary" size="sm" onClick={retry}>
        Thử lại
      </Button>
    </div>
  );
};

const Profile = () => {
  const authorities: string[] = useAppSelector(state => state.authentication.account.authorities ?? []);

  let role: ProfileRole | undefined;

  if (authorities.includes(Authority.STUDENT)) {
    role = 'student';
  } else if (authorities.includes(Authority.INSTRUCTOR) || authorities.includes('ROLE_TEACHER')) {
    role = 'instructor';
  } else if (authorities.includes(Authority.PARENT)) {
    role = 'parent';
  } else {
    role = 'student';
  }

  // Luôn gọi hook trước các nhánh return; API phụ chỉ tải cho đúng role.
  const profileRequest = useProfile(Boolean(role));
  const statisticsRequest = useProfileStatistics(role === 'student');
  const linksRequest = useLinkedStudents(role === 'parent');
  const profile = profileRequest.data;
  const statistics = statisticsRequest.data;
  const fullName = profile?.fullName?.trim() || [profile?.firstName, profile?.lastName].filter(Boolean).join(' ').trim();
  const details = profile
    ? [
        { label: 'Họ và tên', value: fullName || profile.login },
        { label: 'Email', value: profile.email },
        { label: 'Số điện thoại', value: profile.phoneNumber },
        ...(role === 'student'
          ? [
              { label: 'Trường học', value: profile.schoolName },
              { label: 'Khối lớp', value: profile.gradeLevel != null ? `Khối ${profile.gradeLevel}` : null },
            ]
          : []),
        ...(role === 'instructor'
          ? [{ label: 'Trạng thái xác minh', value: verificationLabels[profile.verificationStatus ?? ''] || 'Chưa có thông tin xác minh' }]
          : []),
      ]
    : [];
  const learningStats = statistics
    ? [
        { label: 'Khóa học đã đăng ký', value: statistics.enrolledCourseCount, icon: '📚' },
        { label: 'Chuỗi học hiện tại', value: `${statistics.currentStreak} ngày`, icon: '🔥' },
        { label: 'Chuỗi học dài nhất', value: `${statistics.longestStreak} ngày`, icon: '⚡' },
        { label: 'Số gem tích lũy', value: statistics.gemsBalance, icon: '💎' },
      ]
    : [];

  return (
    <SettingsLayout
      activeTab="profile"
      pageTitle="Cài đặt tài khoản"
      pageSubtitle="Quản lý thông tin cá nhân và bảo mật tài khoản học tập của bạn trên LUMI."
      currentBreadcrumb="Hồ sơ cá nhân"
    >
      <div className="profile-layout-container">
        {/* Header Action Card */}
        <div className="profile-header-card">
          <div className="profile-header-info">
            <h2 className="profile-header-title">Hồ sơ cá nhân</h2>
            <p className="profile-header-desc">Xem và quản lý thông tin tài khoản của bạn trên nền tảng LUMI.</p>
          </div>

          <div className="profile-actions">
            <Link to="/profile/edit" className="profile-edit-btn">
              <svg
                width="16"
                height="16"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2.2"
                strokeLinecap="round"
                strokeLinejoin="round"
              >
                <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" />
                <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" />
              </svg>
              <span>Chỉnh sửa hồ sơ</span>
            </Link>

            {role === 'student' && (
              <Button variant="outline-primary" className="profile-streak-btn" disabled>
                Chuỗi học tập
              </Button>
            )}
          </div>
        </div>

        {!role && (
          <p role="status" className="profile-status-message">
            Tài khoản chưa có giao diện hồ sơ phù hợp.
          </p>
        )}
        <RequestFeedback loading={profileRequest.loading} error={profileRequest.error} retry={profileRequest.reload} />

        {profile && role && (
          <div className="profile-columns">
            {/* Identity Card */}
            <aside className="profile-panel profile-identity" aria-label="Giới thiệu bản thân">
              <div className="profile-avatar-group">
                <div className="profile-avatar-wrapper">
                  <img
                    className="profile-avatar-image"
                    src={profile.imageUrl?.trim() || avatar}
                    alt="Ảnh đại diện"
                    width={110}
                    height={110}
                    onError={event => {
                      if (event.currentTarget.getAttribute('src') !== avatar) {
                        event.currentTarget.setAttribute('src', avatar);
                      }
                    }}
                  />
                </div>

                <span className={`profile-role-badge role-${role}`}>{roleBadges[role]}</span>
              </div>

              <div className="profile-introduction">
                <span className="profile-intro-label">Giới thiệu bản thân</span>
                <p className="profile-intro-text">{profile.bio?.trim() || 'Chưa cập nhật phần giới thiệu cá nhân.'}</p>
              </div>
            </aside>

            {/* Account Details Card */}
            <section className="profile-panel profile-account" aria-labelledby="profile-account-title">
              <h3 id="profile-account-title" className="profile-panel-title">
                Thông tin chi tiết
              </h3>

              <dl className="profile-details">
                {details.map((detail, index) => (
                  <div className="profile-detail-row" key={detail.label}>
                    <dt className="profile-detail-dt">{detail.label}</dt>
                    <dd
                      className={[
                        'profile-detail-dd',
                        index === 0 ? 'profile-detail-name' : '',
                        !detail.value?.trim() ? 'profile-detail-muted' : '',
                      ]
                        .filter(Boolean)
                        .join(' ')}
                    >
                      {detail.value?.trim() || 'Chưa cập nhật'}
                    </dd>
                  </div>
                ))}
              </dl>
            </section>
          </div>
        )}

        {/* Statistics for Student */}
        {role === 'student' && (
          <section className="profile-statistics-section" aria-labelledby="profile-statistics-title">
            <h3 id="profile-statistics-title" className="profile-section-title">
              Thống kê học tập
            </h3>

            <RequestFeedback loading={statisticsRequest.loading} error={statisticsRequest.error} retry={statisticsRequest.reload} />
            {statistics && (
              <div className="profile-stats-grid">
                {learningStats.map((stat, idx) => (
                  <div className={`profile-stat-card stat-card-${idx}`} key={stat.label}>
                    <div className="stat-card-header">
                      <span className="stat-icon">{stat.icon}</span>
                      <span className="stat-label">{stat.label}</span>
                    </div>
                    <strong className="stat-value">{stat.value}</strong>
                  </div>
                ))}
              </div>
            )}
          </section>
        )}

        {/* Linked Students for Parent */}
        {role === 'parent' && (
          <section className="profile-panel profile-linked" aria-labelledby="profile-linked-title">
            <h3 id="profile-linked-title" className="profile-panel-title">
              Học sinh đã liên kết
            </h3>

            <RequestFeedback loading={linksRequest.loading} error={linksRequest.error} retry={linksRequest.reload} />
            {linksRequest.data && linksRequest.data.length === 0 && <p className="profile-empty">Chưa có học sinh đã liên kết.</p>}
            {linksRequest.data && linksRequest.data.length > 0 && (
              <ul className="profile-student-list">
                {linksRequest.data.map(student => (
                  <li key={student.linkId} className="profile-student-item">
                    <span className="student-name">{student.fullName?.trim() || 'Chưa cập nhật họ tên'}</span>
                    <span className="profile-link-status">Đã liên kết</span>
                  </li>
                ))}
              </ul>
            )}
          </section>
        )}
      </div>
    </SettingsLayout>
  );
};

export default Profile;
