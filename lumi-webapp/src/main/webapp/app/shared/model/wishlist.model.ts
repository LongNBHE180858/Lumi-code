import dayjs from 'dayjs';

import { ICourse } from 'app/shared/model/course.model';
import { IUser } from 'app/shared/model/user.model';

export interface IWishlist {
  id?: number;
  createdAt?: dayjs.Dayjs | null;
  student?: IUser;
  course?: ICourse;
}

export const defaultValue: Readonly<IWishlist> = {};
