import dayjs from 'dayjs';

import { ILesson } from 'app/shared/model/lesson.model';
import { IUser } from 'app/shared/model/user.model';

export interface IAiConversation {
  id?: number;
  type?: string;
  createdAt?: dayjs.Dayjs | null;
  user?: IUser;
  lesson?: ILesson | null;
}

export const defaultValue: Readonly<IAiConversation> = {};
