import dayjs from 'dayjs';

import { ICourse } from 'app/shared/model/course.model';
import { IUser } from 'app/shared/model/user.model';

export interface ICourseEnrollment {
  id?: number;
  progressPercent?: number | null;
  status?: string | null;
  enrolledAt?: dayjs.Dayjs | null;
  expiresAt?: dayjs.Dayjs;
  student?: IUser;
  course?: ICourse;
}

export const defaultValue: Readonly<ICourseEnrollment> = {};
