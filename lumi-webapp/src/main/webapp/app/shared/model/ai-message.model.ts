import dayjs from 'dayjs';

import { IAiConversation } from 'app/shared/model/ai-conversation.model';

export interface IAiMessage {
  id?: number;
  senderRole?: string;
  content?: string;
  createdAt?: dayjs.Dayjs | null;
  citations?: string;
  generationStatus?: string;
  modelName?: string | null;
  feedbackRating?: number | null;
  feedbackNote?: string | null;
  feedbackAt?: dayjs.Dayjs | null;
  conversation?: IAiConversation;
}

export const defaultValue: Readonly<IAiMessage> = {};
