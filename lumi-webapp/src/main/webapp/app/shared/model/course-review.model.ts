import dayjs from 'dayjs';

import { ICourse } from 'app/shared/model/course.model';
import { IUser } from 'app/shared/model/user.model';

export interface ICourseReview {
  id?: number;
  rating?: number | null;
  content?: string | null;
  createdAt?: dayjs.Dayjs | null;
  student?: IUser;
  course?: ICourse;
}

export const defaultValue: Readonly<ICourseReview> = {};
