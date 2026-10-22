import axios from 'axios';

import type { ApiResponse } from 'app/modules/profile/profile.model';
import type { InstructorPublicCourse, InstructorPublicProfile } from './instructor-profile.model';

const getPublicData = async <T>(url: string, signal: AbortSignal): Promise<T> => {
  const { data } = await axios.get<ApiResponse<T>>(url, { signal });
  if (data?.code !== 1000 || data.status !== 200 || data.data == null) {
    throw new Error('Không tải được dữ liệu giảng viên.');
  }
  return data.data;
};

export const getInstructorProfile = (id: number, signal: AbortSignal) =>
  getPublicData<InstructorPublicProfile>(`api/profile/instructor/${id}`, signal);

export const getInstructorCourses = (id: number, signal: AbortSignal) =>
  getPublicData<InstructorPublicCourse[]>(`api/profile/instructor/course/${id}`, signal);
