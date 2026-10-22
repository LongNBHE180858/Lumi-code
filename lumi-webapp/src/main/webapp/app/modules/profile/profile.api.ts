import axios from 'axios';

import type { AccountProfile, ApiResponse, LinkedStudent, ProfileStatistics, ProfileUpdate } from './profile.model';

const getProfileData = async <T>(url: string, signal?: AbortSignal): Promise<T> => {
  const response = await axios.get<ApiResponse<T>>(url, { signal });

  const body = response.data;

  if (body?.code !== 1000 || body.status !== 200 || body.data == null) {
    throw new Error('Không lấy được thông tin hồ sơ.');
  }

  return body.data;
};

export const getMyProfile = (signal?: AbortSignal) => getProfileData<AccountProfile>('api/profile', signal);

export const updateMyProfile = async (profile: ProfileUpdate, signal?: AbortSignal): Promise<AccountProfile> => {
  const { data: body } = await axios.put<ApiResponse<AccountProfile>>('api/profile', profile, { signal });
  if (body?.code !== 1000 || body.status !== 200 || body.data == null) {
    throw new Error('Không lưu được hồ sơ. Vui lòng thử lại.');
  }
  return body.data;
};

export const getMyStatistics = (signal?: AbortSignal) => getProfileData<ProfileStatistics>('api/profile/statistics', signal);

export const getMyLinkedStudents = (signal?: AbortSignal) => getProfileData<LinkedStudent[]>('api/profile/linked-students', signal);
