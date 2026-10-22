import axios from 'axios';

import type { ApiResponse } from 'app/modules/profile/profile.model';
import type { CourseOverview, CourseReviewsPage, CourseReviewSort } from './course-detail.model';

export const getCourseOverview = async (id: number, signal: AbortSignal): Promise<CourseOverview> => {
  const { data: body } = await axios.get<ApiResponse<CourseOverview>>(`api/courses/${id}/overview`, { signal });
  if (body?.code !== 1000 || body.status !== 200 || body.data?.id !== id || !Array.isArray(body.data.chapters)) {
    throw new Error('Không tải được thông tin khóa học.');
  }
  return body.data;
};

export const getCourseReviews = async (
  id: number,
  page: number,
  sort: CourseReviewSort,
  signal: AbortSignal,
): Promise<CourseReviewsPage> => {
  const { data: body } = await axios.get<ApiResponse<CourseReviewsPage>>(`api/courses/${id}/reviews`, {
    params: { page, size: 2, sort },
    signal,
  });
  if (body?.code !== 1000 || body.status !== 200 || body.data?.courseId !== id || !Array.isArray(body.data.content)) {
    throw new Error('Không tải được đánh giá khóa học.');
  }
  return body.data;
};
