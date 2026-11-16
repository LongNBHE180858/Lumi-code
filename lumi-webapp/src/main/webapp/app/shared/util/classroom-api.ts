import axios from 'axios';
import type { ApiResponse } from 'app/shared/model/api-response.model';

export interface ClassroomMember {
  id: number;
  studentId: number;
  name: string;
  email: string;
  status: 'ACTIVE' | 'PENDING';
  joinedAt: string | null;
}

export interface ClassroomMaterial {
  id: number;
  title: string;
  fileType: string;
  fileUrl: string;
  uploadedAt: string | null;
  orderIndex: number;
}

export interface ClassroomTask {
  id: number;
  title: string;
  instructions: string | null;
  dueAt: string | null;
  status: string;
}

export interface Classroom {
  id: number;
  name: string;
  description: string | null;
  joinCode: string | null;
  students: ClassroomMember[];
  lessons: ClassroomMaterial[];
  assignments: ClassroomTask[];
}

export interface JoinedClass {
  id: number;
  name: string;
  description: string | null;
  membershipStatus: 'ACTIVE' | 'PENDING';
}

export interface ClassroomPage<T> {
  items: T[];
  page: number;
  totalElements: number;
  totalPages: number;
}

export interface ClassroomMemberPage extends ClassroomPage<ClassroomMember> {
  activeCount: number;
  pendingCount: number;
}

const unwrap = <T>(body: ApiResponse<T>): T => {
  if (body.code !== 1000 || body.data == null) throw new Error(body.message || 'Không tải được dữ liệu lớp học.');
  return body.data;
};

export const classroomError = (error: unknown): string =>
  axios.isAxiosError<{ message?: string }>(error)
    ? error.response?.data?.message || 'Không kết nối được máy chủ.'
    : error instanceof Error
      ? error.message
      : 'Có lỗi xảy ra.';

export const getInstructorClassroom = async (id: number): Promise<Classroom> =>
  unwrap((await axios.get<ApiResponse<Classroom>>(`api/instructor/classes/${id}/workspace`)).data);

export const getInstructorClassMembers = async (id: number, page: number): Promise<ClassroomMemberPage> =>
  unwrap((await axios.get<ApiResponse<ClassroomMemberPage>>(`api/instructor/classes/${id}/members`, { params: { page, size: 10 } })).data);

export const uploadClassMaterial = async (classId: number, title: string, file: File): Promise<ClassroomMaterial> => {
  const form = new FormData();
  form.append('file', file);
  return unwrap(
    (await axios.post<ApiResponse<ClassroomMaterial>>(`api/instructor/classes/${classId}/materials`, form, { params: { title } })).data,
  );
};

export const reorderClassMaterials = async (classId: number, ids: number[]): Promise<ClassroomMaterial[]> =>
  unwrap((await axios.put<ApiResponse<ClassroomMaterial[]>>(`api/instructor/classes/${classId}/materials/order`, ids)).data);

export const openClassMaterial = async (url: string): Promise<void> => {
  const tab = window.open('', '_blank');
  try {
    const response = await axios.get<Blob>(url, { responseType: 'blob' });
    const blobUrl = URL.createObjectURL(response.data);
    if (tab) tab.location.href = blobUrl;
    else window.location.href = blobUrl;
    window.setTimeout(() => URL.revokeObjectURL(blobUrl), 5 * 60 * 1000);
  } catch (error) {
    tab?.close();
    throw error;
  }
};

export const approveClassMember = async (classId: number, memberId: number): Promise<ClassroomMember> =>
  unwrap((await axios.post<ApiResponse<ClassroomMember>>(`api/instructor/classes/${classId}/members/${memberId}/approve`)).data);

export const rejectClassMember = async (classId: number, memberId: number): Promise<void> => {
  await axios.post(`api/instructor/classes/${classId}/members/${memberId}/reject`);
};

export const removeClassMember = async (classId: number, memberId: number): Promise<void> => {
  await axios.delete(`api/instructor/classes/${classId}/members/${memberId}`);
};

export const createClassTask = async (
  classId: number,
  values: { title: string; instructions: string; dueAt: string | null },
): Promise<ClassroomTask> =>
  unwrap((await axios.post<ApiResponse<ClassroomTask>>(`api/instructor/classes/${classId}/assignments`, values)).data);

export const getStudentClasses = async (page: number): Promise<ClassroomPage<JoinedClass>> =>
  unwrap((await axios.get<ApiResponse<ClassroomPage<JoinedClass>>>('api/student/classes', { params: { page, size: 10 } })).data);

export const joinStudentClass = async (code: string): Promise<JoinedClass> =>
  unwrap((await axios.post<ApiResponse<JoinedClass>>('api/student/classes/join', { code })).data);

export const getStudentClassroom = async (id: number): Promise<Classroom> =>
  unwrap((await axios.get<ApiResponse<Classroom>>(`api/student/classes/${id}`)).data);
