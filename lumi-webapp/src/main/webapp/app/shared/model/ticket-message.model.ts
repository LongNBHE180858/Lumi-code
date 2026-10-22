import dayjs from 'dayjs';

import { ISupportTicket } from 'app/shared/model/support-ticket.model';
import { IUser } from 'app/shared/model/user.model';

export interface ITicketMessage {
  id?: number;
  content?: string;
  createdAt?: dayjs.Dayjs | null;
  sender?: IUser;
  ticket?: ISupportTicket;
}

export const defaultValue: Readonly<ITicketMessage> = {};
