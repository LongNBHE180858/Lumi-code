import axios from 'axios';
import type { ApiResponse } from 'app/shared/model/api-response.model';

export type ClassStatus = 'DRAFT' | 'PENDING' | 'ACTIVE' | 'HIDDEN' | 'ARCHIVED' | 'BLOCKED';
export type ClassJoinMode = 'INVITE_ONLY' | 'CODE_APPROVAL' | 'CODE_AUTO';

export interface InstructorClass {
  id: number;
  name: string;
  description: string | null;
  joinCode: string | null;
  joinMode: ClassJoinMode;
  status: ClassStatus;
  subjectId: number | null;
  subjectName: string | null;
  gradeId: number | null;
  gradeName: string | null;
  maxStudents: number | null;
  studentCount: number;
  createdAt: string | null;
}

export interface ClassFormValues {
  name: string;
  description: string;
  joinMode: ClassJoinMode;
  status: ClassStatus;
  maxStudents: number | null;
}

interface ClassPage {
  items: InstructorClass[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

type ClassDetail = Pick<InstructorClass, 'id' | 'name' | 'description' | 'joinCode' | 'joinMode' | 'status' | 'maxStudents' | 'createdAt'>;

const unwrap = <T>(body: ApiResponse<T>, status: number): T => {
  if (body?.code !== 1000 || body.status !== status || body.data == null) {
    throw new Error(body?.message || 'Không xử lý được lớp học.');
  }
  return body.data;
};

export const getInstructorClasses = async (page: number, signal?: AbortSignal): Promise<ClassPage> => {
  const { data } = await axios.get<ApiResponse<ClassPage>>('api/instructor/classes', { params: { page, size: 20 }, signal });
  return unwrap(data, 200);
};

export const createInstructorClass = async (values: ClassFormValues): Promise<ClassDetail> => {
  const { data } = await axios.post<ApiResponse<ClassDetail>>('api/instructor/classes', values);
  return unwrap(data, 201);
};

export const updateInstructorClass = async (id: number, values: ClassFormValues): Promise<ClassDetail> => {
  const { data } = await axios.put<ApiResponse<ClassDetail>>(`api/instructor/classes/${id}`, values);
  return unwrap(data, 200);
};
