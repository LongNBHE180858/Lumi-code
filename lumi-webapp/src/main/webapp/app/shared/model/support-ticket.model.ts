import dayjs from 'dayjs';

import { IUser } from 'app/shared/model/user.model';

export interface ISupportTicket {
  id?: number;
  type?: string;
  subject?: string;
  status?: string | null;
  createdAt?: dayjs.Dayjs | null;
  user?: IUser;
}

export const defaultValue: Readonly<ISupportTicket> = {};
