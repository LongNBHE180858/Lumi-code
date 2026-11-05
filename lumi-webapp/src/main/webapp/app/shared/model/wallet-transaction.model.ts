import dayjs from 'dayjs';

import { ICourseOrder } from 'app/shared/model/course-order.model';
import { TransactionType } from 'app/shared/model/enumerations/transaction-type.model';
import { IInstructorWallet } from 'app/shared/model/instructor-wallet.model';
import { IPayoutRequest } from 'app/shared/model/payout-request.model';
import { IRefund } from 'app/shared/model/refund.model';

export interface IWalletTransaction {
  id?: number;
  type?: keyof typeof TransactionType;
  amount?: number;
  status?: string;
  idempotencyKey?: string | null;
  occurredAt?: dayjs.Dayjs | null;
  balanceBucket?: string;
  wallet?: IInstructorWallet;
  order?: ICourseOrder | null;
  payoutRequest?: IPayoutRequest | null;
  refund?: IRefund | null;
}

export const defaultValue: Readonly<IWalletTransaction> = {};
