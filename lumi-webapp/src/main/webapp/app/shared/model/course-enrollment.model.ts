import dayjs from 'dayjs';

import { ICourse } from 'app/shared/model/course.model';
import { ILesson } from 'app/shared/model/lesson.model';
import { IUser } from 'app/shared/model/user.model';

export interface ICourseEnrollment {
  id?: number;
  progressPercent?: number | null;
  status?: string | null;
  enrolledAt?: dayjs.Dayjs | null;
  expiresAt?: dayjs.Dayjs;
  updatedAt?: dayjs.Dayjs;
  course?: ICourse;
  student?: IUser;
  lastAccessedLesson?: ILesson | null;
}

export const defaultValue: Readonly<ICourseEnrollment> = {};
