import dayjs from 'dayjs';

import { IDiscussion } from 'app/shared/model/discussion.model';
import { IUser } from 'app/shared/model/user.model';

export interface IDiscussionReply {
  id?: number;
  content?: string;
  createdAt?: dayjs.Dayjs | null;
  user?: IUser;
  discussion?: IDiscussion;
  parent?: IDiscussionReply | null;
}

export const defaultValue: Readonly<IDiscussionReply> = {};
