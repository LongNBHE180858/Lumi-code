import dayjs from 'dayjs';

import { IStudyClass } from 'app/shared/model/study-class.model';

export interface IAssignment {
  id?: number;
  title?: string;
  instructions?: string | null;
  dueAt?: dayjs.Dayjs | null;
  maxScore?: number | null;
  allowLate?: boolean | null;
  isDeleted?: boolean | null;
  studyClass?: IStudyClass;
}

export const defaultValue: Readonly<IAssignment> = {
  allowLate: false,
  isDeleted: false,
};
