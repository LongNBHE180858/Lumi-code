import dayjs from 'dayjs';

import { IGrade } from 'app/shared/model/grade.model';
import { ISubject } from 'app/shared/model/subject.model';
import { IUser } from 'app/shared/model/user.model';

export interface IQuestionBank {
  id?: number;
  name?: string;
  visibility?: string;
  createdAt?: dayjs.Dayjs;
  updatedAt?: dayjs.Dayjs;
  instructor?: IUser;
  subject?: ISubject | null;
  grade?: IGrade | null;
}

export const defaultValue: Readonly<IQuestionBank> = {};
