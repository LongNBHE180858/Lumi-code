import dayjs from 'dayjs';

import { IUser } from 'app/shared/model/user.model';

export interface IAuthSession {
  id?: number;
  deviceInfo?: string | null;
  refreshToken?: string | null;
  expiresAt?: dayjs.Dayjs;
  isRevoked?: boolean | null;
  createdAt?: dayjs.Dayjs | null;
  refreshTokenHash?: string | null;
  user?: IUser;
}

export const defaultValue: Readonly<IAuthSession> = {
  isRevoked: false,
};
