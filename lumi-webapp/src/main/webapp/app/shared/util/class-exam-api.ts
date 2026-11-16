import axios from 'axios';
import type { ApiResponse } from 'app/shared/model/api-response.model';

export interface ExamSummary {
  id: number;
  materialId: number | null;
  title: string;
  durationMinutes: number;
  passScore: number;
  status: 'ACTIVE' | 'ARCHIVED';
  questionCount: number;
  assignedStudentId: number | null;
}

export interface ExamQuestionInput {
  content: string;
  explanation: string;
  points: number;
  options: { content: string }[];
  correctIndex: number;
}

export interface SaveExam {
  materialId: number;
  title: string;
  durationMinutes: number;
  passScore: number;
  assignedStudentId: number | null;
  questions: ExamQuestionInput[];
}

export interface ExamEditor {
  summary: ExamSummary;
  questions: ExamQuestionInput[];
}

export interface ExamAttempt {
  attemptId: number;
  examId: number;
  deadlineAt: string;
  questions: { id: number; content: string; points: number; options: { id: number; content: string }[] }[];
}

export interface ExamResult {
  attemptId: number;
  score: number;
  passed: boolean;
  submittedAt: string;
  feedback: {
    questionId: number;
    question: string;
    selected: string | null;
    correct: string;
    isCorrect: boolean;
    explanation: string | null;
  }[];
}

export interface ClassProgress {
  students: {
    studentId: number;
    name: string;
    completedExams: number;
    totalExams: number;
    averageScore: number;
    mistakes: number;
    details: {
      examId: number;
      examTitle: string;
      question: string;
      selected: string | null;
      correct: string;
      explanation: string | null;
    }[];
  }[];
  classAverageScore: number;
  totalExams: number;
}

const unwrap = <T>(body: ApiResponse<T>): T => {
  if (body.code !== 1000 || body.data == null) throw new Error(body.message || 'Không xử lý được bài kiểm tra.');
  return body.data;
};

export const getInstructorExams = async (classId: number): Promise<ExamSummary[]> =>
  unwrap((await axios.get<ApiResponse<ExamSummary[]>>(`api/instructor/classes/${classId}/exams`)).data);

export const getInstructorExam = async (classId: number, examId: number): Promise<ExamEditor> =>
  unwrap((await axios.get<ApiResponse<ExamEditor>>(`api/instructor/classes/${classId}/exams/${examId}`)).data);

export const createClassExam = async (classId: number, values: SaveExam): Promise<ExamSummary> =>
  unwrap((await axios.post<ApiResponse<ExamSummary>>(`api/instructor/classes/${classId}/exams`, values)).data);

export const updateClassExam = async (
  classId: number,
  examId: number,
  values: Omit<SaveExam, 'materialId' | 'questions'> & { status: 'ACTIVE' | 'ARCHIVED'; questions?: ExamQuestionInput[] },
): Promise<ExamSummary> =>
  unwrap((await axios.put<ApiResponse<ExamSummary>>(`api/instructor/classes/${classId}/exams/${examId}`, values)).data);

export interface MatrixRow {
  topic?: string;
  difficulty?: number;
  count: number;
  points?: number;
}

export interface GeneratePreviewRequest {
  bankId: number;
  matrix: MatrixRow[];
}

export interface PickedQuestion {
  questionId: number;
  content: string;
  difficulty: number;
  topic: string;
  points: number;
  options: string[];
  correctIndex: number;
  explanation?: string;
}

export interface CreateFromMatrixRequest {
  title: string;
  durationMinutes: number;
  passScore: number;
  assignedStudentId?: number | null;
  materialId?: number | null;
  bankId: number;
  questionIds: number[];
  pointsPerQuestion?: number;
}

export const generateExamPreview = async (classId: number, values: GeneratePreviewRequest): Promise<PickedQuestion[]> =>
  unwrap((await axios.post<ApiResponse<PickedQuestion[]>>(`api/instructor/classes/${classId}/exams/generate-preview`, values)).data);

export const createExamFromMatrix = async (classId: number, values: CreateFromMatrixRequest): Promise<ExamSummary> =>
  unwrap((await axios.post<ApiResponse<ExamSummary>>(`api/instructor/classes/${classId}/exams/from-matrix`, values)).data);

export const getClassProgress = async (classId: number): Promise<ClassProgress> =>
  unwrap((await axios.get<ApiResponse<ClassProgress>>(`api/instructor/classes/${classId}/progress`)).data);

export const getStudentExams = async (classId: number): Promise<ExamSummary[]> =>
  unwrap((await axios.get<ApiResponse<ExamSummary[]>>(`api/student/classes/${classId}/exams`)).data);

export const getStudentExamResults = async (classId: number, examId: number): Promise<ExamResult[]> =>
  unwrap((await axios.get<ApiResponse<ExamResult[]>>(`api/student/classes/${classId}/exams/${examId}/results`)).data);

export const startClassExam = async (classId: number, examId: number): Promise<ExamAttempt> =>
  unwrap((await axios.post<ApiResponse<ExamAttempt>>(`api/student/classes/${classId}/exams/${examId}/attempts`)).data);

export const submitClassExam = async (
  classId: number,
  examId: number,
  attemptId: number,
  answers: { questionId: number; optionId: number | null }[],
): Promise<ExamResult> =>
  unwrap(
    (await axios.post<ApiResponse<ExamResult>>(`api/student/classes/${classId}/exams/${examId}/attempts/${attemptId}/submit`, { answers }))
      .data,
  );
