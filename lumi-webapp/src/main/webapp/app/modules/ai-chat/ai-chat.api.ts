import axios from 'axios';

export interface Citation {
  sourceNumber: number;
  documentId: number;
  chunkId: number;
  documentTitle: string;
  pageNumber?: number;
}
export interface ChatMessage {
  id: number;
  senderRole: string;
  content: string;
  generationStatus?: GenerationStatus;
  citations: Citation[];
  createdAt?: string;
}
export type GenerationStatus = 'NOT_APPLICABLE' | 'ANSWERED' | 'SOCIAL' | 'CLARIFICATION' | 'INSUFFICIENT_CONTEXT';
interface SentMessage {
  conversationId: number;
  userMessageId: number;
  assistantMessageId: number;
  result: { status: GenerationStatus; answer: string; citations: Citation[] };
}
export const createConversation = async () => (await axios.post<{ conversationId: number }>('api/ai/conversations', {})).data;
export const getHistory = async (id: number, signal?: AbortSignal) =>
  (await axios.get<ChatMessage[]>(`api/ai/conversations/${id}/messages`, { signal })).data;
export const sendQuestion = async (id: number, question: string) =>
  (await axios.post<SentMessage>(`api/ai/conversations/${id}/messages`, { question })).data;
