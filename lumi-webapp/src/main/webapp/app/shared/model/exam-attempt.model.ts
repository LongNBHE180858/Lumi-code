import dayjs from 'dayjs';

import { IExam } from 'app/shared/model/exam.model';
import { IUser } from 'app/shared/model/user.model';

export interface IExamAttempt {
  id?: number;
  startTime?: dayjs.Dayjs;
  endTime?: dayjs.Dayjs | null;
  score?: number | null;
  status?: string | null;
  rowVersion?: number | null;
  student?: IUser;
  exam?: IExam;
}

export const defaultValue: Readonly<IExamAttempt> = {};
