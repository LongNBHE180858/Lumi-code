import dayjs from 'dayjs';

import { IUser } from 'app/shared/model/user.model';

export type NotificationCategory = 'COURSE' | 'CLASSROOM' | 'SYSTEM' | 'FINANCE';

export interface INotification {
  id?: number;
  title?: string;
  content?: string;
  isRead?: boolean | null;
  type?: string;
  category?: NotificationCategory | null;
  actionUrl?: string | null;
  createdAt?: dayjs.Dayjs | string | null;
  targetType?: string | null;
  targetId?: number | null;
  readAt?: dayjs.Dayjs | string | null;
  user?: IUser | { id: number; login: string } | null;
}

export const defaultValue: Readonly<INotification> = {
  isRead: false,
};
