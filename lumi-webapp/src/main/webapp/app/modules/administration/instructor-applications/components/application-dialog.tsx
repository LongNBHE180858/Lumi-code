import React from 'react';
import { X } from 'lucide-react';
import type { InstructorApplication } from '../instructor-applications-api';
import { formatDate, fullName, statusLabel, type InstructorDialogMode } from '../instructor-application-format';

interface ApplicationDialogProps {
  selected: InstructorApplication;
  dialog: InstructorDialogMode;
  reason: string;
  setReason: (reason: string) => void;
  actionError: string;
  acting: boolean;
  detailReady: boolean;
  closeDialog: () => void;
  decide: () => void;
}

const ApplicationDialog = ({
  selected,
  dialog,
  reason,
  setReason,
  actionError,
  acting,
  detailReady,
  closeDialog,
  decide,
}: ApplicationDialogProps) => (
  <div
    className="instructor-applications-overlay"
    role="presentation"
    onMouseDown={event => {
      if (event.target === event.currentTarget) closeDialog();
    }}
  >
    <section className="instructor-applications-dialog" role="dialog" aria-modal="true" aria-labelledby="instructor-dialog-title">
      <button type="button" className="instructor-applications-close" onClick={closeDialog} aria-label="Đóng">
        <X size={20} />
      </button>
      <h2 id="instructor-dialog-title">
        {dialog === 'details' ? 'Hồ sơ giảng viên' : dialog === 'approve' ? 'Duyệt hồ sơ giảng viên' : 'Từ chối hồ sơ giảng viên'}
      </h2>
      {!detailReady && !actionError && <p className="instructor-applications-loading">Đang tải chi tiết hồ sơ...</p>}
      <dl className="instructor-applications-details">
        <div>
          <dt>Họ và tên</dt>
          <dd>{fullName(selected)}</dd>
        </div>
        <div>
          <dt>Tên đăng nhập</dt>
          <dd>{selected.login}</dd>
        </div>
        <div>
          <dt>Email</dt>
          <dd>{selected.email || '—'}</dd>
        </div>
        <div>
          <dt>Số điện thoại</dt>
          <dd>{selected.phoneNumber || '—'}</dd>
        </div>
        <div>
          <dt>Trường học</dt>
          <dd>{selected.schoolName || '—'}</dd>
        </div>
        <div>
          <dt>Khối lớp</dt>
          <dd>{selected.gradeLevel ?? '—'}</dd>
        </div>
        <div>
          <dt>Ngày đăng ký</dt>
          <dd>{formatDate(selected.submittedAt)}</dd>
        </div>
        <div>
          <dt>Trạng thái</dt>
          <dd>{statusLabel(selected.status)}</dd>
        </div>
        <div>
          <dt>Xác minh email</dt>
          <dd>{selected.activated ? 'Đã xác minh' : 'Chưa xác minh'}</dd>
        </div>
        {selected.reviewedAt && (
          <div>
            <dt>Thời điểm xử lý</dt>
            <dd>{formatDate(selected.reviewedAt)}</dd>
          </div>
        )}
        {selected.reviewedBy && (
          <div>
            <dt>Admin xử lý</dt>
            <dd>{selected.reviewedBy}</dd>
          </div>
        )}
        {selected.rejectionReason && (
          <div>
            <dt>Lý do từ chối</dt>
            <dd>{selected.rejectionReason}</dd>
          </div>
        )}
      </dl>
      {dialog === 'reject' && (
        <label className="instructor-applications-reason">
          <span className="instructor-applications-reason-label">
            Lý do từ chối <span aria-hidden="true">*</span>
          </span>
          <textarea
            value={reason}
            onChange={event => setReason(event.target.value)}
            maxLength={2000}
            rows={4}
            placeholder="Nhập lý do để người đăng ký biết cần bổ sung gì..."
          />
          <small>{reason.length}/2000 ký tự</small>
        </label>
      )}
      {actionError && (
        <div className="instructor-applications-error" role="alert">
          {actionError}
        </div>
      )}
      <div className="instructor-applications-dialog-actions">
        <button type="button" className="instructor-applications-button" onClick={closeDialog} disabled={acting}>
          Đóng
        </button>
        {dialog !== 'details' && (
          <button
            type="button"
            className={`instructor-applications-button primary${dialog === 'reject' ? ' danger' : ''}`}
            onClick={decide}
            disabled={acting || !detailReady || selected.status !== 'PENDING'}
          >
            {acting ? 'Đang xử lý...' : dialog === 'approve' ? 'Xác nhận duyệt' : 'Xác nhận từ chối'}
          </button>
        )}
      </div>
    </section>
  </div>
);

export default ApplicationDialog;
