import axios from 'axios';

import type { ApiResponse } from 'app/shared/model/api-response.model';
import type {
  CourseCheckout,
  CreatedCourseOrder,
  CreateCourseOrderRequest,
  CourseOrderDetail,
  CourseOrderPaymentInfo,
} from './course-purchase.model';

export const getCourseCheckout = async (courseId: number, studentId: number, signal: AbortSignal): Promise<CourseCheckout> => {
  const { data: body } = await axios.get<ApiResponse<CourseCheckout>>(`api/courses/${courseId}/checkout`, {
    params: { studentId },
    signal,
  });
  if (
    body?.code !== 1000 ||
    body.status !== 200 ||
    body.data?.courseId !== courseId ||
    body.data.studentId !== studentId ||
    typeof body.data.canPurchase !== 'boolean'
  ) {
    throw new Error('Không tải được thông tin xác nhận mua khóa học.');
  }
  return body.data;
};

const orderStatuses = ['PENDING', 'PAID', 'EXPIRED', 'CANCELLED', 'REFUNDED'];

export const getCourseOrder = async (orderId: number, signal: AbortSignal): Promise<CourseOrderDetail> => {
  const { data: body } = await axios.get<ApiResponse<CourseOrderDetail>>(`api/course-orders/${orderId}`, { signal });
  const order = body?.data;
  if (
    body?.code !== 1000 ||
    body.status !== 200 ||
    order?.orderId !== orderId ||
    !orderStatuses.includes(order.status) ||
    !Number.isSafeInteger(order.courseId) ||
    order.courseId <= 0 ||
    !Number.isSafeInteger(order.studentId) ||
    order.studentId <= 0 ||
    !Number.isSafeInteger(order.finalPrice) ||
    order.finalPrice < 0 ||
    !Number.isFinite(Date.parse(order.serverTime))
  ) {
    throw new Error('Thông tin đơn hàng không hợp lệ. Vui lòng thử lại.');
  }
  return order;
};

export const getCourseOrderPaymentInfo = async (orderId: number, signal: AbortSignal): Promise<CourseOrderPaymentInfo> => {
  const { data: body } = await axios.get<ApiResponse<CourseOrderPaymentInfo>>(`api/course-orders/${orderId}/payment-info`, { signal });
  const info = body?.data;
  if (
    body?.code !== 1000 ||
    body.status !== 200 ||
    info?.orderId !== orderId ||
    !orderStatuses.includes(info.status) ||
    typeof info.paymentRequired !== 'boolean' ||
    !Number.isFinite(Date.parse(info.serverTime)) ||
    (info.status === 'PENDING' && !Number.isFinite(Date.parse(info.expiresAt)))
  ) {
    throw new Error('Thông tin thanh toán không hợp lệ. Vui lòng thử lại.');
  }
  if (info.paymentRequired) {
    const bank = info.paymentInfo;
    if (
      info.status !== 'PENDING' ||
      !bank ||
      !Number.isSafeInteger(bank.amount) ||
      bank.amount <= 0 ||
      bank.currency !== 'VND' ||
      [bank.bankName, bank.accountName, bank.accountNumber, bank.transferContent, bank.qrImageUrl].some(
        value => typeof value !== 'string' || !value.trim(),
      ) ||
      !bank.qrImageUrl.startsWith('https://')
    ) {
      throw new Error('Thông tin chuyển khoản chưa đầy đủ. Vui lòng thử lại.');
    }
  }
  return info;
};

export const createCourseOrder = async (
  request: CreateCourseOrderRequest,
  idempotencyKey: string,
  signal: AbortSignal,
): Promise<CreatedCourseOrder> => {
  const { data: body } = await axios.post<ApiResponse<CreatedCourseOrder>>('api/course-orders', request, {
    headers: { 'Idempotency-Key': idempotencyKey },
    signal,
  });
  if (
    body?.code !== 1000 ||
    body.status !== 200 ||
    !Number.isSafeInteger(body.data?.orderId) ||
    body.data.orderId <= 0 ||
    body.data.courseId !== request.courseId ||
    body.data.studentId !== request.studentId ||
    !['PENDING', 'PAID', 'EXPIRED', 'CANCELLED', 'REFUNDED'].includes(body.data.status)
  ) {
    throw new Error('Không nhận được thông tin đơn hàng hợp lệ.');
  }
  return body.data;
};
