import dayjs from 'dayjs';

import { ILearningDocument } from 'app/shared/model/learning-document.model';
import { ILesson } from 'app/shared/model/lesson.model';
import { IUser } from 'app/shared/model/user.model';

export interface IAiConversation {
  id?: number;
  type?: string;
  createdAt?: dayjs.Dayjs | null;
  updatedAt?: dayjs.Dayjs;
  user?: IUser;
  lesson?: ILesson | null;
  document?: ILearningDocument | null;
}

export const defaultValue: Readonly<IAiConversation> = {};
