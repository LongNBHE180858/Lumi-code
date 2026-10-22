import dayjs from 'dayjs';

import { IUser } from 'app/shared/model/user.model';

export interface IContentReport {
  id?: number;
  targetType?: string;
  targetId?: number;
  reason?: string;
  status?: string | null;
  createdAt?: dayjs.Dayjs | null;
  reporter?: IUser;
}

export const defaultValue: Readonly<IContentReport> = {};
