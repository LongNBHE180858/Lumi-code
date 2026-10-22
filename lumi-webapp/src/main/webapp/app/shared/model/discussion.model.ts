import dayjs from 'dayjs';

import { ILesson } from 'app/shared/model/lesson.model';
import { IUser } from 'app/shared/model/user.model';

export interface IDiscussion {
  id?: number;
  title?: string;
  content?: string;
  createdAt?: dayjs.Dayjs | null;
  student?: IUser;
  lesson?: ILesson;
}

export const defaultValue: Readonly<IDiscussion> = {};
