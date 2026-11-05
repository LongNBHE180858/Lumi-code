import dayjs from 'dayjs';

import { ICourseOrder } from 'app/shared/model/course-order.model';
import { IPayment } from 'app/shared/model/payment.model';
import { IUser } from 'app/shared/model/user.model';

export interface IRefund {
  id?: number;
  amount?: number;
  reason?: string | null;
  status?: string | null;
  createdAt?: dayjs.Dayjs | null;
  processedAt?: dayjs.Dayjs | null;
  reviewNote?: string | null;
  refundReference?: string | null;
  accessRevokedAt?: dayjs.Dayjs | null;
  order?: ICourseOrder;
  payment?: IPayment;
  processedBy?: IUser | null;
}

export const defaultValue: Readonly<IRefund> = {};
