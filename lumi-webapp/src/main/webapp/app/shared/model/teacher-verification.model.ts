import dayjs from 'dayjs';

import { IUser } from 'app/shared/model/user.model';

export interface ITeacherVerification {
  id?: number;
  identityDocumentUrl?: string;
  verificationStatus?: string | null;
  notes?: string | null;
  submittedAt?: dayjs.Dayjs | null;
  approvedAt?: dayjs.Dayjs | null;
  user?: IUser;
}

export const defaultValue: Readonly<ITeacherVerification> = {};
