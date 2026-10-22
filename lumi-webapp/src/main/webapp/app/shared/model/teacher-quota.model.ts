import dayjs from 'dayjs';

import { ISystemPlan } from 'app/shared/model/system-plan.model';
import { IUser } from 'app/shared/model/user.model';

export interface ITeacherQuota {
  id?: number;
  planExpiresAt?: dayjs.Dayjs | null;
  usedStorageBytes?: number | null;
  aiUsedCount?: number | null;
  rowVersion?: number | null;
  user?: IUser;
  currentPlan?: ISystemPlan | null;
}

export const defaultValue: Readonly<ITeacherQuota> = {};
