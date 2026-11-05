import dayjs from 'dayjs';

import { ICourse } from 'app/shared/model/course.model';
import { IUser } from 'app/shared/model/user.model';

export interface ICourseReview {
  id?: number;
  rating?: number | null;
  content?: string | null;
  createdAt?: dayjs.Dayjs | null;
  updatedAt?: dayjs.Dayjs;
  course?: ICourse;
  student?: IUser;
}

export const defaultValue: Readonly<ICourseReview> = {};
