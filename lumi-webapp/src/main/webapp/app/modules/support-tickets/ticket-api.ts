import axios from 'axios';

export type TicketStatus = 'OPEN' | 'IN_PROGRESS' | 'RESOLVED';

export interface Ticket {
  id: number;
  type: string;
  subject: string;
  status: string;
  createdAt: string;
  userId: number;
  userLogin: string;
}

export interface TicketMessage {
  id: number;
  content: string;
  createdAt: string;
  senderId: number;
  senderLogin: string;
}

export interface TicketCreateInput {
  type: string;
  subject: string;
  content: string;
}

const baseUrl = '/api/support-tickets';
const adminUrl = '/api/admin/support-tickets';

export const ticketApi = {
  listMine: (page: number, size: number) => axios.get<Ticket[]>(baseUrl, { params: { page, size, sort: 'createdAt,desc' } }),
  listAdmin: (page: number, size: number, status?: TicketStatus, type?: string, search?: string) =>
    axios.get<Ticket[]>(adminUrl, { params: { page, size, sort: 'createdAt,desc', status, type, search } }),
  create: (input: TicketCreateInput) => axios.post<Ticket>(baseUrl, input),
  get: (id: number) => axios.get<Ticket>(`${baseUrl}/${id}`),
  messages: (id: number, page: number, size: number) => axios.get<TicketMessage[]>(`${baseUrl}/${id}/messages`, { params: { page, size } }),
  sendMessage: (id: number, content: string) => axios.post<TicketMessage>(`${baseUrl}/${id}/messages`, { content }),
  reopen: (id: number) => axios.post<Ticket>(`${baseUrl}/${id}/reopen`),
  changeStatus: (id: number, status: 'IN_PROGRESS' | 'RESOLVED') => axios.patch<Ticket>(`${adminUrl}/${id}/status`, { status }),
};
