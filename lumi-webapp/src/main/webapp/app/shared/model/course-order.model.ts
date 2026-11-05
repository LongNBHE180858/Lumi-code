import dayjs from 'dayjs';

import { ICoupon } from 'app/shared/model/coupon.model';
import { ICourse } from 'app/shared/model/course.model';
import { OrderStatus } from 'app/shared/model/enumerations/order-status.model';
import { ISystemPlan } from 'app/shared/model/system-plan.model';
import { IUser } from 'app/shared/model/user.model';

export interface ICourseOrder {
  id?: number;
  originalPrice?: number;
  discountAmount?: number | null;
  finalPrice?: number;
  revenueSplitRate?: string | null;
  status?: keyof typeof OrderStatus | null;
  idempotencyKey?: string | null;
  rowVersion?: number | null;
  createdAt?: dayjs.Dayjs | null;
  instructorSharePercent?: number | null;
  instructorAmount?: number | null;
  platformAmount?: number | null;
  orderType?: string;
  expiresAt?: dayjs.Dayjs;
  paidAt?: dayjs.Dayjs | null;
  accessDurationValue?: number | null;
  accessDurationUnit?: string | null;
  entitlementStartsAt?: dayjs.Dayjs | null;
  entitlementExpiresAt?: dayjs.Dayjs | null;
  entitlementStatus?: string | null;
  planSnapshot?: string | null;
  updatedAt?: dayjs.Dayjs;
  pricingSnapshot?: string | null;
  student?: IUser;
  course?: ICourse | null;
  plan?: ISystemPlan | null;
  coupon?: ICoupon | null;
}

export const defaultValue: Readonly<ICourseOrder> = {};
