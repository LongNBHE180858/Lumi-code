import axios from 'axios';
import type { ApiResponse } from 'app/shared/model/api-response.model';

export interface BankSummary {
  id: number;
  name: string;
  classId: number | null;
  className: string | null;
  questionCount: number;
  updatedAt: string;
}

export interface CreateBank {
  name: string;
  classId?: number | null;
}

export interface OptionItem {
  id: number;
  content: string;
  isCorrect: boolean;
  orderIndex: number;
}

export interface QuestionItem {
  id: number;
  content: string;
  difficulty: number;
  topic: string;
  explanation: string;
  options: OptionItem[];
}

export interface CreateQuestion {
  content: string;
  difficulty: number;
  topic?: string;
  explanation?: string;
  options: string[];
  correctIndex: number;
}

export interface TopicSummary {
  topic: string;
  totalQuestions: number;
  difficulty1: number;
  difficulty2: number;
  difficulty3: number;
  difficulty4: number;
  difficulty5: number;
}

export interface ImportError {
  row: number;
  message: string;
}

export interface ImportResult {
  importedCount: number;
  errors: ImportError[];
}

const unwrap = <T>(body: ApiResponse<T>): T => {
  if (body.code !== 1000 || body.data == null) {
    throw new Error(body.message || 'Không thể xử lý yêu cầu ngân hàng câu hỏi.');
  }
  return body.data;
};

export const getQuestionBanks = async (classId?: number | null): Promise<BankSummary[]> => {
  const params = classId ? { classId } : undefined;
  const res = await axios.get<ApiResponse<BankSummary[]>>('api/instructor/question-banks', { params });
  return unwrap(res.data);
};

export const createQuestionBank = async (values: CreateBank): Promise<BankSummary> => {
  const res = await axios.post<ApiResponse<BankSummary>>('api/instructor/question-banks', values);
  return unwrap(res.data);
};

export const getBankQuestions = async (bankId: number, topic?: string, difficulty?: number): Promise<QuestionItem[]> => {
  const params: Record<string, any> = {};
  if (topic && topic !== 'ALL') params.topic = topic;
  if (difficulty) params.difficulty = difficulty;
  const res = await axios.get<ApiResponse<QuestionItem[]>>(`api/instructor/question-banks/${bankId}/questions`, { params });
  return unwrap(res.data);
};

export const addBankQuestion = async (bankId: number, values: CreateQuestion): Promise<QuestionItem> => {
  const res = await axios.post<ApiResponse<QuestionItem>>(`api/instructor/question-banks/${bankId}/questions`, values);
  return unwrap(res.data);
};

export const updateBankQuestion = async (bankId: number, questionId: number, values: CreateQuestion): Promise<QuestionItem> => {
  const res = await axios.put<ApiResponse<QuestionItem>>(`api/instructor/question-banks/${bankId}/questions/${questionId}`, values);
  return unwrap(res.data);
};

export const downloadBankTemplate = async (): Promise<void> => {
  const res = await axios.get('api/instructor/question-banks/template', { responseType: 'blob' });
  const url = window.URL.createObjectURL(new Blob([res.data]));
  const link = document.createElement('a');
  link.href = url;
  link.setAttribute('download', 'Mau_Cau_Hoi_Lumi.xlsx');
  document.body.appendChild(link);
  link.click();
  link.remove();
  window.URL.revokeObjectURL(url);
};

export const importBankExcel = async (bankId: number, file: File): Promise<ImportResult> => {
  const formData = new FormData();
  formData.append('file', file);
  try {
    const res = await axios.post<ApiResponse<ImportResult>>(`api/instructor/question-banks/${bankId}/import-excel`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return unwrap(res.data);
  } catch (err: any) {
    if (err.response?.data?.data?.errors) {
      return err.response.data.data;
    }
    throw err;
  }
};

export const getBankTopics = async (bankId: number): Promise<TopicSummary[]> => {
  const res = await axios.get<ApiResponse<TopicSummary[]>>(`api/instructor/question-banks/${bankId}/topics`);
  return unwrap(res.data);
};
