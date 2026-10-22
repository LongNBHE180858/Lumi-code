import dayjs from 'dayjs';

import { IUser } from 'app/shared/model/user.model';

export interface ICoupon {
  id?: number;
  code?: string;
  discountPercent?: number | null;
  maxUses?: number | null;
  usedCount?: number | null;
  validTo?: dayjs.Dayjs | null;
  instructor?: IUser | null;
}

export const defaultValue: Readonly<ICoupon> = {};
