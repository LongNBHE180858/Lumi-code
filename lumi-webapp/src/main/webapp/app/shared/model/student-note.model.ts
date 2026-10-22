import dayjs from 'dayjs';

import { ILesson } from 'app/shared/model/lesson.model';
import { IUser } from 'app/shared/model/user.model';

export interface IStudentNote {
  id?: number;
  videoTimestamp?: number | null;
  content?: string;
  createdAt?: dayjs.Dayjs | null;
  student?: IUser;
  lesson?: ILesson;
}

export const defaultValue: Readonly<IStudentNote> = {};
