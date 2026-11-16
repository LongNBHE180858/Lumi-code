import React, { useState } from 'react';
import { CreateClassFormValues } from '../class-management.model';

interface CreateClassModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (values: CreateClassFormValues) => void;
}

export const CreateClassModal: React.FC<CreateClassModalProps> = ({ isOpen, onClose, onSubmit }) => {
  const [name, setName] = useState('');
  const [course, setCourse] = useState('Toán lớp 6');
  const [grade, setGrade] = useState('Lớp 6');
  const [subject, setSubject] = useState('Toán');
  const [password, setPassword] = useState('');
  const [description, setDescription] = useState('');
  const [errorMsg, setErrorMsg] = useState('');

  if (!isOpen) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim()) {
      setErrorMsg('Vui lòng nhập tên lớp.');
      return;
    }
    onSubmit({
      name: name.trim(),
      course,
      grade,
      subject,
      password: password.trim() || undefined,
      description: description.trim() || undefined,
    });
    setName('');
    setPassword('');
    setDescription('');
    setErrorMsg('');
    onClose();
  };

  return (
    <div className="lumi-modal-overlay" onClick={onClose}>
      <div
        className="lumi-modal-dialog fade-in"
        onClick={e => e.stopPropagation()}
        role="dialog"
        aria-modal="true"
        aria-labelledby="modal-create-class-title"
      >
        {/* Header */}
        <div className="lumi-modal-head">
          <div className="modal-title-wrap">
            <span className="modal-badge-icon">➕</span>
            <h2 id="modal-create-class-title" className="lumi-modal-title">
              Tạo lớp học mới
            </h2>
          </div>
          <button type="button" className="lumi-modal-close" onClick={onClose} aria-label="Đóng hộp thoại">
            ✕
          </button>
        </div>

        <p className="modal-subtitle-text">Tạo không gian lớp học mới để quản lý sĩ số học viên và giao bài tập.</p>

        {errorMsg && <div className="toast-inline-error fade-in">⚠️ {errorMsg}</div>}

        {/* Form */}
        <form onSubmit={handleSubmit} className="lumi-modal-form">
          <div className="form-group-item">
            <label className="form-item-label">
              Tên lớp học <span className="required-star">*</span>
            </label>
            <input
              type="text"
              className="form-item-input"
              placeholder="VD: Toán 6A, Luyện thi nâng cao..."
              value={name}
              onChange={e => {
                setName(e.target.value);
                if (errorMsg) setErrorMsg('');
              }}
              autoFocus
              required
            />
          </div>

          <div className="form-row-2col">
            <div className="form-group-item">
              <label className="form-item-label">Khóa học liên kết</label>
              <select className="form-item-select" value={course} onChange={e => setCourse(e.target.value)}>
                <option value="Toán lớp 6">Toán lớp 6</option>
                <option value="Đại số cơ bản">Đại số cơ bản</option>
                <option value="Lịch sử Việt Nam">Lịch sử Việt Nam</option>
                <option value="Hình học trực quan">Hình học trực quan</option>
              </select>
            </div>

            <div className="form-group-item">
              <label className="form-item-label">Khối lớp</label>
              <select className="form-item-select" value={grade} onChange={e => setGrade(e.target.value)}>
                <option value="Lớp 6">Lớp 6</option>
                <option value="Lớp 7">Lớp 7</option>
                <option value="Lớp 8">Lớp 8</option>
                <option value="Lớp 9">Lớp 9</option>
              </select>
            </div>
          </div>

          <div className="form-row-2col">
            <div className="form-group-item">
              <label className="form-item-label">Môn học</label>
              <select className="form-item-select" value={subject} onChange={e => setSubject(e.target.value)}>
                <option value="Toán">Toán</option>
                <option value="Ngữ văn">Ngữ văn</option>
                <option value="Lịch sử">Lịch sử</option>
                <option value="Khoa học tự nhiên">Khoa học tự nhiên</option>
              </select>
            </div>

            <div className="form-group-item">
              <label className="form-item-label">Mật khẩu vào lớp (nếu có)</label>
              <input
                type="text"
                className="form-item-input"
                placeholder="Để trống nếu công khai"
                value={password}
                onChange={e => setPassword(e.target.value)}
              />
            </div>
          </div>

          <div className="form-group-item">
            <label className="form-item-label">Mô tả lớp học</label>
            <textarea
              className="form-item-textarea"
              rows={2}
              placeholder="Nhập thông tin giới thiệu ngắn về lớp học..."
              value={description}
              onChange={e => setDescription(e.target.value)}
            />
          </div>

          <div className="lumi-modal-foot">
            <button type="button" className="btn-modal-cancel" onClick={onClose}>
              Hủy bỏ
            </button>
            <button type="submit" className="btn-modal-submit">
              Xác nhận tạo lớp
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default CreateClassModal;
