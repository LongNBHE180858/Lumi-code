import dayjs from 'dayjs';

import { ICourseOrder } from 'app/shared/model/course-order.model';
import { TransactionType } from 'app/shared/model/enumerations/transaction-type.model';
import { IInstructorWallet } from 'app/shared/model/instructor-wallet.model';

export interface IWalletTransaction {
  id?: number;
  type?: keyof typeof TransactionType;
  amount?: number;
  status?: string;
  idempotencyKey?: string | null;
  occurredAt?: dayjs.Dayjs | null;
  wallet?: IInstructorWallet;
  order?: ICourseOrder | null;
}

export const defaultValue: Readonly<IWalletTransaction> = {};
