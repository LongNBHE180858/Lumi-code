import dayjs from 'dayjs';

import { IUser } from 'app/shared/model/user.model';

export interface IContentReport {
  id?: number;
  targetType?: string;
  targetId?: number;
  reason?: string;
  status?: string | null;
  createdAt?: dayjs.Dayjs | null;
  resolvedAt?: dayjs.Dayjs | null;
  resolutionNote?: string | null;
  targetVersion?: string | null;
  reporter?: IUser;
  assignedTo?: IUser | null;
  resolvedBy?: IUser | null;
}

export const defaultValue: Readonly<IContentReport> = {};
