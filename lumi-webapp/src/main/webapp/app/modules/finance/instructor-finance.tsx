import React, { useCallback, useEffect, useState } from 'react';
import {
  dateLabel,
  errorLabel,
  financeApi,
  moneyLabel,
  statusLabel,
  type PayoutView,
  type TransactionView,
  type WalletView,
} from './finance-api';
import './finance.scss';

const InstructorFinance = () => {
  const [wallet, setWallet] = useState<WalletView | null>(null);
  const [payouts, setPayouts] = useState<PayoutView[]>([]);
  const [transactions, setTransactions] = useState<TransactionView[]>([]);
  const [amount, setAmount] = useState('');
  const [bankCode, setBankCode] = useState('');
  const [bankAccount, setBankAccount] = useState('');
  const [accountHolderName, setAccountHolderName] = useState('');
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');
  const [saving, setSaving] = useState(false);
  const load = useCallback(async () => {
    try {
      const [nextWallet, nextPayouts, nextTransactions] = await Promise.all([
        financeApi.wallet(),
        financeApi.myPayouts(),
        financeApi.transactions(),
      ]);
      setWallet(nextWallet);
      setPayouts(nextPayouts);
      setTransactions(nextTransactions);
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
      await financeApi.requestPayout({ amount: Number(amount), bankCode, bankAccount, accountHolderName });
      setAmount('');
      setMessage('Đã gửi yêu cầu rút tiền. Số tiền được giữ trong khi chờ admin xử lý.');
      await load();
    } catch (failure) {
      setError(errorLabel(failure));
    } finally {
      setSaving(false);
    }
  };

  return (
    <main className="finance-page">
      <h1>Ví giảng viên</h1>
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
      <div className="finance-grid">
        <section className="finance-card">
          <h2>Đang chờ</h2>
          <strong>{wallet ? moneyLabel(wallet.pendingAmount) : 'Đang tải...'}</strong>
        </section>
        <section className="finance-card">
          <h2>Có thể rút</h2>
          <strong>{wallet ? moneyLabel(wallet.availableAmount) : 'Đang tải...'}</strong>
        </section>
      </div>
      <section className="finance-card">
        <h2>Yêu cầu rút tiền</h2>
        <p>Mỗi yêu cầu từ 50.000 đến 10.000.000 ₫, tối đa một yêu cầu mỗi ngày và không vượt số dư có thể rút.</p>
        <form onSubmit={event => void submit(event)}>
          <div className="finance-grid">
            <label className="finance-field">
              Số tiền
              <input
                required
                type="number"
                min="50000"
                max="10000000"
                step="1"
                value={amount}
                onChange={event => setAmount(event.target.value)}
              />
            </label>
            <label className="finance-field">
              Ngân hàng
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
          <button className="finance-button" disabled={saving || !wallet || Number(amount) > wallet.availableAmount} type="submit">
            Gửi yêu cầu
          </button>
        </form>
      </section>
      <section className="finance-card">
        <h2>Lịch sử yêu cầu</h2>
        <div className="finance-table-wrap">
          <table className="finance-table">
            <thead>
              <tr>
                <th>Mã</th>
                <th>Số tiền</th>
                <th>Ngày gửi</th>
                <th>Trạng thái</th>
                <th>Kết quả</th>
              </tr>
            </thead>
            <tbody>
              {payouts.map(item => (
                <tr key={item.id}>
                  <td>#{item.id}</td>
                  <td>{moneyLabel(item.amount)}</td>
                  <td>{dateLabel(item.createdAt)}</td>
                  <td>{statusLabel(item.status)}</td>
                  <td>{item.rejectionReason ?? item.transferReference ?? '—'}</td>
                </tr>
              ))}
            </tbody>
          </table>
          {payouts.length === 0 && <p>Chưa có yêu cầu rút tiền.</p>}
        </div>
      </section>
      <section className="finance-card">
        <h2>Giao dịch ví</h2>
        <div className="finance-table-wrap">
          <table className="finance-table">
            <thead>
              <tr>
                <th>Thời điểm</th>
                <th>Loại</th>
                <th>Số tiền</th>
                <th>Khoản</th>
                <th>Đơn hàng</th>
              </tr>
            </thead>
            <tbody>
              {transactions.map(item => (
                <tr key={item.id}>
                  <td>{dateLabel(item.occurredAt)}</td>
                  <td>{item.type}</td>
                  <td>{moneyLabel(item.amount)}</td>
                  <td>{item.balanceBucket}</td>
                  <td>{item.orderId ? `#${item.orderId}` : '—'}</td>
                </tr>
              ))}
            </tbody>
          </table>
          {transactions.length === 0 && <p>Chưa có giao dịch.</p>}
        </div>
      </section>
    </main>
  );
};

export default InstructorFinance;
