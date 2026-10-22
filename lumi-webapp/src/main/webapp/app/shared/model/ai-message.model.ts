import dayjs from 'dayjs';

import { IAiConversation } from 'app/shared/model/ai-conversation.model';

export interface IAiMessage {
  id?: number;
  senderRole?: string;
  content?: string;
  createdAt?: dayjs.Dayjs | null;
  conversation?: IAiConversation;
}

export const defaultValue: Readonly<IAiMessage> = {};
