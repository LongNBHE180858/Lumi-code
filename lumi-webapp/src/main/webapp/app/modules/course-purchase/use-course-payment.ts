import { useCallback, useEffect, useRef, useState } from 'react';
import axios from 'axios';

import { useAppSelector } from 'app/config/store';
import { getCourseOrder, getCourseOrderPaymentInfo } from './course-purchase.api';
import type { BankTransferInfo, CourseOrderDetail } from './course-purchase.model';

interface PaymentState {
  key: string;
  order: CourseOrderDetail | null;
  bank: BankTransferInfo | null;
  loading: boolean;
  checking: boolean;
  error: string | null;
  remainingMs: number;
  receivedAt: number;
}

const emptyState: PaymentState = {
  key: '',
  order: null,
  bank: null,
  loading: true,
  checking: false,
  error: null,
  remainingMs: 0,
  receivedAt: 0,
};

export const useCoursePayment = (orderId: number | null, enabled: boolean) => {
  const accountId: number | null = useAppSelector(state =>
    state.authentication.isAuthenticated ? (state.authentication.account.id ?? null) : null,
  );
  const key = enabled && orderId !== null && accountId !== null ? `${accountId}:${orderId}` : '';
  const [state, setState] = useState<PaymentState>(emptyState);
  const [clock, setClock] = useState(() => performance.now());
  const refreshRef = useRef<() => void>(() => undefined);
  const expiredCheckRef = useRef('');
  const refresh = useCallback(() => refreshRef.current(), []);

  useEffect(() => {
    if (!key) return;
    const controller = new AbortController();
    let timer: ReturnType<typeof setTimeout> | undefined;
    let running = false;
    let autoCheck = true;
    let savedOrder: CourseOrderDetail | null = null;
    expiredCheckRef.current = '';
    setState({ ...emptyState, key });
    const ticker = setInterval(() => setClock(performance.now()), 1000);

    const check = async () => {
      if (running || controller.signal.aborted || document.hidden) return;
      clearTimeout(timer);
      running = true;
      let detailReceivedAt: number | null = null;
      setState(previous => ({ ...previous, checking: true }));
      try {
        // Đọc đơn riêng để vẫn nhận được PAID khi cấu hình QR đang lỗi.
        const order = await getCourseOrder(orderId!, controller.signal);
        if (order.buyerId !== accountId) throw new Error('Đơn hàng không khớp tài khoản.');
        if (controller.signal.aborted) return;
        savedOrder = order;
        detailReceivedAt = performance.now();
        let bank: BankTransferInfo | null = null;
        if (order.status === 'PENDING') {
          const payment = await getCourseOrderPaymentInfo(orderId!, controller.signal);
          if (controller.signal.aborted) return;
          if (payment.paymentRequired && payment.paymentInfo?.amount !== order.finalPrice) {
            throw new Error('Số tiền chuyển khoản không khớp đơn hàng. Vui lòng thử lại.');
          }
          savedOrder = {
            ...order,
            status: payment.status,
            paymentRequired: payment.paymentRequired,
            expiresAt: payment.expiresAt,
            serverTime: payment.serverTime,
          };
          bank = payment.paymentRequired ? payment.paymentInfo : null;
        }
        autoCheck = savedOrder.status === 'PENDING';
        if (!autoCheck) clearInterval(ticker);
        setState({
          key,
          order: savedOrder,
          bank: document.hidden ? null : bank,
          loading: false,
          checking: false,
          error: null,
          remainingMs: Math.max(0, Date.parse(savedOrder.expiresAt) - Date.parse(savedOrder.serverTime)),
          receivedAt: performance.now(),
        });
      } catch (error: unknown) {
        if (controller.signal.aborted) return;
        const status = axios.isAxiosError(error) ? error.response?.status : undefined;
        const message = axios.isAxiosError<{ message?: string }>(error) ? error.response?.data?.message : null;
        autoCheck = ![401, 403, 404, 409].includes(status ?? 0) && (!savedOrder || savedOrder.status === 'PENDING');
        setState(previous => ({
          ...previous,
          key,
          order: savedOrder,
          bank: null,
          loading: false,
          checking: false,
          ...(savedOrder && detailReceivedAt !== null
            ? {
                remainingMs: Math.max(0, Date.parse(savedOrder.expiresAt) - Date.parse(savedOrder.serverTime)),
                receivedAt: detailReceivedAt,
              }
            : {}),
          error:
            typeof message === 'string' && message.trim()
              ? message
              : 'Chưa kiểm tra được thanh toán. Vui lòng thử lại; không chuyển tiền lần nữa.',
        }));
      } finally {
        running = false;
        if (!controller.signal.aborted && autoCheck && !document.hidden) timer = setTimeout(() => void check(), 5000);
      }
    };
    refreshRef.current = () => void check();
    const onVisibility = () => {
      clearTimeout(timer);
      if (document.hidden) setState(previous => ({ ...previous, bank: null }));
      else void check();
    };
    document.addEventListener('visibilitychange', onVisibility);
    void check();
    return () => {
      controller.abort();
      clearTimeout(timer);
      clearInterval(ticker);
      refreshRef.current = () => undefined;
      document.removeEventListener('visibilitychange', onVisibility);
    };
  }, [key, orderId, accountId]);

  const current = Boolean(key) && state.key === key;
  const order = current ? state.order : null;
  // Dùng đồng hồ đơn điệu, tránh sai khi người dùng chỉnh giờ máy.
  const remainingSeconds = current ? Math.max(0, Math.ceil((state.remainingMs - Math.max(0, clock - state.receivedAt)) / 1000)) : 0;
  useEffect(() => {
    if (order?.status !== 'PENDING' || !current || state.remainingMs <= 0 || remainingSeconds > 0) return;
    const deadlineKey = `${key}:${order.expiresAt}`;
    if (expiredCheckRef.current !== deadlineKey) {
      expiredCheckRef.current = deadlineKey;
      refresh();
    }
  }, [current, key, order, remainingSeconds, state.remainingMs, refresh]);

  return {
    order,
    bank: current && remainingSeconds > 0 && order?.status === 'PENDING' ? state.bank : null,
    loading: Boolean(key) && (!current || state.loading),
    checking: current && state.checking,
    error: current ? state.error : null,
    remainingSeconds,
    refresh,
  };
};
