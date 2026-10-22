import dayjs from 'dayjs';

import { ICourseOrder } from 'app/shared/model/course-order.model';
import { IPayment } from 'app/shared/model/payment.model';

export interface IRefund {
  id?: number;
  amount?: number;
  reason?: string | null;
  status?: string | null;
  createdAt?: dayjs.Dayjs | null;
  order?: ICourseOrder;
  payment?: IPayment;
}

export const defaultValue: Readonly<IRefund> = {};
