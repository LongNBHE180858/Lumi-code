import dayjs from 'dayjs';

import { IStudyClass } from 'app/shared/model/study-class.model';
import { IUser } from 'app/shared/model/user.model';

export interface IClassMember {
  id?: number;
  status?: string | null;
  joinedAt?: dayjs.Dayjs | null;
  student?: IUser;
  studyClass?: IStudyClass;
}

export const defaultValue: Readonly<IClassMember> = {};
