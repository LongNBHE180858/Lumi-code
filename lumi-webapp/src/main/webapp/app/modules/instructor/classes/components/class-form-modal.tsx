import React, { useEffect, useState } from 'react';
import type { ClassFormValues, InstructorClass } from '../class-management.api';

interface ClassFormModalProps {
  classItem: InstructorClass | null;
  saving: boolean;
  error: string | null;
  onClose: () => void;
  onSubmit: (values: ClassFormValues) => void;
}

const emptyForm: ClassFormValues = {
  name: '',
  description: '',
  joinMode: 'CODE_AUTO',
  status: 'ACTIVE',
  maxStudents: null,
};

export const ClassFormModal: React.FC<ClassFormModalProps> = ({ classItem, saving, error, onClose, onSubmit }) => {
  const [values, setValues] = useState<ClassFormValues>(emptyForm);

  useEffect(() => {
    setValues(
      classItem
        ? {
            name: classItem.name,
            description: classItem.description ?? '',
            joinMode: classItem.joinMode,
            status: classItem.status,
            maxStudents: classItem.maxStudents,
          }
        : emptyForm,
    );
  }, [classItem]);

  const submit = (event: React.FormEvent) => {
    event.preventDefault();
    if (!values.name.trim()) return;
    onSubmit({ ...values, name: values.name.trim(), description: values.description.trim() });
  };

  return (
    <div className="lumi-modal-overlay" onMouseDown={event => event.target === event.currentTarget && !saving && onClose()}>
      <div className="lumi-modal-dialog class-form-dialog fade-in" role="dialog" aria-modal="true" aria-labelledby="class-form-title">
        <div className="lumi-modal-head">
          <div className="modal-title-wrap">
            <span className="modal-badge-icon">{classItem ? '✏️' : '➕'}</span>
            <h2 id="class-form-title" className="lumi-modal-title">
              {classItem ? 'Chỉnh sửa lớp học' : 'Tạo lớp học mới'}
            </h2>
          </div>
          <button type="button" className="lumi-modal-close" onClick={onClose} disabled={saving} aria-label="Đóng hộp thoại">
            ✕
          </button>
        </div>
        <p className="modal-subtitle-text">{classItem ? `Mã lớp: ${classItem.joinCode}` : 'Mã tham gia sẽ được tạo sau khi lưu lớp.'}</p>
        {error && (
          <p className="toast-inline-error" role="alert">
            {error}
          </p>
        )}
        <form className="lumi-modal-form" onSubmit={submit}>
          <div className="form-group-item">
            <label className="form-item-label" htmlFor="class-name">
              Tên lớp học <span className="required-star">*</span>
            </label>
            <input
              id="class-name"
              className="form-item-input"
              value={values.name}
              maxLength={255}
              required
              autoFocus
              disabled={saving}
              onChange={event => setValues({ ...values, name: event.target.value })}
            />
          </div>
          <div className="form-group-item">
            <label className="form-item-label" htmlFor="class-description">
              Mô tả
            </label>
            <textarea
              id="class-description"
              className="form-item-textarea"
              value={values.description}
              maxLength={5000}
              disabled={saving}
              onChange={event => setValues({ ...values, description: event.target.value })}
            />
          </div>
          <div className="form-row-2col">
            <div className="form-group-item">
              <label className="form-item-label" htmlFor="class-limit">
                Sĩ số tối đa
              </label>
              <input
                id="class-limit"
                className="form-item-input"
                type="number"
                min={1}
                value={values.maxStudents ?? ''}
                disabled={saving}
                onChange={event => setValues({ ...values, maxStudents: event.target.value ? Number(event.target.value) : null })}
              />
            </div>
            <div className="form-group-item">
              <label className="form-item-label" htmlFor="class-join-mode">
                Cách tham gia
              </label>
              <select
                id="class-join-mode"
                className="form-item-select"
                value={values.joinMode}
                disabled={saving}
                onChange={event => setValues({ ...values, joinMode: event.target.value as ClassFormValues['joinMode'] })}
              >
                <option value="CODE_AUTO">Nhập mã, vào lớp ngay</option>
                <option value="CODE_APPROVAL">Nhập mã, chờ duyệt</option>
                <option value="INVITE_ONLY">Chỉ nhận lời mời</option>
              </select>
            </div>
          </div>
          <div className="form-group-item">
            <label className="form-item-label" htmlFor="class-status">
              Trạng thái
            </label>
            <select
              id="class-status"
              className="form-item-select"
              value={values.status}
              disabled={saving}
              onChange={event => setValues({ ...values, status: event.target.value as ClassFormValues['status'] })}
            >
              <option value="DRAFT">Bản nháp</option>
              <option value="ACTIVE">Đang hoạt động</option>
              <option value="HIDDEN">Đang ẩn</option>
              <option value="ARCHIVED">Đã lưu trữ</option>
            </select>
          </div>
          <div className="lumi-modal-foot">
            <button type="button" className="btn-modal-cancel" onClick={onClose} disabled={saving}>
              Hủy
            </button>
            <button type="submit" className="btn-modal-submit" disabled={saving}>
              {saving ? 'Đang lưu...' : classItem ? 'Lưu thay đổi' : 'Tạo lớp'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ClassFormModal;
