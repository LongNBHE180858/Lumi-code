import axios from 'axios';

export interface WalletView {
  pendingAmount: number;
  availableAmount: number;
}

export interface TransactionView {
  id: number;
  type: string;
  amount: number;
  status: string;
  balanceBucket: string;
  occurredAt: string;
  orderId: number | null;
}

export interface PayoutView {
  id: number;
  instructorLogin: string;
  amount: number;
  bankCode: string;
  bankAccount: string;
  accountHolderName: string;
  status: string;
  createdAt: string;
  processedAt: string | null;
  rejectionReason: string | null;
  transferReference: string | null;
}

export interface RefundView {
  id: number;
  orderId: number;
  buyerLogin: string | null;
  amount: number;
  reason: string;
  bankCode: string;
  bankAccount: string;
  accountHolderName: string;
  status: string;
  createdAt: string;
  processedAt: string | null;
  reviewNote: string | null;
  refundReference: string | null;
}

const root = '/api/finance';

export const financeApi = {
  wallet: async () => (await axios.get<WalletView>(`${root}/wallet`)).data,
  transactions: async () => (await axios.get<TransactionView[]>(`${root}/wallet/transactions`)).data,
  myPayouts: async () => (await axios.get<PayoutView[]>(`${root}/payouts/my`)).data,
  requestPayout: async (body: { amount: number; bankCode: string; bankAccount: string; accountHolderName: string }) =>
    (await axios.post<PayoutView>(`${root}/payouts`, body)).data,
  myRefunds: async () => (await axios.get<RefundView[]>(`${root}/refunds/my`)).data,
  requestRefund: async (body: { orderId: number; reason: string; bankCode: string; bankAccount: string; accountHolderName: string }) =>
    (await axios.post<RefundView>(`${root}/refunds`, body)).data,
  payouts: async () => (await axios.get<PayoutView[]>(`${root}/admin/payouts`)).data,
  refunds: async () => (await axios.get<RefundView[]>(`${root}/admin/refunds`)).data,
  decidePayout: async (id: number, action: string, reason?: string) =>
    (await axios.post<PayoutView>(`${root}/admin/payouts/${id}/decision`, { action, reason })).data,
  completePayout: async (id: number, reference: string) =>
    (await axios.post<PayoutView>(`${root}/admin/payouts/${id}/complete`, { reference })).data,
  decideRefund: async (id: number, action: string, reason?: string) =>
    (await axios.post<RefundView>(`${root}/admin/refunds/${id}/decision`, { action, reason })).data,
  completeRefund: async (id: number, reference: string) =>
    (await axios.post<RefundView>(`${root}/admin/refunds/${id}/complete`, { reference })).data,
};

export const moneyLabel = (value: number) => new Intl.NumberFormat('vi-VN', { maximumFractionDigits: 0 }).format(value) + ' ₫';
export const dateLabel = (value: string | null) => (value ? new Date(value).toLocaleString('vi-VN') : '—');
export const statusLabel = (value: string) =>
  ({ PENDING: 'Chờ duyệt', APPROVED: 'Đã duyệt, chờ chuyển khoản', COMPLETED: 'Hoàn tất', REJECTED: 'Từ chối' })[value] ?? value;
export const errorLabel = (error: unknown) => {
  if (axios.isAxiosError(error)) return error.response?.data?.detail ?? error.response?.data?.message ?? 'Không thể xử lý yêu cầu';
  return 'Không thể xử lý yêu cầu';
};
