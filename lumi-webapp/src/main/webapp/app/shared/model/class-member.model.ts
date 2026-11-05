import dayjs from 'dayjs';

import { IStudyClass } from 'app/shared/model/study-class.model';
import { IUser } from 'app/shared/model/user.model';

export interface IClassMember {
  id?: number;
  status?: string | null;
  joinedAt?: dayjs.Dayjs | null;
  requestedAt?: dayjs.Dayjs;
  leftAt?: dayjs.Dayjs | null;
  updatedAt?: dayjs.Dayjs;
  studyClass?: IStudyClass;
  student?: IUser;
}

export const defaultValue: Readonly<IClassMember> = {};
