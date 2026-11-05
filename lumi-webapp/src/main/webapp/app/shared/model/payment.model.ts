import dayjs from 'dayjs';

import { ICourseOrder } from 'app/shared/model/course-order.model';

export interface IPayment {
  id?: number;
  gateway?: string;
  transactionNo?: string | null;
  amount?: number;
  status?: string | null;
  paidAt?: dayjs.Dayjs | null;
  paymentReference?: string | null;
  providerEventId?: string | null;
  receivedAt?: dayjs.Dayjs | null;
  order?: ICourseOrder;
}

export const defaultValue: Readonly<IPayment> = {};
