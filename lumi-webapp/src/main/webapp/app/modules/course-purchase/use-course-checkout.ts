import { useCallback, useEffect, useRef, useState } from 'react';
import axios from 'axios';

import { useAppSelector } from 'app/config/store';
import { useLinkedStudents, useProfile } from 'app/modules/profile/use-profile';
import { createCourseOrder, getCourseCheckout } from './course-purchase.api';
import type { CourseCheckout, CreatedCourseOrder } from './course-purchase.model';

const errorMessage = (error: unknown, fallback: string) => {
  const message = axios.isAxiosError<{ message?: string }>(error) ? error.response?.data?.message : null;
  return typeof message === 'string' && message.trim() ? message : fallback;
};

interface CheckoutState {
  key: string;
  revision: number;
  data: CourseCheckout | null;
  error: string | null;
  loading: boolean;
}

interface OrderState {
  key: string;
  saving: boolean;
  data: CreatedCourseOrder | null;
  error: string | null;
}

const emptyOrder: OrderState = { key: '', saving: false, data: null, error: null };

// Giữ cùng key khi mất phản hồi hoặc tải lại trang: BE có thể đã tạo đơn rồi.
const getAttemptKey = (storageKey: string) => {
  const saved = sessionStorage.getItem(storageKey);
  if (saved) return saved;
  const key = crypto.randomUUID();
  sessionStorage.setItem(storageKey, key);
  return key;
};

export const useCourseCheckout = (courseId: number | null, enabled: boolean) => {
  const accountId: number | null = useAppSelector(state =>
    state.authentication.isAuthenticated ? (state.authentication.account.id ?? null) : null,
  );
  const buyerRequest = useProfile(enabled && courseId !== null);
  const childrenRequest = useLinkedStudents(enabled && courseId !== null);
  const children = childrenRequest.data ?? [];
  const [selection, setSelection] = useState<{ accountId: number | null; courseId: number | null; studentId: number }>({
    accountId: null,
    courseId: null,
    studentId: 0,
  });
  const chosenId = selection.accountId === accountId && selection.courseId === courseId ? selection.studentId : null;
  const student = children.find(child => child.studentId === chosenId) ?? (children.length === 1 ? children[0] : null);
  const studentId = student?.studentId ?? null;
  const key = enabled && accountId !== null && courseId !== null && studentId !== null ? `${accountId}:${courseId}:${studentId}` : '';
  const [revision, setRevision] = useState(0);
  const [checkoutState, setCheckoutState] = useState<CheckoutState>({ key: '', revision: -1, data: null, error: null, loading: false });
  const [orderState, setOrderState] = useState<OrderState>(emptyOrder);
  const inFlightRef = useRef<AbortController | null>(null);
  const reloadCheckout = useCallback(() => setRevision(value => value + 1), []);

  useEffect(() => {
    if (!key) return;
    const controller = new AbortController();
    setCheckoutState({ key, revision, data: null, error: null, loading: true });
    const load = async () => {
      try {
        const data = await getCourseCheckout(courseId!, studentId!, controller.signal);
        if (!controller.signal.aborted) setCheckoutState({ key, revision, data, error: null, loading: false });
      } catch (error: unknown) {
        if (!controller.signal.aborted) {
          setCheckoutState({
            key,
            revision,
            data: null,
            loading: false,
            error: errorMessage(error, 'Không tải được thông tin xác nhận mua. Vui lòng thử lại.'),
          });
        }
      }
    };
    void load();
    return () => controller.abort();
  }, [key, courseId, studentId, revision]);

  // Đổi tài khoản, đổi con hoặc rời trang thì bỏ kết quả của request cũ.
  useEffect(
    () => () => {
      inFlightRef.current?.abort();
      inFlightRef.current = null;
    },
    [key],
  );

  const currentCheckout = Boolean(key) && checkoutState.key === key && checkoutState.revision === revision;
  const checkout = currentCheckout && !checkoutState.loading ? checkoutState.data : null;
  const order = key && orderState.key === key ? orderState.data : null;
  const saving = Boolean(key) && orderState.key === key && orderState.saving;
  const canSubmit =
    enabled &&
    checkout?.canPurchase === true &&
    checkout.finalPrice != null &&
    Number.isSafeInteger(checkout.finalPrice) &&
    checkout.finalPrice > 0 &&
    !saving &&
    !order;

  const submit = async () => {
    if (!canSubmit || inFlightRef.current) return;
    const controller = new AbortController();
    inFlightRef.current = controller;
    setOrderState({ key, saving: true, data: null, error: null });
    try {
      const idempotencyKey = getAttemptKey(`lumi.course-order-attempt:${key}`);
      const data = await createCourseOrder({ courseId: courseId!, studentId: studentId! }, idempotencyKey, controller.signal);
      if (data.buyerId !== accountId) throw new Error('Đơn hàng không khớp tài khoản.');
      if (!controller.signal.aborted) setOrderState({ key, saving: false, data, error: null });
    } catch (error: unknown) {
      if (!controller.signal.aborted) {
        setOrderState({
          key,
          saving: false,
          data: null,
          error: errorMessage(error, 'Chưa xác nhận được kết quả tạo đơn. Vui lòng thử lại để kiểm tra cùng yêu cầu.'),
        });
      }
    } finally {
      if (inFlightRef.current === controller) inFlightRef.current = null;
    }
  };

  const selectStudent = (id: number) => {
    if (inFlightRef.current || order) return;
    setSelection({ accountId, courseId, studentId: id });
    reloadCheckout();
  };

  const retryExpiredOrder = () => {
    if (order?.status !== 'EXPIRED') return;
    try {
      sessionStorage.removeItem(`lumi.course-order-attempt:${key}`);
      setOrderState(emptyOrder);
      reloadCheckout();
    } catch {
      setOrderState(previous => ({ ...previous, error: 'Chưa thể chuẩn bị đơn mới. Vui lòng thử lại.' }));
    }
  };

  return {
    buyerRequest,
    childrenRequest,
    children,
    student,
    selectStudent,
    checkout,
    checkoutLoading: Boolean(key) && (!currentCheckout || checkoutState.loading),
    checkoutError: currentCheckout ? checkoutState.error : null,
    reloadCheckout,
    canSubmit,
    saving,
    order,
    orderError: key && orderState.key === key ? orderState.error : null,
    submit,
    retryExpiredOrder,
  };
};
