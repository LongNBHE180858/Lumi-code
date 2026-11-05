import dayjs from 'dayjs';

import { ICourse } from 'app/shared/model/course.model';
import { IUser } from 'app/shared/model/user.model';

export interface ICoupon {
  id?: number;
  code?: string;
  discountPercent?: number | null;
  maxUses?: number | null;
  usedCount?: number | null;
  validTo?: dayjs.Dayjs | null;
  validFrom?: dayjs.Dayjs | null;
  isActive?: boolean;
  minOrderAmount?: number;
  maxUsesPerUser?: number | null;
  instructorDiscountSharePercent?: number | null;
  instructor?: IUser | null;
  course?: ICourse | null;
}

export const defaultValue: Readonly<ICoupon> = {
  isActive: false,
};
