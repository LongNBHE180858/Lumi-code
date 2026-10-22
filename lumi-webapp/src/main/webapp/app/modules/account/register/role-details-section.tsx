import React from 'react';
import { UserRole } from './role-selector';

export interface RoleDetailFields {
  schoolName: string;
  gradeLevel: number | string;
  subjects?: string[];
  yearsOfExperience?: string;
  notes?: string;
}

interface RoleDetailsSectionProps {
  role: UserRole;
  fields: RoleDetailFields;
  onChange: (field: keyof RoleDetailFields, value: any) => void;
  onToggleSubject?: (subject: string) => void;
}

const AVAILABLE_SUBJECTS = ['Toán học', 'Lịch sử'];

const GRADES = [
  { value: 1, label: 'Lớp 1 (Tiểu học)' },
  { value: 2, label: 'Lớp 2 (Tiểu học)' },
  { value: 3, label: 'Lớp 3 (Tiểu học)' },
  { value: 4, label: 'Lớp 4 (Tiểu học)' },
  { value: 5, label: 'Lớp 5 (Tiểu học)' },
  { value: 6, label: 'Lớp 6 (THCS)' },
  { value: 7, label: 'Lớp 7 (THCS)' },
  { value: 8, label: 'Lớp 8 (THCS)' },
  { value: 9, label: 'Lớp 9 (THCS)' },
];

export const RoleDetailsSection: React.FC<RoleDetailsSectionProps> = ({ role, fields, onChange, onToggleSubject }) => {
  const isTeacher = role === 'ROLE_INSTRUCTOR';
  const isParent = role === 'ROLE_PARENT';

  const sectionTitle = isTeacher
    ? 'Thông tin giảng viên & chuyên môn'
    : isParent
      ? 'Thông tin đồng hành phụ huynh'
      : 'Thông tin học sinh & trường lớp';

  const sectionHint = isTeacher
    ? 'Hồ sơ chuyên môn giúp kết nối đúng lớp học và học sinh phù hợp.'
    : isParent
      ? 'Cung cấp thông tin trường lớp của con để nhận báo cáo học tập chính xác.'
      : 'Thông tin giúp LUMI chuẩn hóa ngân hàng đề thi và tài liệu học phù hợp.';

  return (
    <div className="register-column-section">
      <div>
        <div className="section-head-title">
          <span className="section-badge-num">03</span>
          <span>{sectionTitle}</span>
        </div>
        <p className="section-head-hint">{sectionHint}</p>
      </div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: '16px', marginTop: '16px' }}>
        {/* Trường học */}
        <div className="form-field-group">
          <label className="form-label-title">
            {isTeacher ? 'Đơn vị công tác / Trường giảng dạy' : isParent ? 'Trường học của con' : 'Trường đang học'}
          </label>
          <div className="form-input-container">
            <input
              type="text"
              className="form-input-element"
              value={fields.schoolName}
              onChange={e => onChange('schoolName', e.target.value)}
              placeholder="Ví dụ: THPT Chuyên Hà Nội - Amsterdam"
            />
          </div>
        </div>

        {/* Khối lớp */}
        <div className="form-field-group">
          <label className="form-label-title">
            {isTeacher ? 'Khối lớp phụ trách chính' : isParent ? 'Khối lớp của con' : 'Khối lớp học tập'}{' '}
            <span style={{ color: 'var(--color-primary, #24a8e8)' }}>*</span>
          </label>
          <div className="form-input-container">
            <select
              className="form-input-element"
              value={fields.gradeLevel || ''}
              onChange={e => onChange('gradeLevel', e.target.value ? Number(e.target.value) : '')}
            >
              <option value="">-- Chọn khối lớp --</option>
              {GRADES.map(g => (
                <option key={g.value} value={g.value}>
                  {g.label}
                </option>
              ))}
            </select>
          </div>
        </div>

        {/* Thông tin mở rộng dành riêng cho Giảng viên */}
        {isTeacher && (
          <>
            <div className="form-field-group">
              <label className="form-label-title">Môn học giảng dạy chính</label>
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: '8px', marginTop: '6px' }}>
                {AVAILABLE_SUBJECTS.map(subj => {
                  const isChecked = fields.subjects?.includes(subj);
                  return (
                    <button
                      key={subj}
                      type="button"
                      className={`badge-selector-item ${isChecked ? 'active' : ''}`}
                      onClick={() => onToggleSubject && onToggleSubject(subj)}
                      style={{
                        padding: '6px 14px',
                        borderRadius: '20px',
                        border: isChecked ? '1px solid #24a8e8' : '1px solid #d3e8f6',
                        backgroundColor: isChecked ? '#e6f6ff' : '#ffffff',
                        color: isChecked ? '#102b59' : '#4b6382',
                        fontWeight: isChecked ? 700 : 500,
                        cursor: 'pointer',
                        fontSize: '13px',
                        transition: 'all 0.2s ease',
                      }}
                    >
                      {isChecked ? '✓ ' : '+ '}
                      {subj}
                    </button>
                  );
                })}
              </div>
            </div>

            <div className="form-field-group">
              <label className="form-label-title">Số năm kinh nghiệm giảng dạy</label>
              <div className="form-input-container">
                <input
                  type="text"
                  className="form-input-element"
                  value={fields.yearsOfExperience || ''}
                  onChange={e => onChange('yearsOfExperience', e.target.value)}
                  placeholder="Ví dụ: 5 năm luyện thi THPT Quốc gia"
                />
              </div>
            </div>

            <div className="form-field-group">
              <label className="form-label-title">Giới thiệu ngắn / Phương pháp giảng dạy</label>
              <div className="form-input-container">
                <textarea
                  className="form-input-element"
                  rows={3}
                  value={fields.notes || ''}
                  onChange={e => onChange('notes', e.target.value)}
                  placeholder="Tóm tắt về định hướng sư phạm, thành tích giảng dạy hoặc phương pháp học thông minh..."
                  style={{ resize: 'vertical', paddingTop: '10px' }}
                />
              </div>
            </div>
          </>
        )}

        {/* Thông tin mở rộng dành cho Phụ huynh */}
        {isParent && (
          <div className="form-field-group">
            <label className="form-label-title">Mục tiêu đồng hành hoặc ghi chú</label>
            <div className="form-input-container">
              <textarea
                className="form-input-element"
                rows={2}
                value={fields.notes || ''}
                onChange={e => onChange('notes', e.target.value)}
                placeholder="Ví dụ: Muốn theo dõi điểm số môn Toán và Lịch sử của con hàng tuần..."
                style={{ resize: 'vertical', paddingTop: '10px' }}
              />
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default RoleDetailsSection;
