import dayjs from 'dayjs';

import { RecordStatus } from 'app/shared/model/enumerations/record-status.model';
import { IGrade } from 'app/shared/model/grade.model';
import { ISubject } from 'app/shared/model/subject.model';
import { IUser } from 'app/shared/model/user.model';

export interface ICourse {
  id?: number;
  title?: string;
  description?: string | null;
  thumbnailUrl?: string | null;
  price?: number | null;
  shortDescription?: string | null;
  bookSeries?: string | null;
  targetAudience?: string | null;
  prerequisites?: string | null;
  learningObjectives?: string | null;
  introVideoUrl?: string | null;
  validDurationDays?: number | null;
  accessDurationValue?: number | null;
  accessDurationUnit?: string | null;
  version?: number;
  status?: keyof typeof RecordStatus | null;
  isDeleted?: boolean | null;
  createdAt?: dayjs.Dayjs | null;
  updatedAt?: dayjs.Dayjs | null;
  storageLimitBytes?: number | null;
  author?: IUser;
  subject?: ISubject | null;
  grade?: IGrade | null;
}

export const defaultValue: Readonly<ICourse> = {
  isDeleted: false,
};
