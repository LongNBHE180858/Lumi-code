import axios from 'axios';

const baseUrl = 'api/admin/instructor-applications';

export type InstructorApplicationStatus = 'PENDING' | 'ACTIVE' | 'REJECTED';

export interface InstructorApplication {
  userId: number;
  login: string;
  firstName: string | null;
  lastName: string | null;
  email: string | null;
  phoneNumber: string | null;
  schoolName: string | null;
  gradeLevel: number | null;
  activated: boolean;
  status: InstructorApplicationStatus;
  submittedAt: string | null;
  reviewedAt: string | null;
  rejectionReason: string | null;
}

export const instructorApplicationsApi = {
  list: (status: InstructorApplicationStatus, page: number, size: number) =>
    axios.get<InstructorApplication[]>(baseUrl, { params: { status, page, size, sort: 'id,desc' } }),
  get: (userId: number) => axios.get<InstructorApplication>(`${baseUrl}/${userId}`),
  approve: (userId: number) => axios.post<InstructorApplication>(`${baseUrl}/${userId}/approve`),
  reject: (userId: number, reason: string) => axios.post<InstructorApplication>(`${baseUrl}/${userId}/reject`, { reason }),
};
