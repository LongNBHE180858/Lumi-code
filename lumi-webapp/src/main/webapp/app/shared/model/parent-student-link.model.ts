import dayjs from 'dayjs';

import { IUser } from 'app/shared/model/user.model';

export interface IParentStudentLink {
  id?: number;
  linkCode?: string;
  status?: string | null;
  linkedAt?: dayjs.Dayjs | null;
  createdAt?: dayjs.Dayjs;
  codeExpiresAt?: dayjs.Dayjs;
  confirmedAt?: dayjs.Dayjs | null;
  revokedAt?: dayjs.Dayjs | null;
  relationshipType?: string | null;
  guardianVerificationStatus?: string;
  guardianVerifiedAt?: dayjs.Dayjs | null;
  guardianVerificationNote?: string | null;
  parent?: IUser;
  student?: IUser;
  confirmedBy?: IUser | null;
  guardianVerifiedBy?: IUser | null;
}

export const defaultValue: Readonly<IParentStudentLink> = {};
