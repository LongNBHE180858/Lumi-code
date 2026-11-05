import dayjs from 'dayjs';

import { IInstructorWallet } from 'app/shared/model/instructor-wallet.model';
import { IUser } from 'app/shared/model/user.model';

export interface IPayoutRequest {
  id?: number;
  amount?: number;
  bankCode?: string;
  bankAccount?: string;
  status?: string | null;
  createdAt?: dayjs.Dayjs | null;
  accountHolderName?: string;
  processedAt?: dayjs.Dayjs | null;
  rejectionReason?: string | null;
  transferReference?: string | null;
  updatedAt?: dayjs.Dayjs;
  wallet?: IInstructorWallet;
  processedBy?: IUser | null;
}

export const defaultValue: Readonly<IPayoutRequest> = {};
