import React, { useEffect, useState } from 'react';
import { X } from 'lucide-react';
import { moneyLabel } from './finance-api';
import type { FinanceAction } from './admin-finance';

interface AdminFinanceDialogProps {
  action: FinanceAction;
  busy: boolean;
  error: string;
  onClose: () => void;
  onConfirm: (detail: string) => void;
}

const AdminFinanceDialog = ({ action, busy, error, onClose, onConfirm }: AdminFinanceDialogProps) => {
  const [detail, setDetail] = useState('');
  const { item, kind, step } = action;
  const subject = kind === 'payout' ? 'rút tiền' : 'hoàn tiền';
  const title =
    step === 'approve' ? `Duyệt yêu cầu ${subject}` : step === 'reject' ? `Từ chối yêu cầu ${subject}` : `Xác nhận chuyển khoản ${subject}`;
  const trimmed = detail.trim();
  const needsDetail = step !== 'approve';

  useEffect(() => {
    const onKeyDown = (event: KeyboardEvent) => {
      if (event.key === 'Escape' && !busy) onClose();
    };
    document.addEventListener('keydown', onKeyDown);
    return () => document.removeEventListener('keydown', onKeyDown);
  }, [busy, onClose]);

  const submit = (event: React.FormEvent) => {
    event.preventDefault();
    if (busy || (needsDetail && !trimmed)) return;
    onConfirm(trimmed);
  };

  return (
    <div
      className="admin-finance-overlay"
      role="presentation"
      onMouseDown={event => {
        if (event.target === event.currentTarget) onClose();
      }}
    >
      <section className="admin-finance-dialog" role="dialog" aria-modal="true" aria-labelledby="admin-finance-dialog-title">
        <button
          type="button"
          className="admin-finance-dialog-close"
          aria-label="Đóng"
          onClick={onClose}
          disabled={busy}
          autoFocus={step === 'approve'}
        >
          <X size={20} />
        </button>
        <h2 id="admin-finance-dialog-title">{title}</h2>
        <p className="admin-finance-dialog-subtitle">
          {step === 'approve'
            ? 'Kiểm tra thông tin trước khi duyệt yêu cầu.'
            : step === 'reject'
              ? 'Nhập lý do để người gửi biết vì sao yêu cầu bị từ chối.'
              : 'Chỉ xác nhận sau khi đã chuyển khoản cho người nhận.'}
        </p>
        <dl className="admin-finance-dialog-details">
          <div>
            <dt>Mã yêu cầu</dt>
            <dd>#{item.id}</dd>
          </div>
          <div>
            <dt>{kind === 'payout' ? 'Giảng viên' : 'Phụ huynh'}</dt>
            <dd>{kind === 'payout' ? item.instructorLogin : (item.buyerLogin ?? '—')}</dd>
          </div>
          <div>
            <dt>Số tiền</dt>
            <dd>{moneyLabel(item.amount)}</dd>
          </div>
          <div>
            <dt>Ngân hàng</dt>
            <dd>{item.bankCode || '—'}</dd>
          </div>
          <div>
            <dt>Số tài khoản</dt>
            <dd>{item.bankAccount || '—'}</dd>
          </div>
          <div>
            <dt>Chủ tài khoản</dt>
            <dd>{item.accountHolderName || '—'}</dd>
          </div>
          {kind === 'refund' && (
            <div className="wide">
              <dt>Đơn hàng và lý do</dt>
              <dd>
                #{item.orderId} · {item.reason}
              </dd>
            </div>
          )}
        </dl>
        {step === 'approve' && kind === 'refund' && (
          <p className="admin-finance-dialog-note">Khi duyệt hoàn tiền, quyền học của học sinh sẽ bị thu hồi.</p>
        )}
        <form onSubmit={submit}>
          {step === 'reject' && (
            <label className="admin-finance-dialog-field">
              <span className="admin-finance-dialog-field-label">
                Lý do từ chối <span aria-hidden="true">*</span>
              </span>
              <textarea
                autoFocus
                required
                maxLength={2000}
                rows={4}
                value={detail}
                onChange={event => setDetail(event.target.value)}
                placeholder="Nhập lý do từ chối..."
              />
              <small>{detail.length}/2000 ký tự</small>
            </label>
          )}
          {step === 'complete' && (
            <label className="admin-finance-dialog-field">
              <span className="admin-finance-dialog-field-label">
                Mã giao dịch chuyển khoản <span aria-hidden="true">*</span>
              </span>
              <input
                autoFocus
                required
                maxLength={255}
                value={detail}
                onChange={event => setDetail(event.target.value)}
                placeholder="Nhập mã giao dịch từ ngân hàng"
              />
              <small>{detail.length}/255 ký tự</small>
            </label>
          )}
          {error && (
            <div className="admin-finance-error" role="alert">
              {error}
            </div>
          )}
          <div className="admin-finance-dialog-actions">
            <button type="button" className="admin-finance-button" onClick={onClose} disabled={busy}>
              Hủy
            </button>
            <button
              type="submit"
              className={`admin-finance-button primary${step === 'reject' ? ' danger' : ''}`}
              disabled={busy || (needsDetail && !trimmed)}
            >
              {busy
                ? 'Đang xử lý...'
                : step === 'approve'
                  ? 'Xác nhận duyệt'
                  : step === 'reject'
                    ? 'Xác nhận từ chối'
                    : 'Xác nhận đã chuyển'}
            </button>
          </div>
        </form>
      </section>
    </div>
  );
};

export default AdminFinanceDialog;
