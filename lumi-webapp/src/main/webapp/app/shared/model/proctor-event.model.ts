import dayjs from 'dayjs';

import { IProctorSession } from 'app/shared/model/proctor-session.model';

export interface IProctorEvent {
  id?: number;
  violationType?: string;
  photoUrl?: string | null;
  occurredAt?: dayjs.Dayjs | null;
  session?: IProctorSession;
}

export const defaultValue: Readonly<IProctorEvent> = {};
