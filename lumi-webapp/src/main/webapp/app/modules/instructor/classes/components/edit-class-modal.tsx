import React, { useState, useEffect } from 'react';
import { ClassItem, EditClassFormValues, ClassStatus } from '../class-management.model';

interface EditClassModalProps {
  isOpen: boolean;
  classItem: ClassItem | null;
  onClose: () => void;
  onSubmit: (values: EditClassFormValues) => void;
}

export const EditClassModal: React.FC<EditClassModalProps> = ({ isOpen, classItem, onClose, onSubmit }) => {
  const [name, setName] = useState('');
  const [password, setPassword] = useState('');
  const [status, setStatus] = useState<ClassStatus>('active');
  const [description, setDescription] = useState('');

  useEffect(() => {
    if (classItem) {
      setName(classItem.name);
      setPassword(classItem.password || '');
      setStatus(classItem.status);
      setDescription(classItem.description || '');
    }
  }, [classItem]);

  if (!isOpen || !classItem) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim()) return;
    onSubmit({
      name: name.trim(),
      password: password.trim() || undefined,
      status,
      description: description.trim(),
    });
    onClose();
  };

  return (
    <div className="lumi-modal-overlay" onClick={onClose}>
      <div
        className="lumi-modal-dialog fade-in"
        onClick={e => e.stopPropagation()}
        role="dialog"
        aria-modal="true"
        aria-labelledby="modal-edit-class-title"
      >
        <div className="lumi-modal-head">
          <div className="modal-title-wrap">
            <span className="modal-badge-icon">✏️</span>
            <h2 id="modal-edit-class-title" className="lumi-modal-title">
              Chỉnh sửa thông tin lớp
            </h2>
          </div>
          <button type="button" className="lumi-modal-close" onClick={onClose}>
            ✕
          </button>
        </div>

        <p className="modal-subtitle-text">
          Cập nhật thông tin và trạng thái lớp học <strong>{classItem.code}</strong>.
        </p>

        <form onSubmit={handleSubmit} className="lumi-modal-form">
          <div className="form-group-item">
            <label className="form-item-label">
              Tên lớp học <span className="required-star">*</span>
            </label>
            <input type="text" className="form-item-input" value={name} onChange={e => setName(e.target.value)} required />
          </div>

          <div className="form-group-item">
            <label className="form-item-label">Trạng thái lớp</label>
            <select className="form-item-select" value={status} onChange={e => setStatus(e.target.value as ClassStatus)}>
              <option value="active">Đang hoạt động</option>
              <option value="archived">Tạm dừng</option>
            </select>
          </div>

          <div className="form-group-item">
            <label className="form-item-label">Mật khẩu vào lớp</label>
            <input
              type="text"
              className="form-item-input"
              placeholder="Để trống nếu không đặt mật khẩu"
              value={password}
              onChange={e => setPassword(e.target.value)}
            />
          </div>

          <div className="form-group-item">
            <label className="form-item-label">Mô tả lớp</label>
            <textarea className="form-item-textarea" rows={3} value={description} onChange={e => setDescription(e.target.value)} />
          </div>

          <div className="lumi-modal-foot">
            <button type="button" className="btn-modal-cancel" onClick={onClose}>
              Hủy
            </button>
            <button type="submit" className="btn-modal-submit">
              Lưu thay đổi
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default EditClassModal;
