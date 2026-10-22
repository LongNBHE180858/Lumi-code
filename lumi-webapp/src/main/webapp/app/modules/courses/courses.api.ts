import axios from 'axios';

import type { ApiResponse } from 'app/modules/profile/profile.model';
import type { CourseBrowseRequest, CourseBrowseResponse, CourseFilterOptions } from './courses.model';

export const getCourses = async (filters: CourseBrowseRequest, signal: AbortSignal): Promise<CourseBrowseResponse> => {
  const params = new URLSearchParams({ page: String(filters.page), size: '6', feeType: filters.feeType });
  if (filters.keyword.trim()) params.set('keyword', filters.keyword.trim());
  if (filters.subjectId !== null) params.set('subjectId', String(filters.subjectId));
  if (filters.gradeIds.length > 0) params.set('gradeIds', filters.gradeIds.join(','));

  const { data: body } = await axios.get<ApiResponse<CourseBrowseResponse>>('api/courses/browse', { params, signal });
  if (body?.code !== 1000 || body.status !== 200 || !Array.isArray(body.data?.content)) {
    throw new Error('Không tải được danh sách khóa học.');
  }
  return body.data;
};

export const getCourseFilterOptions = async (signal: AbortSignal): Promise<CourseFilterOptions> => {
  // Hai API danh mục hiện trả mảng trực tiếp, không bọc APIResponse.
  const [subjects, grades] = await Promise.all([
    axios.get<CourseFilterOptions['subjects']>('api/subjects', { params: { page: 0, size: 100, sort: 'name,asc' }, signal }),
    axios.get<CourseFilterOptions['grades']>('api/grades', {
      params: { 'level.greaterThanOrEqual': 1, 'level.lessThanOrEqual': 9, page: 0, size: 9, sort: 'level,asc' },
      signal,
    }),
  ]);

  if (!Array.isArray(subjects.data) || !Array.isArray(grades.data)) {
    throw new Error('Không tải được bộ lọc khóa học.');
  }
  return {
    subjects: subjects.data,
    grades: grades.data.filter(grade => grade.level >= 1 && grade.level <= 9).sort((a, b) => a.level - b.level),
  };
};
