import dayjs from 'dayjs';

import { IUser } from 'app/shared/model/user.model';

export interface INotification {
  id?: number;
  title?: string;
  content?: string;
  isRead?: boolean | null;
  type?: string;
  createdAt?: dayjs.Dayjs | null;
  targetType?: string | null;
  targetId?: number | null;
  readAt?: dayjs.Dayjs | null;
  user?: IUser;
}

export const defaultValue: Readonly<INotification> = {
  isRead: false,
};
