import dayjs from 'dayjs';

import { ICourseEnrollment } from 'app/shared/model/course-enrollment.model';
import { ILesson } from 'app/shared/model/lesson.model';

export interface ILessonProgress {
  id?: number;
  status?: string;
  lastPositionSeconds?: number;
  lastAccessedAt?: dayjs.Dayjs | null;
  completedAt?: dayjs.Dayjs | null;
  rowVersion?: number;
  enrollment?: ICourseEnrollment;
  lesson?: ILesson;
}

export const defaultValue: Readonly<ILessonProgress> = {};
