import dayjs from 'dayjs';

import { IUser } from 'app/shared/model/user.model';

export interface IStudentStreak {
  id?: number;
  currentStreak?: number | null;
  longestStreak?: number | null;
  gemsBalance?: number | null;
  lastActiveDate?: dayjs.Dayjs | null;
  user?: IUser;
}

export const defaultValue: Readonly<IStudentStreak> = {};
