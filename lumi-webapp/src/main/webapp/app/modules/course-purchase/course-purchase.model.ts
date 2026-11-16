export type AccessDurationUnit = 'DAY' | 'MONTH' | 'YEAR';
export type CourseOrderStatus = 'PENDING' | 'PAID' | 'EXPIRED' | 'CANCELLED' | 'REFUNDED';

export interface CourseCheckout {
  courseId: number;
  studentId: number;
  title: string;
  thumbnailUrl: string | null;
  subjectName: string | null;
  gradeName: string | null;
  gradeLevel: number | null;
  accessDurationValue: number | null;
  accessDurationUnit: AccessDurationUnit | null;
  originalPrice: number | null;
  taxAmount: number | null;
  finalPrice: number | null;
  canPurchase: boolean;
  purchaseBlockReason: string | null;
}

export interface CreateCourseOrderRequest {
  courseId: number;
  studentId: number;
}

export interface CreatedCourseOrder extends CreateCourseOrderRequest {
  orderId: number;
  buyerId: number;
  status: CourseOrderStatus;
  originalPrice: number;
  taxAmount: number;
  finalPrice: number;
  accessDurationValue: number;
  accessDurationUnit: AccessDurationUnit;
  paymentRequired: boolean;
  createdAt: string;
  expiresAt: string;
  serverTime: string;
}

export interface CourseOrderDetail extends CreatedCourseOrder {
  paidAt: string | null;
}

export interface BankTransferInfo {
  bankCode: string;
  bankName: string;
  accountNumber: string;
  accountName: string;
  amount: number;
  currency: string;
  transferContent: string;
  qrImageUrl: string;
}

export interface CourseOrderPaymentInfo {
  orderId: number;
  status: CourseOrderStatus;
  paymentRequired: boolean;
  expiresAt: string;
  serverTime: string;
  paymentInfo: BankTransferInfo | null;
}
