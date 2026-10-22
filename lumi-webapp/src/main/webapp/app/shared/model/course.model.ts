import { AccessDurationUnit } from 'app/shared/model/enumerations/access-duration-unit.model';
import dayjs from 'dayjs';

import { ICategory } from 'app/shared/model/category.model';
import { RecordStatus } from 'app/shared/model/enumerations/record-status.model';
import { IGrade } from 'app/shared/model/grade.model';
import { ISubject } from 'app/shared/model/subject.model';
import { IUser } from 'app/shared/model/user.model';

export interface ICourse {
  shortDescription?: string | null;
  bookSeries?: string | null;
  targetAudience?: string | null;
  prerequisites?: string | null;
  learningObjectives?: string[] | null;
  introVideoUrl?: string | null;
  accessDurationValue?: number | null;
  accessDurationUnit?: keyof typeof AccessDurationUnit | null;
  version?: number | null;

  id?: number;
  title?: string;
  description?: string | null;
  thumbnailUrl?: string | null;
  price?: number | null;
  validDurationDays?: number | null;
  status?: keyof typeof RecordStatus | null;
  isDeleted?: boolean | null;
  createdAt?: dayjs.Dayjs | null;
  updatedAt?: dayjs.Dayjs | null;
  author?: IUser;
  category?: ICategory | null;
  subject?: ISubject | null;
  grade?: IGrade | null;
}

export const defaultValue: Readonly<ICourse> = {
  isDeleted: false,
};
