import dayjs from 'dayjs';

import { IUser } from 'app/shared/model/user.model';

export interface IParentStudentLink {
  id?: number;
  linkCode?: string;
  status?: string | null;
  linkedAt?: dayjs.Dayjs | null;
  parent?: IUser;
  student?: IUser;
}

export const defaultValue: Readonly<IParentStudentLink> = {};
