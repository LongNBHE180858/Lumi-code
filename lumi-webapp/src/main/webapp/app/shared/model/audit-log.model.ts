import dayjs from 'dayjs';

import { IUser } from 'app/shared/model/user.model';

export interface IAuditLog {
  id?: number;
  action?: string;
  targetEntity?: string;
  targetId?: string;
  ipAddress?: string | null;
  details?: string | null;
  occurredAt?: dayjs.Dayjs | null;
  actor?: IUser | null;
}

export const defaultValue: Readonly<IAuditLog> = {};
