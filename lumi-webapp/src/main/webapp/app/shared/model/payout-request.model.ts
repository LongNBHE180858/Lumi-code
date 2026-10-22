import dayjs from 'dayjs';

import { IInstructorWallet } from 'app/shared/model/instructor-wallet.model';

export interface IPayoutRequest {
  id?: number;
  amount?: number;
  bankCode?: string;
  bankAccount?: string;
  status?: string | null;
  createdAt?: dayjs.Dayjs | null;
  wallet?: IInstructorWallet;
}

export const defaultValue: Readonly<IPayoutRequest> = {};
