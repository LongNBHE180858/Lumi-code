import React, { useCallback, useEffect, useState } from 'react';
import { Check, RefreshCw, Send, X } from 'lucide-react';
import AdminLayout from 'app/modules/administration/layout/admin-layout';
import { dateLabel, errorLabel, financeApi, moneyLabel, statusLabel, type PayoutView, type RefundView } from './finance-api';
import AdminFinanceDialog from './admin-finance-dialog';
import './finance.scss';

export type FinanceAction =
  | { kind: 'payout'; item: PayoutView; step: 'approve' | 'reject' | 'complete' }
  | { kind: 'refund'; item: RefundView; step: 'approve' | 'reject' | 'complete' };

const AdminFinance = () => {
  const [payouts, setPayouts] = useState<PayoutView[]>([]);
  const [refunds, setRefunds] = useState<RefundView[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');
  const [dialog, setDialog] = useState<FinanceAction | null>(null);
  const [actionError, setActionError] = useState('');
  const [busy, setBusy] = useState(false);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const [nextPayouts, nextRefunds] = await Promise.all([financeApi.payouts(), financeApi.refunds()]);
      setPayouts(nextPayouts);
      setRefunds(nextRefunds);
      setError('');
    } catch (failure) {
      setError(errorLabel(failure));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void load();
  }, [load]);

  const openDialog = (next: FinanceAction) => {
    setActionError('');
    setMessage('');
    setDialog(next);
  };

  const closeDialog = () => {
    if (busy) return;
    setDialog(null);
    setActionError('');
  };

  const confirmAction = async (detail: string) => {
    if (!dialog || busy) return;
    setBusy(true);
    setActionError('');
    try {
      if (dialog.kind === 'payout') {
        if (dialog.step === 'complete') await financeApi.completePayout(dialog.item.id, detail);
        else await financeApi.decidePayout(dialog.item.id, dialog.step === 'approve' ? 'APPROVE' : 'REJECT', detail);
      } else if (dialog.step === 'complete') await financeApi.completeRefund(dialog.item.id, detail);
      else await financeApi.decideRefund(dialog.item.id, dialog.step === 'approve' ? 'APPROVE' : 'REJECT', detail);
      setMessage(
        dialog.step === 'approve' ? 'Đã duyệt yêu cầu.' : dialog.step === 'reject' ? 'Đã từ chối yêu cầu.' : 'Đã ghi nhận chuyển khoản.',
      );
      setDialog(null);
      await load();
    } catch (failure) {
      setActionError(errorLabel(failure));
    } finally {
      setBusy(false);
    }
  };

  const actions = (kind: 'payout' | 'refund', item: PayoutView | RefundView) => (
    <div className="admin-finance-row-actions">
      {item.status === 'PENDING' && (
        <>
          <button type="button" disabled={busy} onClick={() => openDialog({ kind, item, step: 'approve' } as FinanceAction)}>
            <Check size={15} aria-hidden="true" /> Duyệt
          </button>
          <button
            type="button"
            className="danger"
            disabled={busy}
            onClick={() => openDialog({ kind, item, step: 'reject' } as FinanceAction)}
          >
            <X size={15} aria-hidden="true" /> Từ chối
          </button>
        </>
      )}
      {item.status === 'APPROVED' && (
        <button type="button" disabled={busy} onClick={() => openDialog({ kind, item, step: 'complete' } as FinanceAction)}>
          <Send size={15} aria-hidden="true" /> Xác nhận đã chuyển
        </button>
      )}
      {(item.status === 'COMPLETED' || item.status === 'REJECTED') && <span className="admin-finance-no-action">Đã xử lý</span>}
    </div>
  );

  const status = (value: string, detail?: string | null) => (
    <>
      <span className={`admin-finance-status ${value.toLowerCase()}`}>{statusLabel(value)}</span>
      {detail && <small>{detail}</small>}
    </>
  );

  return (
    <AdminLayout>
      <main className="admin-page-content admin-finance">
        <div className="admin-finance-card">
          <div className="admin-finance-heading">
            <div>
              <h1>Rút tiền & Hoàn tiền</h1>
              <p>Xem và xử lý yêu cầu chuyển khoản của giảng viên và phụ huynh.</p>
            </div>
            <button type="button" className="admin-finance-button" onClick={() => void load()} disabled={loading || busy}>
              <RefreshCw size={16} aria-hidden="true" /> Làm mới
            </button>
          </div>
          {error && (
            <div className="admin-finance-error" role="alert">
              {error}
            </div>
          )}
          {message && (
            <div className="admin-finance-success" role="status">
              {message}
            </div>
          )}

          <section className="admin-finance-section" aria-labelledby="admin-payouts-title">
            <div className="admin-finance-section-heading">
              <h2 id="admin-payouts-title">Yêu cầu rút tiền</h2>
              <span>{payouts.length} yêu cầu</span>
            </div>
            <div className="admin-finance-table-wrap">
              <table>
                <thead>
                  <tr>
                    <th>Mã</th>
                    <th>Giảng viên</th>
                    <th>Số tiền</th>
                    <th>Tài khoản nhận</th>
                    <th>Ngày gửi</th>
                    <th>Trạng thái</th>
                    <th>Thao tác</th>
                  </tr>
                </thead>
                <tbody>
                  {payouts.map(item => (
                    <tr key={item.id}>
                      <td>
                        <strong>#{item.id}</strong>
                      </td>
                      <td>{item.instructorLogin}</td>
                      <td>
                        <strong>{moneyLabel(item.amount)}</strong>
                      </td>
                      <td>
                        <strong>
                          {item.bankCode} · {item.bankAccount}
                        </strong>
                        <small>{item.accountHolderName}</small>
                      </td>
                      <td>{dateLabel(item.createdAt)}</td>
                      <td>{status(item.status, item.rejectionReason ?? item.transferReference)}</td>
                      <td>{actions('payout', item)}</td>
                    </tr>
                  ))}
                  {payouts.length === 0 && (
                    <tr>
                      <td colSpan={7} className="admin-finance-empty">
                        {loading ? 'Đang tải yêu cầu...' : 'Chưa có yêu cầu rút tiền.'}
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </section>

          <section className="admin-finance-section" aria-labelledby="admin-refunds-title">
            <div className="admin-finance-section-heading">
              <h2 id="admin-refunds-title">Yêu cầu hoàn tiền</h2>
              <span>{refunds.length} yêu cầu</span>
            </div>
            <div className="admin-finance-table-wrap">
              <table>
                <thead>
                  <tr>
                    <th>Mã</th>
                    <th>Đơn hàng</th>
                    <th>Phụ huynh</th>
                    <th>Số tiền</th>
                    <th>Lý do</th>
                    <th>Tài khoản nhận</th>
                    <th>Trạng thái</th>
                    <th>Thao tác</th>
                  </tr>
                </thead>
                <tbody>
                  {refunds.map(item => (
                    <tr key={item.id}>
                      <td>
                        <strong>#{item.id}</strong>
                      </td>
                      <td>#{item.orderId}</td>
                      <td>{item.buyerLogin ?? '—'}</td>
                      <td>
                        <strong>{moneyLabel(item.amount)}</strong>
                      </td>
                      <td>{item.reason}</td>
                      <td>
                        <strong>
                          {item.bankCode} · {item.bankAccount}
                        </strong>
                        <small>{item.accountHolderName}</small>
                      </td>
                      <td>{status(item.status, item.reviewNote ?? item.refundReference)}</td>
                      <td>{actions('refund', item)}</td>
                    </tr>
                  ))}
                  {refunds.length === 0 && (
                    <tr>
                      <td colSpan={8} className="admin-finance-empty">
                        {loading ? 'Đang tải yêu cầu...' : 'Chưa có yêu cầu hoàn tiền.'}
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </section>
        </div>
      </main>
      {dialog && <AdminFinanceDialog action={dialog} busy={busy} error={actionError} onClose={closeDialog} onConfirm={confirmAction} />}
    </AdminLayout>
  );
};

export default AdminFinance;
