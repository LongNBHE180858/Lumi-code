import React, { useState } from 'react';
import type { ClassItem } from '../types';

interface JoinClassModalProps {
  isOpen: boolean;
  allClasses: ClassItem[];
  onClose: () => void;
  onRequestJoin: (classItem: ClassItem, password?: string) => void;
}

export const JoinClassModal: React.FC<JoinClassModalProps> = ({ isOpen, allClasses, onClose, onRequestJoin }) => {
  const [inputCode, setInputCode] = useState('LUMI-MATH6B');
  const [foundClass, setFoundClass] = useState<ClassItem | null>(null);
  const [searchError, setSearchError] = useState<string | null>(null);
  const [enteredPassword, setEnteredPassword] = useState('');
  const [passwordError, setPasswordError] = useState<string | null>(null);

  if (!isOpen) return null;

  const handleSearchClass = (e?: React.FormEvent) => {
    if (e) e.preventDefault();
    const clean = inputCode.trim().toUpperCase();
    if (!clean) {
      setSearchError('Vui lòng nhập mã lớp.');
      setFoundClass(null);
      return;
    }
    // Find class matching code
    let match = allClasses.find(c => c.code.toUpperCase() === clean);
    // If user entered LUMI-MATH6B and it doesn't exist yet, generate a realistic match for demo
    if (!match && clean === 'LUMI-MATH6B') {
      match = {
        id: 'toan-6b',
        name: 'Toán 6B',
        code: 'LUMI-MATH6B',
        course: 'Toán lớp 6',
        grade: 'Lớp 6',
        subject: 'Toán',
        studentCount: 28,
        status: 'active',
        password: '',
        description: 'Lớp Toán 6B cơ bản và củng cố kiến thức hình học trực quan.',
        teacherName: 'Lan Nguyễn',
      };
    }

    if (match) {
      setFoundClass(match);
      setSearchError(null);
    } else {
      setFoundClass(null);
      setSearchError(`Không tìm thấy lớp nào có mã "${clean}". Vui lòng kiểm tra lại.`);
    }
  };

  const handleConfirmJoin = () => {
    if (!foundClass) return;
    if (foundClass.password && foundClass.password !== '••••••••' && enteredPassword !== foundClass.password) {
      setPasswordError('Mật khẩu lớp không chính xác. Vui lòng hỏi lại giáo viên.');
      return;
    }
    onRequestJoin(foundClass, enteredPassword);
    onClose();
  };

  return (
    <div className="lumi-modal-overlay" onClick={onClose}>
      <div className="lumi-modal-dialog join-class-modal-dialog fade-in" onClick={e => e.stopPropagation()} role="dialog" aria-modal="true">
        <div className="lumi-modal-head">
          <div className="modal-title-wrap">
            <span className="modal-badge-icon">🎓</span>
            <h2 className="lumi-modal-title">Tham gia lớp bằng mã</h2>
          </div>
          <button type="button" className="lumi-modal-close" onClick={onClose}>
            ✕
          </button>
        </div>

        <p className="modal-subtitle-text">Nhập mã lớp giáo viên cung cấp, kiểm tra thông tin lớp rồi gửi yêu cầu tham gia.</p>

        {/* Step 1: Input Code */}
        <div className="join-modal-step-box">
          <label className="form-item-label">
            1. Nhập mã lớp <span className="required-star">*</span>
          </label>
          <div className="join-code-input-row">
            <input
              type="text"
              className="form-item-input font-code"
              placeholder="VD: LUMI-MATH6A"
              value={inputCode}
              onChange={e => {
                setInputCode(e.target.value.toUpperCase());
                setSearchError(null);
              }}
              autoFocus
            />
            <button type="button" className="btn-pill-primary btn-search-code" onClick={() => handleSearchClass()}>
              Tìm lớp
            </button>
          </div>
          <span className="field-hint-small">Nhập đầy đủ mã, bao gồm tiền tố LUMI và dấu gạch ngang (nếu có).</span>

          {searchError && (
            <div className="toast-inline-error fade-in" style={{ marginTop: '10px' }}>
              ⚠️ {searchError}
            </div>
          )}
        </div>

        {/* Step 2: Confirmation If Found */}
        {foundClass && (
          <div className="join-modal-result-box fade-in">
            <div className="result-status-tag">✓ Đã tìm thấy lớp · Sẵn sàng gửi yêu cầu</div>

            <div className="found-class-header">
              <span className="found-icon">🏫</span>
              <div>
                <h3 className="found-name">{foundClass.name}</h3>
                <code className="found-code">{foundClass.code}</code>
              </div>
            </div>

            <div className="found-class-grid">
              <div className="found-grid-item">
                <span className="found-label">Giáo viên:</span>
                <span className="found-val">{foundClass.teacherName || 'Lan Nguyễn'}</span>
              </div>
              <div className="found-grid-item">
                <span className="found-label">Khóa học:</span>
                <span className="found-val">{foundClass.course}</span>
              </div>
              <div className="found-grid-item">
                <span className="found-label">Khối / Môn:</span>
                <span className="found-val">
                  {foundClass.grade} · {foundClass.subject}
                </span>
              </div>
              <div className="found-grid-item">
                <span className="found-label">Trạng thái:</span>
                <span className="status-pill status-active" style={{ padding: '2px 8px' }}>
                  Đang hoạt động
                </span>
              </div>
            </div>

            {/* Password field if required */}
            {foundClass.password && foundClass.password !== '' && (
              <div className="form-group-item" style={{ marginTop: '12px' }}>
                <label className="form-item-label">Mật khẩu lớp (yêu cầu từ giáo viên)</label>
                <input
                  type="password"
                  className="form-item-input"
                  placeholder="Nhập mật khẩu do giáo viên cung cấp"
                  value={enteredPassword}
                  onChange={e => {
                    setEnteredPassword(e.target.value);
                    setPasswordError(null);
                  }}
                />
                {passwordError && (
                  <span className="toast-inline-error" style={{ display: 'block', marginTop: '6px' }}>
                    {passwordError}
                  </span>
                )}
              </div>
            )}

            <div className="join-rules-callout">
              <span className="rules-icon">ℹ️</span>
              <div>
                <strong>Quy định phê duyệt:</strong>
                <p>
                  Lớp cần giáo viên phê duyệt. Sau khi gửi, yêu cầu sẽ ở trạng thái <em>Chờ duyệt</em>. Bạn sẽ xem được tài liệu và bài tập
                  ngay khi giáo viên chấp nhận.
                </p>
              </div>
            </div>

            <div className="account-tag-bar">
              Gửi yêu cầu với tài khoản: <strong>Nguyễn Minh Anh</strong> (@minhanh)
            </div>
          </div>
        )}

        {/* Modal Actions */}
        <div className="modal-actions-bar">
          <button type="button" className="btn-pill-outline" onClick={onClose}>
            Hủy
          </button>
          <button type="button" className="btn-pill-primary" disabled={!foundClass} onClick={handleConfirmJoin}>
            Gửi yêu cầu tham gia
          </button>
        </div>
      </div>
    </div>
  );
};

export default JoinClassModal;
