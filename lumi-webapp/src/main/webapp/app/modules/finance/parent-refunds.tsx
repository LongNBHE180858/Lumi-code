import React, { useCallback, useEffect, useState } from 'react';
import { useSearchParams } from 'react-router';
import { dateLabel, errorLabel, financeApi, moneyLabel, statusLabel, type RefundView } from './finance-api';
import './finance.scss';

const ParentRefunds = () => {
  const [search] = useSearchParams();
  const [requests, setRequests] = useState<RefundView[]>([]);
  const [orderId, setOrderId] = useState(search.get('orderId') ?? '');
  const [reason, setReason] = useState('');
  const [bankCode, setBankCode] = useState('');
  const [bankAccount, setBankAccount] = useState('');
  const [accountHolderName, setAccountHolderName] = useState('');
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');
  const [saving, setSaving] = useState(false);
  const load = useCallback(async () => {
    try {
      setRequests(await financeApi.myRefunds());
      setError('');
    } catch (failure) {
      setError(errorLabel(failure));
    }
  }, []);
  useEffect(() => {
    void load();
  }, [load]);

  const submit = async (event: React.FormEvent) => {
    event.preventDefault();
    setSaving(true);
    setError('');
    setMessage('');
    try {
      await financeApi.requestRefund({ orderId: Number(orderId), reason, bankCode, bankAccount, accountHolderName });
      setReason('');
      setMessage('Đã gửi yêu cầu. Quyền học được giữ nguyên trong lúc chờ duyệt.');
      await load();
    } catch (failure) {
      setError(errorLabel(failure));
    } finally {
      setSaving(false);
    }
  };

  return (
    <main className="finance-page">
      <h1>Yêu cầu hoàn tiền</h1>
      <p>Đơn được thanh toán chưa đủ 7 ngày có thể yêu cầu hoàn 80% số tiền đã trả. Admin sẽ kiểm tra và xử lý chuyển khoản.</p>
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
        <h2>Gửi yêu cầu</h2>
        <form onSubmit={event => void submit(event)}>
          <div className="finance-grid">
            <label className="finance-field">
              Mã đơn hàng
              <input required type="number" min="1" step="1" value={orderId} onChange={event => setOrderId(event.target.value)} />
            </label>
            <label className="finance-field">
              Ngân hàng nhận tiền
              <input required maxLength={50} value={bankCode} onChange={event => setBankCode(event.target.value)} />
            </label>
            <label className="finance-field">
              Số tài khoản
              <input required maxLength={100} value={bankAccount} onChange={event => setBankAccount(event.target.value)} />
            </label>
            <label className="finance-field">
              Tên chủ tài khoản
              <input required maxLength={255} value={accountHolderName} onChange={event => setAccountHolderName(event.target.value)} />
            </label>
          </div>
          <label className="finance-field">
            Lý do hoàn tiền
            <textarea required value={reason} onChange={event => setReason(event.target.value)} />
          </label>
          <button className="finance-button" type="submit" disabled={saving}>
            Gửi yêu cầu hoàn tiền
          </button>
        </form>
      </section>
      <section className="finance-card">
        <h2>Yêu cầu của tôi</h2>
        <div className="finance-table-wrap">
          <table className="finance-table">
            <thead>
              <tr>
                <th>Mã</th>
                <th>Đơn hàng</th>
                <th>Số tiền hoàn</th>
                <th>Ngày gửi</th>
                <th>Trạng thái</th>
                <th>Kết quả</th>
              </tr>
            </thead>
            <tbody>
              {requests.map(item => (
                <tr key={item.id}>
                  <td>#{item.id}</td>
                  <td>#{item.orderId}</td>
                  <td>{moneyLabel(item.amount)}</td>
                  <td>{dateLabel(item.createdAt)}</td>
                  <td>{statusLabel(item.status)}</td>
                  <td>{item.reviewNote ?? item.refundReference ?? '—'}</td>
                </tr>
              ))}
            </tbody>
          </table>
          {requests.length === 0 && <p>Chưa có yêu cầu hoàn tiền.</p>}
        </div>
      </section>
    </main>
  );
};

export default ParentRefunds;
