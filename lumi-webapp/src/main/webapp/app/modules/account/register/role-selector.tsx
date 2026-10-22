import React from 'react';

export type UserRole = 'ROLE_STUDENT' | 'ROLE_INSTRUCTOR' | 'ROLE_PARENT';

interface RoleSelectorProps {
  selectedRole: UserRole;
  onSelectRole: (role: UserRole) => void;
}

export const RoleSelector: React.FC<RoleSelectorProps> = ({ selectedRole, onSelectRole }) => {
  const roles: { id: UserRole; title: string; hint: string; icon: string }[] = [
    {
      id: 'ROLE_STUDENT',
      title: 'Học sinh',
      hint: 'Tự học và luyện thi theo khối lớp',
      icon: '🎓',
    },
    {
      id: 'ROLE_INSTRUCTOR',
      title: 'Giảng viên',
      hint: 'Giảng dạy và xây dựng bài giảng, khóa học',
      icon: '👨‍🏫',
    },
    {
      id: 'ROLE_PARENT',
      title: 'Phụ huynh',
      hint: 'Đồng hành và theo dõi học tập của con',
      icon: '👨‍👩‍👧',
    },
  ];

  return (
    <div className="form-field-group">
      <div className="section-head-title">
        <span className="section-badge-num">01</span>
        <span>Chọn vai trò tài khoản</span>
      </div>
      <p className="section-head-hint">Chọn đối tượng sử dụng phù hợp để LUMI tối ưu giao diện và lộ trình học tập cho bạn.</p>

      <div className="role-selector-grid">
        {roles.map(r => {
          const isSelected = selectedRole === r.id;
          return (
            <div
              key={r.id}
              className={`role-card-item ${isSelected ? 'selected' : ''}`}
              onClick={() => onSelectRole(r.id)}
              role="button"
              tabIndex={0}
              onKeyDown={e => {
                if (e.key === 'Enter' || e.key === ' ') {
                  onSelectRole(r.id);
                }
              }}
            >
              <div className="role-radio-circle">{isSelected && <div className="role-radio-dot" />}</div>
              <div>
                <div className="role-title">
                  <span style={{ marginRight: '6px' }}>{r.icon}</span>
                  {r.title}
                </div>
                <div className="role-hint">{r.hint}</div>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default RoleSelector;
