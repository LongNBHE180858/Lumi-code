import React, { useCallback, useEffect, useState } from 'react';
import AdminLayout from 'app/modules/administration/layout/admin-layout';
import { dateLabel, errorLabel, financeApi, moneyLabel, statusLabel, type PayoutView, type RefundView } from './finance-api';
import './finance.scss';

const AdminFinance = () => {
  const [payouts, setPayouts] = useState<PayoutView[]>([]);
  const [refunds, setRefunds] = useState<RefundView[]>([]);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');
  const [busy, setBusy] = useState(false);
  const load = useCallback(async () => {
    try {
      const [nextPayouts, nextRefunds] = await Promise.all([financeApi.payouts(), financeApi.refunds()]);
      setPayouts(nextPayouts);
      setRefunds(nextRefunds);
      setError('');
    } catch (failure) {
      setError(errorLabel(failure));
    }
  }, []);
  useEffect(() => {
    void load();
  }, [load]);

  const action = async (kind: 'payout' | 'refund', id: number, step: 'approve' | 'reject' | 'complete') => {
    let detail = '';
    if (step === 'reject') {
      detail = window.prompt('Nhập lý do từ chối')?.trim() ?? '';
      if (!detail) return;
    }
    if (step === 'complete') {
      detail = window.prompt('Nhập mã giao dịch chuyển khoản đã thực hiện')?.trim() ?? '';
      if (!detail) return;
    }
    if (step === 'approve' && !window.confirm('Xác nhận duyệt yêu cầu này?')) return;
    setBusy(true);
    setError('');
    setMessage('');
    try {
      if (kind === 'payout') {
        if (step === 'complete') await financeApi.completePayout(id, detail);
        else await financeApi.decidePayout(id, step === 'approve' ? 'APPROVE' : 'REJECT', detail);
      } else {
        if (step === 'complete') await financeApi.completeRefund(id, detail);
        else await financeApi.decideRefund(id, step === 'approve' ? 'APPROVE' : 'REJECT', detail);
      }
      setMessage('Đã cập nhật yêu cầu.');
      await load();
    } catch (failure) {
      setError(errorLabel(failure));
    } finally {
      setBusy(false);
    }
  };

  return (
    <AdminLayout>
      <main className="admin-page-content finance-page">
        <h1>Rút tiền và hoàn tiền</h1>
        {error && (
          <p className="finance-error" role="alert">
            {error}
          </p>
        )}
        {message && (
          <p className="finance-success" role="status">
            {message}
          </p>
        )}
        <section className="finance-card">
          <h2>Yêu cầu rút tiền</h2>
          <div className="finance-table-wrap">
            <table className="finance-table">
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
                    <td>#{item.id}</td>
                    <td>{item.instructorLogin}</td>
                    <td>{moneyLabel(item.amount)}</td>
                    <td>
                      {item.bankCode} · {item.bankAccount}
                      <br />
                      {item.accountHolderName}
                    </td>
                    <td>{dateLabel(item.createdAt)}</td>
                    <td>
                      {statusLabel(item.status)}
                      <br />
                      {item.rejectionReason ?? item.transferReference}
                    </td>
                    <td>
                      <div className="finance-actions">
                        {item.status === 'PENDING' && (
                          <>
                            <button
                              type="button"
                              className="finance-button"
                              disabled={busy}
                              onClick={() => void action('payout', item.id, 'approve')}
                            >
                              Duyệt
                            </button>
                            <button
                              type="button"
                              className="finance-secondary"
                              disabled={busy}
                              onClick={() => void action('payout', item.id, 'reject')}
                            >
                              Từ chối
                            </button>
                          </>
                        )}
                        {item.status === 'APPROVED' && (
                          <button
                            type="button"
                            className="finance-button"
                            disabled={busy}
                            onClick={() => void action('payout', item.id, 'complete')}
                          >
                            Xác nhận đã chuyển
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            {payouts.length === 0 && <p>Chưa có yêu cầu rút tiền.</p>}
          </div>
        </section>
        <section className="finance-card">
          <h2>Yêu cầu hoàn tiền</h2>
          <div className="finance-table-wrap">
            <table className="finance-table">
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
                    <td>#{item.id}</td>
                    <td>#{item.orderId}</td>
                    <td>{item.buyerLogin ?? '—'}</td>
                    <td>{moneyLabel(item.amount)}</td>
                    <td>{item.reason}</td>
                    <td>
                      {item.bankCode} · {item.bankAccount}
                      <br />
                      {item.accountHolderName}
                    </td>
                    <td>
                      {statusLabel(item.status)}
                      <br />
                      {item.reviewNote ?? item.refundReference}
                    </td>
                    <td>
                      <div className="finance-actions">
                        {item.status === 'PENDING' && (
                          <>
                            <button
                              type="button"
                              className="finance-button"
                              disabled={busy}
                              onClick={() => void action('refund', item.id, 'approve')}
                            >
                              Duyệt
                            </button>
                            <button
                              type="button"
                              className="finance-secondary"
                              disabled={busy}
                              onClick={() => void action('refund', item.id, 'reject')}
                            >
                              Từ chối
                            </button>
                          </>
                        )}
                        {item.status === 'APPROVED' && (
                          <button
                            type="button"
                            className="finance-button"
                            disabled={busy}
                            onClick={() => void action('refund', item.id, 'complete')}
                          >
                            Xác nhận đã chuyển
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            {refunds.length === 0 && <p>Chưa có yêu cầu hoàn tiền.</p>}
          </div>
        </section>
      </main>
    </AdminLayout>
  );
};

export default AdminFinance;
