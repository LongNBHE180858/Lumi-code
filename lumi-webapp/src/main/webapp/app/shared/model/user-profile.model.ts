import dayjs from 'dayjs';

import { RecordStatus } from 'app/shared/model/enumerations/record-status.model';
import { IUser } from 'app/shared/model/user.model';

export interface IUserProfile {
  id?: number;
  phoneNumber?: string | null;
  bio?: string | null;
  schoolName?: string | null;
  gradeLevel?: number | null;
  is2faEnabled?: boolean | null;
  instructorStatus?: keyof typeof RecordStatus | null;
  updatedAt?: dayjs.Dayjs | null;
  user?: IUser;
}

export const defaultValue: Readonly<IUserProfile> = {
  is2faEnabled: false,
};
